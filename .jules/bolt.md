# Bolt's Performance Journal

## 2026-03-31 - Direct Reference Tracking for List Partitioning
**Learning:** Calling `List.lastOrNull()` and `List.last()` repeatedly inside a loop when grouping sequential list elements by property introduces redundant index/size checks and method overhead. Tracking active references (`currentSegment` and `currentSegmento`) avoids `lastOrNull()`/`last()` lookups and eliminates unnecessary intermediate list copies or reallocations.
**Action:** When grouping or chunking collections sequentially by state or key, maintain explicit local pointer references instead of querying collection boundary methods in every iteration.
