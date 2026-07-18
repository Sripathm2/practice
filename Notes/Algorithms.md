# Algorithms — Implementation Notes

Idea, complexity, and notes for each algorithm in the `Algorithms/` package, reconciled against the code. Complexity claims describe **this repo's implementations** (e.g. the naive suffix-array build), with faster variants noted as upgrades. **Code flag** lines mark spots where the implementation deviates or has a known bug (details in `01_Mismatch_report.md`). Notes for the `Data_structures/` and `Problems/` packages live in their own files.

## Contents
**Primers:** [Time complexity](#time-complexity--growth-rates-worth-recognizing) · [Hamiltonian cycles & NP-hardness](#hamiltonian-cycles--np-hardness)
**Strings (suffix array):** [Unique_substrings](#unique_substrings) · [Longest_repeated_substring](#longest_repeated_substring) · [Longest_common_substring](#longest_common_substring)
**Divide & conquer:** [Divide and conquer](#divide-and-conquer) · [Merge_sort](#merge_sort) · [Strassen_matrix_multiplication](#strassen_matrix_multiplication)
**Greedy:** [Huffman_encoding](#huffman_encoding)
**Graphs:** [Overview](#graphs--overview) · [Depth_first_search](#depth_first_search) · [Breadth_first_search](#breadth_first_search) · [Root_tree](#root_tree) · [Leaf_node_sum](#leaf_node_sum) · [Tree_center](#tree_center) · [Tree_isomorphism](#tree_isomorphism) · [Lowest_common_ancestor](#lowest_common_ancestor) · [Topological_sort_dfs](#topological_sort_dfs) · [Topological_sort_kahn](#topological_sort_kahn) · [Dag_shortest_longest_path](#dag_shortest_longest_path) · [Dijkstra](#dijkstra) · [Bellman_ford](#bellman_ford) · [Floyd_warshall](#floyd_warshall) · [Tarjan_scc](#tarjan_scc) · [Articulation_points](#articulation_points) · [Eulerian_path](#eulerian_path) · [Prim_minimum_spanning_tree](#prim_minimum_spanning_tree)
**Max flow:** [Ford_fulkerson](#ford_fulkerson) · [Edmonds_karp](#edmonds_karp) · [Capacity_scaling](#capacity_scaling) · [Dinics](#dinics)

The graph representation notes (`Graph_adjacency_matrix`, `Graph_adjacency_list`) live in `Data_structures_notes.md` — those classes belong to the `Data_structures/` package. The recursion exercises, DP problems, `Grid_shortest_path`, `Traveling_salesman`, and the bipartite-matching flow problems are in `Problems_notes.md`.

---

## Time complexity — growth rates worth recognizing

A quick reference for the growth rates that keep appearing in this repo, with the shapes that produce them. The question to ask of any loop or recursion: *how does the work shrink or accumulate as n grows?*

### O(1) — constant
Work independent of input size: array indexing, hash-table average lookup, heap peek, the sparse table's two-block query.

### O(log n) — halving
The input is **cut by a constant factor each step**, so the step count is "how many times can I halve n" = log₂ n. Doubling n adds *one* step. Recognize it in: binary search, BST/AVL walks (height of a balanced tree), heap sift up/down, Fenwick-tree loops (one step per set bit), the doubling branch of `Recursive_multiplication`, and each delta-phase count in capacity scaling (log of max capacity). Base of the log doesn't matter asymptotically — log₂, log₃, ln differ by a constant.

### O(√n) — square root
Work proportional to the root of the input — rarer, but a distinct shape worth spotting. Classic producers: **trial-division primality/factorization** (divisors pair up as d · n/d, so testing up to √n suffices), **square-root decomposition** (split an array into √n blocks of √n elements; a range query touches at most 2√n partial elements + √n whole blocks — the "poor man's segment tree"), and the baby-step-giant-step pattern (meet in the middle over √n-sized halves). Also the count of *distinct values* of ⌊n/k⌋ — a number-theory staple. Sits strictly between log n and n: for n = 10⁶, log n ≈ 20, √n = 1000, n = 10⁶.

### O(n) — linear
Touch every element a constant number of times: array scans, linked-list walks, `heapify` (the bottom-up build — perhaps surprisingly not n log n, because most nodes are near the bottom with short sift paths), Kasai's LCP, one BFS/DFS pass counting V+E as "the input size," Fenwick's O(n) constructor, `Three_way_min` (3T(n/3) + O(1) still visits every leaf).

### O(n log n) — linearithmic
The two canonical producers:
- **log n levels × O(n) work per level** — merge sort's recursion tree, the sparse table's build (log n rows × n cells).
- **n iterations × an O(log n) structure operation** — n heap pushes/pops (heapsort, lazy Prim/Dijkstra's heap traffic per edge), sorting-based algorithms in general (comparison sorting has an n log n lower bound), prefix-doubling suffix-array rounds.
Practically the "still fine at n = 10⁶–10⁷" class.

### Beyond
- **O(n²), O(n³)** — nested full scans: the naive LCP build, Floyd-Warshall's triple loop.
- **O(2ⁿ · n²), O(n!)** — subset DP (Held-Karp TSP) and permutation brute force; the wall where NP-hardness lives (see the NP-hardness note below).

Rule-of-thumb budget at ~10⁸ simple ops/sec: n ≤ 10⁶–10⁷ for n log n, n ≤ ~10⁴ for n², n ≤ ~500 for n³, n ≤ ~20 for 2ⁿ·n².

---

## Unique_substrings

Count the distinct non-empty substrings of a string.

### Idea
Every substring is a prefix of exactly one suffix. Across all suffixes there are `n(n+1)/2` prefixes — every substring counted *with* duplicates. Adjacent suffixes in sorted order share `lcp[i]` leading characters, and those shared prefixes are exactly the double-counts, so subtract them: `distinct = n(n+1)/2 − sum(lcp)`. The naive alternative (enumerate every substring into a hash set) is O(n³) time and O(n²) memory; this identity replaces all of it with one sum.

### Complexity
Dominated by the repo's **naive** suffix-array build: O(n² log n) time and O(n²) memory (all suffix strings are materialized; the LCP is direct adjacent comparison, not Kasai's). The final sum is O(n). With a prefix-doubling build + Kasai's, the whole thing drops to O(n log n) / O(n) — the upgrade path, not the current state.

### Notes
- The empty string short-circuits to 0; null throws.
- **Code flag (C1):** `n(n+1)/2` is computed in **`int`** (and the LCP sum via an int stream) even though the method returns `long` — it overflows once n passes ~65,535. The naive O(n²)-memory build gives out around the same scale, but the int math is the sharper cliff; compute both in `long`.


---

## Longest_repeated_substring

Find the longest substring that occurs at least twice.

### Idea
A repeated substring is a common prefix of two *distinct* suffixes, so the longest one has length `max(lcp)`. Recover it as that many leading characters of the suffix at the rank where the maximum sits (`suffixAt(rank)` hands the sorted suffix straight back).

### Complexity
Dominated by the naive suffix-array build: O(n² log n) time, O(n²) memory. The scan for the maximum LCP is O(n). (O(n log n) overall with the fast build.)

### Notes
- If `max(lcp) == 0` nothing repeats → return `""` (empty input also returns `""`; null throws).
- **Tie-break as implemented:** the scan uses strict `>`, so the *first* maximum in sorted-rank order wins — i.e. the lexicographically smallest of the tied longest repeats.


---

## Longest_common_substring

Find the longest substring appearing in at least k of n input strings (2 ≤ k ≤ n).

### Idea
Concatenate the strings with a unique sentinel after each — sentinels must be lexicographically smaller than every real character *and* mutually distinct, so no suffix compares equal across a boundary. **As implemented:** every real character is shifted **up** by `41 + numStrings`, and the sentinels are the raw code points `41, 42, 43, …` (one per string) — cheap guaranteed separation without hunting for unused characters; the final answer is shifted back down before returning.

Build the suffix array + LCP over the combined string, then color each suffix by which source string it came from. **As implemented:** a suffix's color is found by scanning it for the *first sentinel character it contains* — the nearest following boundary identifies the owner (an O(length) scan per suffix, O(N²) total; the positional arithmetic the textbook uses would be O(1) per suffix).

Slide a window over the sorted ranks, skipping suffixes that begin with a sentinel: the substring shared by every suffix in a window has length equal to the minimum LCP strictly inside the window. Keep a `color → count` table; grow the window until it covers ≥ k distinct colors, then shrink from the left while recording candidates. The window-min LCP is the candidate length; keep the longest, recovered from the suffix at the window's left edge.

### Complexity
Dominated by the naive suffix-array build over the combined length N: O(N² log N) time, O(N²) memory. The window scan **as implemented** keeps the running minimum in the repo's own `Priority_queue` — whose `remove` is an O(size) scan — so the scan itself is up to O(N²) too (a monotonic deque or a sparse table over the LCP array would make it O(N), the standard upgrade).

### Notes
- The ≥ k colors test sorts the count table's values and checks that the k-th largest is nonzero.
- k = n is the all-strings case; k = 2 is "shared by any pair." If no window ever reaches k colors, return `""`.
- **Code flag (C2):** the "does this suffix start with a sentinel?" test compares against `'A' + 41 + numStrings`, which silently assumes every input character is ≥ `'A'` — digits, spaces, or punctuation below `'A'` would be misclassified as sentinels. Fine for the letter-only tests; a real fix compares against the true smallest shifted character (or uses positional coloring).


---

## Divide and conquer

A problem-solving technique: break a problem into smaller, independent subproblems of the *same type*, solve each (usually recursively), then combine their answers into the final result. It's recursion with a specific shape — split into multiple subproblems and merge.

### Idea
Three steps:
- **Divide** — split the input into subproblems. Often two halves, but it can be any number of parts (a *k-way* split into thirds, quarters, etc.).
- **Conquer** — solve each subproblem recursively; a base case handles inputs small enough to answer directly.
- **Combine** — merge the sub-results into the answer for the whole.

The combine step is what distinguishes divide-and-conquer from plain single-branch recursion: there are several subproblems and their results have to be reconciled (the merge in merge sort, the min-of-parts in Three_way_min, the cross-boundary case in maximum-subarray).

### Complexity
Cost follows a recurrence `T(n) = a·T(n/b) + f(n)`, where `a` = number of subproblems, `n/b` = subproblem size, and `f(n)` = divide + combine work. The **Master Theorem** solves the common shapes:
- Merge sort — `T(n) = 2T(n/2) + O(n)` → O(n log n).
- Binary search — `T(n) = T(n/2) + O(1)` → O(log n).
- Three-way min — `T(n) = 3T(n/3) + O(1)` → O(n) (implemented in `Problems/`, see `Problems_notes.md`).

Splitting more ways doesn't beat O(n) when every element must be examined (as in min/max) — the split count changes the recursion tree's shape, not the fact that all `n` leaves get visited. Divide-and-conquer wins big when the combine step lets you *discard* work (binary search) or when a clever merge beats the naive bound (Karatsuba, FFT).

### Applications
Merge sort, quicksort, binary search, maximum-subarray, closest pair of points, Karatsuba integer multiplication, Strassen matrix multiplication, and the fast Fourier transform.


---

## Merge_sort

Sort a 1D array with divide and conquer: split in half, sort each half recursively, then merge the two sorted halves.

### Idea
Maps directly onto divide / conquer / combine:
- **Divide** — split the range at the midpoint `(lo + hi) / 2`.
- **Conquer** — recursively merge-sort each half. Base case: a single-element range returns a fresh one-element array (a *copy*, so the caller's array is never aliased or touched).
- **Combine** — **merge** two sorted halves into one sorted array with a two-pointer walk: compare the current front of each half, take the smaller, advance that pointer; when one half runs out, copy the rest of the other. This merge is the whole trick — it turns two sorted pieces into one in linear time.

The recursion tree has `log n` levels, and each level does O(n) total merging work across all its pieces — that's the shape of the cost.

### Complexity
`T(n) = 2·T(n/2) + O(n)` → **O(n log n)** time in the worst, average, and best case (it always splits and always merges). Space is O(n) per level of live merge buffers — this version allocates a new array at every merge rather than reusing one shared temp buffer. It is **stable**: the merge takes from the left half on ties (`<=`, not `<`), so equal elements keep their original relative order.

### Notes
- **Stability hinges on that merge tie rule.** Not observable for bare `int`s, but it matters when sorting records by a key.
- **Top-down vs bottom-up.** This recursive form is top-down. Bottom-up merge sort iterates, merging runs of size 1, 2, 4, … — same O(n log n), no recursion stack.
- **Allocation.** Returning new arrays at each level is the clearest form; the standard optimization is one shared temp buffer allocated once. True in-place merge is possible but intricate and rarely worth it.
- **Non-destructive contract here:** the input array is provably unchanged after sorting (the runner verifies), because every level works on copies. Empty input returns a new empty array; null throws.
- **Generic version.** Swap `int[]` for `T[]` with `T extends Comparable<T>` (or a `Comparator`) and compare via `compareTo` — the structure is identical.


---

## Strassen_matrix_multiplication

*(Skeleton stage — written for the intended algorithm; reconcile against the code once implemented.)*

Multiply two n×n matrices in sub-cubic time — the classic "a cleverer combine step beats the naive bound" divide-and-conquer result.

### Idea
Split each matrix into four n/2 × n/2 quadrants. The naive block recursion computes the four result quadrants from **8** sub-multiplications — `T(n) = 8T(n/2) + O(n²)` → O(n³), no better than three loops. Strassen's insight: **7 carefully chosen products of quadrant sums/differences suffice**:

```
M1 = (A11 + A22)(B11 + B22)      M5 = (A11 + A12) B22
M2 = (A21 + A22) B11             M6 = (A21 − A11)(B11 + B12)
M3 = A11 (B12 − B22)             M7 = (A12 − A22)(B21 + B22)
M4 = A22 (B21 − B11)

C11 = M1 + M4 − M5 + M7          C12 = M3 + M5
C21 = M2 + M4                    C22 = M1 − M2 + M3 + M6
```

Trading one multiplication for a pile of O(n²) additions changes the recurrence to `T(n) = 7T(n/2) + O(n²)` → **O(n^log₂7) ≈ O(n^2.81)** — the same move as Karatsuba (3 products instead of 4 for integer multiplication). Non-power-of-two sizes: zero-pad up to the next power of two, multiply, crop — padding can't change the product, and at most doubles n (a constant factor).

### Complexity
O(n^2.81) time, O(n²) memory per recursion level for the temporaries (O(n² ) total, geometric series). In practice the constant factor is large: real implementations recurse only down to a **cutoff** (~64–128) and switch to the naive kernel below it — the skeleton's base-case hint.

### Notes
- **Verify against the naive multiplier, always** — the seven formulas are the easiest thing in this repo to mis-transcribe, and a single sign error still produces plausible-looking numbers. The runner cross-checks fixed constants *and* random matrices against an inline O(n³) reference.
- Sub-cubic ≠ numerically identical: with floats, Strassen's extra additions cost accuracy; with `long`s (this skeleton) results are exact but intermediate sums grow — entries up to ~√(MAX/n) are safe.
- Asymptotically better algorithms exist (Coppersmith–Winograd line, ω < 2.372) but are galactic — Strassen is the one that's actually used, and the pedagogical point: the Master Theorem's `a` (subproblem count) is a knob you can sometimes turn down.

---

## Huffman_encoding

*(Skeleton stage — written for the intended algorithm; reconcile against the code once implemented.)*

Build an optimal **prefix-free** binary code for a text: frequent characters get short codes, rare ones long — the flagship greedy algorithm.

### Idea
A prefix-free code (no codeword is a prefix of another) is exactly a **binary tree with characters at the leaves**: left = 0, right = 1, code = root-to-leaf path; prefix-freeness is "no character sits on the path to another." The cost to minimize is the weighted path length `Σ freq(c) · depth(c)` — the encoded bit total.

**Huffman's greedy:** put every character in a min-heap keyed by frequency; repeatedly pop the two lightest nodes, hang them under a fresh internal node weighing their sum, push it back. Last node = root. The exchange argument for optimality: the two rarest symbols can always be assumed to be sibling leaves at maximum depth (swapping them down never increases cost), and after merging them the problem recurses on one fewer symbol.

Encoding = concatenate codes; decoding = walk bits through the tree (or greedily match the code table — prefix-freeness makes the match unambiguous, no separators needed).

### Complexity
O(k log k) for k distinct characters (k−1 heap merges) after an O(n) frequency count; encode/decode O(output length). The repo's `Priority_queue` (comparator-ordered) is the natural heap here.

### Notes
- **The codes are not unique — the total length is.** Tie-breaks among equal weights flip siblings and reorder merges, producing different but equally optimal tables. Tests must therefore assert the *total encoded length* (unique, = the sum of the two popped weights over all merges), round-trip identity, and prefix-freeness — never specific bit strings.
- **Single-distinct-character edge case:** the tree is one leaf, path length 0 → an empty code encodes to nothing and can't be decoded. Convention: assign `"0"`.
- Huffman is optimal among per-symbol prefix codes given the frequencies; arithmetic coding beats it by encoding fractional bits, and adaptive Huffman drops the two-pass requirement — the upgrade paths, not this exercise.
- Greedy contrast worth internalizing: Huffman and Job_sequencing (in `Problems_notes.md`) both *prove* their greedy via exchange arguments — the difference between "greedy that works" and "greedy that feels right" (0/1 knapsack) is exactly whether that argument exists.

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
How you store the graph drives every algorithm's cost. Let `V` = vertices, `E` = edges.

- **Adjacency matrix** — a `V × V` grid; cell `(u, v)` holds the edge (presence/weight). Edge and weight lookup **O(1)**, but **O(V²)** memory and iterating all edges is **O(V²)**. Best for **dense** graphs.
- **Adjacency list** — each vertex stores a list of its outgoing edges. **O(V + E)** memory; iterating a vertex's neighbors is **O(degree)**; a specific edge lookup is **O(degree)**. Best for **sparse** graphs (the common case).
- **Edge list** — a flat list of `(u, v, w)` triples. Compact, natural for algorithms that sweep all edges (Kruskal), poor for "who are v's neighbors?".

## Problems & algorithms this unlocks
- **Shortest path** — BFS (unweighted), Dijkstra (non-negative weights), Bellman-Ford (handles negatives), Floyd-Warshall (all pairs), one-pass relaxation on DAGs.
- **Detecting negative cycles** — Bellman-Ford and Floyd-Warshall flag them.
- **Strongly connected components** — Tarjan's / Kosaraju's on digraphs.
- **Traveling salesman** — min-cost tour visiting every vertex (Held-Karp DP).
- **Bridges and articulation points** — a **bridge** is an edge whose removal increases the number of connected components; the vertex analogue is an **articulation point**. Found with a DFS low-link pass.
- **Minimum spanning tree** — cheapest edge set connecting all vertices (Kruskal, Prim).
- **Maximum flow** — most flow routable from source to sink (Ford-Fulkerson family / Dinic's).

The two representations are implemented in `Data_structures/` — full Positives/Negatives/Algorithm notes for them are in `Data_structures_notes.md`.

Most of these build on two traversals — **DFS** and **BFS** — so those come first.


---

## Depth_first_search

Traverse a graph by diving as deep as possible along each branch before backtracking. DFS is the workhorse behind connectivity, cycle detection, topological sort, bridges/articulation points, and strongly connected components.

### Idea
**As implemented: iterative, with the repo's own `Stack`, producing preorder.**

```
push start
while stack not empty:
    v = pop
    if v not visited:
        mark v visited; record v          # preorder = order of first visits
        push v's neighbors in REVERSE order
```

Two implementation choices carry the behavior:
- **Reverse-order pushing.** Popping is LIFO, so pushing neighbors high-to-low makes the *lowest* neighbor pop first — the traversal explores ascending neighbors exactly like the recursive form would.
- **Mark on pop, guard on pop.** A vertex can sit in the stack multiple times (once per discovering neighbor); the `if not visited` check on pop discards the duplicates. The stack can hold up to O(E) entries, but each vertex expands once and each edge pushes once, so the work is still O(V+E).

The **visited set** is essential regardless of style: without it, cycles loop forever and shared vertices reprocess.

Built on top of `preorder`:
- **reachable(start)** — run the traversal, mark everything it returned: the set reachable from `start`, verbatim.
- **componentCount()** — repeatedly launch `preorder` from a not-yet-covered vertex, merging each result into a global visited set, until every vertex is covered. Each launch is one **connected component** (meaningful for undirected graphs; for digraphs the analogous notion is SCCs, which need Tarjan's).

There is **no postorder function** in this file — reverse-postorder lives where it's needed, in `Topological_sort_dfs`.

### Complexity
O(V + E) — each vertex expands once, each edge is pushed once (twice for undirected, once per stored direction). O(V) for visited plus up-to-O(E) stack entries in the duplicate-tolerant style.

### Notes
- **Iterative vs recursive:** same order, same cost; the explicit stack sidesteps stack-overflow on deep graphs (V ~ 10⁵ paths), which is exactly why this form was chosen.
- **DFS vs BFS.** Both O(V+E), both visit everything reachable; they differ in *order*. DFS (stack — deep first) feeds topological sort, cycle detection, low-link algorithms. BFS (queue — level by level) gives shortest paths in **unweighted** graphs; DFS does not.
- **Determinism.** Ascending neighbor visits make runs reproducible — currently guaranteed by the reverse-push plus how tests build graphs (see the adjacency-list ordering flag).


---

## Breadth_first_search

Traverse a graph level by level: the start, then everything one edge away, then two, and so on. Because it reaches vertices in order of increasing distance, BFS gives **shortest paths in unweighted graphs** — and recording where each vertex was first seen lets you rebuild the path.

### Idea
**As implemented — a mark-on-dequeue variant with a parent guard:**

```
parent[*] = -1;  parent[start] = -2          # -2 marks the ROOT; -1 = undiscovered
enqueue start
while queue not empty:
    v = dequeue
    if v not visited:
        mark v visited; record v in order
        for each neighbor w of v (list order):
            if parent[w] == -1: parent[w] = v   # first discovery wins
            enqueue w                            # enqueued unconditionally
```

The textbook discipline is to mark visited **on enqueue** so each vertex enters the queue once; this code marks **on dequeue** instead and lets duplicates into the queue. It stays correct for shortest paths because of the `parent[w] == -1` guard: a vertex's parent is set the *first* time any processed vertex sees it, and processed vertices leave the queue in nondecreasing distance order — so the recorded parent always lies on a shortest path. The price is a fatter queue (up to O(E) entries) and redundant pops; total work is still O(V+E). Worth knowing both styles: mark-on-enqueue is the leaner discipline, this one trades queue size for a simpler loop.

**There is no `dist[]` array.** Distance and path both come from the parent chain:
- **shortestPath(start, target)** — run the BFS, then walk `parent` backward from `target` until hitting the `-2` root marker, and reverse. `start == target` short-circuits to `[start]`; an undiscovered target (`parent == -1`) returns an empty array (no path).
- **shortestPath_compute_once** — the same backward walk *without* re-running the BFS; it reuses the `parent` state left by a prior `order()` call (the parent array is a static field — see notes).
- **distances(start)** — runs the BFS once, then for every vertex reconstructs its path and returns `length − 1`, with **−1 as the unreachable sentinel** (empty path → 0 − 1). Reconstructing per-vertex makes this **O(V²)** worst case (sum of all path lengths) — a `dist[]` filled during the BFS would be the O(V+E) version.

### Complexity
O(V + E) time for the traversal itself (each edge enqueues once, duplicates pop in O(1)); O(E) worst-case queue. `distances()` is O(V²) worst case as described.

### Notes
- **`parent[start] = -2`** is the root marker and the stop condition of every backward walk; `-1` strictly means "never discovered."
- **Shortest paths hold only for unweighted graphs** (or all-equal weights). BFS counts *edges*; with varying weights you need Dijkstra. The "first time reached = shortest" guarantee comes from level-by-level FIFO order.
- **Ties.** When several shortest paths exist, which one you get depends on neighbor order and first-parent-wins. All are equally short; the length is unique even though the path isn't.
- **Static state (C9):** `parent` is a static field shared across calls — `shortestPath_compute_once` silently depends on which `order()` ran last, and nothing is thread-safe. Fine for a study repo; a returned parent array would be the cleaner API.
- **BFS vs DFS.** Same O(V+E), opposite order: queue (wide) vs stack (deep). BFS → unweighted shortest paths, level structure, bipartite checks. DFS → topo sort, cycle detection, SCC. Reach for BFS whenever "fewest edges" or "closest first" matters.


---

## Root_tree

Turn an undirected tree (adjacency lists) into a rooted `Tree_node` structure by choosing a root and orienting every edge away from it.

### Idea
An undirected tree has no inherent parent/child direction — that appears only once you pick a root. **As implemented: iterative, with the repo's `Stack`, belt-and-suspenders guards.**

```
push new Tree_node(root, null)
while stack not empty:
    node = pop
    if node.id not visited:
        mark visited
        for each neighbor w of node.id:
            if node has a parent and w == parent.id: continue    # don't walk back up
            child = new Tree_node(w, node); node.children.add(child); push child
```

Children are attached at push time in adjacency order, so a node's `children` list mirrors its neighbor list (minus the parent) regardless of pop order. The root's parent is `null`, hence the `parent != null` guard before the skip test.

### Complexity
O(n) — each vertex and edge touched once. The explicit stack replaces recursion depth (a path-shaped tree would be O(n) deep).

### Notes
- **Two guards where one would do:** in a genuine tree, the skip-the-parent check alone suffices (no other cycles exist) and each node is pushed exactly once — the `visited[]` array is redundant. It's cheap insurance: on malformed input (a cycle), it prevents infinite work, though the resulting "tree" would still contain duplicate ids. Contract says tree; the guard just makes failure graceful.
- **Re-rooting the same tree gives a different structure.** Parent/child relationships are relative to the root; rooting at a leaf vs. the center produces different shapes of the same underlying tree.
- Validation: null adjacency throws, empty graph rejected, root bounds-checked.


---

## Leaf_node_sum

Sum the ids of all leaves in a rooted tree (a leaf is a node with no children).

### Idea
A post-order recursion: a node with no children is a leaf and contributes its own id; otherwise it contributes the summed contributions of its subtrees.

```
sum(node):
    if node is a leaf: return node.id
    return sum over children of sum(child)
```

### Complexity
O(n) time, O(height) stack. Sums in `long`.

### Notes
- **A single-node tree is a leaf** — its root has no children, so it contributes its own id. Handle that base case; don't assume "root = internal."
- Trivially adapts to summing a stored value, counting leaves, or finding the deepest leaf — same tree walk, different per-node contribution.


---

## Tree_center

Find the 1 or 2 vertices at the middle of an undirected tree (the center of its longest path).

### Idea
Peel leaves inward. Repeatedly remove every current leaf (degree ≤ 1) as a whole layer, decrementing neighbors' degrees so new leaves surface, until at most 2 vertices remain. The survivors are the center(s) — a tree always has exactly one or two, depending on whether its longest path has an odd or even number of vertices.

**As implemented:**
```
degree[v] = adj[v].size();  leaves = all v with degree <= 1;  removed = leaves.size()
while removed < n:
    next = []
    for each leaf: for each neighbor: degree[neigh]--
        if degree[neigh] <= 1 and not already processed and not already in next: next.add(neigh)
    mark this layer processed;  leaves = next;  removed += next.size()
return leaves
```

The `processed[]` flag plus the not-already-queued check stop a vertex from entering a layer twice when several of its leaf-neighbors peel simultaneously.

### Complexity
O(n) peel overall — each vertex leaves the frontier once. (One implementation nit: the `newleaves.contains` duplicate check is a linear scan of the layer; a boolean "queued" array would keep the layer-building strictly O(degree). Correctness unaffected.)

### Notes
- **Always 1 or 2 centers.** Odd-vertex longest path → one; even → two adjacent. Never zero, never three.
- **Small trees are base cases handled by the loop condition:** `n == 1` → the lone vertex (which enters `leaves` via the `degree == 0` case); `n == 2` → both. Neither enters the while loop.
- This is the "trim leaves layer by layer" idea (a.k.a. minimum-height-trees) — the center minimizes tree height when chosen as root, which is exactly why Tree_isomorphism roots there.


---

## Tree_isomorphism

Decide whether two undirected trees have the same shape, ignoring vertex labels.

### Idea
Use the **AHU (Aho–Hopcroft–Ullman) canonical encoding**. First reduce the unrooted problem to a rooted one by rooting each tree at its **center** — isomorphic trees have corresponding centers, so this anchors the comparison label-independently. Then encode each rooted tree canonically:

```
encode(node):
    labels = [ encode(child) for each child ]
    sort labels                       # sibling order must not matter
    return "(" + concat(labels) + ")"
```

A leaf encodes as `"()"`. Two rooted trees are isomorphic **iff their encodings are identical** — sorting the child labels at every node is what makes the string independent of arbitrary neighbor order.

**Handling two centers, as implemented:** tree A is rooted at its *first* center only and encoded once; tree B is rooted at *each* of its centers in turn, and the trees match if either of B's encodings equals A's. (Trying both on one side suffices — which center corresponds to which isn't known in advance, but A's first center must map to *one* of B's.)

### Complexity
Center + rooting are O(n). Encoding sorts child label strings at every node — O(n log n) comparisons, up to O(n²) characters in the concatenation worst case (long path); fine at these sizes.

### Notes
- **Different sizes → immediately not isomorphic** (checked first); two empty trees count as isomorphic.
- **Root at the center, not an arbitrary vertex.** Rooting both at vertex 0 would compare structure relative to labels — wrong.
- **Sorting siblings is essential.** Without it, the same tree with children listed in a different order produces a different string and falsely reads non-isomorphic.
- Verified pairs in the runner: relabeled A ≅ B, A ≇ path, star ≇ path, single ≅ single.


---

## Lowest_common_ancestor

The lowest common ancestor of `u` and `v` in a rooted tree is the deepest node that is an ancestor of both.

### Idea
**As implemented: Euler tour + sparse-table range-minimum — O(1) per query after O(n log n) preprocessing.** (This is the many-queries method; the simple "equalize depths and climb parents" alternative is sketched in the notes below.)

The classical reduction: LCA on a tree **is** a range-minimum query on its Euler tour.

**Preprocessing (constructor):**
1. Root the tree with `Root_tree`.
2. DFS the rooted tree recording an **Euler tour**: visit the node on entry, and again after *each* child returns. Two parallel lists grow together — `nodes` (the tour) and `depth` (that visit's depth) — plus a `last[id] = tour index` map recording each node's most recent appearance (any occurrence works; last is what the code keeps).
3. Build the repo's `Sparse_table` over the `depth` list with `Integer::min` — its index-tracking argmin variant (`queryOverlap_index_min_operation_only`) exists precisely for this.

**Query `lca(u, v)`:** take `l = min(last[u], last[v])`, `r = max(...)`. Between any occurrence of `u` and any occurrence of `v`, the tour walks down and up through their meeting point — the **shallowest node in that tour window is exactly the LCA**. One O(1) argmin over `depth[l..r]`, then read `nodes[that index].id`.

### Complexity
O(n) tour (length 2n−1) + O(n log n) sparse-table build; **O(1) per query**. Preprocessing memory O(n log n).

### Notes
- **Why RMQ works:** the tour enters the LCA's subtree before touching `u` or `v` and can't leave it between them, and the LCA is the unique minimum-depth vertex the walk passes through on the way from one to the other. Idempotent min → the sparse table's two overlapping blocks answer it in O(1).
- **Ancestry is relative to the root.** Re-rooting changes every LCA. Root once (constructor), query many — the shape this class enforces.
- `lca(u, u) == u` falls out for free (a window of width... `l == r` — the argmin of one cell is that cell). Out-of-range ids throw.
- **The zero-structure alternative** (record `parent[]` + `depth[]`, lift the deeper node, then climb in lockstep) is O(height) per query with no extra machinery — the right tool when queries are few; **binary lifting** (O(log n)/query after O(n log n)) sits between the two.
- The DFS recursion is O(height) deep — a path-shaped tree recurses n deep; fine at test sizes.


---

## Topological_sort_dfs

A topological ordering of a DAG lists its vertices so that every edge `u → v` has `u` before `v`. (Only directed *acyclic* graphs have one.)

### Idea
DFS; a vertex is emitted when it **finishes** (after all its descendants are done), and reverse-finish-order is a topological order: a vertex finishes only after everything reachable from it, so reversing puts it before them.

**As implemented:**
```
for each unvisited vertex i: dfs(i, launch = i)
dfs(v, launch):
    if v == launch and v is visited: throw     # cycle returned to the launch vertex
    if v visited: return
    mark v visited
    for each out-neighbor w: dfs(w, launch)
    order.add(0, v)                            # PREPEND on finish
```

Two departures from the textbook sketch:
- **Prepend instead of reverse.** Emitting each finished vertex at the *front* of the list yields the topological order directly — no reversal step to forget. The cost: `ArrayList.add(0, ·)` shifts the whole list, so order-building is O(V²) in the worst case (an `ArrayDeque.addFirst`, or append + reverse, is the O(V) version).
- **Cycle detection is only partial — code flag (B4).** The only check is "the recursion returned to the very vertex this DFS was launched from." A cycle that doesn't pass through a launch vertex is missed entirely: `0→1→2→1` quietly returns `[0, 1, 2]` even though no valid ordering exists. Full detection needs the third color — *in-progress* (on the current recursion stack) vs *done* — where any edge to an in-progress vertex is a back edge ⇒ cycle. Two colors genuinely aren't enough: an edge to a *done* vertex is a harmless cross/forward edge; only the on-stack case is a cycle.

`sort_with_start_node(g, start)` runs the launch loop but starts with a preferred vertex first — used by the DAG shortest-path code so the source's reachable region is ordered before the strays; remaining unvisited vertices still get launched afterward, so the order always covers all V vertices.

### Complexity
O(V + E) traversal + O(V²) worst-case list prepending. O(V) recursion depth (deep DAGs can overflow the stack; Kahn's is the iterative escape).

### Notes
- The output isn't unique; any order respecting all edges is valid — runners should verify the edge property, not one fixed sequence.
- State is static (`order`, `visited`) — one sort at a time (C9).
- **DFS-topo vs Kahn's:** same O(V+E), same "any valid order." This one is terser and naturally produces the reverse-postorder that SCC algorithms also use; Kahn's is iterative (no stack-depth risk), detects *all* cycles for free, and its intermediate state (currently-available vertices) is meaningful.


---

## Topological_sort_kahn

Same goal — a topological order of a DAG — built with in-degrees and a queue instead of recursion.

### Idea
A vertex can come next exactly when it has no remaining incoming edges. Compute every vertex's **in-degree** (one sweep over all adjacency lists), seed a queue with the in-degree-0 vertices, then repeatedly dequeue one, append it to the order, and "remove" its outgoing edges by decrementing each out-neighbor's in-degree — enqueuing any that drop to 0.

```
compute indeg[v] for all v
queue = all v with indeg[v] == 0
while queue not empty:
    v = dequeue; order.add(v)
    for each out-neighbor w: indeg[w]--; if indeg[w] == 0: enqueue w
if order.size() < V: cycle -> THROW IllegalArgumentException
```

### Complexity
O(V + E) — each vertex enqueued once, each edge relaxed once. O(V) for the queue and in-degree array. Fully iterative — no recursion-depth risk.

### Notes
- **The count is the cycle test — and here it catches *every* cycle.** Vertices trapped in a cycle never reach in-degree 0, so the loop emits fewer than V and the code throws (contrast with the DFS version's partial detection, flag B4). No coloring needed.
- **Determinism:** swapping the plain queue for a min-priority queue yields the lexicographically smallest valid order, if a canonical one is ever needed.
- Uses the repo's own `Queue`; state is static (C9).
- **Code flag (C10):** this file also contains a full copy-paste duplicate of `Root_tree.rootTree` — a straggler from a working session; delete it and import the real one.


---

## Dag_shortest_longest_path

Single-source shortest (or longest) path in a **weighted DAG**. Because the graph is acyclic, one relaxation pass in topological order solves it — no priority queue, no repeated passes, and negative weights are fine.

### Idea
Two steps:
1. **Topologically sort**, via `Topological_sort_dfs.sort_with_start_node(g, source)` — the source-first variant, so the source's region is ordered before unreachable strays (which still appear in the order and must be skipped during relaxation).
2. **Relax edges in topo order.** `dist[source] = 0`, everything else at a sentinel; walk vertices in order, relaxing each outgoing edge `u → v` with `dist[u] + w < dist[v]`.

Why one pass works: in topological order, **every edge into `u` comes from a vertex that appears earlier**, so by the time `u` is processed, `dist[u]` is final. That's the whole payoff of acyclicity.

**Longest paths, as implemented: the negation trick.** `longestPaths` negates every edge weight, runs the same shortest-path relaxation, and negates the results at the end. (The equivalent alternative — flip the comparison and start from −∞ — isn't what this code does.) A convenient accident makes the sentinel survive: `UNREACHABLE_LONGEST = Long.MIN_VALUE`, and `MIN_VALUE * −1` overflows back to `MIN_VALUE`, so untouched vertices keep their sentinel through the final negation.

### Complexity
O(V + E) — topo sort plus one pass over every edge. Beats Dijkstra's O((V+E) log V) and, unlike Dijkstra, tolerates negative weights (a DAG can't contain a negative cycle — it can't contain any cycle). Distances accumulate in `long`.

### Notes
- **Only for DAGs.** With a cycle no topological order exists. Caveat inherited from flag B4: the underlying DFS topo sort only *throws* for cycles through a launch vertex — other cyclic graphs will be silently "ordered" and produce garbage distances. Kahn's underneath would make the not-a-DAG rejection airtight.
- **Skip unreachable vertices during relaxation.** `shortestPaths` does this correctly (`continue` when `dist[u]` is the sentinel — adding a weight to "infinity" is meaningless and overflows).
  **Code flag (B5):** `longestPaths` is missing that guard. `Long.MIN_VALUE + weight` wraps to a huge positive, and the `|| dist[neigh] == UNREACHABLE_LONGEST` clause then happily installs the garbage — so an unreachable vertex fed by another unreachable vertex reports a bogus finite "longest path" instead of the sentinel (reproduced: source 0 with a disconnected edge 1→2 yields −9223372036854775803 at vertex 2). One `continue` fixes it.
- **Use `long` for distances** and keep sentinels out of arithmetic — the two rules this pair of methods demonstrates by having one follow them and one not.
- Verified in the runner: shortest from 0 → `[0,2,3,9,6,8,∞]`, longest → `[0,2,4,9,10,14,−∞]`; a negative-weight DAG routes through the −4 edge.


---

## Dijkstra

Single-source shortest paths on a graph with **non-negative** edge weights.

### Idea
Greedily settle vertices in increasing distance order. Keep tentative distances; repeatedly pop the unsettled vertex with the smallest tentative distance from a priority queue, finalize it, and relax its outgoing edges. Once a vertex is popped it's done — its distance can never improve, **because all weights are non-negative**, so no later, longer-prefix path can undercut it.

**As implemented** — `java.util.PriorityQueue` over `(vertex, dist)` pairs ordered by distance, with both a lazy-deletion skip *and* a visited array:

```
dist[source] = 0, rest = Long.MAX_VALUE (UNREACHABLE);  pq = {(source, 0)}
while pq not empty:
    (u, d) = pop-min;  visited[u] = true
    if dist[u] < d: continue                 # stale entry, skip
    for each edge u->v:  if visited[v]: continue
        if dist[u] + w < dist[v]: dist[v] = dist[u] + w; push (v, dist[v])
```

### Complexity
O((V + E) log V) with the binary heap. An indexed PQ with decrease-key (the repo's `Indexed_priority_queue` fits exactly) trims heap size from O(E) to O(V); "push duplicates, skip stale" is simpler and asymptotically the same.

### Notes
- **Non-negative weights only.** A single negative edge breaks the settled-is-final invariant — Bellman-Ford handles those.
- **Lazy deletion:** rather than decrease-key, push a fresh `(v, dist)` on every improvement and discard a popped entry whose stored distance is worse than the current `dist[u]`. The `visited[]` skip on neighbors is belt-and-suspenders — the stale check alone suffices; skipping visited neighbors just avoids pointless pushes.
- Unreachable vertices keep the `Long.MAX_VALUE` sentinel; distances accumulate in `long` so sentinel + weight is never computed (relaxation only proceeds from popped, hence reachable, vertices).


---

## Bellman_ford

Single-source shortest paths that tolerates **negative** weights and detects negative cycles.

### Idea
A shortest path uses at most `V−1` edges, so relaxing **every edge** `V−1` times settles all finite shortest distances (each pass extends the correct frontier by at least one edge). Then run `V−1` **more** passes: any edge that can *still* relax means its target is reachable from a negative cycle — mark those `NEGATIVE_INF` and let the marker propagate (a vertex fed by a −∞ vertex is also −∞, hence full sweeps, not one pass).

**As implemented:** edges are iterated as "for every vertex, for every out-neighbor" over the `Graph` interface (adjacency form of the same edge sweep). Two methods share the skeleton:
- `shortestPaths` — phase 1 relaxes with a `dist[u] != UNREACHABLE` guard; phase 2 marks `NEGATIVE_INF` where improvement is still possible.
- `hasNegativeCycle` — same two phases, returning whether phase 2 ever fired.

### Complexity
O(V·E) — `V−1` sweeps twice. Slower than Dijkstra; that's the price of negatives.

### Notes
- **Guard every relaxation against the sentinels** — `UNREACHABLE + w` overflows `long` and leaks a bogus finite distance. This is the rule; the implementation applies it unevenly:
  **Code flag (B6):** `shortestPaths`' **phase 2** has no `UNREACHABLE` guard — `MAX_VALUE + w` wraps negative and "improves," so a merely-unreachable vertex fed by another unreachable vertex gets falsely branded `NEGATIVE_INF` (reproduced with a disconnected edge 1→2, source 0). Mirror problem in `hasNegativeCycle`: its **phase 1** lacks the guard, so wrapped values contaminate distances there instead — and on the same graph the two methods disagree (`shortestPaths` shows a −∞; `hasNegativeCycle` says false). Both fixes are the same one-line guard phase 2 of `hasNegativeCycle` already has in spirit.
- **Two-phase detection** is the elegant part: "still improving after V−1 passes" *is* the negative-cycle witness, and running the marking phase V−1 times is what propagates −∞ to everything downstream of the cycle.
- **Early exit optimization** (stop when a full pass changes nothing) isn't implemented — worth adding; it makes the common no-negative-cycle case much faster.


---

## Floyd_warshall

**All-pairs** shortest paths; handles negative edges and detects negative cycles.

### Idea
Allow paths to route through intermediate vertices `0, 1, …, k` one at a time. After incorporating intermediate `k`, `dp[i][j]` is the shortest path using only intermediates from `{0..k}`. The update: could `i → k → j` beat the current `i → j`?

```
dp[i][j] = 0 on the diagonal, weight(i,j) if the edge exists, else UNREACHABLE
for k: for i: for j:
    if dp[i][k], dp[k][j] both finite and dp[i][k] + dp[k][j] < dp[i][j]:
        dp[i][j] = that sum
```

**k must be the outermost loop** — the DP relies on `dp[i][k]` and `dp[k][j]` being final for the current `k` before moving on.

**Negative-cycle handling, as implemented:** after the main triple loop, run the **same triple loop again**; any cell that can *still* improve lies on a path through a negative cycle → set it to `NEGATIVE_INF`. (The equivalent classic check — `dp[i][i] < 0` flags vertex `i` on a negative cycle — isn't what this code uses; the rerun-and-detect-improvement sweep subsumes it and directly marks affected *pairs*.)

### Complexity
O(V³) time (twice, with the detection sweep), O(V²) space. Beats running single-source algorithms from every vertex once the graph is dense, and it's dead simple to code.

### Notes
- **Overflow guard:** both loops skip when either operand is `UNREACHABLE` — the sentinel discipline done right here (contrast Bellman-Ford's flag B6).
- **Code flag (C10):** the code also fills a `path[][]` next-vertex matrix for route reconstruction (`path[i][j] = path[i][k]` on improvement, −1 through negative cycles) — but never returns or exposes it. Dead weight: either add a `reconstructPath(i, j)` or drop the matrix.
- **Code flag (B9):** `hasNegativeCycle`'s initialization is missing an `else` — `if (i==j){dp=0} if (hasEdge){...} else {UNREACHABLE}` — so a diagonal cell without a self-loop is set to 0 and immediately overwritten with `UNREACHABLE`. `allPairsShortestPaths` has the correct `else if` chain; make them match.


---

## Tarjan_scc

Strongly connected components of a **directed** graph in a single DFS. An SCC is a maximal set of vertices where every vertex can reach every other.

### Idea
DFS the graph, assigning each vertex an increasing **discovery index** and a **low value** — the smallest index reachable from its DFS subtree using edges into vertices still on an auxiliary **stack**. Push each vertex on first visit. When a vertex `u` finishes with `low[u] == index[u]`, it's the **root** of an SCC: pop the stack down to and including `u` — those vertices are exactly one component.

**As implemented:**
```
dfs(u):
    ids[u] = low[u] = counter++;  push u;  onStack[u] = true
    for each edge u->v:
        if v unvisited: dfs(v)
        if onStack[v]: low[u] = min(low[u], low[v])     # one rule for tree AND back edges
    if ids[u] == low[u]:                                # u roots an SCC
        pop the stack down through u; each popped vertex gets low[] = low[u]; off-stack them
```

Two things worth naming precisely:
- **One min rule, not two.** The textbook formulation distinguishes tree edges (`min` with the child's *low*) from back edges (`min` with the neighbor's *index*). This implementation uses `min(low[u], low[v])` for **any on-stack neighbor** — the simplified variant (Fiset's, among others). It is **correct for identifying SCCs**: an on-stack neighbor's low always names some vertex in the same still-open region, so components pool to the right roots — though the resulting `low` values aren't textbook low-links (they can undershoot). If you later build *bridges/articulation points*, the distinction returns with teeth: there, using `low` where `index` belongs genuinely breaks the algorithm.
- **`onStack` is the load-bearing guard.** A visited neighbor already assigned to a *finished* SCC must be ignored — otherwise you'd merge components that aren't mutually reachable.

The public API: `sccIds` returns the `low` array after all DFS launches — every member of a component holds its root's discovery index, so equal value ⇔ same component. `sccCount` counts the roots.

### Complexity
O(V + E) — one DFS, optimal, single pass (unlike Kosaraju's two). O(V) for the arrays plus recursion depth.

### Notes
- **Components complete in reverse topological order** of the condensation (the DAG of SCCs). The specific ids are arbitrary — test the *partition* (same-id groups), not the numbers.
- A DAG has V singleton SCCs; a single directed cycle is one SCC of size V. Runner-verified: `{0,1,2},{3,4},{5}` for the linked-cycles graph; DAG → singletons; ring → one component.
- Uses the repo's own `Stack`; state is static (C9); deep graphs recurse deep.


---

## Articulation_points

*(Skeleton stage — written for the intended algorithm; reconcile against the code once implemented.)*

Find the **cut vertices** of an undirected graph — vertices whose removal increases the number of connected components — in one low-link DFS.

### Idea
Same instrument family as Tarjan SCC (discovery indices + low values), different tune — and here the detail Tarjan's SCC variant blurs comes back with teeth. For undirected graphs define `low[v]` = the smallest discovery index reachable from v's DFS subtree using tree edges plus **at most one back edge**. Then:

- **Non-root v is an articulation point iff some DFS child c has `low[c] >= ids[v]`** — c's subtree cannot climb strictly above v, so every path from that subtree to the rest of the graph passes through v.
- **The DFS root is a special case:** it's an articulation point **iff it has ≥ 2 DFS children** (its low-link test is vacuous — everything is below it). Note: DFS children, not degree — a root inside a triangle has degree 2 but one DFS child, and is not a cut vertex.

Updates during the DFS: tree edge → `low[v] = min(low[v], low[child])` after recursing; back edge to visited w (≠ the parent you arrived from) → `low[v] = min(low[v], ids[w])` — **`ids[w]`, not `low[w]`**. Using `low` on back edges lets reachability "teleport" through vertices more than once and silently erases cut vertices; this is exactly the tree-vs-back-edge distinction the SCC note said would return with teeth.

Disconnected graphs: launch a DFS per component, each launch its own root with its own root rule. The bridge analogue differs by one character — `low[c] > ids[v]` (strict) marks edge (v, c) a **bridge**.

### Complexity
O(V + E), one DFS pass, O(V) arrays + recursion depth.

### Notes
- **The `>=` vs `>` pair is the exam question:** `>=` for cut vertices (the child may climb back *to* v — v is still the bottleneck), `>` for bridges (climbing back to v means the edge itself is bypassable).
- **Parent handling:** skip only the edge you arrived by. In a simple graph, skipping the parent's id suffices; with parallel edges you'd skip by edge id instead.
- Independent verification is cheap and worth it: v is a cut vertex iff deleting it raises the component count — the runner cross-checks every case (and random graphs) against that O(V·(V+E)) brute force.
- Applications: network single-points-of-failure, biconnected-component decomposition (the regions between cut vertices), and robustness analysis before adding redundancy.

---

## Eulerian_path

**Problem.** Find a trail through a directed graph that uses **every edge exactly once** (an Eulerian path), or determine none exists. Start == end makes it an Eulerian **circuit**. Vertices may repeat; edges may not.

### Idea
Two parts: **check existence** with degree conditions, then **build** the trail with Hierholzer's algorithm.

**Existence (directed).** Compute in/out-degree per vertex (one sweep; the out-degree array will double as the edge pointer). An Eulerian path exists iff at most one vertex has `out − in == +1` (the **start**), at most one has `in − out == +1` (the **end**), every other vertex is balanced, and all edges lie in one connected piece (checked after the walk — below). A lone start without an end (or vice versa), or any vertex off by more than one, rejects immediately. All balanced → circuit.

**Build — as implemented, recursive Hierholzer consuming each list from the back:**
```
dfs(v):
    while out[v] > 0:
        next = adj[v][ out[v] - 1 ]      # out-degree doubles as the edge pointer
        out[v]--                          # consume that edge forever
        dfs(next)
    order.add(0, v)                       # emit on dead-end, PREPENDED
```
Each vertex's remaining out-degree points one past its next unused edge, so edges are taken from the **end of the adjacency list backward** and never rescanned — that monotone pointer is what keeps the walk linear. Emitting a vertex only when it's stuck (post-order) and building the path front-first correctly splices sub-loops together; prepending replaces the usual append-then-reverse.

**Connectivity, as implemented:** after the walk, if **any vertex still has unused out-edges**, the degree conditions held but the edges spanned more than one component — return `[]`. (Equivalent to the `path length == E + 1` test, checked from the other side.) A graph with zero edges returns `[]` up front.

### Complexity
O(V + E) — each edge consumed exactly once, each vertex stuck a bounded number of times. Caveats: `order.add(0, ·)` on an ArrayList is O(path) per emit → O(E²) worst case for the list-building alone (an ArrayDeque prepend fixes it), and the recursion goes as deep as the longest unstuck walk (up to E frames).

### Notes
- **Pick the right start.** A genuine path *must* start at the `out−in == +1` vertex — starting elsewhere strands edges. For a circuit, any vertex **with an out-edge** works.
  **Code flag (B7):** the circuit case hard-codes `start = 0`. If vertex 0 happens to be isolated while a valid circuit lives elsewhere (isolated 0, cycle 1→2→1), the walk consumes nothing and the leftover-edges check rejects a graph that *has* an Eulerian circuit (reproduced). Fix: pick any vertex with `out > 0`.
- **Multi-edges and self-loops are fine** — duplicates in the adjacency list are just multiple edges; the countdown pointer treats each entry as one edge.
- **Undirected variant** swaps the degree rule (path iff 0 or 2 vertices of **odd** degree) and needs each undirected edge marked used from both endpoints; the Hierholzer skeleton is otherwise the same.


---

## Prim_minimum_spanning_tree

**Problem.** Given a connected, weighted, undirected graph, find a **minimum spanning tree** — an edge subset connecting all vertices, acyclic, of smallest total weight.

### Idea
Grow one tree outward. Maintain the set of vertices already in the tree; repeatedly take the **cheapest edge crossing the boundary** (in-tree to out-of-tree), which pulls exactly one new vertex in. A priority queue supplies that minimum crossing edge.

**Lazy Prim, as implemented** (fixed start at vertex 0, `java.util.PriorityQueue` of `(from, to, weight)` ordered by weight):
```
visited[0] = true;  push all edges leaving 0
while pq not empty:
    (from, to, w) = pop-min
    if visited[to]: continue              # stale edge into the tree — discard
    visited[to] = true;  record the edge;  weight += w
    push to's edges leading to unvisited vertices
if any vertex unvisited: disconnected -> weight = NO_SPANNING_TREE, edges = empty
```

**Why the greedy choice is safe (cut property):** for any split of the vertices into in-tree vs out, the minimum-weight crossing edge belongs to some MST. Prim applies this repeatedly to the growing boundary.

**Output shape, as implemented:** the recorded edges are normalized (`u < v` per edge) and sorted by endpoints, so `minimumSpanningEdges` returns a canonical, deterministic `[u, v, w]` list; `minimumSpanningWeight` runs the same routine and reports the accumulated weight — or the `NO_SPANNING_TREE` sentinel (`Long.MAX_VALUE`) for disconnected input. Trivial graphs (0 or 1 vertex) return an empty edge set with weight 0.

### Complexity
Lazy Prim: O(E log E) — every edge may enter the heap once. **Eager Prim** with an indexed PQ (decrease-key: keep only the best crossing edge per outside vertex) is O(E log V) with O(V) heap — the repo's `Indexed_priority_queue` is the exact upgrade part. (No `edgesUsed == n−1` early exit here; the heap just drains — same asymptotics.)

### Notes
- **Skip stale edges** — the lazy heap's analogue of Dijkstra's skip-stale-entry.
- **Disconnected graphs have no spanning tree** — sentinel, not a partial-tree weight.
- **Prim vs Kruskal.** Both ride the cut/cycle properties. Prim grows one tree with a heap (dense-friendly, natural with adjacency lists); Kruskal sorts all edges and unions with union-find (sparse/edge-list-friendly). Same total weight; edge sets can differ under ties.
- **Ties → MST not unique.** Test the *total weight* (and that the returned edges form a spanning tree achieving it), not one specific edge set — the canonical sorted output makes exact-match tests tempting, resist it where ties exist.


---

## Ford_fulkerson

**Problem.** In a flow network — a directed graph where each edge has a capacity — push as much flow as possible from a **source** to a **sink**, respecting capacities and conserving flow at every other vertex (in = out). Return the maximum total flow.

### Idea
Repeatedly find a source→sink path with spare capacity (an **augmenting path**) and push flow along it, until none remains. The key mechanism is the **residual graph**: alongside each edge's remaining forward capacity, a **reverse residual edge** lets a later path *cancel* earlier flow if that frees a better routing.

**As implemented — generic Ford-Fulkerson with DFS path-finding on an `int[][]` residual matrix:**
```
residual = deep copy of the capacity matrix
while dfs(source, INF) returns f > 0:  maxflow += f;  bump the visit stamp

dfs(node, flow):                        # flow = bottleneck so far
    if node == sink: return flow
    stamp node visited
    for each i with residual[node][i] > 0 and i unstamped:
        f = dfs(i, min(flow, residual[node][i]))
        if f > 0:
            residual[node][i] -= f;  residual[i][node] += f     # forward - , reverse +
            return f
    return 0
```
The bottleneck is threaded *down* the recursion as `min(flow, edge)` and the residual updates happen on the *unwind* — one pass per augmentation, no separate find-then-apply walk. The per-round visited set uses an **integer stamp** (`visited[v] == round#`) so nothing is cleared between rounds. A matrix representation makes reverse residual edges automatic: `residual[v][u]` always exists as a slot, starting at 0 and growing as flow is pushed.

### Complexity
Generic Ford-Fulkerson: **O(E · maxflow)** augmentations-wise — each augmentation raises the flow by ≥ 1 with integer capacities, and a bad DFS order can take that many rounds. On the matrix, each DFS is O(V²), so O(V² · maxflow) here. Fine for small integer capacities (like the bipartite problems below); Edmonds-Karp's BFS choice is what removes the capacity dependence.

### Notes
- **The reverse residual edge is the whole trick.** `residual[i][node] += f` is what lets a later path *undo* a suboptimal earlier commitment; without it a greedy first path can strand the network below the true maximum (the runner's "needs residual reroute" case exercises exactly this).
- **Any augmenting path works for correctness** with integer capacities — the choice of path only affects speed. DFS (here) is the simplest; BFS-shortest is Edmonds-Karp; wide-paths-first is capacity scaling; blocking flows are Dinic's. Irrational capacities can make generic FF fail to terminate — pathological, but the classical reason the refinements exist.
- **Max-flow min-cut theorem:** the max flow equals the capacity of the minimum source/sink cut; after the final failed search, the vertices still reachable from the source in the residual graph form one side of a min cut — a handy way to verify or extract the cut.
- Input validation: square non-null matrix, non-negative capacities, distinct in-range source/sink. State is static (C9).


---

## Edmonds_karp

Ford-Fulkerson that always augments along the **shortest** augmenting path (fewest edges), found by BFS.

### Idea
The only change from generic Ford-Fulkerson is *how the path is picked*: BFS on the residual graph, so each augmenting path has the minimum number of edges. That single choice bounds the work — source-to-sink residual distance never decreases and must increase within O(E) augmentations, capping the total at O(V·E) augmentations.

**As implemented** (same matrix + visit-stamp scaffolding as the DFS version): each round, a BFS from the source records `prev[]` pointers and stops when the sink is dequeued; if the sink was never reached (`prev[t] == −1`), done. Otherwise two backward walks along `prev`: one to take the **minimum residual on the path** (the bottleneck), one to apply it — subtract along forward edges, add along the reverses.

### Complexity
**O(V·E²)** — O(V·E) augmentations × O(E) per BFS (O(V²) per BFS on the matrix, so O(V³·E) matrix-flavored; the bound that matters is that it's **independent of capacity values**). Guaranteed termination even with huge capacities — the fix for plain FF's O(E·maxflow).

### Notes
- **BFS, not DFS, is the whole point.** DFS can pick long, silly paths and take a number of augmentations proportional to the flow value; BFS's shortest paths give the polynomial bound.
- Everything else — residual matrix, reverse edges, bottleneck — is identical to Ford-Fulkerson; here the bottleneck is found and applied in two explicit passes (contrast the DFS version's single recursive pass), which is the natural shape once a `prev[]` chain exists.


---

## Capacity_scaling

Ford-Fulkerson that pushes **large amounts first**: only use augmenting paths whose every edge has residual ≥ a threshold `delta`, halving `delta` as each level exhausts.

### Idea
Set `delta` to the largest power of two ≤ the maximum edge capacity — **as implemented, `Integer.highestOneBit(maxCapacity)`**, with the max capacity gathered during input validation. Repeatedly find augmenting paths restricted to edges with `residual >= delta` and saturate them; when none remain, halve `delta`. At `delta = 1` the restriction disappears and it degenerates into ordinary Ford-Fulkerson, which is exact — so the final flow is maximum.

The path search **as implemented** is the same recursive DFS as the plain Ford-Fulkerson (same matrix, same visit stamps, same on-the-unwind residual updates) with one extra parameter: the edge filter `capacity[node][i] >= delta` instead of `> 0`. The graph searched is always the full residual graph — only which edges the walk may traverse changes.

### Complexity
O(E² log U), `U` = max capacity: each delta-phase adds O(E) augmentations, and there are log U phases. Great when capacities are large but the graph is small — the bulk of the flow moves in a few wide pushes before the algorithm fusses over small residuals.

### Notes
- **Delta phases = the bit-length of the max capacity** — the "process the flow value bit by bit" view of the same idea as the doubling trick in Recursive_multiplication.
- Zero-capacity-everywhere input is handled by clamping the starting delta to ≥ 1 (one vacuous phase, flow 0).


---

## Dinics

The fast general-purpose max-flow algorithm: alternate **BFS level graphs** with **DFS blocking flows**.

### Idea
Each phase has two steps:
1. **BFS builds the level graph.** From the source, assign every vertex its BFS distance (`level[]`) in the residual graph (unreached = −1). Only edges going from level `L` to level `L+1` count — no shortcuts, no back-edges. If the sink got no level, the flow is maximum: done.
2. **DFS extracts a blocking flow.** Repeatedly push flow source→sink along level-respecting edges (`level[next] == level[node] + 1`) until no augmenting path remains *within this level graph*. Many paths per phase, not one.

**As implemented:** the same residual-matrix scaffolding as the other three, with a `level[]` refilled by BFS each phase, and — the crucial optimization — a per-vertex **current-edge pointer** `next[]` (reset to 0 each phase): the DFS's neighbor loop is `for (; next[node] < V; next[node]++)`, so an edge that proved dead this phase is never rescanned by later DFS attempts. Bottleneck threads down as `min(flow, edge)`; residuals update on the unwind — the Ford-Fulkerson DFS wearing a level-graph seatbelt.

### Complexity
O(V²·E) in general. On **unit-capacity** graphs (including bipartite matching) it's O(E·√V) — why Dinic's is the go-to for matching. Each phase strictly increases the source-sink residual distance, so there are at most V phases.

### Notes
- **Two nested ideas:** BFS gives the level structure (Edmonds-Karp's shortest paths), but instead of one augmentation per BFS, the DFS drains an entire *blocking flow* at that distance before levels are rebuilt. Fewer BFS rebuilds → faster.
- **The current-arc pruning is mandatory for the bound** — re-scanning dead edges every DFS attempt is what it exists to prevent. `level[]` and `next[]` describe *this phase's* residual graph, hence the per-phase reset.
- **Only `level+1` edges in the DFS** — that restriction keeps the search acyclic and guarantees progress; wandering to same- or lower-level vertices would reintroduce cycles.
- **Code flag (C10):** the class still carries `visited`/`visited_count` fields (and bumps the counter) copied over from the FF scaffold — the level-graph + pointer machinery replaced them and nothing reads them. Delete.

---

## Hamiltonian cycles & NP-hardness

The complexity wall this repo touches at `Traveling_salesman`: what it means, and which famous problems live on the wrong side of it.

### Hamiltonian path / cycle
A **Hamiltonian path** visits every **vertex** exactly once; a **Hamiltonian cycle** additionally returns to its start. The instructive contrast is with the Eulerian path (every **edge** exactly once — implemented in `Algorithms/Eulerian_path`):

| | Eulerian (edges) | Hamiltonian (vertices) |
|---|---|---|
| Existence test | O(V+E) degree conditions | **NP-complete** — no known efficient test |
| Construction | Hierholzer's, O(V+E) | Exponential (brute force / DP over subsets) |

That asymmetry is the whole lesson: two near-identical-sounding questions, one trivially checkable by local degree counting, the other with no known structure to exploit — you essentially must search. Best known exact approaches are backtracking with pruning, or the Held-Karp-style **DP over subsets** in O(2ⁿ·n²) — exactly the `Traveling_salesman` machinery, since TSP is the weighted version of Hamiltonian cycle (Hamiltonian cycle = "is there a tour of cost ≤ k" with unit weights).

### P, NP, NP-complete, NP-hard — the working definitions
- **P** — decision problems solvable in polynomial time.
- **NP** — problems whose *yes*-answers can be **verified** in polynomial time given a certificate (a proposed Hamiltonian cycle is easy to check; finding one is the hard part).
- **NP-complete** — the hardest problems *in* NP: everything in NP reduces to them in polynomial time. Solve any one in polynomial time and P = NP. (SAT was the first, by Cook–Levin; the rest follow by reductions.)
- **NP-hard** — at least as hard as NP-complete, but not required to be in NP itself (needn't be a decision problem, or verifiable). Optimization forms usually sit here: "find the *minimum* tour" is NP-hard; its decision form "is there a tour ≤ k" is NP-complete.

### Common NP-hard problems worth recognizing on sight
- **SAT / 3-SAT** — satisfiability of boolean formulas; the original NP-complete problem and the usual reduction source.
- **Traveling salesman** (min-cost Hamiltonian cycle) — in this repo, exact via Held-Karp O(2ⁿ·n²).
- **Hamiltonian path / cycle** — above.
- **0/1 Knapsack & Subset sum** — NP-complete, *but* pseudo-polynomial: the O(n·W) DP in `Problems/Knapsack_01` is efficient only because W is numerically small; the input size is really the bit-length of W.
- **Graph coloring** (≥ 3 colors; 2-coloring = bipartite check is easy — BFS layer parity).
- **Maximum clique** and **Maximum independent set** — complements of each other.
- **Minimum vertex cover** — NP-hard, though König's theorem makes it polynomial *on bipartite graphs* via max matching (i.e. the max-flow machinery in this repo).
- **Set cover / Steiner tree / Partition / Bin packing** — the classic Karp-21 crowd.

### What to do when a problem is NP-hard
The practical playbook, in the order to try it: (1) **shrink the exponent's base** — subset DP (2ⁿ), meet-in-the-middle (2^(n/2)), branch and bound with pruning; (2) **exploit special structure** — pseudo-polynomial DP when numbers are small (knapsack), polynomial special cases (vertex cover on bipartite graphs, 2-SAT, trees/DAGs generally); (3) **approximate** — e.g. Christofides gives ≤ 1.5× optimal for metric TSP, greedy set cover is ln n-competitive; (4) **heuristics** (local search, simulated annealing) when guarantees can go.

### Notes
- "Exponential" is not "hopeless" — n ≤ 20 is fine for 2ⁿ·n² (Held-Karp's practical range), n ≤ 40 with meet-in-the-middle.
- Recognizing an NP-hard core early saves you from hunting for a polynomial algorithm that (as far as anyone knows) doesn't exist; the interview-relevant skill is naming the reduction ("this is vertex cover in disguise") and then reaching for the playbook above.