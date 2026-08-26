# CLDR Survey Tool Frontend — JavaScript/Vue.js Bug Analysis

**Analysis date:** 2026-07-09  
**Scope:** `tools/cldr-apps/js/` — Vue 3.2.x, Ant Design Vue, webpack 5, ESM modules

---

## Definite Bugs

### 1. ReferenceError: `specialPage` undeclared in `errBox` (Runtime Crash)

- **File:** `src/esm/cldrVue.mjs:148-158`
- **Severity:** HIGH — crashes when triggered
- **Description:** `errBox(e, operation)` references the bare identifier `specialPage` which is neither a parameter nor defined in its lexical scope:

```js
function errBox(e, operation) {
  console.error(`There was a problem ${operation} “${specialPage}”`);  // ReferenceError
  notification.error({
    message: `Problem ${operation} “${specialPage}”`,                  // ReferenceError
    ...
  });
}
```

- **Effect:** If `show()` catches an error (`cldrVue.mjs:135-138`), calling `errBox` will throw a `ReferenceError` before the notification displays. The error is swallowed by the outer try/catch-less caller, leaving the user uninformed.

### 2. `removeClass` Only Works When Class Is First in the List

- **File:** `src/esm/cldrDom.mjs:13-21`
- **Severity:** HIGH — visual/functional corruption
- **Description:** When removing a class that is not the first in `className`, the logic truncates incorrectly:

```js
function removeClass(obj, className) {
  if (obj.className.indexOf(className) > -1) {
    obj.className = obj.className.substring(className.length + 1);  // BUG
  }
}
```

For `obj.className = "d-item pu-select"`, removing `"pu-select"`:
- `indexOf("pu-select")` returns 7  
- `substring("pu-select".length + 1)` = `substring(10)` = `"ct"` (trailing chars, not the remaining class)

Correct implementation: replace only the matched occurrence.

- **Callers at risk:** `cldrTable.mjs:1331/1342/1356/1362/1382` — row selection highlighting via `"pu-select"` and `"selectShow"` classes.

### 3. `!obj.nodeName != "TD"` Always True (Inverted Logic)

- **File:** `src/esm/cldrTable.mjs:1351`
- **Severity:** MEDIUM — prevents correct TD targeting
- **Description:**

```js
if (!obj.nodeName != "TD") {
```

Due to operator precedence: `(!obj.nodeName)` evaluates to `false` (non-empty string is truthy), then `false != "TD"` is always `true`. The condition always enters the block, even when `obj.nodeName === "TD"`.

- **Fix:** Replace with `obj.nodeName !== "TD"`.

### 4. Operator Precedence: `!theRow.confirmStatus == "missing"` Always False

- **File:** `src/esm/cldrTable.mjs:628`
- **Severity:** MEDIUM — error detection check never fires
- **Description:**

```js
!theRow.confirmStatus == "missing"
```

Evaluated as `(!theRow.confirmStatus) == "missing"` → `false == "missing"` → `false`, always. This means a missing winningVhash with a confirmStatus other than "missing" silently passes without console error.

- **Fix:** `theRow.confirmStatus !== "missing"`

### 5. `retry()` Called as Global But Is Module-Private

- **File:** `src/esm/cldrRetry.mjs:67`
- **Severity:** MEDIUM — "Reload" button on disconnect page does not work
- **Description:** The `getHtml()` function generates HTML with `onClick='retry()'`. The `retry` function is defined in the module scope and is not exported to `window`. After this HTML is set via `innerHTML`, clicking the button will throw a `ReferenceError: retry is not defined`.

### 6. `mount()` Creates Fragment, Mounts, Then Replaces Parent (Fragile)

- **File:** `src/esm/cldrVue.mjs:40-47`
- **Severity:** MEDIUM — potentially unstable DOM mounting
- **Description:**

```js
function mount(component, el, extraProps) {
  const fragment = document.createDocumentFragment();
  const app = create(component, null, extraProps).mount(fragment);
  const childEl = document.createElement("div");
  el.appendChild(childEl);
  childEl.replaceWith(fragment);  // fragment includes the app's root element
  return app;
}
```

Mounting to a detached fragment, then appending a dummy child and replacing it with the fragment, is fragile. If Vue's reactivity triggers DOM reads before the `replaceWith`, internal references may be stale. Additionally, the fragment is empty after `replaceWith` (its children are moved), so if `mount` is called again on the same `el`, the old fragment has no effect.

### 7. `for...in` on Array in `insertRowsIntoTbody`

- **File:** `src/esm/cldrTable.mjs:105,176`
- **Severity:** LOW-MEDIUM — potential unexpected iteration
- **Description:**

```js
for (let c in rowChildren) {   // rowChildren is an Array
```

`for...in` iterates enumerable properties including inherited ones. While uncommon in practice, if `Array.prototype` is extended, this will pick up those properties. Should use `for...of` or `forEach`.

Also at line 176 in the same function: `for (let i in rowList)` where `rowList` is an array.

Also at `cldrSurvey.mjs:737`: `getTagChildren` uses `for...in` on a `NodeList`; same issue.

---

## Threading / Async Issues

### 8. Race Between Row Update Check and Async Vote Response

- **File:** `src/esm/cldrTable.mjs:245`
- **Severity:** MEDIUM — voted row data may be overwritten by stale response
- **Description:**

```js
if (tr.className !== "tr_checking1" && tr.className !== "tr_checking2") {
  updateRow(tr, theRow);
}
```

If an async vote response comes back between the className check on line 245 and the `updateRow` call on line 246, changing the className back to a non-checking value, a stale multiple-row response could overwrite the just-refreshed single-row data. The response ordering is not guaranteed.

### 9. `setInterval` Leak on Re-entrant `reloadV`

- **File:** `src/esm/cldrLoad.mjs:491,509-519`
- **Severity:** LOW-MEDIUM — accumulating timers
- **Description:** The spinner interval `timerToKill` is captured per-call, but if `reloadV()` is called again (e.g., rapid hash changes) before the spinner is cleared (the `untilFlipped` callback fires), the old interval keeps running. The closure replaces the local reference, but the old interval ID is lost.

---

## Memory / DOM Issues

### 10. Live HTMLCollection Mutation During `removeClassFromAll` Iteration

- **File:** `src/esm/cldrDom.mjs:27-34`
- **Severity:** LOW — elements may be skipped
- **Description:**

```js
function removeClassFromAll(className) {
  for (let obj of document.getElementsByClassName(className)) {
    removeClass(obj, className);
  }
}
```

`getElementsByClassName` returns a **live** `HTMLCollection`. As each element's className changes, the collection updates, potentially causing elements to be skipped. If multiple elements share the same class and removing it from one affects another's membership (it doesn't in this case, but the live mutation is still a concern for correctness), iteration order may be unpredictable.

### 11. `_stlisteners` Not Cleaned Up on DOM Removal

- **File:** `src/esm/cldrDom.mjs:170-207`
- **Severity:** LOW — potential detached DOM retention
- **Description:** The `_stlisteners` property is set on DOM nodes to track event listeners. When nodes are removed from the DOM (e.g., by `Flipper.flipTo` clearing a section), the `_stlisteners` object keeps references to the handler functions and the nodes themselves, preventing garbage collection until the module-level references are cleared.

---

## XSS / Security Issues

### 12. `innerHTML` with Unsanitized Partition Names

- **File:** `src/esm/cldrTable.mjs:287-291`
- **Severity:** LOW-MEDIUM (depends on data source)
- **Description:**

```js
newHeading[0].innerHTML = newPartition.name;
```

Partition names come from server data. If a partition name contains HTML, it will be interpreted. While partition names are normally from controlled CLDR data, this bypasses text node safety.

### 13. `updateIf` Uses `innerHTML` for Plain Text

- **File:** `src/esm/cldrDom.mjs:144-154`
- **Severity:** LOW — the comment `TODO shold only use for plain text` acknowledges this
- **Description:** `updateIf` sets `innerHTML` even when the content `txt` is plain text. This is an XSS vector if `txt` contains user-influenced data. Should use `textContent`.

---

## Performance Issues

### 14. `JSON.stringify(theRow)` on Every `updateRow`

- **File:** `src/esm/cldrTable.mjs:447`
- **Severity:** MEDIUM for large pages (1000+ rows)
- **Description:**

```js
const rowChecksum = cldrChecksum(JSON.stringify(theRow));
```

Every row update (including initial load and refresh) serializes the full row object to JSON even if the row hasn't changed. The checksum optimization at line 448 mitigates repeated updates, but the initial load serializes every row unnecessarily. The checksum could be computed incrementally or the server could provide one.

### 15. `Object.keys(...).length` in `tablesAreCompatible` (Hot Path)

- **File:** `src/esm/cldrTable.mjs:150`
- **Severity:** LOW
- **Description:** Called on every `insertRows`, creates two temporary arrays from `Object.keys()` to compare row counts. Fine for typical pages (<500 rows), but allocation-heavy for large pages.

---

## Code Quality & Maintainability

### 16. Deprecated `substr` Usage

- **File:** `src/esm/cldrLoad.mjs:189`
- **Severity:** LOW
- **Description:** `id.substr(0, 2)` — `String.prototype.substr` is deprecated. Should use `id.slice(0, 2)` or `id.substring(0, 2)`.

### 17. Unused Import / Dead Code

- **File:** `src/esm/cldrSurvey.mjs:34` — `let loadOnOk = null` with TODO referencing server-side scripts
- **File:** `src/esm/cldrSurvey.mjs:36` — `let clickContinue = null` — same pattern
- **File:** `src/esm/cldrSurvey.mjs:44` — `let specialHeader = null` — noted as "supposed to be same as specialHeader in cldrStatus.mjs, or not?"

### 18. Minimal Test Coverage

Many test files exist but verify only basic imports or trivial scenarios:

| Test file | Actual assertions |
|-----------|-----------------|
| `TestCldrTable.mjs` | 3 `it()` blocks, only tests `makeHeaderId`/`isHeaderId` |
| `TestCldrLoad.mjs` | 1 `it()` block, tests `setTheLocaleMap` → `localeMapReady()` |
| `TestCldrStatus.mjs` | 7 `it()` blocks, tests `getRunningStamp`, `runningStampChanged`, `getCurrentId`/`setCurrentId` |
| `TestCldrGui.mjs` | 4 `it()` blocks, tests HTML validity |

Untested in any meaningful way: `cldrVue.mjs`, `cldrVote.mjs`, `cldrTable.mjs` (updateRow, insertRows, listen, etc.), all `.vue` components, `cldrRetry.mjs` (the `retry()` global bug would be caught by a test), `cldrDom.mjs` (the `removeClass` bug would be caught by a test).

### 19. `handleVoteSubmitted` Captures `json` from Outer Scope

- **File:** `src/esm/cldrVote.mjs:212-214`
- **Severity:** LOW
- **Description:** The anonymous callback for `refreshSingleRow` references `json` from the outer `handleVoteSubmitted` scope. By the time the callback fires (after network response), `json` could potentially be stale if `handleVoteOk` was called multiple times. The `json` variable shadows the outer `json` correctly in the catch clause but could be confusing.

### 20. `findPartition` Linear Scan on Every Row

- **File:** `src/esm/cldrTable.mjs:260-271`
- **Severity:** LOW
- **Description:** For each row (potentially 1000+), `findPartition` scans the partition list linearly from the beginning if `curPartition` doesn't match. If partitions are large, this is O(rows × partitions). Using binary search or an index map would be O(rows log partitions) or O(rows).

---

## Vue Component Bugs (Batch 2)

### 8. `nextTick(fn())` Calls Function Immediately Instead of Passing Callback

- **File:** `src/views/AddValue.vue:338`
- **Severity:** MEDIUM — cursor position not restored
- **Description:** `nextTick(focusInputAndSetRange(insertionPoint))` invokes `focusInputAndSetRange` immediately and passes its return value (`undefined`) to `nextTick`. The cursor is never positioned at the insertion point after a character is inserted:

```js
nextTick(focusInputAndSetRange(insertionPoint));  // BUG: should be nextTick(() => focusInputAndSetRange(insertionPoint))
```

### 9. `FlaggedItems.vue`: `let = null` Reassigned to `ref()` — Non-Reactive Bindings

- **File:** `src/views/FlaggedItems.vue:14-16,60-62`
- **Severity:** MEDIUM — template never updates with table data
- **Description:** `tableBody`, `tableHeader`, and `tableComments` are declared as `let tableBody = null` (plain variables), then later reassigned to `ref(data.tableHeader)` in `setData()`. Vue's reactivity system cannot track plain `let` variable reassignments; the template bindings to `tableBody`, `tableHeader`, `tableComments` will always see `null`:

```js
// Line 13-15: declared as plain variables
let tableBody = null;
let tableHeader = null;
let tableComments = null;

// Line 60-62: reassigned to refs — too late, template already captured null
tableHeader = ref(data.tableHeader);
tableBody = ref(data.tableBody);
tableComments = ref(data.tableComments);
```

**Fix:** Declare as `const tableBody = ref(null)` from the start and assign via `.value`.

### 10. `FlaggedItems.vue:33` — `hasPermission` Ref Object Always Truthy

- **File:** `src/views/FlaggedItems.vue:33`
- **Severity:** MEDIUM — permission guard never works
- **Description:** `if (hasPermission)` checks the `Ref` wrapper object itself, which is always truthy. Should be `if (hasPermission.value)`:

```js
if (hasPermission) {  // BUG: always true, should be hasPermission.value
```

### 11. `LookUp.vue:50` — String Minus Operator Produces `NaN`

- **File:** `src/views/LookUp.vue:50`
- **Severity:** LOW — cosmetic bug in search results
- **Description:** `{{ x.loc - x.name }}` uses the `-` operator between two strings. JavaScript coerces both to numbers (producing `NaN`) and renders the text "NaN". Should use separate interpolations:

```html
{{ x.loc - x.name }}  <!-- BUG: produces "NaN" -->
<!-- Fix: {{ x.loc }} - {{ x.name }} -->
```

### 12. `WaitingPanel.vue:120` — Error Handler Is a No-Op

- **File:** `src/views/WaitingPanel.vue:120`
- **Severity:** LOW — fetch errors silently ignored
- **Description:** The rejection handler `(err) => this.attemptedLoadErr` merely **accesses** the property without assigning to it. Should be `(err) => (this.attemptedLoadErr = err)`:

```js
fetch("survey").then(
  () => this.attemptedLoadCount++,
  (err) => this.attemptedLoadErr  // BUG: no-op, missing assignment
);
```

### 13. `VettingSummary.vue:238` — `sort().reverse()` Mutates Reactive Array

- **File:** `src/views/VettingSummary.vue:238`
- **Severity:** LOW — relies on string sort of ISO dates, fragile
- **Description:** `this.snapshotArray = snapshots.array.sort().reverse()` mutates the reactive array in-place with a string sort. Works for ISO 8601 date strings but will silently produce wrong order for any other format:

```js
this.snapshotArray = snapshots.array.sort().reverse();
```

### 14. `DashboardWidget.vue:263-274` — `this.console.warn()` via Computed Property

- **File:** `src/views/DashboardWidget.vue:263-274,510`
- **Severity:** LOW — bizarre pattern, but functional
- **Description:** `this.console.warn("...")` works only because of `computed: { console: () => console }` at line 510, which exposes the global `console` object as a computed property. Calls like `this.console.warn()` are valid but non-standard and fragile.

### 15. `DashboardWidget.vue:393` — Deprecated `substr`

- **File:** `src/views/DashboardWidget.vue:393`
- **Severity:** LOW
- **Description:** `category.substr(0, 1)` uses the deprecated `String.prototype.substr`. Should use `category.charAt(0)` or `category.slice(0, 1)`.

### 16. `UploadPanel.vue:144,149` — `ref()` Inside Options API `data()`

- **File:** `src/views/UploadPanel.vue:144,149`
- **Severity:** LOW — works but non-standard and confusing
- **Description:** `xlsFileList: ref([])` and `xlsHeaders: ref([])` appear inside `data()` returning a plain object. Vue 3's Options API merges refs inside `reactive()` by auto-unwrapping, so it works — but mixing Composition API refs inside Options API `data()` is non-standard. These should be plain arrays: `xlsFileList: [], xlsHeaders: []`.

### 17. `DashboardWidget.vue:165` — `v-html` XSS Risk

- **File:** `src/views/DashboardWidget.vue:165`
- **Severity:** MEDIUM — untrusted server data rendered as HTML
- **Description:** `v-html="item.comment"` renders arbitrary comment HTML from server data. If `item.comment` contains `<script>` or event handlers, XSS is possible.

### 18. `InfoSelectedItem.vue:22,26-30` — `v-html` XSS Risk

- **File:** `src/views/InfoSelectedItem.vue:22,26-30`
- **Severity:** MEDIUM — untrusted data rendered as HTML
- **Description:** Both `v-html="testHtml"` and `v-html="exampleHtml"` render raw HTML from server-supplied strings. If either can contain user-contributed content, XSS is possible.

### 19. `OverallErrors.vue:36` — Top-Level Async Promise Without Error Boundary

- **File:** `src/views/OverallErrors.vue:36`
- **Severity:** LOW — error handler attached but runs on module import
- **Description:** `loadData()` is called at module top level. If `loadData` rejects synchronously before returning a promise, the `.catch()` handler never fires and the error propagates uncaught as an unhandled promise rejection.

---

## Summary

| Category | Count | Key Items |
|----------|-------|-----------|
| **Definite Bugs** | 11 | `specialPage` ReferenceError, `removeClass` logic, inverted `!nodeName` check, operator precedence, global `retry()` call, fragile `mount()`, `for...in` on arrays, `nextTick(fn())`, `FlaggedItems` ref misuse, `hasPermission` Ref truthiness, `WaitingPanel` no-op handler |
| **Async/Race Conditions** | 2 | Row update race, `setInterval` leak |
| **XSS/Security** | 4 | `innerHTML` in partition names and `updateIf`, `DashboardWidget`/`InfoSelectedItem` `v-html` |
| **Performance** | 3 | `JSON.stringify` per row, `Object.keys` hot path, partition linear scan |
| **Code Quality** | 5 | Deprecated `substr` (2 occurrences), minimal test coverage, dead code annotations, `UploadPanel` `ref()` in `data()`, `this.console` pattern |
| **Memory/DOM** | 2 | Live HTMLCollection mutation, listener cleanup |
| **Cosmetic/UI** | 2 | `LookUp` NaN rendering, `VettingSummary` fragile sort |
