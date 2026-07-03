# Algorithms — Implementation Notes

Idea, complexity, and notes for each algorithm.

## Contents
1. [Unique_substrings](#unique_substrings)
2. [Longest_repeated_substring](#longest_repeated_substring)
3. [Longest_common_substring](#longest_common_substring)
4. [Recursion](#recursion)
5. [Recursive multiplication](#recursive_multiplication)
6. [Recursive list sum](#recursive_list_sum)
7. [Recursive string reversal](#recursive_string_reversal)
8. [Recursive max 2D](#recursive_max_2d)
9. [Divide and conquer](#divide-and-conquer)
10. [Merge sort](#merge_sort)

---

## Unique_substrings

Count the distinct non-empty substrings of a string.

### Idea
Every substring is a prefix of exactly one suffix. Across all suffixes there are `n(n+1)/2` prefixes — every substring counted *with* duplicates. Adjacent suffixes in sorted order share `lcp[i]` leading characters, and those shared prefixes are exactly the double-counts, so subtract them: `distinct = n(n+1)/2 - sum(lcp)`. The naive alternative (enumerate every substring into a hash set) is O(n³) time and O(n²) memory; this identity replaces all of it with one sum.

### Complexity
O(n log n) to build the suffix array, O(n) for Kasai's LCP, O(n) to sum — dominated by the build.

### Notes
Use a `long` for the count and the sum: `n(n+1)/2` overflows `int` well before n gets large.

---

## Longest_repeated_substring

Find the longest substring that occurs at least twice.

### Idea
A repeated substring is a common prefix of two *distinct* suffixes, so the longest one has length `max(lcp)`. Recover it as that many leading characters of the suffix at the rank where the maximum sits.

### Complexity
O(n log n), dominated by the suffix-array build; the scan for the maximum LCP is O(n).

### Notes
If `max(lcp) == 0` nothing repeats → return empty. Ties (several substrings of the same maximal length) — any is acceptable; return the first found.

---

## Longest_common_substring

Find the longest substring appearing in at least k of n input strings (2 ≤ k ≤ n).

### Idea
Concatenate the strings with a unique sentinel between each — a character lexicographically smaller than any real one, and distinct per boundary so no suffix straddles two source strings. Build the suffix array + LCP over the combined string, then color each suffix by which source string it came from (its start position relative to the boundaries; the boundary marker doubles as the color id) and skip suffixes that begin with a sentinel. Slide a window over the sorted ranks: the substring shared by every suffix in a window has length equal to the minimum LCP strictly inside that window. Keep a hashtable `color -> count`; grow the window until it covers ≥ k distinct colors, then shrink to stay minimal, tracking the window-min LCP as the candidate length. Keep the longest candidate. The smallest useful window is size k.

### Complexity
O(N log N) for the suffix array over the total combined length N; the windowed scan is O(N) with a monotonic deque (or sparse table) supplying the window-minimum LCP.

### Notes
Sentinels must be smaller than every real character *and* mutually distinct, or suffixes from different strings could compare as equal across a boundary. k = n is the all-strings case; k = 2 is "shared by any pair." If no window ever reaches k colors, there is no common substring → return empty.

---

## Recursion

A function that calls itself, used when a problem can be broken into smaller subproblems of the same shape. Each call solves a smaller instance until the problem is small enough to answer directly.

### Idea
Every recursive function has three pieces:

- **Base case** — the terminating condition that stops the recursion. It can be implicit (e.g. falling through when the input is empty). Without a reachable base case you get infinite recursion → stack overflow.
- **Recursive call** — the function calls itself on a **strictly smaller** subproblem, moving closer to the base case. This shrink is the crucial part: if the subproblem doesn't get smaller each time, the recursion never terminates.
- **Body / work** — the actual computation at each level: whatever this step contributes, combined with the result(s) of the recursive call(s).

### Complexity
Time = (number of recursive calls) × (work done per call); a recurrence like `T(n) = a·T(n/b) + f(n)` captures it, and the Master Theorem solves the common divide-and-conquer shapes. Space is O(maximum recursion depth) for the call stack — every pending call holds a frame until it returns, so space can be significant even when the total work is small. Deep or unbounded recursion risks stack overflow; a tail-recursive or iterative rewrite (or an explicit stack) can reduce the stack cost.

### Applications
Divide and conquer (merge sort, quicksort, binary search), tree and graph traversal (DFS), backtracking, memoized recursion / dynamic programming, generating permutations and combinations, and problems defined recursively by nature (factorial, Fibonacci, GCD).

---

## Recursive_multiplication

Multiply two integers using recursion, without the `*` operator.

### Idea
Multiplication is repeated addition: `a * b` is `a` added `b` times. The straightforward recursion peels off one `b` at a time — `multiply(a, b) = a + multiply(a, b - 1)`, with base case `multiply(a, 0) = 0`. Each call shrinks `b` toward 0.

Two refinements matter:
- **Sign.** The `b - 1` shrink only heads toward 0 for positive `b`. Handle negatives by recursing on `|b|` and applying the sign at the top (or recurse on the smaller operand so `4 * 1000000` doesn't go 1,000,000 deep).
- **Depth / the doubling version.** The linear form recurses `|b|` times, so large `b` overflows the call stack. A logarithmic version uses doubling: `multiply(a, b) = 2 * multiply(a, b / 2) + (b odd ? a : 0)`, base `b == 0 -> 0`. Depth drops to `log2(b)`.

### Complexity
Linear (repeated addition): O(b) time and O(b) stack depth. Doubling: O(log b) time and depth. Return `long` — `int * int` overflows `int`.

### Notes
The stack-depth trap is the real lesson: recurse on `min(|a|, |b|)`, or use the doubling form, before trusting it on big inputs. Java has no tail-call optimization, so even a tail-recursive linear version still uses O(b) frames.

---

## Recursive_list_sum

Iterate an array with recursion instead of a loop — e.g. sum all elements, or sum only the odd-valued ones.

### Idea
Process one element, then recurse on the rest: `f(arr, i) = g(arr[i]) + f(arr, i + 1)`, with base case `i == arr.length -> 0` (an empty tail contributes nothing). The index `i` is how the problem shrinks — each call moves one step toward the end. A public method with no index wraps a private helper that carries `i`, starting at 0.

The only thing that changes between "sum all" and "sum odds" is `g`: for the total, `g(x) = x`; for odds, `g(x) = (x is odd ? x : 0)`. Same traversal skeleton, different per-element contribution.

### Complexity
O(n) time and O(n) stack depth — one frame per element until the base case returns and the additions unwind. A loop or an accumulator-passing (tail) form avoids the recursion, though Java still keeps O(n) frames without tail-call optimization.

### Notes
"Odd" as `x % 2 != 0` is correct for negatives (`-3 % 2 == -1`). Because depth equals the array length, very large arrays overflow the stack — this pattern is for learning the recursion shape, not for production traversal. Two standard shapes: **head recursion** (do the work on `arr[i]`, then recurse) and **tail recursion** (pass a running accumulator down and return it at the base case).

--- 

## Recursive_string_reversal

Reverse a string using recursion.

### Idea
Peel one character off, reverse what's left, and put the peeled character on the far end. Two symmetric ways to write it:
- Take the **first** character and append it after the reversed rest: `reverse(s) = reverse(s[1:]) + s[0]`.
- Take the **last** character and prepend it before the reversed front: `reverse(s) = s[last] + reverse(s[:last])`.

Base case: a string of length 0 or 1 is its own reverse, so return it unchanged. Each call shrinks the string by one character, heading toward the base case.

### Complexity
Depth is O(n) — one frame per character. The naive `substring` + `+` version is **O(n²) time and memory**, because Java strings are immutable: every `substring` and every `+` copies a fresh string, and you do that at each of the n levels. To make it O(n), recurse over a `char[]` with two indices (`lo`, `hi`): swap the ends and recurse inward (`reverse(arr, lo+1, hi-1)`), base case `lo >= hi`. That does O(1) work per level and no copying.

### Notes
`substring(1)` is the readable version and fine for learning, but call out the quadratic cost — it's the same immutability trap that makes string concatenation in a loop slow. The two-pointer `char[]` form is the one to reach for if reversal size matters. Double-reversing any string returns the original, which is a cheap invariant to test against.

---

## Recursive_max_2d

Find the maximum value in a 2D array along with its `(row, col)` position, recursively. Same "bundle multiple returns into a result object" idea as the 1D version, but now the traversal spans two dimensions.

### Idea
Two clean ways to recurse over a grid:

- **Flatten to one index.** Treat the `R × C` grid as a single sequence of `R*C` cells. Recurse over a linear index `k` from `0` to `R*C - 1`; decode it with `row = k / C`, `col = k % C`. This collapses the problem back to the 1D max-with-index recursion — base case is the last cell, and you compare `grid[row][col]` against the best of `k+1 ...`. Requires a rectangular grid (uses a single `C`).

- **Two-level recursion.** A column recursion finds the max (and first col) within one row; a row recursion walks the rows, computing each row's max via the column recursion and combining. This is "recursion calling recursion" and naturally handles jagged rows, since each row is measured on its own length.

Either way, resolve ties to **row-major first**: scanning cells in row-major order and using `>=` (keep the current cell when it merely equals the best-so-far) makes the earliest `(row, col)` win — lower row first, then lower column.

### Complexity
O(R·C) time — every cell is visited once. Stack depth is O(R·C) for the flattened form (one frame per cell), or O(R + C) for the two-level form (a row-recursion frame plus a column-recursion frame at a time). One small `Result` allocation per level.

### Notes
The flattened-index trick is the transferable idea here: a `k → (k / cols, k % cols)` decode turns any 2D (or higher-D) traversal into a 1D recursion, as long as the grid is rectangular. For jagged arrays, prefer the two-level form. As with the 1D case, **fix the tie-break policy explicitly** (first vs last, and in which scan order) — row-major-first is the natural default and is what a `>`-based brute-force check over nested loops produces.

---

## Divide and conquer

A problem-solving technique: break a problem into smaller, independent subproblems of the *same type*, solve each (usually recursively), then combine their answers into the final result. It's recursion with a specific shape — split into multiple subproblems and merge.

### Idea
Three steps:
- **Divide** — split the input into subproblems. Often two halves, but it can be any number of parts (a *k-way* split into thirds, quarters, etc.).
- **Conquer** — solve each subproblem recursively; a base case handles inputs small enough to answer directly.
- **Combine** — merge the sub-results into the answer for the whole.

The combine step is what distinguishes divide-and-conquer from plain single-branch recursion: there are several subproblems and their results have to be reconciled (the merge in merge sort, the min-of-parts in a min search, the cross-boundary case in maximum-subarray).

### Complexity
Cost follows a recurrence `T(n) = a·T(n/b) + f(n)`, where `a` = number of subproblems, `n/b` = subproblem size, and `f(n)` = divide + combine work. The **Master Theorem** solves the common shapes. Examples:
- Merge sort — `T(n) = 2T(n/2) + O(n)` → O(n log n).
- Binary search — `T(n) = T(n/2) + O(1)` → O(log n).
- Three-way min — `T(n) = 3T(n/3) + O(1)` → O(n).

Note that splitting more ways doesn't beat O(n) when every element must be examined (as in min/max) — the split count changes the recursion tree shape, not the fact that all `n` leaves are visited. Divide-and-conquer wins big when the combine step lets you *discard* work (binary search) or when a clever merge beats the naive bound (Karatsuba, FFT).

### Applications
Merge sort, quicksort, binary search, maximum-subarray, closest pair of points, Karatsuba integer multiplication, Strassen matrix multiplication, and the fast Fourier transform.

---

## Merge_sort

Sort a 1D array with divide and conquer: split in half, sort each half recursively, then merge the two sorted halves.

### Idea
Maps directly onto divide / conquer / combine:
- **Divide** — split the array at the midpoint into a left and right half.
- **Conquer** — recursively merge-sort each half. Base case: a length-0 or length-1 array is already sorted, so return it (a *copy*, so the caller's array isn't aliased).
- **Combine** — **merge** two sorted halves into one sorted array with a two-pointer walk: compare the current front of each half, take the smaller, advance that pointer; when one half runs out, copy the rest of the other. This merge is the whole trick — it's what turns two sorted pieces into one in linear time.

The recursion tree has `log n` levels, and each level does O(n) total merging work across all its pieces — that's the shape of the cost.

### Complexity
`T(n) = 2·T(n/2) + O(n)` → **O(n log n)** time, in the worst, average, and best case (it always splits and always merges). Space is O(n) for the merge buffers — merge sort is not in-place in its natural array form. It is **stable**: equal elements keep their original relative order, as long as the merge takes from the left half when the two fronts are equal (`<=`, not `<`).

### Notes
- **Stability hinges on the merge tie rule.** On a tie, pull from the *left* half so earlier-originating equal elements stay first. (Not observable for bare `int`s, but it matters when sorting records by a key.)
- **Top-down vs bottom-up.** This recursive form is top-down. Bottom-up merge sort iterates, merging runs of size 1, then 2, 4, … — same O(n log n), no recursion stack.
- **In-place vs auxiliary.** Returning new arrays at each level is the clearest form but allocates. A common optimization is one shared temp buffer reused across merges (allocate once, not per call). True in-place merge is possible but intricate and usually not worth it.
- **Non-destructive contract here.** Base case returns a *copy* so mutating the result never touches the caller's input; verify the input array is unchanged after sorting.
- **Generic version.** Swap `int[]` for `T[]` with `T extends Comparable<T>` (or a `Comparator`) and compare via `compareTo` instead of `<` — the structure is identical.

---