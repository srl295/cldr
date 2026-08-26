# CLDR `cldr-code` Analysis — Performance, Threading & Bugs

**Date:** 2026-07-08  
**Package analyzed:** `org.unicode.cldr.util`  
**Scope:** `/src/cldr/tools/cldr-code/src/main/java/org/unicode/cldr/util/`

---

## Table of Contents

1. [Critical Bugs](#1-critical-bugs)
2. [Performance Issues](#2-performance-issues)
3. [Threading & Concurrency Concerns](#3-threading--concurrency-concerns)
4. [Code Quality & Migrations](#4-code-quality--migrations)
5. [Top Recommendations](#5-top-recommendations)

---

## 1. Critical Bugs

### 1.1 `Counter.compareTo()` — Wrong iterator (`Counter.java:296`)

```java
// BUG: reads from 'i' (this) instead of 'j' (other)
T jj = i.next();   // should be 'j.next()'
```

Copy-paste bug. Iterates over the wrong collection, returning incorrect comparison results and potentially throwing `NoSuchElementException` if `this` has more entries than `other`.

### 1.2 `Counter2.compareTo()` — Same iterator bug (`Counter2.java:206`)

```java
// Same bug:
T jj = i.next();   // should be 'j.next()'
```

### 1.3 `Counter.equals()` — Always returns false (`Counter.java:317`)

```java
@Override
public boolean equals(Object o) {
    return map.equals(o);  // compares Map<RWLong> to Counter — different types!
}
```

Unless `o` is the exact same reference (caught by `==` above in `Map.equals`), this always returns `false`. Should be:

```java
if (o instanceof Counter) {
    return map.equals(((Counter<?>) o).map);
}
return false;
```

### 1.4 `Counter2` — Mutable public static fields (`Counter2.java:58-59`)

```java
public static Double ZERO = (double) 0;
public static Double ONE = (double) 1;
```

Non-final statics can be reassigned by any code. Should be `public static final Double`.

### 1.5 `LockSupportMap.removeItemLock()` — Broken synchronization (`LockSupportMap.java:50-54`)

```java
public Object removeItemLock(E itemToRemove) {
    synchronized (getItemLock(itemToRemove)) {
        return locks.remove(itemToRemove);
    }
}
```

Synchronizes on the lock object, then removes it from the map. The next call to `getItemLock` for the same key returns a **different** object, making the synchronization ineffective. The intent (serializing remove + next getItemLock) is not achieved.

### 1.6 `Log.java` — Static mutable state, no thread safety (`Log.java:18-43`)

```java
private static PrintWriter log;   // accessed everywhere, no synchronization

public static void logln(boolean test, String message) {
    if (log != null && test) log.println(message);
}

public static void setLog(PrintWriter newlog) {
    log = newlog;
}
```

Multiple threads calling `setLog()` and `logln()` concurrently will cause:
- **NullPointerException**: race between `if (log != null)` and the `println` call
- **Stale writes**: no happens-before edge
- **Interleaved output**: no exclusion on the underlying writer

### 1.7 `LockSupportMap.getItemLock()` — Eager allocation on every call (`LockSupportMap.java:34`)

```java
public Object getItemLock(E item) {
    Object oldLock = new Object();              // allocated every time
    Object newLock = locks.putIfAbsent(item, oldLock);
    Object sync = newLock == null ? oldLock : newLock;
    return sync;
}
```

A new `Object` is allocated on every call, even when the key already has a lock. Should use `computeIfAbsent`:

```java
public Object getItemLock(E item) {
    return locks.computeIfAbsent(item, k -> new Object());
}
```

---

## 2. Performance Issues

### 2.1 `Counter.getTotal()` — 2N map lookups (`Counter.java:157-162`)

```java
public long getTotal() {
    long count = 0;
    for (T item : map.keySet()) {       // N iterations
        count += map.get(item).value;   // N additional lookups
    }
    return count;
}
```

Should iterate `map.values()` or `map.entrySet()` — avoids the per-element `get()`:

```java
for (RWLong val : map.values()) {
    count += val.value;
}
```

Same pattern in `Counter2.getTotal()` (lines 80-86).

### 2.2 `IntMap.CompactStringIntMapFactory` — O(n²·m) build time (`IntMap.java:160`)

```java
int position = data.indexOf(string);  // linear scan of growing StringBuilder
```

For `n` strings of average length `m`, this is O(n²·m). A `HashSet` for deduplication + `StringBuilder` at the end would give O(n·m) amortized.

### 2.3 `IntMap.BasicIntMapFactory` — Heap pollution (`IntMap.java:85`)

```java
return new BasicIntMap<>((T[]) new ArrayList<>(new HashSet<>(values)).toArray());
```

`Collection.toArray()` returns `Object[]`, not `T[]`. The cast is unsafe and will cause heap pollution. Should use `toArray(T[])` with a properly sized array argument.

### 2.4 `ChainedMap.rows()` — Full materialization on every call (`ChainedMap.java:55-67`)

```java
public Iterable<Row.R3<K2, K1, V>> rows() {
    List<R3<K2, K1, V>> result = new ArrayList<>();
    for (Entry<Object, Object> entry0 : super.mapBase.entrySet()) {
        for (Entry<Object, Object> entry1 : ...) {
            result.add(...);
        }
    }
    return result;
}
```

Builds an entire `ArrayList` of all rows on every invocation. For large maps this generates significant garbage. Should provide a lazy/streaming alternative.

### 2.5 `SimpleFactory.handleMake()` — No caching when USE_COMBINEDCACHE=false (`SimpleFactory.java:566-582`)

When `USE_COMBINEDCACHE` is `false` and the file is unresolved (`resolved=false`), every call creates a new frozen `CLDRFile`. The result is never stored for reuse. For a frequently accessed locale like `"en"`, this means repeated XML parsing and object creation.

### 2.6 SimpleFactory — Synchronizes on ConcurrentMap (`SimpleFactory.java:610`)

```java
synchronized (mapToSynchronizeOn) {    // mapToSynchronizeOn = combinedCache.asMap()
```

`combinedCache` is a Guava `Cache` whose `asMap()` returns a `ConcurrentMap`. Guava already handles internal concurrency. Synchronizing externally defeats the lock striping and adds unnecessary contention.

### 2.7 `XPathParts` static cache — Mutable objects shared (`XPathParts.java:54`)

```java
private static final Map<String, XPathParts> cache = new ConcurrentHashMap<>();
```

Stores mutable `XPathParts` objects. If one caller retrieves an instance and calls `clear()` or adds/removes elements, all future users of that xpath path will see corrupted data. Only frozen copies should be cached, or defensive copies returned.

### 2.8 `Counter.getKeysetSortedByCount()` — Double allocation (`Counter.java:205-216`)

```java
Set<Entry<T>> count_key = new TreeSet<>(...);
// ... populate TreeSet ...
Set<T> result = new LinkedHashSet<>();
// ... copy to LinkedHashSet ...
```

The intermediate `TreeSet` is unnecessary. Sorting a list directly and populating a `LinkedHashSet` would halve the allocation.

---

## 3. Threading & Concurrency Concerns

### 3.1 `VoteResolver` — Documented non-thread-safe (`VoteResolver.java:35`)

```java
// ... It isn't thread-safe, so either have a separate one per thread (they
// are small), or synchronize.
```

Instance fields like `transcript`, `pathHeader`, `valueIsLocked` are mutable. If multiple threads use the same `VoteResolver` instance despite the warning, state corruption is guaranteed.

### 3.2 `Counter.RWLong.uniqueCount` — Non-volatile static (`Counter.java:47`)

```java
static int uniqueCount;
// ...
{   // instance initializer
    synchronized (RWLong.class) {
        forceUnique = uniqueCount++;
    }
}
```

`uniqueCount` is written under a lock but read in `compareTo()` without synchronization or `volatile`. Other threads may see stale values. Should be `private static final AtomicInteger uniqueCount = new AtomicInteger();`.

### 3.3 `CLDRFile.HACK_ORDER` — Public static mutable flag (`CLDRFile.java:113`)

```java
public static boolean HACK_ORDER = false;
```

Any thread can flip this at any time, causing global non-deterministic behavior. Should be `private volatile` with a controlled setter.

### 3.4 XMLSource — Caches cleared without synchronization (`XMLSource.java`)

Multiple cache fields (`aliasCache`, `reverseAliasCache`, `getFullPathAtDPathCache`) are backed by unsynchronized `HashMap`/`TreeMap` and cleared via `clearCache()`. If accessed concurrently, this can produce infinite loops (HashMap resize race) or stale reads.

### 3.5 `Log.java` — No thread-safe initialization (`Log.java:51-67`)

Methods like `setLog(String, String)` directly assign the static `log` field without any guard. Two threads calling `setLog` + `logln` concurrently can see null or stale references.

---

## 4. Code Quality & Migrations

| Severity | File | Line | Issue |
|----------|------|------|-------|
| Medium | `SimpleFactory.java` | 488-489 | Uses deprecated `LruMap` (wrapped in `synchronizedMap`) instead of Guava Cache. The class itself is `@Deprecated`. |
| Low | `Counter2.java` | 62 | `addN()` is a trivial wrapper with `// TODO Auto-generated method stub` |
| Low | `ChainedMap.java` | throughout | Heavy use of raw `Map` types and unchecked casts — defeats generic type safety |
| Low | `DtdData.java` | 56 | `nameToElement` is a `HashMap` without synchronization, but `DtdData` is meant to be immutable after construction |
| Low | `Counter.java` | throughout | All methods return `Counter<T>` for chaining but most callers don't use the return value |
| Info | 20+ files | — | Debug logging via `System.out.println` rather than a proper logging framework |
| Info | `LruMap.java` | 10-11 | Javadoc says to migrate to Guava Cache — no migration has occurred |

### 4.1 `LruMap` usage should be migrated

`LruMap` is `@Deprecated` with a note to use Guava's `Cache`/`CacheBuilder`. It is still instantiated in:

```
SimpleFactory.java:488-489
```

These should be replaced with `CacheBuilder` (which is already used elsewhere in the class for other caches).

### 4.2 Raw type usage in `ChainedMap`

```java
private final Map<Object, Object> mapBase;
private final Constructor<Map<Object, Object>>[] mapConstructors;
```

All public methods accept and return `Object` with unchecked casts. The class would benefit from a cleaner generic design, possibly replaced with Guava's `Table` or nested `Map` structures.

---

## 5. Top Recommendations

### Fix immediately (silent data corruption)

1. **Fix `Counter.compareTo` and `Counter2.compareTo`** — swap `i.next()` → `j.next()` on the duplicate iterator read.
2. **Fix `Counter.equals`** — delegate to `((Counter<?>) o).map` instead of comparing `Map` to `Counter`.

### Fix soon (incorrect behavior)

3. **Replace `LockSupportMap` with `ConcurrentHashMap.computeIfAbsent`** — eliminates allocation overhead and fixes the remove-while-synchronized bug.
4. **Add synchronization to `Log`** — or replace with `java.util.logging` / SLF4J.
5. **Migrate off `LruMap`** — instantiate Guava `Cache` directly, as already done in other parts of the project.

### Performance improvements

6. **Fix `Counter.getTotal()` / `Counter2.getTotal()`** — iterate `entrySet()` or `values()` instead of `keySet()` + `get()`.
7. **Fix `IntMap.CompactStringIntMapFactory` deduplication** — use `HashSet` instead of `StringBuilder.indexOf`.
8. **Cache frozen `XPathParts`** — prevent mutation of shared cached instances.
9. **Add `volatile` to `Counter.RWLong.uniqueCount`** or use `AtomicInteger`.

### Architectural

10. **Replace `Counter`/`Counter2` with Guava `AtomicLongMap` or `Multiset`** — the existing implementations have bugs and performance issues that well-tested standard library replacements would solve cleanly.

---

*Generated by automated code analysis.*
