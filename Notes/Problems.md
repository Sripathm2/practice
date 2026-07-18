# Problems — Implementation Notes

Idea, complexity, and notes for each named problem in the `Problems/` package — the recursion exercises, the DP problems, grid shortest path, Held-Karp TSP, and the bipartite max-flow problems. Reconciled against the code; **Code flag** lines mark implementation deviations and known bugs (details in `01_Mismatch_report.md`).

*(The five recursion/divide-and-conquer exercise files — `Recursive_multiplication`, `Recursive_list_sum`, `Recursive_string_reversal`, `Recursive_max_2d`, `Three_way_min` — are documented here per the reorganization: they are exercise-style leaf files, recommended to move from `Algorithms/` to `Problems/`.)*

## Contents
**Recursion exercises:** [Recursion](#recursion) · [Recursive_multiplication](#recursive_multiplication) · [Recursive_list_sum](#recursive_list_sum) · [Recursive_string_reversal](#recursive_string_reversal) · [Recursive_max_2d](#recursive_max_2d) · [Three_way_min](#three_way_min)
**Dynamic programming:** [Dynamic programming](#dynamic-programming) · [Magical_cows](#magical_cows) · [Tiling](#tiling) · [Tiling_general](#tiling_general) · [Domino_tiling_3xn](#domino_tiling_3xn) · [Domino_tromino_tiling](#domino_tromino_tiling) · [Mountain_scenes](#mountain_scenes) · [Narrow_art_gallery](#narrow_art_gallery) · [Knapsack_01](#knapsack_01) · [Knapsack_01_items](#knapsack_01_items)
**Graph problems:** [Grid_shortest_path](#grid_shortest_path) · [Traveling_salesman](#traveling_salesman)
**Bipartite matching via max flow:** [Overview](#bipartite-matching-via-max-flow--notes) · [Mice_and_owls](#mice_and_owls) · [Elementary Math](#elementary-math-variant--notes-only-no-implementation)

---

## Recursion

A function that calls itself, used when a problem can be broken into smaller subproblems of the same shape. Each call solves a smaller instance until the problem is small enough to answer directly.

### Idea
Every recursive function has three pieces:

- **Base case** — the terminating condition that stops the recursion. It can be implicit (e.g. falling through when the input is empty). Without a reachable base case you get infinite recursion → stack overflow.
- **Recursive call** — the function calls itself on a **strictly smaller** subproblem, moving closer to the base case. This shrink is the crucial part: if the subproblem doesn't get smaller each time, the recursion never terminates.
- **Body / work** — the actual computation at each level: whatever this step contributes, combined with the result(s) of the recursive call(s).

### Complexity
Time = (number of recursive calls) × (work per call); a recurrence like `T(n) = a·T(n/b) + f(n)` captures it, and the Master Theorem solves the common divide-and-conquer shapes. Space is O(maximum recursion depth) for the call stack — every pending call holds a frame until it returns, so space can be significant even when total work is small. Deep or unbounded recursion risks stack overflow; a tail-recursive or iterative rewrite (or an explicit stack) reduces the stack cost — though Java has no tail-call optimization, so even a tail-recursive form keeps its frames.

### Applications
Divide and conquer (merge sort, quicksort, binary search), tree and graph traversal (DFS), backtracking, memoized recursion / dynamic programming, generating permutations and combinations, and problems defined recursively by nature (factorial, Fibonacci, GCD). The four `Recursive_*` exercises below practice the pattern; merge sort and DFS (see `Algorithms_notes.md`) are where it graduates into real algorithms.


---

## Recursive_multiplication

Multiply two integers using recursion, without the `*` operator.

### Idea
**As implemented, the recursion is on `a` (the first operand), and the workhorse is the doubling form:**

- Base case: `a == 0 → 0`.
- Positive `a`: `multiply(a, b) = 2·multiply(a/2, b) + (a odd ? b : 0)` — where the doubling itself is written as `m + m` (an addition, keeping the "no `*`" rule honest). Each call halves `a`, so the depth is `log₂(a)`.
- Negative `a`: peel toward zero one step at a time — `multiply(a, b) = multiply(a+1, b) − b`. Correct for any sign of `b`, but **linear**: depth is `|a|`.

`b` is never recursed on and never restricted — it just rides along and gets added/subtracted.

### Complexity
Positive `a`: O(log a) time and stack depth. Negative `a`: O(|a|) time and depth — a large negative first operand overflows the call stack. Returns `long` so `int × int` results don't wrap.

### Notes
- **The asymmetry is the real lesson:** the doubling trick was applied to the positive branch only. The clean fix is to normalize the sign first (recurse on `|a|`, apply the sign at the top) so *both* signs get the O(log) form — or recurse on `min(|a|, |b|)` if you also want protection against a huge second operand in a linear form.
- Doubling works because of binary decomposition: `a·b = (a/2)·b + (a/2)·b + (a odd ? b : 0)` is just reading `a`'s bits high-to-low.
- The runner cross-checks against real `*` over a grid of signed values, which is what pinned down the negative-branch behavior.


---

## Recursive_list_sum

Iterate an array with recursion instead of a loop — sum all elements, or sum only the odd-valued ones.

### Idea
Recurse first, then contribute: `f(arr, i) = f(arr, i+1) + g(arr[i])`, with base case `i == arr.length → 0` (an empty tail contributes nothing). The index `i` is how the problem shrinks — one step toward the end per call; the additions happen on the unwind, so the sum accumulates right-to-left (irrelevant for `+`, worth noticing for non-commutative folds). A public method with no index wraps a private helper that carries `i`, starting at 0.

The only thing that changes between "sum all" and "sum odds" is `g`: for the total, `g(x) = x`; for odds, skip when `x % 2 == 0`, else add. Same traversal skeleton, different per-element contribution.

### Complexity
O(n) time and O(n) stack depth — one frame per element until the base case returns and the additions unwind. Sums accumulate in `long`.

### Notes
- Skipping on `x % 2 == 0` keeps negative odds (`−3 % 2 == −1 ≠ 0`) correctly included.
- Because depth equals the array length, very large arrays overflow the stack — this pattern is for learning the recursion shape, not production traversal. The two standard shapes are **head recursion** (recurse, then combine — what this code does) and **tail recursion** (pass an accumulator down); Java keeps O(n) frames either way.


---

## Recursive_string_reversal

Reverse a string using recursion.

### Idea
**As implemented, each call peels *both* ends:** take the last character, the first character, and reverse the middle between them —

```
reverse(s) = s[last] + reverse(s[1 .. last-1]) + s[0]
```

Base case: a string of length 0 or 1 is its own reverse. Each call shrinks the string by **two** characters, so the depth is ⌈n/2⌉ — half the frames of the classic peel-one-character forms (`reverse(s[1:]) + s[0]` or `s[last] + reverse(s[:last])`), same idea.

### Complexity
O(n) depth-wise? No — **O(n²) time and memory**, because Java strings are immutable: every `substring` and every `+` copies a fresh string at each of the n/2 levels. Stack depth is ⌈n/2⌉. To make it O(n): recurse over a `char[]` with two indices, swap the ends, recurse inward (`reverse(arr, lo+1, hi−1)`, base `lo >= hi`) — O(1) work per level, no copying. That two-pointer form is the array twin of exactly the both-ends peel this code already does.

### Notes
- The quadratic `substring`+`+` cost is the same immutability trap that makes string concatenation in a loop slow — fine for learning, call it out.
- Double-reversing any string returns the original — a cheap invariant the runner tests.


---

## Recursive_max_2d

Find the maximum value in a 2D array along with its `(row, col)` position, recursively — the "bundle multiple returns into a result object" idea extended to a grid.

### Idea
**As implemented:** recurse with an explicit `(i, j)` pair that advances in row-major order — bump the column; when it hits `grid[0].length`, wrap to column 0 of the next row. Each call computes the best `Result` of the **suffix** (all cells after `(i, j)`), then compares the current cell against it:

- Base case: `i` past the last row → return a sentinel `Result(Integer.MIN_VALUE, −1, −1)` (an empty suffix can't win against any real cell).
- Combine: keep the current cell when `suffixBest.value <= grid[i][j]` — the `<=` means the **current (earlier) cell wins ties**, so the earliest cell in row-major order is reported: lowest row first, then lowest column. That's the same answer a `>`-based nested-loop brute force produces, which is what the runner cross-checks.

This is one frame per cell — neither the flattened single-index form (`k → (k/C, k%C)`) nor the two-level row/column recursion, though it's closest in spirit to the flattened one with the decode replaced by explicit carry logic.

### Complexity
O(R·C) time — every cell visited once. Stack depth is O(R·C) (one frame per cell), so big grids will exhaust the stack; the two-level row/column form would cut depth to O(R + C). One small `Result` allocation per level.

### Notes
- **Rectangular grids only:** the column wrap uses `grid[0].length`, so a jagged array with rows shorter than row 0 indexes out of bounds (and longer rows are only partially scanned). The validation rejects empty grids and null rows but not jaggedness — a two-level recursion would handle jagged rows naturally.
- **Fix the tie-break policy explicitly** (first vs last, in which scan order) and test it — here it's earliest-in-row-major, enforced by that single `<=`.


---

## Three_way_min

Find the minimum of an array with a **three-way** divide-and-conquer split — the k-way-split exercise from the divide-and-conquer primer (see `Algorithms_notes.md`).

### Idea
Split `[l, r]` into three roughly equal segments at `m1 = l + len/3` and `m2 = l + 2·len/3`, recurse into each, and combine by returning the smallest of the three sub-minimums (a chain of `≤` comparisons that also happens to favor the leftmost segment on ties — irrelevant for a value-only min, but the kind of thing that matters the moment you also return an index).

Base cases: one element returns itself; two elements return the smaller — needed because a 3-way split of a 2-element range would produce an empty middle segment.

### Complexity
`T(n) = 3T(n/3) + O(1)` → **O(n)** time (all n leaves are visited — no split count avoids that for min). Stack depth O(log₃ n).

### Notes
- The point of the exercise is the recurrence shape, not speed — a loop is obviously simpler and faster in constants.
- **Code flag (C8):** the right recursion starts at `m2 − 1`, so the middle and right segments overlap by one element. Harmless for min (overlap can't change a minimum, and no element is *skipped*), but the split isn't a clean partition — `min(m2, r)` is the intended boundary.
- Null throws; an empty array is rejected with `IllegalArgumentException` — min of nothing has no answer.


---

## Dynamic programming

A technique for problems that break into **overlapping** subproblems: solve each distinct subproblem once, store its answer, and reuse it instead of recomputing. It applies when a problem has *optimal substructure* (the answer is built from answers to smaller instances) and *overlapping subproblems* (the same smaller instances recur many times). That overlap is what separates DP from divide-and-conquer — D&C subproblems are independent, so caching buys nothing; DP subproblems repeat, so caching is the whole point.

### Idea
Four things to pin down:
- **State** — the parameters that uniquely identify a subproblem (e.g. "index `i`, remaining capacity `w`").
- **Transition / recurrence** — how a state's answer is built from smaller states.
- **Base cases** — the smallest states, answered directly.
- **Evaluation order** — either **top-down** (write the natural recursion, add a memo table so each state computes once — how Mountain_scenes, Narrow_art_gallery, and Domino_tromino_tiling are written here) or **bottom-up** (tabulation in dependency order — how the tilings, knapsacks, Magical_cows, and TSP are written here).

### Complexity
Roughly (number of distinct states) × (work per transition). Memory is the table size, but it can often shrink: if a state only depends on the previous row/day, keep just that (a **rolling array**) and drop the full table.

### Applications
Fibonacci, 0/1 knapsack, longest common subsequence, edit distance, coin change, matrix-chain multiplication, and DP-flavored graph algorithms (Bellman-Ford, Floyd-Warshall, Held-Karp TSP).


---

## Magical_cows

**Problem.** Farms hold cows, up to a capacity `C`. Every night a farm with `v` cows doubles to `2v`; if `2v <= C` it becomes one farm of `2v`, otherwise it splits into two farms of `v` cows each. Given the initial farms and a list of query days, report the total number of farms after each queried number of nights.

### Idea
Simulating individual farms explodes — farms can double every night, so after `D` nights you may have up to `N·2^D` of them. The unlock: **individual farms don't matter, only how many cows each holds**, and cow counts only ever range over `1..C`. So track `cnt[v]` = number of farms currently holding `v` cows, and advance the whole distribution one night at a time:

- for each `v` with `2v <= C`: those farms grow — `next[2v] += cnt[v]` (one farm each, now at `2v`).
- for each `v` with `2v > C`: those farms split — `next[v] += 2 * cnt[v]` (two farms each, still at `v`).

That's an O(C) step regardless of how many farms exist.

**As implemented:** the code builds the **full table** `farm_count[day][v]` for every day from 0 up to the largest query day (day 0 seeded from the initial farms, each next row derived from the previous by the two rules above). A query is then answered by **summing that day's row** — O(C) per query, a plain table lookup of the distribution rather than a precomputed total.

### Complexity
O(C · D_max) to build the table (D_max = largest query day), then **O(C) per query** (row sum). Space is **O(C · D_max)** for the full table. Both are easy to tighten if you ever care: keep only two rolling rows (O(C) space) and record the running total per day as you go (O(1) per query) — the note-worthy optimizations, not what the code currently does. Farm totals grow up to ~`N·2^D`; counts are `long` (fits comfortably for the usual constraints `D ≤ 50`, `N, C ≤ 1000`).

### Notes
- The two transition cases are the crux: `2v <= C` → one farm at `2v`; `2v > C` → **two** farms at `v` (the split doubles that bucket's count).
- Day 0 is just the initial distribution — a query of 0 nights reads row 0 directly, which the table layout handles for free.
- Validation: capacity ≥ 1, no negative query days, every initial herd in `[1, C]`.
- Overflow is the lurking bug — the table and sums are `long`, not `int`.


---

## Tiling

**Problem.** Count the number of ways to completely fill a 1×n strip of slots using tiles of length 1 and length 2.

### Idea
Look at how the **last slot** gets covered — there are exactly two disjoint, exhaustive possibilities:
- a size-1 tile sits in the last slot, leaving `n − 1` slots to fill → `ways(n − 1)` ways, or
- a size-2 tile covers the last two slots, leaving `n − 2` slots → `ways(n − 2)` ways.

Since those cases don't overlap and cover everything, `ways(n) = ways(n − 1) + ways(n − 2)`. Base cases: `ways(0) = 1` (the empty strip has exactly one tiling — place nothing) and `ways(1) = 1`. That's the Fibonacci recurrence; in fact `ways(n) = Fib(n + 1)`.

The subproblems overlap heavily — plain recursion recomputes `ways(n−2)`, `ways(n−3)`, … exponentially many times. **As implemented:** bottom-up tabulation in a `long[n+3]` table (padded so tiny n never under-allocates), seeded with `ways(0)=1, ways(1)=1, ways(2)=2`, filled left to right.

### Complexity
O(n) time, O(n) space for the table as written. Since each value depends only on the previous two, two rolling variables would make it O(1) space — the table is kept for clarity. Values grow like Fibonacci (≈ φⁿ), overflowing `long` around n = 91 — `BigInteger` beyond that.

### Notes
- **`ways(0) = 1` is the load-bearing base case.** An empty strip has one tiling (do nothing); setting it to 0 makes every larger answer wrong — the empty-product/empty-sum convention again.
- **It's Fibonacci in disguise.** Recognizing that unlocks fast-doubling or matrix exponentiation for O(log n) if inputs get huge.
- **Generalizes.** Tiles of sizes `{1..k}` give `ways(n) = ways(n−1) + … + ways(n−k)` — which is exactly `Tiling_general` with unit color counts.


---

## Tiling_general

**Problem.** Count the ways to fill a 1×n strip using tiles of various lengths, where a given length may come in several colors. Tiles arrive as parallel arrays: `lengths[i]` is an available length, `colors[i]` how many distinct colors it comes in. A tiling is a left-to-right sequence of colored tiles that exactly fills n.

### Idea
Same last-piece reasoning as basic tiling, generalized. Look at the **last tile** placed: it has some length `L` and some color, and it covers the final `L` slots, leaving `n − L` before it. Sum over every way to choose that last tile:

```
ways(0) = 1
ways(m) = sum over i of  colors[i] * ways(m - lengths[i])   for lengths[i] <= m
```

Two dimensions of choice fall out naturally:
- **Different lengths** → separate terms in the sum, each reaching back a different distance.
- **Different colors of the same length** → the `colors[i]` multiplier: each color is a distinct way to place that tile.

The basic 1-and-2 tiling is the special case `lengths = {1, 2}`, `colors = {1, 1}` (Fibonacci). A single length-1 tile with `k` colors gives `k^n`. Unit-color `{1, 2, 3}` gives Tribonacci.

**As implemented:** bottom-up over a `long[n+3]` table, inner loop over tile types with the `lengths[i] <= m` guard; inputs validated (equal-length arrays, lengths and color counts ≥ 1, n ≥ 0).

### Complexity
O(n · t) time, `t` = number of tile types. O(n) space for the table (a rolling window of `maxLength` entries would suffice — the recurrence never reaches further back — but the full table is clearer).

### Notes
- **`ways(0) = 1`** is still the anchor — one way to tile nothing.
- **The color count is a multiplier, not a new term.** Two colors of length 1 turn `+ ways(m−1)` into `+ 2·ways(m−1)`.
- **Skip out-of-range tiles.** A tile longer than the remaining strip can't be the last piece — the `lengths[i] <= m` guard.
- **Watch overflow.** Up to `k^n` growth; sums are `long`, `BigInteger` if the numbers can exceed it.
- Representing colors as counts (rather than listing each colored tile separately) keeps the sum compact — listing them would be arithmetically identical, just wasteful.


---

## Domino_tiling_3xn

**Problem.** Count the ways to completely tile a 3×n board with 1×2 dominoes (horizontal or vertical).

### Idea
**As implemented, this is a broken-profile (bitmask) column DP with 8 states** — the general technique, not the height-3 shortcut.

Fill the board column by column. The only thing the future needs to know about the past is the **profile**: which of the 3 cells at the current column boundary are already covered by dominoes sticking out of the previous column. Three cells → a 3-bit mask → 8 states. `dp[i][s]` = number of ways to tile everything left of column `i` such that column `i` has exactly the cells in mask `s` pre-filled... read at the answer end as: `dp[i][7]` = ways to tile the first `i` columns flush (all 3 bits set = column fully covered, nothing protruding).

Transitions (hand-enumerated in the code, one line per legal way to complete a column):
- **Complement pairs** — a cell left open in the previous column's profile must be covered by a horizontal domino poking into this column, so state `s` at column `i` draws from state `7−s` at column `i−1`: `0←7, 1←6, 2←5, 3←4, 4←3, 5←2, 6←1, 7←0`.
- **Vertical placements** — within a column, a vertical domino can cover two adjacent cells, adding the extra transitions: `3←7` and `6←7` (a flush previous column plus one vertical pair in this column, bottom-pair or top-pair) and `7←3, 7←6` (a protruding pair completed by a vertical domino on the remaining two... i.e. the mask gains two adjacent bits at once). The middle-plus-edge combinations that would need a "vertical" over non-adjacent cells simply have no transition — that's how illegal placements are excluded.

Base: `dp[0][7] = 1` (zero columns, flush boundary — one way: do nothing). Answer: `dp[n][7]`.

### Complexity
O(n · 8) time and, as written, O(n · 8) space (a full table; two rolling rows would be O(1)). Values grow ~`(2+√3)^(n/2)` — `long` overflows eventually; `BigInteger` for large n.

### Notes
- **Parity is automatic.** A 3×n board has `3n` cells, so odd n → 0; the mask DP produces those zeros on its own (no flush completion exists), no special-casing needed. The answer sequence runs `1, 0, 3, 0, 11, 0, 41, …`.
- **Equivalent formulations** worth knowing: the two-state coupled form `f(n) = f(n−2) + 2g(n−1)`, `g(n) = f(n−1) + g(n−2)` (f = flush, g = one-cell bump, the `2` = mirror-image bumps), and the even-n closed form `f(n) = 4f(n−2) − f(n−4)` with `f(0)=1, f(2)=3`. The 8-state mask version implemented here is the one that generalizes to any board height — the profile just grows to `2^height` states.
- Independent check: exhaustive backtracking confirms small n — kept in the runner, since hand-coded transition tables are easy to mis-transcribe.


---

## Domino_tromino_tiling

**Problem.** Count the ways to fully tile a 2×n board using 2×1 dominoes and L-shaped trominoes (a 2×2 square minus one corner, any of 4 orientations). LeetCode 790; answers mod 1e9+7.

### Idea
Trominoes let a column boundary be *ragged* — one cell of a column filled while the other still waits on a piece from the next column. **As implemented: top-down memoized recursion over the state `(column i, top cell free?, bottom cell free?)`** — the profile DP directly, with a `dp[n][4]` memo (−1 = uncomputed) and the two booleans encoding the current column's raggedness:

- Both cells free (state "full choice"): place a vertical domino (→ next column untouched), two horizontal dominoes (→ next column both cells taken... expressed as recursing with the next column's flags), or one of two mirror-image trominoes (→ next column has one cell taken, top or bottom).
- Exactly one cell free (two mirror states): finish with a horizontal domino into the next column, or a tromino that also bites one cell of the next column.
- No cells free: this column is done — advance with the next column fully free.

Base case: `i == n` with a clean boundary → 1 tiling. Each branch is guarded so nothing pokes past column n−1. Results are stored mod 1e9+7; each state's sum is at most a handful of sub-results, so `long` never overflows before the mod.

### Complexity
O(n) states × O(1) transitions → O(n) time, O(n) memo space (the closed form below runs in O(1) space, kept as a cross-check).

### Notes
- **The closed recurrence `f(n) = 2f(n−1) + f(n−3)`** with `f(0)=1, f(1)=1, f(2)=2` (sequence `1, 1, 2, 5, 11, 24, 53, 117, …`) is what the state recursion collapses to — useful for verifying, and what you'd hand-roll once you trust it. It is *not* what the code runs; the state form is the one that transfers to wider boards and other tile sets.
- **No parity shortcut here.** Unlike 3×n dominoes, a 2×n board with trominoes is tileable for every n ≥ 0.
- **Mod discipline:** mod on store; all terms non-negative, so no wrap-around fix needed.
- Independent check: exhaustive backtracking that lays each piece covering the first empty cell confirms small n — the branch set and its guards are easy to mis-enumerate.


---

## Mountain_scenes

**Problem** (Kattis "scenes", NAIPC 2016). A ribbon of length `n` is cut into `w` columns, each filled bottom-up to an integer height in `[0, h]`. Total ribbon used (sum of heights) must be ≤ `n` — she needn't use it all. A **mountain** scene must be *uneven*: all-equal columns make a "plain," not a mountain. Count distinct mountain scenes, mod 1e9+7.

### Idea
Count everything, then subtract the non-mountains — cleaner than counting mountains directly.

**Total scenes** = height assignments `(c_1, …, c_w)`, each `c_i ∈ [0, h]`, with `sum ≤ n`. **As implemented: top-down memoized recursion** `count(column, ribbonLeft)` — at each column try every height `0..h`, recursing with the ribbon reduced; base cases: negative ribbon → 0, past the last column → 1. Memo table `dp[w+1][n+1]`, −1 sentinel, mod on every addition.

**Flat scenes** (the ones to remove): every column equal to some height `k`, using `w·k` ribbon, valid while `w·k ≤ n` — there are `min(h, ⌊n/w⌋) + 1` of them (computed in the code as `min(w·h, n)/w + 1`, same value).

**Answer** = `(total − flats + MOD) mod 1e9+7` — the `+ MOD` before the final mod keeps the subtraction non-negative after wrapping.

### Complexity
**O(w · n · h)** time as written — `w·n` states, each summing over `h+1` heights — and **O(w · n)** memo space. Two standard tightenings, noted but not implemented: cap the ribbon dimension at `min(n, w·h)` (the frame can't hold more, so larger `n` only bloats the table), and replace the inner height loop with a prefix sum over the previous column's row (each column reads a contiguous window), dropping the time to O(w · min(n, w·h)).

### Notes
- **"Uneven" means subtract *all* flats, not just the empty scene** — every constant-height configuration, including all-zero and completely-full. The samples pin this down: `25 5 5 → 6^5 − 6 = 7770`.
- **`w = 1` is always 0** — a single column is trivially uniform. Good sanity check.
- Sample checks (all in the runner): `25 5 5 → 7770`, `15 5 5 → 6050`, `10 10 1 → 1022`, `4 2 2 → 6`.
- Implementation notes: state lives in static fields (memo, W/H/N) — fine solo, not reentrant; validation requires `w, h ≥ 1`, `n ≥ 0`.


---

## Narrow_art_gallery

**Problem** (Kattis "narrowartgallery", 2014 NAQ). A gallery has N rows of 2 rooms (left, right), each with a value. Exactly `k` rooms must be closed. To keep the gallery walkable top-to-bottom you may **not** close two rooms in the same row, nor two diagonally-touching rooms in adjacent rows (left in one row and right in the next, or vice versa). Choose closures leaving the **maximum total value open**.

### Idea
**As implemented: minimize the value *closed*, then complement** — `answer = totalValue − minLost`. (Maximizing open value directly is the equivalent framing; the complement is what this code — like Fiset's — does, and it makes the recursion's contribution per step a single room value instead of a row sum.)

Top-down recursion `minLost(row, closuresLeft, prevSide)` where `prevSide ∈ {left-closed, free, right-closed}` — the only fact about the past the no-diagonal rule cares about. Per row, three moves:
- **Close nothing** → lose 0 here, `k` unchanged, next side = free. Allowed after anything.
- **Close left** → lose `rooms[row][0]`, spend a closure, next side = left. Allowed only when prevSide is free or left (left-after-right is the forbidden diagonal).
- **Close right** → symmetric, allowed when prevSide is free or right.

Base cases: `closuresLeft == 0` → 0 (the remaining rows all stay open — this is what makes it *exactly* k, since the count can't go below zero); rows exhausted with closures still owed → an infeasibility sentinel so that branch never wins. Memo `dp[row][k][side]` with −1 sentinel.

### Complexity
O(N · k · 3) states, O(1) work each → **O(N·k)** time and memo space. Values are small ints.

### Notes
- **"At most one per row" + "no diagonal" is the whole constraint** — both fall out of the three-way side state; you never look back more than one row.
- **Feasibility:** any `0 ≤ k ≤ N` is achievable (close the same side in k rows), so a valid answer always exists in range — enforced by the `k ≤ rooms.length` validation.
- **Code flag (C7):** the infeasibility sentinel is the literal **10000**. A legitimate closed-sum above 10000 (e.g. 200 rows of ~100-value rooms) would lose to the "infeasible" branch and inflate the answer. Use a large sentinel (`Integer.MAX_VALUE/2`) — the fix is one constant.
- Sample checks (verified against an independent subset brute force in the runner): `17`, `17`, `102`.


---

## Knapsack_01

**Problem.** Given `n` items, each with a weight and a value, and a knapsack of capacity `W`, pick a subset with total weight ≤ `W` and maximal total value. Each item is taken **at most once** (0/1 — no fractions, no duplicates).

### Idea
The decision for each item is binary: take it or leave it. Process items one at a time and track, for every capacity budget, the best value achievable so far.

`dp[i][w]` = best value using the first `i` items with budget `w`. For item `i` (1-indexed, so `weights[i-1]`):
```
dp[0][w] = 0                                     (no items -> no value)
dp[i][w] = dp[i-1][w]                            if weights[i-1] > w   (can't fit -> must skip)
         = max( dp[i-1][w],                      skip item i
                values[i-1] + dp[i-1][w - weights[i-1]] )   take item i
```
Answer: `dp[n][W]`. **As implemented:** the full 2D table, filled row by row with `w` ascending — safe with two rows in play, because "take" always reads the *previous* row. Inputs are validated (equal-length arrays, non-negative weights/values/capacity); zero items short-circuits to 0.

### Complexity
O(n·W) time and O(n·W) space as written. The classic space squeeze — one rolling row, **capacity loop descending** — gets O(W); descending is what keeps each item single-use (`dp[w − weight]` still means the previous item's row), while ascending on one row would silently become *unbounded* knapsack. Worth internalizing even though this file keeps the 2D table (its sibling needs the table anyway for reconstruction).

Note this is "pseudo-polynomial": `W` is a value, not an input length, so the cost is exponential in `W`'s bit-length. Knapsack is NP-hard; the DP is efficient only when `W` is modest.

### Notes
- **The one-line 0/1-vs-unbounded switch** (rolling-row loop direction) is the interview classic riding on this problem.
- **Fractional knapsack is a different problem** — greedy by value/weight ratio is optimal there; for 0/1, greedy fails and you need the DP.
- Sample checks (independently verified): `w{1,3,4,5} v{1,4,5,7} cap7 → 9`, `w{2,3,4,5} v{3,4,5,6} cap5 → 7`.


---

## Knapsack_01_items

**Problem.** Same as 0/1 knapsack, but return **both** the maximum value and *which items* achieve it.

### Idea
Build the identical full 2D table, then walk it backward to recover the choices:

```
w = W
for i from n down to 1:
    if dp[i][w] != dp[i-1][w]:      # value changed => item i-1 was taken
        record index i-1
        w -= weights[i-1]           # give back its weight
    # else: skipped, budget unchanged
```

`dp[i][w]` differs from `dp[i-1][w]` exactly when including item `i−1` beat excluding it — the optimum at this cell *used* the item. **As implemented:** indices are collected during the walk (descending) and then `Arrays.sort`ed ascending — same result as the push-front/reverse approach. Value and items are returned together in a small immutable `Result` (with `equals`/`hashCode`, so runners can compare directly).

### Complexity
O(n·W) time and O(n·W) space — the table is now mandatory (the O(W) rolling row forgets the per-item history the walk needs). The backward walk adds O(n), the sort O(n log n) — noise.

### Notes
- **The "equal ⇒ skipped" rule is a tie-break.** When taking and skipping tie, this convention skips — producing one specific optimal set. The item set isn't unique in general, only the value is; so the runner validates the returned set (distinct indices, fits capacity, sums to the optimal value) rather than demanding one exact answer, except where the optimum is provably unique.
- **Value-only vs items:** `Knapsack_01.maxValue` and this method must agree on the value for every input — a cheap consistency check the runner performs.


---

## Grid_shortest_path

**Problem.** Given a rectangular grid where `0` is open and `1` is a wall, find the fewest 4-directional moves from a start cell to a target cell, stepping only on open cells. Return the move count, or `-1` if unreachable. (Lives in `Problems/` — it's a named problem built on the BFS idea.)

### Idea
The grid *is* a graph, just implicit: each open cell is a vertex, each pair of orthogonally adjacent open cells an edge. Every move costs 1, so this is unweighted shortest path → **BFS**, generating neighbors on the fly from coordinates instead of building a graph.

**As implemented** — the same mark-on-dequeue style as the graph BFS (see `Breadth_first_search` in `Algorithms_notes.md`), with a `dist` grid instead of parents:

```
if start or target is a wall: return -1
dist[*][*] = -1;  dist[start] = 0;  enqueue start
while queue not empty:
    (r, c) = dequeue                       # two parallel Queues carry r and c
    if not visited[r][c]:
        mark visited
        for each of the 4 directions:
            (nr, nc) = neighbor
            if in bounds, open:
                if dist[nr][nc] == -1: dist[nr][nc] = dist[r][c] + 1   # first sight wins
                enqueue (nr, nc)
return dist[tr][tc]        # still -1 if never reached
```

`dist` is set at **first discovery** (guarded by `== -1`), which preserves shortest distances for the same reason the graph BFS's first-parent-wins does. The `-1` initialization doubles as the unreachable sentinel, so the final read needs no special case. There is no early exit on reaching the target — the BFS runs to completion and the answer is read out of the table (an `if (r,c)==target return` on dequeue is a valid optimization, just not taken).

### Complexity
O(R·C) time and space — each cell expands once, ≤ 4 neighbors each; the duplicate-tolerant queue holds at most O(R·C) redundant entries (constant-degree grid, so no blowup).

### Notes
- **Reject wall endpoints up front** — the code returns −1 immediately if start or target is a wall (the BFS would conclude the same, the explicit check is clearer), after validating bounds and rectangularity.
- **Bounds check every neighbor** before indexing — edge cells are where the out-of-bounds access hides.
- **Coordinates travel as two parallel queues** (`r_index`, `c_index`) dequeued in lockstep — the third option next to encoding `r*C + c` or a small cell object.
- **Variants** slot in cleanly: 8-directional movement (extend the direction arrays), multiple sources (enqueue all at distance 0), weighted terrain (breaks the unit-cost assumption → Dijkstra / 0-1 BFS). Path reconstruction would add a parent grid exactly like the graph BFS.


---

## Traveling_salesman

**Problem.** Given `n` cities and an `n × n` cost matrix, find the cheapest tour that starts at a city, visits every city exactly once, and returns to the start (minimum-cost Hamiltonian cycle). Works for asymmetric costs (`dist[i][j]` may differ from `dist[j][i]`).

### Idea
Brute force tries all `(n−1)!` orderings — hopeless past ~12 cities. **Held-Karp** memoizes over *sets* of visited cities: the cost of finishing depends only on which cities are visited and where you stand, not the order behind you.

State: `memo[i][mask]` = minimum cost of a path that starts at city 0, has visited exactly the bitmask `mask` (bit `i` set), and currently stands at `i`.

**As implemented — bottom-up by subset size:**
```
base:  memo[i][ {0, i} ] = dist[0][i]   for each i != 0
for r = 3..n:                            # subset sizes
    for each mask with bitCount r containing city 0:
        for each next in mask (not 0):
            memo[next][mask] = min over end in mask\{0,next} of
                               memo[end][mask ^ (1<<next)] + dist[end][next]
answer: min over end != 0 of memo[end][fullMask] + dist[end][0]
```
Iterating masks in increasing subset size guarantees every predecessor state is finished before it's read. `notIN(i, mask)` is the bit-test helper; `fullMask = (1<<n) − 1`; INF = `Integer.MAX_VALUE/2` so `INF + dist` can't overflow.

### Complexity
O(2ⁿ · n²) time, O(2ⁿ · n) space — practical to n ≈ 18–20. Still exponential (TSP is NP-hard); just vastly better than factorial.

### Notes
- **Fix the start at city 0.** A tour's cost is rotation-invariant; pinning the start removes the n-fold symmetry and anchors the base case. `n = 1` short-circuits to 0; `n = 2` falls out as `dist[0][1] + dist[1][0]`.
- **Sentinel discipline:** halved MAX_VALUE keeps `INF + dist` finite; unvisited memo cells default to 0 but are never read thanks to the subset-size ordering and the in-mask guards.
- The table size is allocated via `(int) Math.pow(2, n)` — works, but `1 << n` is the exact, idiomatic form.
- **Code flag (B10):** after computing the cost, the method also runs a **tour reconstruction** loop — whose comparison is inverted (it replaces the candidate when the incumbent is strictly *better*, building a worst tour) — and then **discards the result** (the `tour` array is never returned). Dead code hiding a bug no test can reach: either fix the comparison and return the tour, or delete the loop.
- Sample checks (independently verified): symmetric 4-city → `80`, asymmetric 3-city → `3`.


---

# Bipartite Matching via Max Flow — Notes

Two problems that look unrelated but share one shape: model them as a bipartite graph, add a super-source and super-sink, and the maximum flow *is* the maximum matching. The pattern: `source → left nodes (cap 1) → right nodes (edge where allowed) → sink (cap 1 or a limit)`.

## Mice_and_owls

**Problem.** Mice are scattered on a plane; owls will strike. Each mouse can run at most a fixed distance and must reach a hole to survive. Each hole has a capacity. Maximize mice saved (equivalently, minimize eaten).

### Idea
Bipartite max flow with capacities on the right side. **As implemented:** one capacity matrix of size `mice + holes + 2`, with mice at indices `0..m−1`, holes at `m..m+h−1`, source at `m+h`, sink at `m+h+1`:
- **Source → each mouse**, capacity 1 (each mouse is one unit to route).
- **Mouse → hole**, capacity 1, whenever `distance(mouse, hole) <= maxDistance` (the `canReach` predicate — Euclidean, and `<=` so a mouse exactly at its limit still makes it).
- **Hole → sink**, capacity = the hole's capacity.

Max flow (computed with the repo's `Ford_fulkerson`) = mice simultaneously routable to holes without exceeding any hole = **mice saved**. Mice eaten = total − flow.

### Why it works
Each flow unit is one mouse traveling source → mouse → hole → sink. Unit capacity on `source → mouse` uses each mouse once; the hole's limit sits on its sink edge; integer capacities guarantee an integral max flow, i.e. an actual assignment. This is the classic reduction of capacity-constrained matching to max flow.

### Notes
- **Hole capacity lives on the hole → sink edge, not the mouse → hole edges** — that's what caps a hole's intake no matter how many mice can reach it.
- Comparing squared distances would avoid the `sqrt` for exact integer inputs; doubles are fine here.
- Any max-flow routine works; the networks are small, so the DFS Ford-Fulkerson is plenty.

## Elementary Math (variant — notes only, no implementation)

**Problem.** Given `N` expressions, each a pair `(a, b)`, choose one operation per pair — `a + b`, `a − b`, or `a × b` — so that all `N` results are **distinct**. Decide feasibility (and, in the full problem, output the choices).

### Idea
This is **pure bipartite matching**, not a capacity problem:
- **Left nodes = the N expressions.** **Right nodes = the candidate result values** — each expression contributes at most 3, so ≤ 3N distinct values; dedupe them.
- **Edge expression → value** for each result that expression can produce.
- **Source → expression** cap 1; **value → sink** cap 1 (each result value usable by at most one expression).

A **perfect matching** — max flow = N — means every expression gets a distinct result; flow < N means impossible. The matched edges name the operations for constructive output.

### Why it's the same family as Mice and Owls
Both are "assign each left item to a right slot, each slot used a limited number of times, respecting an allowed-pairs relation." Mice/holes has capacities > 1 on the sink edges; Elementary Math is capacity 1 everywhere (a pure matching). Same source→left→right→sink skeleton; only the meaning of the right nodes and the sink capacities differ.

### Notes
- **Distinctness = unit capacity on the value → sink edge.** That single-use constraint *is* "all results distinct."
- The value set is small (≤ 3N): enumerate each expression's three results, dedupe, map each distinct value to an index. Watch `a − b` signs and `a × b` magnitudes when hashing — negative and large products are legitimately distinct values (use `long`).