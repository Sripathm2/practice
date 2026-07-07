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