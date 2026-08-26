# CLDR Code (`cldr-code`) — Bug & Performance Analysis

> Generated: 2026-07-08
> Scope: `/src/cldr/tools/cldr-code/src/main/java/org/unicode/cldr/` (638 Java files)

---

## Table of Contents

1. [Definite Bugs](#1-definite-bugs)
2. [Thread-Safety Issues](#2-thread-safety-issues)
3. [Performance Issues](#3-performance-issues)
4. [Resource Leaks](#4-resource-leaks)
5. [Appendix: Files Reviewed](#5-appendix-files-reviewed)

---

## 1. Definite Bugs

### Bug A — `SimpleXMLSource.cloneAsThawed()` silently drops source locations

**File:** `util/SimpleXMLSource.java:105`

```java
result.locationHash.putAll(result.locationHash);  // copies to itself — no-op
```

Should be `result.locationHash.putAll(this.locationHash)`. Any code path that clones a `SimpleXMLSource` after source locations have been set will silently lose them.

**Impact:** `CLDRFile.cloneAsThawed()` delegates to `XMLSource.cloneAsThawed()`, which for `SimpleXMLSource` calls this method. All callers that clone a loaded CLDR file and later call `getSourceLocation()` on the clone will get `null` for every path that had a source location in the original.

**Fix:** Change to `result.locationHash.putAll(this.locationHash)`.

---

### Bug B — `clearCache()` leaves stale `reverseAliasCache`

**File:** `util/XMLSource.java:476-478`

```java
private void clearCache() {
    aliasCache = null;  // does NOT null reverseAliasCache
}
```

`getReverseAliases()` (line 448) returns the stale `reverseAliasCache` as long as it's non-null:
```java
private LinkedHashMap<String, List<String>> getReverseAliases() {
    if (cachingIsEnabled && reverseAliasCache != null) {
        return reverseAliasCache;   // stale!
    }
    ...
```

**Impact:** After any mutation (`putValueAtPath` / `removeValueAtPath`), `getReverseAliases()` returns data built from the old alias set. This propagates to `valueChanged()` in the `ResolvingSource` inner class (lines 1357-1377), which calls `getDirectAliases()` → `getReverseAliases()` when deciding which cache entries to invalidate. It's using stale alias mappings, so cache entries that depend on changed aliases may not be properly invalidated.

**Fix:** Add `reverseAliasCache = null;` inside `clearCache()`.

---

### Bug C — `MainCache` instance-level lock on static map

**File:** `draft/MainCache.java:7-21`

```java
private static Map<Class<?>, Map<Object, Object>> cache = new HashMap<>();

protected synchronized Object get(Object key) {  // locks on THIS
    Class<?> classKey = this.getClass();
    Map<Object, Object> submap = cache.get(classKey);
    if (submap == null) {
        cache.put(classKey, submap = new HashMap<>());
    }
    Object result = submap.get(key);
    if (result == null) {
        result = createObject(key);
        submap.put(key, result);
    }
    return result;
}
```

`synchronized` on `this` (the instance) but the cache map is `static`. Two different instances of the same subclass use **different locks** to access the **same shared data**.

**Impact:** Two threads can simultaneously enter `get()` on two different `MainCache` instances, both see `submap == null` for the same class, both `put()` a new inner `HashMap`, and one overwrites the other. Concurrent access to the static `HashMap` can also cause infinite loops, data loss, or NPEs.

**Fix:** Make the method `synchronized static` or use a `static final Object lock` or replace with `ConcurrentHashMap<Class<?>, ConcurrentHashMap<Object, Object>>`.

---

## 2. Thread-Safety Issues

### Issue 1 — `getFullPathAtDPathCache` is an unsynchronized `HashMap`

**File:** `util/XMLSource.java:783`

```java
Map<String, String> getFullPathAtDPathCache = new HashMap<>();  // not thread-safe
```

Accessed from `getFullPath()` (lines 828-861) with `cachingIsEnabled=true` and no synchronization:

```java
public String getFullPath(String distinguishingXPath) {
    ...
    if (cachingIsEnabled) {
        String result = getFullPathAtDPathCache.get(distinguishingXPath); // race
        if (result != null) return result;
    }
    ...
    if (cachingIsEnabled) {
        getFullPathAtDPathCache.put(distinguishingXPath, xpath); // race
    }
    return xpath;
}
```

**Severity: High.** Under concurrent load (Survey Tool serving multiple requests), `HashMap` can corrupt — causing infinite loops in `get()`, lost entries, or NPEs.

**Fix:** Use `ConcurrentHashMap<String, String>`.

---

### Issue 2 — `getCachedKeySet()` unsynchronized lazy init

**File:** `util/XMLSource.java:1313-1319`

```java
private transient Set<String> cachedKeySet = null;

private Set<String> getCachedKeySet() {
    if (cachedKeySet == null) {        // not volatile, no synchronization
        cachedKeySet = fillKeys();
        cachedKeySet = Collections.unmodifiableSet(cachedKeySet);
    }
    return cachedKeySet;
}
```

**Problems:**
1. `cachedKeySet` is not `volatile` — a thread may see a non-null reference to a **partially constructed** set (JMM allows instruction reordering where the reference is written before the constructor completes)
2. Multiple threads can call `fillKeys()` simultaneously (wasted work)
3. Called from `iterator()` on `ResolvingSource` — every iteration over resolved CLDR data hits this code path

**Severity: High.**

**Fix:** Add `volatile` to `cachedKeySet` and use double-checked locking (or initialize eagerly).

---

### Issue 3 — `XPathParts.freeze()` race with concurrent mutation

**File:** `util/XPathParts.java:1175-1187` and mutation methods (e.g. `putAttributeValue()` at ~line 447-451)

```java
// freeze():
public XPathParts freeze() {
    if (!frozen) {
        List<Element> temp = new ArrayList<>(elements.size());
        for (Element element : elements) {
            temp.add(element.makeImmutable());
        }
        elements = ImmutableList.copyOf(temp);
        frozen = true;
    }
    return this;
}

// Mutation (e.g., putAttributeValue):
public XPathParts putAttributeValue(int elementIndex, String attributeName, String attributeValue) {
    makeElementsMutable();                      // reads frozen, does nothing if already ArrayList
    makeElementMutable(elementIndex);           // reads frozen again
    elements.get(elementIndex).putAttribute(attributeName, attributeValue);
    return this;
}
```

**Race:** Thread A enters `putAttributeValue()`, `makeElementsMutable()` reads `frozen == false`, does nothing because `elements` is still an `ArrayList`. Thread B calls `freeze()`, replaces `elements` with `ImmutableList`, sets `frozen = true`. Thread A then calls `elements.get(elementIndex).putAttribute(...)` on the `ImmutableList` → **`UnsupportedOperationException`**.

**Severity: High.** This will crash threads under concurrent access.

**Fix:** Synchronize mutation and freezing on the same lock, or use a `ReentrantReadWriteLock`.

---

### Issue 4 — `listeners` list not thread-safe

**File:** `util/XMLSource.java:183,1510,1519-1529`

```java
private final List<WeakReference<Listener>> listeners = new ArrayList<>();
```

`addListener()` does `listeners.add(...)` and `notifyListeners()` does `listeners.get(i)` / `listeners.remove(i)` — no synchronization on either.

**Severity: Medium-High.** Concurrent calls from multiple threads can produce `ConcurrentModificationException` or silently corrupt the list.

**Fix:** Use `CopyOnWriteArrayList` or synchronize access.

---

### Issue 5 — `cachingIsEnabled` not volatile

**File:** `util/XMLSource.java:166`

```java
private static boolean cachingIsEnabled = true;

public static void disableCaching() { cachingIsEnabled = false; }
```

Read in several unsynchronized paths (`getReverseAliases()`, `getCachedFullStatus()`, `getFullPath()`, `valueChanged()`). Written by `disableCaching()`. Without `volatile`, the change may never be visible to reader threads.

**Severity: Low-Medium.** The method is called rarely (mostly in test tear-down), so the window is small, but it's still a JMM violation.

**Fix:** Add `volatile` keyword.

---

### Issue 6 — `SimpleXMLSource.locationHash` not thread-safe

**File:** `util/SimpleXMLSource.java:239`

```java
private Map<String, SourceLocation> locationHash = new HashMap<>();  // not ConcurrentHashMap
```

Accessed from `addSourceLocation()` and `getSourceLocation()` without synchronization.

**Severity: Medium.**

**Fix:** Use `ConcurrentHashMap` or add synchronization.

---

### Issue 7 — `Factory` data races

**File:** `util/Factory.java`

- `supplementalDirectory` (line 39): non-volatile, read from `make()` (line 135), written from `setSupplementalDirectory()` (line 290)
- `ignoreExplicitParentLocale` (line 24): non-volatile, read from `makeResolvingSource()` (line 209) and `makeWithFallback()` (line 157), written from `setIgnoreExplicitParentLocale()` (line 31)

Neither field is `volatile` and no lock protects reads/writes.

**Severity: Medium.** In practice these are typically set once during construction, so the race is benign in most workflows — but it violates the JMM and could cause confusing bugs under specific concurrent initialization patterns.

**Fix:** Make both fields `volatile`.

---

### Issue 8 (Bonus) — `SimpleFactory` synchronization on Guava `Cache.asMap()`

**File:** `util/SimpleFactory.java` (when `USE_COMBINEDCACHE=true`)

```java
mapToSynchronizeOn = combinedCache.asMap();
synchronized (mapToSynchronizeOn) {
    Object returned = mapToSynchronizeOn.get(cacheKey);
    ...
    mapToSynchronizeOn.put(cacheKey, result);
}
```

If `Cache.asMap()` returns a **new wrapper instance on each call** (which Guava's `LocalCache.asMap()` can do), each thread synchronizes on a different object, making the `synchronized` block a no-op for mutual exclusion.

**Severity: Low.** Guava Cache internals are themselves thread-safe, so the synchronized block provides no additional safety — but the double-checked locking pattern is ineffective. Wasted object creation on concurrent access.

---

## 3. Performance Issues

### Bottleneck — `getNondraftNonaltXPath()` global synchronization

**File:** `util/CLDRFile.java:1582-1624`

```java
private static final Object syncObject = new Object();  // global across entire JVM

public static String getNondraftNonaltXPath(String xpath) {
    if (!xpath.contains("draft=\"") && !xpath.contains("alt=\"")) {
        return xpath;
    }
    synchronized (syncObject) {     // ✂ serialize ALL threads, ALL call sites
        XPathParts parts = XPathParts.getFrozenInstance(xpath).cloneAsThawed();
        ...
    }
}
```

**Problem:** This static method serializes across ALL threads and ALL calling classes. Called from:
- `CLDRFile.putAll()` (merge operations)
- `CLDRFile.removeDuplicates()`
- Logical grouping operations

For a hot code path in the Survey Tool (which processes multiple requests concurrently), this is a throughput bottleneck. The synchronization is needed because `XPathParts.cloneAsThawed()` mutates a shared parsed representation (for the `!frozen` case in `cloneAsThawed()`), but the scope is overly coarse.

**Mitigation:** Use a per-thread `ThreadLocal<XPathParts>` pool, or synchronize at a finer granularity.

---

### Sub-optimal — `WeakHashMap` with interned String keys

**File:** `util/XMLSource.java:946-947` (ResolvingSource inner class)

```java
private final transient Map<String, AliasLocation> getSourceLocaleIDCache = new WeakHashMap<>();
```

Keys are often `intern()`ed — explicit calls at lines 1019, 1100, 1120, 1136, 1141, 1252:

```java
if (doInternStrings) { xpath = xpath.intern(); }
```

**Problem:** Interned strings live forever in the JVM string pool. The weak references in `WeakHashMap` never trigger eviction for these keys, so the map grows **unbounded** as new distinct paths are encountered. Meanwhile, non-interned keys **can** be GC'd, causing nondeterministic recomputation. The semantics are inconsistent.

**Fix:** Use a proper size-bounded cache (Guava `CacheBuilder` with `maximumSize()`, or a `LinkedHashMap` with `removeEldestEntry()`).

---

### Sub-optimal — `PathHeader.fromPath()` two-level locking

**File:** `util/PathHeader.java:753-820`

Acquire sequence:
1. `synchronized (cache)` — read (line 757), **released** at line 762
2. `synchronized (lookup)` — compute (line 763), held through line 795
3. `synchronized (cache)` — write (line 796), **nested inside `lookup`**

Comments at lines 687-693 document the intent:
```java
// synchronized with lookup
// synchronized with cache
```

**Problem:** The pattern is fragile — if any future code path holds `cache` first then tries to acquire `lookup` while retaining `cache`, **deadlock** results. Currently no code path does this, so it's not an active deadlock, but the nesting is inverted from the safe pattern (always acquire in the same order). The double-check with two separate `synchronized (cache)` blocks is also unnecessary overhead.

---

### Minor — `StringBuffer` in single-threaded contexts

**File:** `util/CldrUtility.java` (lines 629, 1147, 1214) and other files

`StringBuffer` (synchronized) is used where `StringBuilder` would be faster. Not a correctness issue — just ~5-15% overhead on string operations in these methods.

---

## 4. Resource Leaks

### Critical — Not closed on exception path

| File | Lines | Resource | Issue |
|------|-------|----------|-------|
| `util/Zipper.java` | 25 | `FileOutputStream fos = new FileOutputStream(zipFileName)` | `fos.close()` at line 29. If `zipFile()` throws between 25-28, the stream is leaked. |
| `util/Zipper.java` | 56 | `FileInputStream fis = new FileInputStream(file)` | `fis.close()` at line 63. If `fis.read()` throws between 56-62, the stream is leaked. |
| `util/CldrUtility.java` | 1146-1155 | `BufferedReader br = FileUtilities.openUTF8Reader(dir, filename)` | `br.close()` at line 1155 is NOT in a finally block. If `readLine()` at line 1148 throws, the underlying `FileInputStream` is leaked. |
| `util/XMLValidator.java` | 305 | `BufferedReader br = new BufferedReader(new FileReader(filename))` | `br.close()` at line 318 is inside the try block, not in `finally`. If `br.readLine()` at line 308 throws, the `FileReader` is leaked. |
| `util/LDMLComparator.java` | 251-252 | `FileOutputStream(fileName)` → `OutputStreamWriter` | Never closed. When the enclosing try block (line 219) exits, these are leaked. |
| `util/LDMLComparator.java` | 1497-1500 | Two `FileOutputStream` → `OutputStreamWriter` → `PrintWriter` | Never closed. No `finally` or ARM. |
| `util/FileProcessor.java` | 57-59, 69-71 | `FileInputStream` → `InputStreamReader` → `BufferedReader` | `BufferedReader.close()` is called in `process(BufferedReader)` at line 105, but if an exception occurs before that (lines 61-72), the resources are never closed. |
| `util/FixEras.java` | 97 | `FileOutputStream(destfile)` → `OutputStreamWriter` | Writer closed at line 102 only if `printDOMTree` at line 99 succeeds. If it throws, `close()` is never reached (the catch at line 103 calls `System.exit()`). |
| `util/ModifyCase.java` | 70 | `FileWriter(destfile)` → `BufferedWriter m_out` | `closeLDML()` calls `m_out.close()` at line 76, but if `openLDML()` or `makeLowerCase()` throws before line 76, the writer is leaked. |
| `util/TempPrintWriter.java` | 192-193 | `BufferedReader(FileReader(file1))` and `BufferedReader(FileReader(file2))` | If an exception occurs on lines 197-210 (the comparison logic), neither reader is ever closed (no try-finally). |
| `util/FileCopier.java` | 206 | `copyAndReplace(InputStreamReader, ... new FileWriter(...))` | Method doc says "leaves the writer open" — but the caller never closes the `FileWriter` either. Confirmed leak. |

### Low risk (resources passed to caller)

| File | Lines | Resource | Notes |
|------|-------|----------|-------|
| `util/InputStreamFactory.java` | 26 | `new FileInputStream(f)` | Factory method — callers use try-with-resources |
| `util/FileReaders.java` | 71 | `FileInputStream` → `InputStreamReader` | Factory method — callers must close |

---

## 5. Appendix: Files Reviewed

### Core utility classes (read in full)
- `util/CLDRConfig.java` (786 lines)
- `util/CLDRConfigImpl.java`
- `util/CLDRFile.java` (3383 lines — key sections)
- `util/XMLSource.java` (1674 lines — full)
- `util/SimpleXMLSource.java` (256 lines — full)
- `util/Factory.java`
- `util/SimpleFactory.java`
- `util/XPathParts.java` (1330 lines — key sections)
- `util/PathHeader.java` (2668 lines — key sections)
- `util/CLDRLocale.java` (733 lines — full)
- `util/CldrUtility.java` (1828 lines — resource leak search)
- `util/VoteResolver.java` (threading analysis)
- `util/SupplementalDataInfo.java` (5423 lines — volatile fields)
- `util/CLDRTransforms.java`
- `util/LockSupportMap.java`
- `util/Annotations.java`
- `util/Emoji.java`
- `util/Validity.java`
- `util/UnitConverter.java`
- `util/DtdData.java`
- `util/NestedMap.java`
- `util/PathChecker.java`
- `util/LanguageTagCanonicalizer.java`
- `util/NameGetter.java`
- `util/StandardCodes.java`

### API layer (read in full)
- `api/XmlDataSource.java` (386 lines — volatile + double-checked locking)
- `api/CldrDataSupplier.java`

### Draft code
- `draft/MainCache.java` (24 lines — full)

### Tool classes (sections)
- `tool/Main.java`
- `tool/CLDRModify.java`
- `tool/ToolConfig.java`
- `tool/CLDRFileTransformer.java`

### Resource leak search coverage
- `util/Zipper.java`
- `util/FileProcessor.java`
- `util/TempPrintWriter.java`
- `util/InputStreamFactory.java`
- `util/FileReaders.java`
- `util/XMLFileReader.java`
- `util/XMLNormalizingLoader.java`
- `util/XMLValidator.java`
- `util/ElementAttributeInfo.java`
- `util/FileCopier.java`
- `util/LDMLComparator.java`
- `util/RegexFileParser.java`
- `util/ScriptToExemplars.java`
- `util/DTD2XSD.java`
- `util/FindDTDOrder.java`

---

*End of analysis. 3 definite bugs, 8 threading issues, 3 performance issues, 11 resource leak locations identified.*
