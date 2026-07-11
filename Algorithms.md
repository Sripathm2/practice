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

## Dynamic programming

A technique for problems that break into **overlapping** subproblems: solve each distinct subproblem once, store its answer, and reuse it instead of recomputing. It applies when a problem has *optimal substructure* (the answer is built from answers to smaller instances) and *overlapping subproblems* (the same smaller instances recur many times). That overlap is what separates DP from divide-and-conquer — D&C subproblems are independent, so caching buys nothing; DP subproblems repeat, so caching is the whole point.

### Idea
Four things to pin down:
- **State** — the parameters that uniquely identify a subproblem (e.g. "index `i`, remaining capacity `w`").
- **Transition / recurrence** — how a state's answer is built from smaller states.
- **Base cases** — the smallest states, answered directly.
- **Evaluation order** — either **top-down** (write the natural recursion, add a memo table so each state computes once) or **bottom-up** (tabulation: fill a table in dependency order so every state's inputs are ready before it).

### Complexity
Roughly (number of distinct states) × (work per transition). Memory is the table size, but it can often shrink: if a state only depends on the previous row/day, keep just that (a **rolling array**) and drop the full table.

### Applications
Fibonacci, 0/1 knapsack, longest common subsequence, edit distance, coin change, matrix-chain multiplication, and DP-flavored graph algorithms (Bellman-Ford, Floyd-Warshall).

---

## Magical_cows

**Problem.** Farms hold cows, up to a capacity `C`. Every night a farm with `v` cows doubles to `2v`; if `2v <= C` it becomes one farm of `2v`, otherwise it splits into two farms of `v` cows each. Given the initial farms and a list of query days, report the total number of farms after each queried number of nights.

### Idea
Simulating individual farms explodes — farms can double every night, so after `D` nights you may have up to `N·2^D` of them. The unlock: **individual farms don't matter, only how many cows each holds**, and cow counts only ever range over `1..C`. So track a vector `cnt[v]` = number of farms currently holding `v` cows, and advance the whole vector one night at a time:

- for each `v` with `2v <= C`: those farms grow — `next[2v] += cnt[v]` (one farm each, now at `2v`).
- for each `v` with `2v > C`: those farms split — `next[v] += 2 * cnt[v]` (two farms each, still at `v`).

That's an O(C) step regardless of how many farms exist. Precompute the vector day by day up to the largest query day, recording the **total farm count** (the sum of `cnt`) after each day; then every query is a table lookup. The DP is over the *distribution of cow counts across days*, and the payoff is precomputing once to answer all queries, instead of re-simulating per query.

### Complexity
O(C · D_max) to precompute (D_max = largest query day), then O(1) per query once daily totals are stored. Space is O(C) with a rolling pair of vectors, plus O(D_max) for the recorded totals. Farm totals grow up to ~`N·2^D`; use `long` (fits comfortably for the usual constraints `D ≤ 50`, `N, C ≤ 1000` — about `10^18` < `long` max).

### Notes
- The two transition cases are the crux: `2v <= C` → one farm at `2v`; `2v > C` → **two** farms at `v` (the split doubles that bucket's count).
- Day 0 is just the initial farm count — make sure the loop handles a query of 0 nights.
- Record the running total after each night as you go; don't re-run the simulation for each query.
- Overflow is the lurking bug — sum into `long`, not `int`.


--- 

## Tiling

**Problem.** Count the number of ways to completely fill a 1×n strip of slots using tiles of length 1 and length 2.

### Idea
Look at how the **last slot** gets covered — there are exactly two disjoint, exhaustive possibilities:
- a size-1 tile sits in the last slot, leaving `n - 1` slots to fill → `ways(n - 1)` ways, or
- a size-2 tile covers the last two slots, leaving `n - 2` slots → `ways(n - 2)` ways.

Since those cases don't overlap and cover everything, `ways(n) = ways(n - 1) + ways(n - 2)`. Base cases: `ways(0) = 1` (the empty strip has exactly one tiling — place nothing) and `ways(1) = 1`. That's the Fibonacci recurrence; in fact `ways(n) = Fib(n + 1)`.

The subproblems overlap heavily — plain recursion recomputes `ways(n-2)`, `ways(n-3)`, … an exponential number of times. Memoize (top-down) or tabulate (bottom-up) to make it O(n). And since each value depends only on the previous two, you don't need a table at all — two rolling variables suffice.

### Complexity
O(n) time. O(1) space with two rolling variables (or O(n) if you keep the full table). Values grow like Fibonacci (≈ φⁿ), so they overflow `long` at around n = 91 — use `BigInteger` beyond that.

### Notes
- **`ways(0) = 1` is the load-bearing base case.** An empty strip has one tiling (do nothing); setting it to 0 makes every larger answer wrong. This is the same subtlety as the empty-product / empty-sum convention.
- **It's Fibonacci in disguise.** Recognizing that lets you reach for fast-doubling or matrix exponentiation to get O(log n) if the input is huge.
- **No array needed.** Keep only the last two results and roll them forward; the full DP table is wasted memory here.
- **Generalizes.** Tiles of sizes `{1, 2, ..., k}` give `ways(n) = ways(n-1) + ... + ways(n-k)`; a single tile size `m` restricts the recurrence to `ways(n-1) + ways(n-m)`. Same last-slot reasoning, more branches.


---

## Tiling_general

**Problem.** Count the ways to fill a 1×n strip using tiles of various lengths, where a given length may come in several colors. Tiles are given as two parallel arrays: `lengths[i]` is an available length and `colors[i]` is how many distinct colors that length comes in. A tiling is a left-to-right sequence of colored tiles that exactly fills n.

### Idea
Same last-piece reasoning as basic tiling, generalized. Look at the **last tile** placed. It has some length `L` and some color, and it covers the final `L` slots, leaving `n - L` slots before it. Summing over every way to choose that last tile:

```
ways(0) = 1
ways(m) = sum over i of  colors[i] * ways(m - lengths[i])   for lengths[i] <= m
```

Two dimensions of choice fall out naturally:
- **Different lengths** → separate terms in the sum, each reaching back a different distance (`m - lengths[i]`).
- **Different colors of the same length** → the `colors[i]` multiplier: each of the `colors[i]` colors is a distinct way to place that tile, so the subproblem's count is multiplied by it.

The basic 1-and-2 tiling is just the special case `lengths = {1, 2}`, `colors = {1, 1}`, which collapses to Fibonacci. A single length with `k` colors gives `k^n`. Three unit-color lengths `{1,2,3}` gives the Tribonacci numbers.

### Complexity
O(n · t) time, where `t` is the number of tile types — for each of the n subproblems you sum over all types. O(n) space for the DP table (or O(maxLength) with a rolling window, since `ways(m)` only reaches back `maxLength` slots).

### Notes
- **`ways(0) = 1`** is still the anchor — one way to tile nothing.
- **The color count is a multiplier, not a new term.** Adding a color of an existing length doesn't add a term to the sum; it scales that length's existing term. Two colors of length 1 turn `+ ways(m-1)` into `+ 2·ways(m-1)`.
- **Watch overflow.** With many colors the count grows fast (up to `k^n` for k colors of length 1); sum into `long`, and reach for `BigInteger` if the numbers can exceed it.
- **Skip out-of-range tiles.** Only include the term when `lengths[i] <= m`; a tile longer than the remaining strip can't be the last piece.
- Representing colors as counts (rather than listing each colored tile separately) keeps the sum compact — otherwise you'd duplicate the same length term `colors[i]` times, which is arithmetically identical but wasteful.

---

## Domino_tiling_3xn

**Problem.** Count the ways to completely tile a 3×n board with 1×2 dominoes (each domino covers two adjacent cells, laid horizontally or vertically).

### Idea
Two observations set it up:

- **Parity.** A 3×n board has `3n` cells and each domino covers 2, so a full tiling needs `3n` to be even — i.e. **n even**. Every odd n gives 0. (This is why the answer sequence is `1, 0, 3, 0, 11, 0, 41, …` — zeros on the odds.)

- **One state isn't enough.** Filling column by column, a column boundary isn't always flush: a horizontal domino can stick out into the next column, leaving a "bump." So you need a second quantity alongside "fully tiled up to here." Track two states:
  - `f(n)` = ways to tile a full 3×n block (flush right edge),
  - `g(n)` = ways to tile 3×n with exactly one cell protruding into column n+1 (a bump).

  Coupled recurrence:
  ```
  f(n) = f(n-2) + 2*g(n-1)
  g(n) = f(n-1) + g(n-2)
  base: f(0)=1, f(1)=0, g(0)=0, g(1)=1
  ```
  The `2*g(n-1)` counts the two mirror-image bump orientations (top or bottom row). This form handles parity automatically — the odd terms come out 0 on their own.

- **Closed form.** Eliminating `g` gives a clean recurrence over even n only: `f(n) = 4*f(n-2) - f(n-4)`, with `f(0)=1`, `f(2)=3`. Check: `f(4)=4·3-1=11`, `f(6)=4·11-3=41`, `f(8)=4·41-11=153`.

### Complexity
O(n) time and O(1) space either way (two rolling states for the coupled form, or four rolling values for the closed form). Values grow ~`(2+√3)^(n/2)`, so they overflow `long` eventually — use `BigInteger` for large n.

### Notes
- **Odd n → 0** is the first thing to handle; skip straight to returning 0 (or let the coupled recurrence produce it).
- **`f(0) = 1`** — the empty board has one tiling.
- The coupled two-state form is the honest "profile DP" and generalizes to other board heights; the `4f(n-2) - f(n-4)` closed form is specific to height 3 and is what you'd use once you trust it. Either is fine — the closed form is fewer lines, the coupled form is more instructive.
- Independent check: exhaustive backtracking (lay dominoes cell by cell) confirms the recurrence for small n — worth keeping as a test since the recurrence is easy to mis-transcribe.

--- 

## Domino_tromino_tiling

**Problem.** Count the ways to fully tile a 2×n board using 2×1 dominoes and L-shaped trominoes. A tromino covers 3 cells — a 2×2 square minus one corner — in any of its 4 orientations. (LeetCode 790; answer taken modulo 1e9+7 since it grows exponentially.)

### Idea
Unlike the pure-domino 2×n case (which is plain Fibonacci), trominoes let a tile leave the column boundary *ragged* — one cell of a column can be filled while the other is still open, waiting on a piece from the next column. The clean way to capture that is two states:

- `f(n)` = ways to tile a **fully filled** 2×n prefix (flush right edge),
- `p(n)` = ways to tile 2×n with **exactly one cell of the last column protruding** (a ragged edge).

Coupled recurrence:
```
f(n) = f(n-1) + f(n-2) + 2*p(n-1)
p(n) = f(n-1) + p(n-1)
```
The `f(n-1)` and `f(n-2)` are the vertical- and double-horizontal-domino closings; the `2*p(n-1)` are the two tromino orientations that finish a ragged edge (top or bottom).

Collapsing the two states into one gives the compact closed recurrence used in the code:
```
f(0) = 1, f(1) = 1, f(2) = 2
f(n) = 2*f(n-1) + f(n-3)
```
which produces `1, 1, 2, 5, 11, 24, 53, 117, 258, 569, 1255, …` (verified against exhaustive backtracking).

### Complexity
O(n) time, O(1) space (roll the last three `f` values, or two states for the coupled form). Counts grow ~`1.8^n`, so **take everything modulo 1e9+7** — apply the mod after every addition so intermediate sums never overflow `long`.

### Notes
- **No parity shortcut here.** Unlike 3×n dominoes, a 2×n board with trominoes is tileable for every n ≥ 0 (trominoes cover 3 cells, so the "board must have even area" argument doesn't apply — two trominoes together cover 6 cells and pair up).
- **Base cases `f(0)=1, f(1)=1, f(2)=2`** must all be seeded; the recurrence reaches back 3 steps.
- **Mod discipline:** `f(n) = (2*f(n-1) + f(n-3)) % MOD`. Because `2*f(n-1)` can approach `2·MOD`, keep it in `long` and mod immediately; the result stays non-negative since all terms are non-negative.
- The coupled `f`/`p` form is the more transferable "profile DP" and is what generalizes to wider boards or other tile sets; the `2f(n-1)+f(n-3)` closed form is the shortcut once you trust it.
- Independent check: exhaustive backtracking that lays each domino/tromino covering the first empty cell confirms the recurrence for small n — worth keeping, since the recurrence and its base cases are easy to mis-seed.

---

## Mountain_scenes

**Problem** (Kattis "scenes", NAIPC 2016). A ribbon of length `n` is cut into `w` columns, each filled from the bottom to an integer height in `[0, h]`. The total ribbon used (sum of column heights) must be at most `n` — she needn't use all of it. A **mountain** scene must be *uneven*: if every column is the same height it's a "plain," not a mountain. Count the distinct mountain scenes, modulo 1e9+7. (Two scenes differ iff the covered regions differ; at most one ribbon piece per column.)

### Idea
Count everything, then subtract the non-mountains — cleaner than counting mountains directly.

**Total scenes** = number of height assignments `(c_1, …, c_w)` with each `c_i ∈ [0, h]` and `sum(c_i) ≤ n`. This is a DP over columns and ribbon used:
```
dp[j] = number of ways for the columns placed so far to use exactly j inches
dp[0] = 1
for each of the w columns:
    next[j] = sum of dp[j - t] for t in 0..h   (add a column of height t)
total = sum of dp[j] for j in 0..cap
```
Each column adds a contiguous window of the previous row, so a **prefix sum** makes the inner sum O(1) → O(w · cap) overall. Cap the ribbon dimension at `cap = min(n, w·h)`: the frame can't hold more than `w·h` inches, so any `n` beyond that is wasted and the total is just `(h+1)^w`.

**Flat scenes** (the ones to remove): every column equal to some height `k`, using `w·k` inches, valid when `w·k ≤ n`. The number of such `k` in `[0, h]` is `min(h, floor(n/w)) + 1`.

**Answer** = `(total − flat) mod 1e9+7`.

### Complexity
O(w · min(n, w·h)) time with the prefix-sum trick — at most 100 · 10000 = 10⁶ operations. O(cap) space (one rolling dp row plus a prefix array).

### Notes
- **"Uneven" means subtract all flats, not just the empty scene.** Every constant-height configuration (including all-zero and completely-full) is a plain. This is the subtlety the samples pin down — e.g. `25 5 5` gives `6^5 − 6 = 7770`.
- **Cap the ribbon at `w·h`.** `n` can be 10000 while the frame holds far less; without the cap the dp array is needlessly huge (or you miscount). When `n ≥ w·h` the total is simply `(h+1)^w`.
- **`w = 1` is always 0** — a single column is trivially uniform, so no scene is ever a mountain. Good sanity check.
- **Mod discipline:** keep the dp sums under the modulus (mod after each addition), and compute the final `total − flat` as `(total − flat % MOD + MOD) % MOD` so the subtraction can't go negative after the total has wrapped.
- Sample checks (all verified): `25 5 5 → 7770`, `15 5 5 → 6050`, `10 10 1 → 1022`, `4 2 2 → 6`.

--- 

## Narrow_art_gallery

**Problem** (Kattis "narrowartgallery", 2014 NAQ). A gallery has N rows of 2 rooms (left, right), each room with a value. Exactly `k` rooms must be closed. To keep the gallery walkable top-to-bottom, you may **not** close two rooms in the same row, nor two rooms in adjacent rows that touch diagonally (left in one row and right in the next, or vice versa). Choose the `k` closures that leave the **maximum total value open**; report that value.

### Idea
Maximize the open value directly. Go row by row; the only thing a row needs to know about its predecessor is **which side (if any) the previous row closed**, because that's exactly what the no-diagonal rule constrains. So the state is `(row, rooms closed so far, previous row's closed side ∈ {none, left, right})`.

Per row you have three moves:
- **Close nothing** → both rooms stay open, add `left + right`, `k` unchanged, new side = none. Allowed after any previous side.
- **Close left** → only the right room open, add `right`, spend one closure, new side = left. Allowed only if the previous side was **none or left** (closing left after a right-close is the forbidden diagonal).
- **Close right** → only the left room open, add `left`, spend one closure, new side = right. Allowed only if previous side was **none or right**.

`dp[i][j][s]` = max open value over rows `0..i` having closed exactly `j` rooms with row `i` in side-state `s`. Transition takes the best feasible predecessor state; the answer is `max` over the three side-states of `dp[N-1][k][·]`. Mark infeasible states as −∞ so they can't be chosen.

### Complexity
O(N · k · 3) states, O(1) work each → **O(N·k)** time. Space O(N·k) for the full table, or O(k) with a rolling row (each row depends only on the previous). Values are small ints; no overflow concern.

### Notes
- **"At most one per row" + "no diagonal" is the whole constraint.** Both fall out of the three-way side-state transition — you never need to look back more than one row.
- **Exactly k, not at most k.** Track the closure count precisely and read the answer at `j == k`; seed unreachable counts as −∞ so a state that can't reach exactly `k` never wins.
- **Feasibility.** Any `0 ≤ k ≤ N` is achievable — closing the left room in any `k` rows (all same side) satisfies both rules — so a valid answer always exists in range.
- **Equivalent framings.** Maximizing open value equals `totalValue − (minimum closed value)`; some solutions (e.g. Fiset's recursive one) minimize the closed sum instead. Same optimum, just complement.
- Sample checks (verified via DP and an independent subset brute): `17`, `17`, `102`.

--- 

## Knapsack_01

**Problem.** Given `n` items, each with a weight and a value, and a knapsack of capacity `W`, pick a subset whose total weight is ≤ `W` and whose total value is maximal. Each item is taken **at most once** (0/1 — you can't take fractions or duplicates).

### Idea
The decision for each item is binary: take it or leave it. Process items one at a time and track, for every capacity budget, the best value achievable so far.

`dp[i][w]` = best value using the first `i` items with a weight budget of `w`. For item `i` (1-indexed, so `weights[i-1]`):
```
dp[0][w] = 0                                     (no items -> no value)
dp[i][w] = dp[i-1][w]                            if weights[i-1] > w   (can't fit -> must skip)
         = max( dp[i-1][w],                      skip item i
                values[i-1] + dp[i-1][w - weights[i-1]] )   take item i
```
The answer is `dp[n][W]`. The "take" branch charges the item's weight against the budget and adds its value; the `max` picks whichever of take/skip is better.

### Complexity
O(n·W) time and O(n·W) space. Space collapses to **O(W)** with a single rolling row — but you must iterate the capacity loop **downward** (`w` from `W` to `weights[i-1]`). Going downward guarantees each item is used at most once, since `dp[w - weight]` still refers to the *previous* item's row; iterating upward would let the same item be re-taken (that's exactly the unbounded-knapsack variant).

Note this is "pseudo-polynomial": `W` is a value, not an input length, so the cost is exponential in the number of bits of `W`. Knapsack is NP-hard; the DP is efficient only when `W` is modest.

### Notes
- **Downward capacity loop is the 0/1 vs unbounded switch.** Descending `w` → each item once (0/1). Ascending `w` → unlimited copies (unbounded knapsack). Same code otherwise — this one line is the whole difference.
- **Base case is all zeros** (no items, or capacity 0, yields value 0), assuming non-negative values.
- **Fractional knapsack is a different problem** — there a greedy by value/weight ratio is optimal; for 0/1, greedy fails and you need this DP.
- Reconstructing *which* items were chosen (not just the value) means walking the full `dp[i][w]` table backward: at each `i`, if `dp[i][w] != dp[i-1][w]` the item was taken, so subtract its weight and continue. That requires the 2D table, not the O(W) rolling row.
- Sample checks (independently verified): `w{1,3,4,5} v{1,4,5,7} cap7 → 9`, `w{2,3,4,5} v{3,4,5,6} cap5 → 7`.

---

## Knapsack_01_items

**Problem.** Same as 0/1 knapsack, but return **both** the maximum value and *which items* achieve it — the indices of an optimal subset, not just the number.

### Idea
Compute the value exactly as before, but keep the **full 2D table** `dp[i][w]` (the rolling O(W) row can't reconstruct — it forgets the history). Then walk the table backward to recover the choices:

```
build dp[0..n][0..W]  as in plain 0/1 knapsack
w = W
for i from n down to 1:
    if dp[i][w] != dp[i-1][w]:      # value changed => item i-1 was taken
        record index i-1
        w -= weights[i-1]           # give back its weight
    # else: dp[i][w] == dp[i-1][w] => item i-1 was skipped, w unchanged
reverse the recorded indices to get them ascending
```

The logic: `dp[i][w]` differs from `dp[i-1][w]` exactly when including item `i-1` beat excluding it, i.e. the optimum at this cell *used* that item. When they're equal, the item wasn't needed, so skip it and keep the same budget.

### Complexity
O(n·W) time and O(n·W) space — the space is now mandatory, since reconstruction needs the whole table. The backward walk is an extra O(n) on top.

### Notes
- **Reconstruction forces the 2D table.** The O(W) rolling optimization discards the per-item rows, so it can give the value but not the items. If you only need the value, use the rolling row; if you need the picks, keep the table.
- **The "equal ⇒ skipped" rule is a tie-break.** When taking and skipping an item yield the same value, this convention skips it, producing one specific optimal set. A different rule (prefer taking on ties) yields a different but equally optimal set — so **the item set isn't unique in general**, only the value is. That's why a good test validates the returned set (distinct, fits capacity, sums to the optimal value) rather than demanding one exact answer, except where the optimum is unique.
- **Return items ascending.** The backward walk collects indices from `n-1` down to `0`, so reverse them (or push-front) before returning.
- **Value-only vs items:** the plain `maxValue` and this `maxValueWithItems` must agree on the value for every input — a cheap consistency check between the two versions.

---

# Graphs — Overview

A **graph** is a set of vertices (nodes) connected by edges. It's the most general of the structures here: trees, linked lists, and grids are all special cases. Almost any "things and the relationships between them" problem is a graph problem.

## Kinds of graphs
- **Undirected** — edges have no direction; `u—v` means you can travel both ways.
- **Directed (digraph)** — edges are one-way arrows; `u→v` does not imply `v→u`.
- **Weighted** — each edge carries a value (cost, distance, capacity). Unweighted graphs are the weight-1 special case.
- **Tree** — a connected, acyclic undirected graph; `n` vertices and exactly `n−1` edges, with a unique path between any two vertices.
- **Rooted tree** — a tree with one vertex designated the root, giving every edge a direction: an **out-tree** (edges point away from the root, the usual case) or an **in-tree** (edges point toward the root).
- **DAG (directed acyclic graph)** — a digraph with no directed cycles. Models dependencies/orderings; admits a topological sort and underlies most DP-on-graphs.
- **Bipartite** — vertices split into two sets with edges only *between* the sets. Equivalent to being **2-colorable** and to having **no odd-length cycle**.
- **Complete graph** — every pair of distinct vertices is joined by exactly one edge (`n(n−1)/2` edges undirected).

## Representations
How you store the graph drives every algorithm's cost. Let `V` = number of vertices, `E` = number of edges.

- **Adjacency matrix** — a `V × V` grid; cell `(u, v)` holds the edge (presence/weight). Edge and weight lookup is **O(1)**, but it uses **O(V²)** memory and iterating all edges is **O(V²)**. Best for **dense** graphs.
- **Adjacency list** — each vertex stores a list of its outgoing edges. Memory is **O(V + E)** and iterating a vertex's neighbors is **O(degree)**; a specific edge/weight lookup is **O(degree)**. Best for **sparse** graphs (the common case).
- **Edge list** — a flat list of `(u, v, w)` triples. Compact and simple, natural for algorithms that just sweep all edges (e.g. Kruskal's MST), but poor for "who are v's neighbors?" queries. Bad for dense or very large graphs where you need adjacency.

## Problems & algorithms this unlocks
- **Shortest path** — BFS (unweighted), Dijkstra (non-negative weights), Bellman-Ford (handles negatives), Floyd-Warshall (all pairs).
- **Detecting negative cycles** — Bellman-Ford flags them.
- **Strongly connected components** — Tarjan's / Kosaraju's on digraphs.
- **Traveling salesman** — min-cost tour visiting every vertex (Held-Karp DP).
- **Bridges and articulation points** — a **bridge** is an edge whose removal increases the number of connected components; the vertex analogue is an **articulation point**. Found with a DFS low-link pass.
- **Minimum spanning tree** — cheapest set of edges connecting all vertices (Kruskal, Prim).
- **Maximum flow** — most flow routable from source to sink (Ford-Fulkerson / Dinic).

Most of these are built on two traversals — **DFS** and **BFS** — so those come first.

---

## Graph_adjacency_matrix

A graph stored as a `V × V` grid: cell `(u, v)` records whether the edge `u → v` exists and, if weighted, its weight. Implements the common `Graph` interface so algorithms don't care which representation they're handed.

### Positives
- **O(1) edge and weight lookup** — "is there an edge `u → v`?" and "what's its weight?" are single array reads.
- Simple and cache-friendly; trivial to add, remove, or update an edge in O(1).
- Best for **dense** graphs (edges near `V²`), where the matrix is mostly full anyway.

### Negatives
- **O(V²) memory** regardless of how few edges exist — wasteful for sparse graphs.
- **Listing a vertex's neighbors is O(V)** — you scan its whole row even if it has one neighbor.
- Iterating all edges is O(V²). For a graph with `V = 10⁵`, the matrix alone is `10¹⁰` cells — infeasible.

### Algorithm / thought process
Keep an `n`-vertex graph in two `n × n` arrays: a boolean `present[u][v]` (does the edge exist) and an int `weight[u][v]` (its weight, valid only where present). Splitting presence from weight avoids the ambiguity of "is a 0 in the cell a weight-0 edge or no edge?".

- **addEdge(u, v, w):** set `present[u][v] = true`, `weight[u][v] = w`; if **undirected**, mirror into `[v][u]`. Re-adding an existing edge just overwrites the weight and must **not** bump the edge count.
- **hasEdge(u, v):** return `present[u][v]` — O(1).
- **weight(u, v):** return `weight[u][v]`, or throw if not present.
- **neighbors(v):** scan row `v` and collect every `u` with `present[v][u]` — O(V), naturally in ascending order.
- **edgeCount:** track it as you add; for undirected, count each edge once (increment only when the edge is genuinely new).

The whole trade is memory and neighbor-iteration cost (both tied to `V`) in exchange for constant-time edge lookup. Reach for it when the graph is dense or when you're doing lots of "does this specific edge exist?" queries.

---

## Graph_adjacency_list

A graph stored as, per vertex, a list of its outgoing edges (each edge a `(neighbor, weight)` pair). Implements the common `Graph` interface. This is the default representation for most graph work.

### Positives
- **O(V + E) memory** — you store exactly the edges that exist, nothing more. Ideal for **sparse** graphs (the common case).
- **Neighbor iteration is O(degree)** — walking a vertex's edges touches only its real neighbors, which is exactly what DFS/BFS/Dijkstra do repeatedly.
- Scales to huge graphs where a `V × V` matrix wouldn't fit.

### Negatives
- **Edge/weight lookup is O(degree)** — checking "is there an edge `u → v`?" means scanning `u`'s list (a matrix does this in O(1)).
- Slightly more pointer/object overhead per edge than a flat matrix cell.
- Neighbors aren't inherently sorted; if an algorithm needs a deterministic order you sort them (or keep the list ordered on insert).

### Algorithm / thought process
Hold a list-of-lists: `adj.get(u)` is the list of edges leaving `u`. Each edge stores its destination and weight (a tiny `Edge {to, weight}`).

- **addEdge(u, v, w):** append an `Edge(v, w)` to `adj.get(u)`; if **undirected**, also append `Edge(u, w)` to `adj.get(v)`. Re-adding an existing edge should update that edge's weight in place rather than appending a duplicate (and must not double-count).
- **hasEdge(u, v):** scan `adj.get(u)` for an edge to `v` — O(degree).
- **weight(u, v):** same scan; return the weight or throw if absent.
- **neighbors(v):** map `adj.get(v)` to destination vertices; return them **ascending** (sort, or keep the list sorted) so traversals are deterministic.
- **edgeCount:** maintain a counter, incremented only when a genuinely new edge is added (once per undirected edge, not once per stored direction).

The trade is the mirror image of the matrix: cheap memory and cheap neighbor iteration, at the cost of O(degree) edge lookups. Because traversal algorithms iterate neighbors far more than they test individual edges, the adjacency list is the right default for all but the densest graphs.

---

## Depth_first_search

Traverse a graph by diving as deep as possible along each branch before backtracking. DFS is the workhorse behind connectivity, cycle detection, topological sort, bridges/articulation points, and strongly connected components.

### Idea
Start at a vertex, mark it visited, and recurse into its first unvisited neighbor — going *deep* before *wide*. When a vertex has no unvisited neighbors, back up to the previous vertex and try its next one. The **visited set** is essential: without it, cycles send you looping forever and shared vertices get processed repeatedly.

```
dfs(v):
    mark v visited
    (pre-order work on v)
    for each neighbor w of v:
        if w not visited:
            dfs(w)
    (post-order work on v)
```

Two natural outputs:
- **Preorder** — the order vertices are first visited (record `v` when you enter `dfs(v)`).
- **Postorder** — the order vertices finish (record `v` after the loop); the reverse of postorder is a topological sort on a DAG.

To cover a possibly-disconnected graph, run DFS from every not-yet-visited vertex; each launch starts a new **DFS tree**, and the number of launches is the number of connected components (for undirected graphs).

### Complexity
O(V + E) — each vertex is entered once and each edge is examined once (twice for undirected, once per stored direction). O(V) extra space for the visited array plus the recursion/stack depth, which can reach V on a long path.

### Notes
- **Recursion vs explicit stack.** The natural form is recursive; on deep graphs (V up to ~10⁵) that risks a stack overflow, so an iterative version with an explicit stack is the safe alternative. Same O(V+E) work.
- **DFS vs BFS.** Both are O(V+E) and both visit everything reachable; they differ in *order*. DFS uses a stack (goes deep) and is the tool for topological sort, cycle detection, and low-link algorithms. BFS uses a queue (goes level by level) and gives shortest paths in **unweighted** graphs — DFS does not.
- **Directed vs undirected.** The traversal code is identical; only the neighbor sets differ. "Connected components via DFS-from-every-vertex" is meaningful for undirected graphs; for digraphs the analogous notion is strongly connected components, which needs more than plain DFS.
- **Determinism.** Visiting neighbors in ascending order (as `Graph.neighbors` guarantees) makes the traversal order reproducible — important for testing, irrelevant to correctness.
- **Reachability** falls straight out of DFS: whatever it marks visited from `start` is exactly the set reachable from `start`.

---

## Breadth_first_search

Traverse a graph level by level: visit the start, then all vertices one edge away, then all two edges away, and so on. Because it reaches vertices in order of increasing distance, BFS gives **shortest paths in unweighted graphs** — and recording where each vertex was first reached lets you rebuild the path.

### Idea
Use a **queue** (FIFO). Seed it with the start, then repeatedly dequeue a vertex and enqueue its unvisited neighbors. The FIFO order is what makes it breadth-first: everything at distance `d` is dequeued before anything at distance `d+1`.

```
mark start visited; dist[start] = 0; parent[start] = -1; enqueue start
while queue not empty:
    v = dequeue
    for each neighbor w of v (ascending):
        if w not visited:
            mark w visited
            dist[w]  = dist[v] + 1
            parent[w] = v          # remember how we first reached w
            enqueue w
```

**Path reconstruction.** `parent[w]` is the vertex from which `w` was first discovered — i.e. the step before `w` on a shortest path. To get the route from `start` to `target`, walk parents backward from `target` (`target, parent[target], parent[parent[target]], …`) until you hit `start`, then **reverse**. If `target` was never visited (`parent`/`dist` still unset), there is no path.

### Complexity
O(V + E) time — each vertex enqueued once, each edge examined once. O(V) space for the queue plus the `visited`, `dist`, and `parent` arrays.

### Notes
- **Mark visited when you ENQUEUE, not when you dequeue.** If you only mark on dequeue, a vertex can be enqueued several times (once per neighbor that points to it) before it's processed, inflating work to worse than O(V+E) and letting duplicates through. Marking on enqueue guarantees each vertex enters the queue exactly once — this is the single most common BFS bug.
- **Shortest paths hold only for unweighted graphs** (or all-equal weights). BFS counts *edges*, not weight; with varying weights you need Dijkstra. The "first time reached = shortest" guarantee comes precisely from the level-by-level FIFO order.
- **`parent[start] = -1`** marks the root of the search and is the stop condition for the backward walk.
- **Ties.** When several shortest paths exist, which one you reconstruct depends on neighbor order (ascending here) and first-parent-wins. All are equally short; the length is unique even though the path isn't.
- **BFS vs DFS.** Same O(V+E), same "visit everything reachable," opposite order: queue (wide) vs stack/recursion (deep). BFS → unweighted shortest paths, level structure, bipartite checking. DFS → topological sort, cycle detection, bridges/SCC. Reach for BFS whenever "fewest edges" or "closest first" matters.

---
## Grid_shortest_path

**Problem.** Given a rectangular grid where `0` is open and `1` is a wall, find the fewest 4-directional moves (up/down/left/right) from a start cell to a target cell, stepping only on open cells. Return the move count, or `-1` if the target can't be reached.

### Idea
The grid *is* a graph, just implicit: each open cell is a vertex, and each pair of orthogonally adjacent open cells is an edge. Every move costs 1, so this is unweighted shortest path → **BFS**. You never build an explicit graph; you generate neighbors on the fly from the coordinates.

```
if start or target is a wall: return -1
dist[start] = 0; enqueue start; mark visited
while queue not empty:
    (r, c) = dequeue
    if (r,c) == target: return dist[r][c]
    for each of the 4 directions (dr, dc):
        (nr, nc) = (r+dr, c+dc)
        if in bounds, open, and not visited:
            mark visited; dist[nr][nc] = dist[r][c] + 1; enqueue (nr, nc)
return -1        # target never dequeued
```

The four directions are the edge set of every cell: `{(-1,0),(1,0),(0,-1),(0,1)}`. A cell's "neighbors" are just those four offsets, filtered to in-bounds, open, unvisited.

### Complexity
O(R·C) time and space — each cell is enqueued at most once and each has ≤ 4 neighbors, so total work is linear in the number of cells.

### Notes
- **Same BFS rules as the graph version.** Mark visited **on enqueue** (not dequeue) so no cell enters the queue twice. Distances propagate as `dist[neighbor] = dist[current] + 1`.
- **Reject wall endpoints up front.** If the start or target is a wall, there's no path — return −1 before starting, or the BFS will just never reach the target and you'll return −1 anyway, but the explicit check is clearer.
- **Bounds check every neighbor.** The `(nr, nc)` must be inside `[0,R) × [0,C)` before you index `grid[nr][nc]` — the edge cells are where an out-of-bounds access hides.
- **Path reconstruction** works exactly like the graph BFS: keep a `parent[r][c]` (store the previous cell, e.g. as an encoded index `r*C + c`), and walk it back from the target, reversing to get start→target. Only the distance is asked here, but the machinery is identical.
- **Variants** slot in cleanly: 8-directional movement (add the 4 diagonals to the direction list), multiple sources (enqueue them all at distance 0), or weighted terrain (that breaks the unit-cost assumption → use Dijkstra / 0-1 BFS instead of plain BFS).
- **Early exit** on dequeuing the target is a valid optimization; without it you just finish the BFS and read `dist[target]`.


---

## Root_tree

Turn an undirected tree into a rooted one by choosing a root and orienting every edge away from it.

### Idea
An undirected tree has no inherent parent/child direction — that only appears once you pick a root. Do a DFS from the chosen root; for each neighbor that isn't the node you came from, that neighbor is a child, so create it, link parent↔child, and recurse. The "came from" check is what stops you from walking back up the edge you just descended (there are no other cycles to worry about, since it's a tree).

```
build(node, parent):
    for each neighbor w of node in adj:
        if w != parent:            # don't go back the way you came
            child = new Tree_node(w, node)
            node.children.add(child)
            build(child, node)
```

### Complexity
O(n) — each vertex and edge is touched once. O(n) recursion depth in the worst case (a path).

### Notes
- **The only guard needed is "skip the parent."** A tree has no other cycles, so you don't need a visited set — tracking the immediate parent is enough.
- **Re-rooting the same tree gives a different structure.** Parent/child relationships are relative to the root; rooting at a leaf vs. the center produces different shapes of the same underlying tree.
- Deep trees can overflow the recursion stack; an explicit stack works identically.

---

## Leaf_node_sum

Sum the ids of all leaves in a rooted tree (a leaf is a node with no children).

### Idea
A post-order recursion: if a node has no children it's a leaf and contributes its own id; otherwise it contributes the summed contributions of its subtrees.

```
sum(node):
    if node is a leaf: return node.id
    total = 0
    for each child c: total += sum(c)
    return total
```

### Complexity
O(n) time, O(height) stack.

### Notes
- **A single-node tree is a leaf** — its root has no children, so it contributes its own id. Handle that base case, don't assume "root = internal."
- Trivially adapts to summing a stored value per node, counting leaves, or finding the deepest leaf — the skeleton is the same tree walk, only the per-node contribution changes.

---

## Tree_center

Find the 1 or 2 vertices at the middle of an undirected tree (the center of its longest path).

### Idea
Peel leaves inward. Repeatedly remove every current leaf (degree ≤ 1) as a whole layer, decrementing neighbors' degrees so new leaves surface, until only **1 or 2** vertices remain. Those survivors are the center(s) — a tree always has exactly one or two, depending on whether its longest path has an odd or even number of vertices.

```
degree[v] = len(adj[v]);  leaves = all v with degree <= 1;  remaining = n
while remaining > 2:
    next = []
    for each leaf: remaining--; for each neighbor: degree--; if degree == 1: next.add(neighbor)
    leaves = next
return the surviving 1 or 2 vertices
```

### Complexity
O(n) — this is a topological-style peel; each vertex leaves the frontier once.

### Notes
- **Always 1 or 2 centers.** Odd-length longest path → one center; even-length → two adjacent centers. Never zero, never three.
- **Small trees are base cases:** `n == 1` → the lone vertex; `n == 2` → both vertices. Your loop condition `remaining > 2` handles these by never entering the loop.
- This is exactly the "trim leaves layer by layer" idea (a.k.a. the minimum-height-trees algorithm) — the center minimizes the tree's height when chosen as root.

---

## Tree_isomorphism

Decide whether two undirected trees have the same shape, ignoring vertex labels.

### Idea
Use the **AHU (Aho–Hopcroft–Ullman) canonical encoding**. First reduce the unrooted problem to a rooted one by rooting each tree at its **center** — isomorphic trees have centers that correspond, so this anchors the comparison. Then encode each rooted tree into a canonical string:

```
encode(node, parent):
    labels = [ encode(child, node) for each child ]
    sort labels                       # sibling order must not matter
    return "(" + concat(labels) + ")"
```

A leaf encodes as `"()"`. Two rooted trees are isomorphic **iff their encodings are identical**. Sorting the child labels at every node is what makes the encoding independent of the arbitrary neighbor order.

For the unrooted trees: root tree A at its (first) center and encode it; a tree can have two centers, so root tree B at **each** of its centers and compare — if either encoding matches A's, they're isomorphic.

### Complexity
O(n log n) with string sorting of child labels (O(n²) in the pathological string-concat worst case, still fine for these sizes). Center + rooting are O(n).

### Notes
- **Different sizes → immediately not isomorphic.** Check `n` first.
- **Root at the center, not an arbitrary vertex.** Rooting both at, say, vertex 0 would compare structure relative to labels — wrong. The center is a label-independent anchor.
- **Two-center subtlety:** try both of one tree's centers against the other's single chosen center, since which center maps to which isn't known in advance.
- **Sorting siblings is essential.** Without sorting the child encodings, the same tree drawn with children in a different order would produce different strings and falsely read as non-isomorphic.
- Verified pairs: A ≅ B (relabeled), A ≇ path, star ≇ path, single ≅ single.

---

## Lowest_common_ancestor

The lowest common ancestor of `u` and `v` in a rooted tree is the deepest node that is an ancestor of both.

### Idea
Preprocess once: root the tree and record each node's **parent** and **depth** (a single DFS/BFS from the root). Then for a query, bring the two nodes to the same depth and walk them up together:

```
lca(u, v):
    while depth[u] > depth[v]: u = parent[u]     # lift deeper node
    while depth[v] > depth[u]: v = parent[v]      # lift the other
    while u != v: u = parent[u]; v = parent[v]    # climb in lockstep
    return u
```

Once both are at equal depth, moving both up one step at a time keeps them level, and the first place they coincide is their lowest common ancestor. If one is an ancestor of the other, the depth-equalizing step alone lands on it.

### Complexity
O(n) preprocessing, O(height) per query (up to O(n) on a degenerate path). Binary lifting brings queries to O(log n) after O(n log n) preprocessing; an Euler-tour + sparse-table (RMQ) gives O(1) queries after O(n log n) — worth reaching for when there are many queries.

### Notes
- **Ancestry is relative to the root.** Re-rooting changes every LCA. Root once, then query.
- **`parent[root] = -1`, `depth[root] = 0`** anchor the walk; a node is its own ancestor, so `lca(u, u) == u`.
- The simple "equalize then climb" version needs no extra structure beyond `parent[]` and `depth[]` — reach for binary lifting only when per-query O(height) is too slow.

---

## Topological_sort_dfs

A topological ordering of a DAG lists its vertices so that every edge `u → v` has `u` before `v`. (Only directed *acyclic* graphs have one.)

### Idea
DFS and record each vertex when it **finishes** (post-order — after all its descendants are done). The reverse of that finish order is a topological order: a vertex finishes only after everything reachable from it, so reversing puts it before them.

```
for each unvisited vertex: dfs(v)
dfs(v):
    mark v "in progress"
    for each out-neighbor w:
        if w in progress: CYCLE  -> no ordering exists
        if w unvisited:   dfs(w)
    mark v "done"; push v onto the output stack
reverse the stack (or the append order) => topological order
```

**Cycle detection** rides along: three colors — unvisited / in-progress (on the recursion stack) / done. An edge to an *in-progress* vertex is a back edge, which means a cycle, so no ordering exists.

### Complexity
O(V + E) — every vertex and edge visited once. O(V) stack/recursion space.

### Notes
- **Reverse the post-order.** Appending on finish gives the ordering *backwards*; reverse it (or push onto a stack and pop). Forgetting to reverse is the classic bug.
- **Two colors aren't enough for cycle detection.** You must distinguish "on the current recursion stack" from "fully done" — an edge to a done vertex is fine (cross/forward edge), only an edge to an on-stack vertex is a cycle.
- The output isn't unique; any order respecting all edges is valid.

---

## Topological_sort_kahn

Same goal — a topological order of a DAG — built with in-degrees and a queue instead of recursion.

### Idea
A vertex can come next in the order exactly when it has no remaining incoming edges. So: compute every vertex's **in-degree**, seed a queue with all in-degree-0 vertices, then repeatedly pull one out, append it, and "remove" its outgoing edges by decrementing each out-neighbor's in-degree — enqueuing any that drop to 0.

```
compute indeg[v] for all v
queue = all v with indeg[v] == 0
order = []
while queue not empty:
    v = dequeue; order.add(v)
    for each out-neighbor w: indeg[w]--; if indeg[w] == 0: enqueue w
if order.size() < V: CYCLE  -> some vertices never reached in-degree 0
```

### Complexity
O(V + E) — each vertex enqueued once, each edge relaxed once. O(V) for the queue and in-degree array.

### Notes
- **The count is the cycle test.** If the loop emits fewer than `V` vertices, the leftovers are trapped in a cycle (their in-degree never reaches 0). No separate coloring needed — Kahn's detects cycles for free.
- **Kahn's vs DFS.** Same O(V+E), same "any valid order." Kahn's is iterative (no recursion-depth risk) and its intermediate state — the set of currently-available vertices — is meaningful (e.g. it exposes which tasks could run in parallel). DFS is terser and naturally yields the reverse-post-order used elsewhere (e.g. in SCC algorithms).
- **Determinism:** replacing the plain queue with a min-priority queue yields the lexicographically smallest topological order, if you need a canonical one.

--- 

## Dag_shortest_longest_path

Single-source shortest (or longest) path in a **weighted DAG**. Because the graph is acyclic, one pass in topological order solves it — no priority queue, no repeated relaxation, and negative weights are fine.

### Idea
Two steps:
1. **Topologically sort** the DAG.
2. **Relax edges in topo order.** Initialize `dist[source] = 0` and every other vertex to +∞ (shortest) or −∞ (longest). Walk vertices in topological order; for each vertex `u` whose `dist[u]` is finite, relax each outgoing edge `u → v`:
   - shortest: `if dist[u] + w < dist[v]: dist[v] = dist[u] + w`
   - longest:  `if dist[u] + w > dist[v]: dist[v] = dist[u] + w`

Why one pass works: in topological order, **every edge into `u` comes from a vertex that appears earlier**, so by the time you process `u`, `dist[u]` is already final. You never need to revisit it. That's the whole payoff of the DAG structure.

**Longest = shortest with the sign flipped.** Either flip the comparison and start from −∞ (as above), or negate every edge weight, run shortest path, and negate the result. Both give the longest path.

### Complexity
O(V + E) — the topo sort is O(V+E) and the relaxation pass touches each edge once. This *beats* Dijkstra's O((V+E) log V) and, unlike Dijkstra, it tolerates negative weights (safe here because a DAG can't contain a negative cycle — it can't contain any cycle).

### Notes
- **Only for DAGs.** If the graph has a cycle, no topological order exists — detect it (the topo sort fails / emits fewer than V vertices) and report "not a DAG." Longest path in a *general* graph is NP-hard; the DAG restriction is what makes it easy.
- **Negative weights are allowed** and are the main reason to use this over Dijkstra when the graph happens to be acyclic.
- **Skip unreachable vertices during relaxation.** Only relax out of a `u` whose `dist[u]` is finite; a vertex still at ±∞ has no path from the source yet, and adding a weight to ∞ is meaningless (and risks overflow). Unreachable vertices keep their ±∞ sentinel in the result.
- **Use `long` for distances.** Summing many `int` weights (especially with a long path or large weights) overflows `int`; accumulate in `long`, and keep the ±∞ sentinels clear of the real range (e.g. `Long.MAX_VALUE` / `Long.MIN_VALUE`), skipping them so `sentinel + w` never wraps.
- Verified: shortest from 0 → `[0,2,3,9,6,8,∞]`, longest → `[0,2,4,9,10,14,−∞]`; a negative-weight DAG routes the shortest path through the `−4` edge.

---

## Dijkstra

Single-source shortest paths on a graph with **non-negative** edge weights.

### Idea
Greedily settle vertices in increasing order of distance. Keep tentative distances; repeatedly pick the unsettled vertex with the smallest tentative distance (via a priority queue), finalize it, and relax its outgoing edges (`if dist[u] + w(u,v) < dist[v]: update`). Once a vertex is popped it's done — its distance can never improve, **because all weights are non-negative**, so no later, longer-prefix path can undercut it.

```
dist[source] = 0, rest = INF; pq = {(0, source)}
while pq not empty:
    (d, u) = pop-min
    if d > dist[u]: continue          # stale entry, skip
    for each edge u->v with weight w:
        if dist[u] + w < dist[v]: dist[v] = dist[u] + w; push (dist[v], v)
```

### Complexity
O((V + E) log V) with a binary-heap priority queue. An indexed PQ with decrease-key can tighten the constant; a plain heap with "push duplicates, skip stale" is simpler and just as good asymptotically.

### Notes
- **Non-negative weights only.** A single negative edge breaks the "settled = final" invariant — use Bellman-Ford instead.
- **Lazy deletion.** Rather than decrease-key, push a new `(dist, v)` on every improvement and skip an entry when its stored distance is worse than `dist[v]` (`if d > dist[u]: continue`). Simplest correct approach with a standard heap.
- Unreachable vertices keep the `INF`/`UNREACHABLE` sentinel.

---

## Bellman_ford

Single-source shortest paths that tolerates **negative** weights and detects negative cycles.

### Idea
A shortest path visits at most `V-1` edges, so relaxing **every edge** `V-1` times is enough to settle all finite shortest distances (each pass extends the correctly-known frontier by one more edge). Then do `V-1` **more** passes: any edge that can *still* relax means its target is reachable from a negative cycle, so its true distance is `-infinity` — mark those `NEGATIVE_INF` and propagate.

```
dist[source] = 0, rest = INF
repeat V-1 times: for each edge (u,v,w): if dist[u] finite and dist[u]+w < dist[v]: dist[v] = dist[u]+w
repeat V-1 times: for each edge (u,v,w): if dist[u] finite and dist[u]+w < dist[v]: dist[v] = NEGATIVE_INF
```

### Complexity
O(V·E) — `V-1` passes over all `E` edges (twice, for the negative-cycle sweep). Slower than Dijkstra, but it's the price of handling negatives.

### Notes
- **Guard against relaxing from an unreachable/`INF` vertex** — `INF + w` would overflow and leak a bogus finite distance. Only relax when `dist[u]` is finite.
- **Two-phase detection.** The first `V-1` passes compute distances; the second `V-1` passes turn "still improving" into the `-infinity` marker, which must be *propagated* (a node fed by a `-infinity` node is also `-infinity`), hence a second full sweep, not a single pass.
- **Early exit optimization:** if a full pass makes no change, distances are settled and you can stop (no negative cycle among reached vertices).

---

## Floyd_warshall

**All-pairs** shortest paths; handles negative edges and detects negative cycles.

### Idea
Consider allowing paths to route through intermediate vertices `0, 1, …, k` one at a time. After considering intermediate `k`, `dist[i][j]` is the shortest path using only intermediates from `{0..k}`. The update is: could going `i → k → j` beat the current `i → j`?

```
dist[i][j] = weight(i,j)  (0 on the diagonal, INF if no edge)
for k in 0..V-1:
    for i in 0..V-1:
        for j in 0..V-1:
            if dist[i][k] + dist[k][j] < dist[i][j]: dist[i][j] = that sum
```

The triple loop order matters: **k is the outermost loop** — you finish incorporating intermediate `k` for all pairs before moving to `k+1`.

### Complexity
O(V³) time, O(V²) space. Beats running Dijkstra/Bellman-Ford from every source once the graph is dense, and it's dead simple to code.

### Notes
- **k must be the outer loop.** Swapping the loop order gives wrong answers — the DP relies on `dist[i][k]` and `dist[k][j]` being final for the current `k`.
- **Negative cycle detection:** after the algorithm, if any `dist[i][i] < 0`, vertex `i` sits on a negative cycle. Pairs whose shortest path passes through such a cycle are `-infinity`.
- **Overflow guard:** as in Bellman-Ford, skip `dist[i][k] + dist[k][j]` when either operand is the `INF` sentinel, or the addition overflows into a false improvement.

---

## Tarjan_scc

Strongly connected components of a **directed** graph in a single DFS. An SCC is a maximal set of vertices where every vertex can reach every other.

### Idea
DFS the graph, assigning each vertex an increasing **index** (discovery time) and a **low-link** = the smallest index reachable from its DFS subtree, including via a single back-edge to a vertex still on an auxiliary **stack**. Push each vertex onto the stack when first visited. When a vertex `u` finishes with `low[u] == index[u]`, it's the **root** of an SCC: pop the stack down to and including `u` — those vertices are exactly one component.

```
dfs(u):
    index[u] = low[u] = counter++; push u; onStack[u] = true
    for each edge u->v:
        if v unvisited: dfs(v); low[u] = min(low[u], low[v])
        elif onStack[v]: low[u] = min(low[u], index[v])    # back-edge to stack
    if low[u] == index[u]:                                  # u is an SCC root
        pop vertices off the stack until u (inclusive) into a new component
```

### Complexity
O(V + E) — one DFS. Optimal, and single-pass (unlike Kosaraju's two passes).

### Notes
- **Back-edge uses `index[v]`, tree-edge uses `low[v]`.** Updating `low[u]` from a *visited, on-stack* neighbor uses that neighbor's **index** (not its low-link); updating from a child in the DFS tree uses the child's **low**. Mixing these up is the classic Tarjan bug.
- **Only on-stack neighbors count.** A visited neighbor already assigned to a finished SCC must be ignored (`onStack` guards this) — otherwise you'd merge components that aren't mutually reachable.
- **Components come out in reverse topological order** of the condensation (the DAG of SCCs). Useful, but if you only need the grouping, the exact ids are arbitrary — test the partition, not the numbers.
- A DAG has `V` singleton SCCs; a single directed cycle is one SCC of size `V`.
- Verified: `{0,1,2},{3,4},{5}` for the linked-cycles graph; DAG → 3 singletons; ring → 1 component.

---


## Traveling_salesman

**Problem.** Given `n` cities and the cost to travel between each pair (an `n × n` matrix), find the cheapest tour that starts at a city, visits every city exactly once, and returns to the start (a minimum-cost Hamiltonian cycle). Works for directed/asymmetric costs (`dist[i][j]` may differ from `dist[j][i]`).

### Idea
Brute force tries all `(n-1)!` orderings — hopeless past ~12 cities. The **Held-Karp** DP does far better by memoizing over *sets* of visited cities, exploiting that the cost of finishing a tour depends only on which cities remain and where you currently are, not on the order you visited the earlier ones.

State: `dp[mask][i]` = minimum cost of a path that starts at city 0, has visited exactly the set of cities in the bitmask `mask`, and currently sits at city `i` (with bit `i` set in `mask`).

```
base:       dp[{0}][0] = 0                       (start at 0, only 0 visited)
transition: for each state (mask, i) and each city j not in mask:
              dp[mask | (1<<j)][j] = min(that, dp[mask][i] + dist[i][j])
answer:     min over i of dp[fullMask][i] + dist[i][0]   (close the loop back to 0)
```

The bitmask encodes the visited set; fixing the start at city 0 removes the `n`-fold rotational symmetry (every cycle can be written starting at 0).

### Complexity
O(2ⁿ · n²) time — `2ⁿ` masks × `n` current-cities × `n` next-cities — and O(2ⁿ · n) space. Practical up to about n = 18–20. This is still exponential (TSP is NP-hard); Held-Karp is just far better than `(n-1)!` factorial brute force.

### Notes
- **Fix the start at city 0.** A tour is a cycle, so its cost is independent of where you "start" describing it; pinning start = 0 avoids counting each cycle `n` times and anchors the base case.
- **Unreachable states need a sentinel.** Initialize `dp` to a large "infinity," and when combining `dp[mask][i] + dist[i][j]`, skip states still at infinity so they don't leak a bogus finite cost. Pick INF large enough to dominate real tours but small enough that `INF + dist` doesn't overflow `int` — or use `long`.
- **Bit tricks:** `mask & (1<<i)` tests membership; `mask | (1<<j)` adds city `j`; `fullMask = (1<<n) - 1`. Iterate masks in increasing numeric order so every predecessor `mask` is finished before the states it extends.
- **n = 1** is a degenerate tour of cost 0. **n = 2** is just `dist[0][1] + dist[1][0]`.
- **Reconstruction** (the actual tour, not just the cost) works like knapsack's: store parent pointers or re-derive by checking which predecessor achieved each `dp` value, walking back from the closing state.
- Sample checks (independently verified): symmetric 4-city → `80`, asymmetric 3-city → `3`.

---

## Eulerian_path

**Problem.** Find a trail through a directed graph that uses **every edge exactly once** (an Eulerian path), or determine that none exists. If the trail starts and ends at the same vertex it's an Eulerian **circuit**. (Vertices may be revisited; edges may not.)

### Idea
Two parts: **check existence** with degree/connectivity conditions, then **build** the trail with Hierholzer's algorithm.

**Existence (directed).** Compute each vertex's in-degree and out-degree. An Eulerian path exists iff:
- at most one vertex has `out − in == +1` (the **start**), and at most one has `in − out == +1` (the **end**),
- every other vertex is **balanced** (`in == out`), and
- all edges lie in a single connected piece (checked implicitly — see below).

If all vertices are balanced it's a **circuit** (start anywhere with an out-edge); if there's a +1/−1 pair, the path must start at the `out−in == 1` vertex and end at the `in−out == 1` vertex.

**Build (Hierholzer's).** Walk edges, never reusing one, keeping a pointer into each vertex's adjacency list. When you get stuck (a vertex with no unused out-edges), you've closed a sub-loop — record that vertex and back up, splicing sub-loops together. The vertices are emitted in **reverse**, so reverse at the end.

```
in[], out[] ; verify Eulerian conditions or return []
start = the out-in==1 vertex, else any vertex with out>0
edgePtr[v] = 0 for all v          # next unused out-edge of v
stack = [start] ; path = []
while stack not empty:
    v = stack.top
    if edgePtr[v] < adj[v].size():        # v has an unused out-edge
        w = adj[v][ edgePtr[v]++ ]
        stack.push(w)
    else:                                  # dead end -> commit v
        path.add(v); stack.pop()
reverse path
if path.size() != edgeCount + 1: return []   # graph was disconnected
return path
```

### Complexity
O(V + E) — each edge is traversed once (the per-vertex pointer never rewinds), and each vertex is pushed/popped a bounded number of times.

### Notes
- **The `path.size() != E + 1` check is the connectivity test.** If the Eulerian degree conditions hold but the edges span more than one component, Hierholzer from the start consumes only its own component and the final path is too short — reject it. You don't need a separate connectivity pass.
- **Pick the right start.** For a genuine path (not circuit) you *must* start at the `out−in == 1` vertex; starting elsewhere strands the last edge. For a circuit, any vertex with an out-edge works.
- **Advance the edge pointer, don't rescan.** The `edgePtr[v]` that only moves forward is what keeps it linear; re-searching for an unused edge each step would make it O(E²).
- **Emit on the way back (post-order) and reverse.** Adding a vertex to the path only when it's a dead end, then reversing, is what correctly interleaves the sub-loops — appending on the way *down* gives a wrong order.
- **Multi-edges and self-loops are fine** — the adjacency list just has duplicates; Hierholzer treats each list entry as one edge.
- **Undirected variant** swaps the degree rule (Eulerian path iff 0 or 2 vertices have **odd** degree) and needs care to mark each undirected edge used from both endpoints; the Hierholzer skeleton is otherwise the same.

---

## Prim_minimum_spanning_tree

**Problem.** Given a connected, weighted, undirected graph, find a **minimum spanning tree** — a subset of edges that connects all vertices with no cycles and the smallest possible total weight.

### Idea
Grow one tree outward from a start vertex. Maintain the set of vertices already in the tree; repeatedly add the **cheapest edge that crosses the boundary** (connects an in-tree vertex to an out-of-tree vertex), which pulls exactly one new vertex in. A priority queue supplies that minimum crossing edge in log time.

**Lazy Prim** (simplest): push candidate edges into a min-heap; pop the cheapest, and if it leads to a new vertex, take it and push that vertex's outgoing edges.

```
visited[start] = true; push all edges from start as (weight, from, to)
mstWeight = 0; edgesUsed = 0
while pq not empty and edgesUsed < n-1:
    (w, from, to) = pop-min
    if visited[to]: continue           # stale edge to an already-added vertex
    visited[to] = true; mstWeight += w; edgesUsed++
    for each edge to->next with weight w2:
        if not visited[next]: push (w2, to, next)
if edgesUsed != n-1: graph is disconnected -> no spanning tree
return mstWeight
```

Why the greedy choice is safe (**cut property**): for any way of splitting the vertices into "in tree" vs "out," the minimum-weight edge crossing that split is always safe to add to some MST. Prim just repeatedly applies this to the growing tree's boundary.

### Complexity
Lazy Prim: O(E log E) — every edge may enter the heap once. **Eager Prim** with an indexed priority queue (decrease-key: keep only the best edge per outside vertex) is O(E log V) and uses O(V) heap space instead of O(E). Both dominated by the heap operations.

### Notes
- **Skip stale edges.** With the lazy heap, an edge whose `to` is already visited is obsolete — pop and ignore it (`if visited[to]: continue`). This is the analogue of Dijkstra's "skip stale entry."
- **Disconnected graphs have no spanning tree.** If you finish and `edgesUsed != n-1`, the graph wasn't connected — report that (a sentinel or exception), don't return a partial-tree weight.
- **Prim vs Kruskal.** Both find an MST via the cut/cycle properties. Prim grows one tree with a heap (great for dense graphs, natural with adjacency lists); Kruskal sorts all edges and unions components with union-find (great for sparse/edge-list graphs). Same total weight; the specific edges can differ when weights tie.
- **Ties → MST not unique.** When several edges share a weight, different valid MSTs exist; only the total weight is unique. Test the *total* (and that your edges form a spanning tree of that weight), not one specific edge set.
- **Edge reconstruction** works like Dijkstra's: when you settle a vertex, remember the edge that brought it in; collect those `n-1` edges as the tree.
- **Eager vs lazy** is purely an efficiency choice — the eager version replaces "push every crossing edge" with "keep the single best crossing edge per outside vertex in an indexed PQ," trimming heap size from O(E) to O(V).

---

## Ford_fulkerson

**Problem.** In a flow network — a directed graph where each edge has a capacity — push as much flow as possible from a **source** to a **sink**, respecting capacities and conserving flow at every other vertex (in = out). Return the maximum total flow.

### Idea
Repeatedly find a path from source to sink that still has spare capacity (an **augmenting path**) and push flow along it, until no such path remains. The key mechanism is the **residual graph**: alongside each edge's remaining forward capacity, keep a **reverse residual edge** that lets a later path *cancel* earlier flow if that frees up a better routing.

```
residual = copy of capacity
maxflow = 0
while an augmenting path source->sink exists in residual (found via BFS):
    bottleneck = min residual capacity along that path
    for each edge (u,v) on the path:
        residual[u][v] -= bottleneck      # consume forward capacity
        residual[v][u] += bottleneck      # open reverse capacity (cancellation)
    maxflow += bottleneck
return maxflow
```

Using **BFS** to find the augmenting path (shortest in edge count) is the **Edmonds-Karp** refinement — it guarantees termination and a good bound. Generic Ford-Fulkerson allows any path (e.g. DFS), which works for integer capacities but can be slow.

### Complexity
Edmonds-Karp (BFS paths): **O(V·E²)**, independent of capacity magnitudes. Generic Ford-Fulkerson with DFS: O(E · maxflow), which is fine for small integer capacities but can degrade badly with large ones. With integer capacities the algorithm always terminates (each augmentation raises the flow by ≥ 1).

### Notes
- **The reverse residual edge is the whole trick.** `residual[v][u] += bottleneck` is what lets a subsequent augmenting path "undo" a suboptimal earlier commitment by pushing flow back. Without it, a greedy first path can strand the network below the true maximum (the "needs residual reroute" test exercises exactly this).
- **Bottleneck = the minimum residual capacity on the path.** You can only push as much as the tightest edge allows; walk the path once to find it, then a second time to apply it.
- **BFS over DFS for the path** (Edmonds-Karp) gives the polynomial O(V·E²) guarantee; a bad DFS order can make plain Ford-Fulkerson take a number of steps proportional to the flow value.
- **Max-flow min-cut theorem:** the maximum flow equals the capacity of the minimum cut separating source from sink. After the final BFS, the vertices still reachable from the source in the residual graph form one side of a minimum cut — a handy way to *verify* the answer or extract the cut.
- **Reverse edges of a capacity-0 pair.** When `u->v` has capacity but `v->u` doesn't, the reverse residual still needs a slot (it starts at 0 and grows as flow is pushed) — a matrix handles this automatically; an adjacency-list version must explicitly pair each edge with its reverse.
- **Integer capacities** guarantee termination and an integer max flow; irrational capacities can, in pathological cases, make generic Ford-Fulkerson fail to terminate — another reason to prefer Edmonds-Karp.


--- 


# Bipartite Matching via Max Flow — Notes

Two problems that look unrelated but are the same shape: model them as a bipartite graph, add a super-source and super-sink, and the maximum flow *is* the maximum matching. The pattern: `source → left nodes (cap 1) → right nodes (edge where allowed) → sink (cap 1 or a limit)`.

## Mice_and_owls

**Problem.** Mice are scattered on a plane; owls will strike. Each mouse can run at most a fixed distance and must reach a hole to survive. Each hole has a limited capacity (how many mice fit). Maximize the number of mice saved (equivalently, minimize the number eaten).

### Idea
Bipartite max flow with capacities on the right side:
- **Source → each mouse**, capacity 1 (each mouse is one unit to route).
- **Mouse → hole**, capacity 1, whenever the straight-line distance from the mouse to the hole is ≤ the mouse's travel distance (that's the geometry the `distance` / `canReach` helpers provide).
- **Hole → sink**, capacity = the hole's capacity (a hole holding `k` mice becomes an edge of capacity `k`).

Max flow from source to sink = the number of mice that can simultaneously be routed to holes without exceeding any hole's capacity = **mice saved**. **Mice eaten = total mice − max flow.**

### Why it works
Each unit of flow is one mouse traveling source → mouse → hole → sink. The unit capacity on `source → mouse` ensures each mouse is used once; the hole capacity on `hole → sink` enforces the hole limit; an integral max flow (guaranteed for integer capacities) gives an integral assignment. This is the classic reduction of *matching with capacities* to max flow.

### Notes
- **The distance test decides which edges exist.** Reachability is `distance(mouse, hole) <= maxDistance` — use `<=` (a mouse exactly at the limit still makes it). Compute distance as Euclidean; comparing squared distances avoids the `sqrt` if you want exact integer arithmetic, but doubles are fine here.
- **Hole capacity lives on the hole → sink edge, not the mouse → hole edges.** Putting the limit on the sink edge is what caps how many mice a hole absorbs regardless of how many can reach it.
- Any max-flow routine works; the network is small, so plain Ford-Fulkerson / Edmonds-Karp is plenty.

## Elementary Math (variant — notes only)

**Problem.** You're given `N` expressions, each a pair `(a, b)`. For each you must choose one operation — `a + b`, `a − b`, or `a × b` — so that all `N` chosen results are **distinct**. Decide whether it's possible (and, in the full problem, output the choices).

### Idea
This is **bipartite matching**, not a capacity problem:
- **Left nodes = the `N` expressions.**
- **Right nodes = the candidate result values** — each expression contributes at most 3 (`a+b`, `a−b`, `a×b`), so at most `3N` distinct values overall. Dedupe them and give each a node.
- **Edge expression → value** for each of the (≤3) results that expression can produce.
- **Source → each expression** (cap 1); **each value → sink** (cap 1, since each result value may be used by at most one expression).

A **perfect matching** — max flow equal to `N` — means every expression can be assigned a *distinct* result. If max flow `< N`, it's impossible. To output the answer, read which value each expression is matched to and print the operation that produced it.

### Why it's the same family as Mice and Owls
Both are "assign each left item to a right slot, each slot used a limited number of times, respecting an allowed-pairs relation." Mice/holes has hole capacities > 1 on the sink edges; Elementary Math has capacity 1 everywhere (a pure matching). Same source→left→right→sink skeleton; the only differences are what the right nodes represent and what the sink-edge capacities are.

### Notes
- **Distinctness = unit capacity on the value → sink edge.** That single-use constraint is exactly "all results distinct."
- **The value set is small** (≤ 3N), so building the right side by enumerating each expression's three results and deduplicating is cheap. Map each distinct value to an index.
- **Watch `a − b` sign and `a × b` magnitude** when hashing values — negative and large products are valid distinct values; don't accidentally collapse them.
- It's a feasibility (perfect-matching) question: you only care whether max flow reaches `N`; the matching edges give the operation choices for the constructive output.


---

## Edmonds_karp

Ford-Fulkerson that always augments along the **shortest** augmenting path (fewest edges), found by BFS.

### Idea
The only change from generic Ford-Fulkerson is *how you pick the path*: use **BFS** on the residual graph, so each augmenting path has the minimum number of edges. That single choice bounds the work — the source-to-sink distance never decreases and increases at least once every `O(E)` augmentations, capping the total at `O(V·E)` augmentations.

```
res = copy of capacity
while BFS finds a residual path source->sink (parent[]):
    bottleneck = min residual on the path
    subtract bottleneck along forward edges, add along reverse edges
    flow += bottleneck
```

### Complexity
O(V·E²), independent of capacity values — the fix that makes plain Ford-Fulkerson's O(E·maxflow) capacity-dependent bound go away. Guaranteed to terminate even with large capacities.

### Notes
- **BFS, not DFS, is the whole point.** DFS can pick long, silly paths and take a number of augmentations proportional to the flow value; BFS's shortest paths give the polynomial bound.
- Everything else — residual graph, reverse edges, bottleneck — is identical to Ford-Fulkerson.

---

## Capacity_scaling

Ford-Fulkerson that pushes **large amounts first**: only use augmenting paths whose every edge has residual capacity ≥ a threshold `delta`, shrinking `delta` by halves.

### Idea
Set `delta` to the largest power of two ≤ the maximum edge capacity. Repeatedly find augmenting paths that stay on edges with residual ≥ `delta` (a "wide" path) and saturate them; when none remain, **halve `delta`** and continue. By the time `delta` reaches 1 you're considering every edge, so the final flow is maximum.

```
delta = largest power of two <= max capacity
while delta >= 1:
    while a residual path source->sink using only edges with residual >= delta exists:
        augment along it
    delta /= 2
```

### Complexity
O(E² · log U), where `U` is the maximum capacity. Each `delta`-phase adds O(E) augmentations, and there are `log U` phases (the halvings). Great when capacities are large but the graph is small.

### Notes
- **`delta` phases = the bit-length of the max capacity.** Starting high and halving means you route the bulk of the flow in a few big pushes before fussing over small residuals.
- **The path search is restricted, not the graph.** You still search the full residual graph; you just refuse to traverse an edge whose residual is below `delta`. A DFS/BFS with that filter finds the wide paths.
- Correct because when `delta = 1` the restriction disappears and it degenerates into ordinary Ford-Fulkerson, which is exact.
- The largest power of two ≤ U is `Integer.highestOneBit(maxCapacity)` — handy for initializing `delta`.

---

## Dinics

The fast general-purpose max-flow algorithm: alternate **BFS level graphs** with **DFS blocking flows**.

### Idea
Each phase has two steps:
1. **BFS to build the level graph.** From the source, assign every vertex its BFS distance (`level`) in the residual graph. Keep only edges that go from level `L` to level `L+1` — this "level graph" has no shortcuts or back-edges. If the sink is unreachable, you're done.
2. **DFS blocking flow.** Repeatedly push flow from source to sink along level-respecting edges (only `u→v` where `level[v] == level[u] + 1`) until no augmenting path remains *in the level graph*. Multiple paths are found per phase, not one.

```
while BFS assigns levels and sink is reachable:
    while a blocking-flow DFS from source can push more:
        push along level+1 edges, updating residuals
```

The crucial optimization inside the DFS is a **per-vertex "current edge" pointer** (`iter[]`): once an edge from `u` leads to a dead end this phase, skip it on future DFS attempts. Without this pruning the DFS is slow; with it, the blocking flow is efficient.

### Complexity
O(V²·E) in general. On **unit-capacity** graphs (including bipartite matching) it's O(E·√V) — which is why Dinic's is the go-to for matching problems. Each phase strictly increases the source-sink distance, so there are at most `V` phases.

### Notes
- **Two nested ideas:** BFS gives the level structure (like Edmonds-Karp's shortest paths), but instead of one augment per BFS, the DFS extracts an entire *blocking flow* (all the augmenting paths at that distance) before rebuilding levels. Fewer BFS rebuilds → faster.
- **The `iter[]` / current-arc pruning is mandatory for the bound.** Re-scanning dead-end edges every DFS turns O(V²E) into something much worse. Advance the per-vertex edge pointer as edges get saturated or lead nowhere this phase.
- **Only follow `level+1` edges in the DFS.** That restriction is what keeps the DFS acyclic and guarantees progress; wandering to same- or lower-level vertices reintroduces cycles.
- **Reset `level[]` and `iter[]` each phase** — they describe the residual graph as it is at the start of that phase.
- Same residual/reverse-edge machinery underneath; Dinic's just organizes the augmentations far more efficiently than picking one path at a time.