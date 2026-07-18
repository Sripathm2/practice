# Study Lab

A personal study laboratory — one repository for everything I'm working through, across languages and formats. It currently holds a from-scratch **data structures & algorithms track in Java** (interview and research-engineering preparation) and a **machine-learning / statistics track in Python** (book labs and experiments); planned areas include **probability** (Harvard Stat 110), **LeetCode / NeetCode 150 practice**, and further ML coursework — all under the same roof, same conventions, same notes discipline. Everything is written by hand, documented in prose notes, and validated (Java: self-contained test runners; Python: executable notebooks). The work is paced and deliberate: the goal is durable understanding, not just passing tests.

## Repo map

- **`Data_structures/`** — generic structures plus shared types (`Graph` interface, both graph representations, `Tree_node`), each in its own file with a `<Name>_Main` test runner.
- **`Algorithms/`** — reusable algorithms that build on those structures: suffix-array string algorithms, merge sort, graph traversals, tree algorithms, shortest paths, topological sorts, SCC, MST, Eulerian paths, and four max-flow implementations. Same per-file test-runner pattern.
- **`Problems/`** — specific named problems and exercises: the recursion/divide-and-conquer exercises, tilings, knapsacks, Kattis problems, Held-Karp TSP, grid shortest path, and the Mice-and-Owls bipartite-flow problem. The rule of thumb separating the two code packages: `Algorithms/` holds reusable procedures another file might import; `Problems/` holds leaf files that answer one specific question.
- **`ml/`** — the machine-learning / math track (Python): topic folders holding book labs and my own experiments, plus the shared conda environment spec. Organized by **topic, not by book** — every book feeds the same tree (see `ml/` layout below).
- **`Data_structures_notes.md`, `Algorithms_notes.md`, `Problems_notes.md`, `ML_notes.md`** — prose notes, one file per area (see the topic index).
- **Planned areas** (same pattern — a root folder + a root notes file each): `probability/` for Stat 110, `leetcode/` for NeetCode-150 / LeetCode practice.
- **`Images/`** — figures referenced by the notes.
- **`Makefile`** — root build/run harness for all three packages.

## Topic index (notes)

One notes file per package — a class's note is always in the file matching its folder:

| File | Covers |
|---|---|
| `Data_structures_notes.md` | Arrays, linked lists, stack/queue, heap & priority queues, union-find, BST & AVL, hash tables, Fenwick tree, suffix array, indexed PQ, sparse table, and both graph representations (adjacency matrix & list) |
| `Algorithms_notes.md` | Suffix-array string algorithms (unique substrings, LRS, LCS), divide & conquer + merge sort, graphs overview, DFS/BFS, tree algorithms (rooting, leaf sum, center, isomorphism, LCA), topological sorts, DAG paths, Dijkstra, Bellman-Ford, Floyd-Warshall, Tarjan SCC, Eulerian path, Prim, and the four max-flow algorithms |
| `Problems_notes.md` | Recursion/D&C exercises (multiplication, list sum, string reversal, max-2D, three-way min), DP problems (magical cows, tilings, mountain scenes, narrow art gallery, knapsacks), grid shortest path, Held-Karp TSP, and bipartite matching via max flow (Mice and Owls, Elementary Math) |
| `ML_notes.md` | The ML/statistics track, built chapter by chapter from book highlights (currently: ISL). Concept notes in Definition / Intuition / Notes format, grouped by topic |

## The `ml/` area (Python)

The ML track lives beside the Java packages, in Python, organized by **topic — never per book** (books come and go; topics accumulate). Each topic folder holds book labs and my own experiments, distinguished by filename prefix:

```
ml/
  environment.yml           # the shared conda env spec (see Conda_setup_mac.md)
  data/                     # datasets (large files gitignored)
  linear-regression/        #   islp_lab_ch03.ipynb, exp_gradient_descent.py, ...
  classification/           # logistic regression, LDA/QDA, naive Bayes, KNN
  resampling/               # cross-validation, bootstrap
  regularization/           # ridge, lasso, PCR/PLS, model selection
  nonlinear/                # polynomials, splines, GAMs
  trees/                    # decision trees, bagging, random forests, boosting
  svm/
  deep-learning/            # ISL ch10 labs now; Karpathy/Géron material later
  survival/                 # censored data, survival curves
  unsupervised/             # PCA, clustering
  inference/                # multiple testing; Stat 110 probability work later
```

Naming convention inside a topic folder: `<source>_<what>.ipynb|py` — e.g. `islp_lab_ch08.ipynb` (a book lab, kept close to the book's version), `exp_tree_depth_sweep.py` (my own experiment). One conda environment (`ml`) serves the whole tree; per-topic environments only if a book genuinely conflicts (deep-learning may eventually get its own for torch pinning).

Notes for all of it go in the single `ML_notes.md` at the root — same one-notes-file-per-area rule as the Java packages — built chapter by chapter via the handoff workflow in `ML_handoff.md`.

## Conventions

Every **Java** class follows the same rules so the codebase reads consistently (the `ml/` area follows the lighter Python conventions above instead):

- **Generic backing** via `Object[]` with `@SuppressWarnings("unchecked")` casts on read (Java erasure rules out `new E[n]`). Numeric-only structures (e.g. `Fenwick_tree`) use a primitive backing like `long[]` instead.
- **Contract-only comments** above each method — what it does, returns, and throws — with no implementation hints, so the method body is the exercise.
- **Self-contained test runner**: each file declares a `<Name>_Main` class with `checkEquals`, `checkTrue`, and `checkThrows` helpers that print PASS/FAIL inline, tally results, print a summary, and `System.exit(1)` on any failure. Tests cover edge cases (empty, single element, bounds, null-safety) and, where useful, cross-check against a brute-force reference; test constants are verified against an independent method before being trusted.
- **Null-safety** with `java.util.Objects.equals` for value comparisons.
- Structure-specific touches, e.g. doubly linked lists carry a `toStringReverse` that walks `prev` pointers to verify back-pointer maintenance.

## Build & run

Requires a JDK for the Java packages (developed and tested on JDK 21; a fresh container needs `apt-get install -y default-jdk-headless`) and the `ml` conda environment for notebooks (see `Conda_setup_mac.md`). One root `Makefile` drives everything:

```sh
make run F=Stack              # compile all Java, run Data_structures.Stack_Main, clean
make run F=Magical_cows       # package auto-detected (Problems here)
make run-nb N=islp_lab_ch03   # execute a notebook top-to-bottom in place
                              #   (bare name is found under ml/; a path works too)
make run-latest               # run the most recently modified source anywhere:
                              #   .java -> its _Main, .ipynb -> execute it,
                              #   ml/ .py -> run it in the ml env
make run-all                  # run every Java *_Main, log to test-results.log,
                              #   print only failures
make list                     # list runnable classes and notebooks, newest first
make clean                    # delete .class files, .ipynb_checkpoints, and
                              #   generated artifacts under ml/ (pngs, exports,
                              #   csv dumps outside ml/data/ — protected types:
                              #   .ipynb .py .yml .yaml .md and all of ml/data/)
make clean-all                # clean + strip output cells from every notebook
                              #   (run before committing notebooks)
```

`make run` picks the Java package automatically based on which folder contains `<F>.java`, then auto-cleans the generated `.class` files. Interface/type files with no `_Main` are skipped by `run-all` automatically. Notebook execution and output-stripping use the `ml` conda env when conda is on the machine (`conda run -n ml`), falling back to whatever `jupyter` is on PATH.

For interactive work in `ml/`: `conda activate ml`, then `jupyter lab` from the repo root. Setup instructions: `Conda_setup_mac.md`.

## Notes format

Data-structure notes use a three-part template — **Positives / Negatives / Algorithm (thought process)** — and algorithm/problem notes use **Idea / Complexity / Notes**. ML concept notes use **Definition / Intuition / Notes** (terms get a crisp definition, a plain-language why-it-matters, then caveats and connections). Notes live in root-level files mirroring the areas, each with a grouped table of contents. Complexity claims describe *this repo's implementations*; faster textbook variants are noted as upgrades. Where an implementation has a known deviation or bug, the note carries a **Code flag** line pointing at the reconciliation report.

## How Claude is used in this project

Claude (Anthropic) is used as a coding tutor and scaffolding tool, not as a code generator that does the work:

- **Skeletons.** Claude produces the class scaffold — fields, contract-only method stubs, and a complete `<Name>_Main` test runner (known-answer cases plus brute-force cross-checks) — matching the conventions above. The method bodies are left empty for me to implement.
- **Socratic debugging.** When I share buggy code, Claude points at the bug and walks the failing trace, edge cases, and complexity trade-offs rather than handing over a fix. It only writes the fix when I explicitly ask.
- **Notes.** Claude converts my handwritten notes into the templates above, flagging and correcting errors rather than copying them verbatim, and periodically reconciles the notes against the implemented code (the code is the source of truth).

The intent is that the learning happens while filling in the skeletons and chasing down the test failures; the scaffolding just removes boilerplate and keeps the structure uniform.

## Study resources & curriculum

### Coding / DSA spine
- **NeetCode 150** — https://neetcode.io/practice
- **Grokking the Coding Interview** (pattern-based prep) — https://www.designgurus.io/course/grokking-the-coding-interview (also on Educative: https://www.educative.io/courses/grokking-coding-interview)
- **William Fiset — Data Structures** (YouTube) — https://www.youtube.com/playlist?list=PLDV1Zeh2NRsB6SWUrDFW2RmDotAfPbeHu — *completed*
- **William Fiset — Graph Theory** (YouTube) — https://www.youtube.com/playlist?list=PLDV1Zeh2NRsDGO4--qE8yH72HFL1Km93P — *completed*
- **William Fiset — Algorithms** (companion Java repo) — https://github.com/williamfiset/Algorithms
- **Abdul Bari — Algorithms** (YouTube) — https://www.youtube.com/playlist?list=PLAPEtbmG9XgTQqVYWAgAR6MilRB93OeMQ — *up next*

### ML / math track
- **Introduction to Statistical Learning (ISL / ISLP)** — https://www.statlearning.com — *in progress; labs under `ml/`, notes in `ML_notes.md`*
- **Harvard Stat 110 — Probability** (Joe Blitzstein) — https://stat110.hsites.harvard.edu/ · lectures: https://www.youtube.com/playlist?list=PL2SOU6wwxB0uwwH80KTQ6ht66KWxbzTIo · free book: https://probabilitybook.net
- **Andrej Karpathy — Neural Networks: Zero to Hero** — https://karpathy.ai/zero-to-hero.html · playlist: https://www.youtube.com/playlist?list=PLAqhIrjkxbuWI23v9cThsA9GvCAUhRvKZ
- **Aurélien Géron — Hands-On Machine Learning** (ch. 10–16; code repo) — https://github.com/ageron/handson-ml3
- **Chip Huyen — Designing Machine Learning Systems** — https://huyenchip.com/books/ · **Stanford CS329S** — https://stanford-cs329s.github.io/
- **Khang Pham — Machine Learning Interviews** (late-stage drilling) — https://mlengineer.io/

---

*Environment: macOS, zsh. Links above were checked against their sources; the ML-track entries should be re-verified periodically as course pages move.*

----

# Bug List — Working Checklist

All confirmed bugs (B) and hazards/cleanups (C) from the reconciliation session, condensed into a fix-me checklist. Each B was reproduced with a small program; full detail and reproduction descriptions are in `01_Mismatch_report.md`. Suggested order: contract-breaking bugs first (B1, B2, B3), sentinel/overflow bugs next (B5, B6), then the rest. Re-run `make run-all` after each fix — everything passes today, so any new failure is the fix itself.

## Confirmed bugs

- [ ] **B1 — `Data_structures/Binary_search_tree.java` — insert accepts duplicates at leaves.** The descent loop exits at a leaf (or single-node root) before the equality check runs, so a duplicate of any leaf value is inserted as its left child (size grows, ordering invariant broken). Internal-node duplicates are rejected correctly. Repro: `insert(5); insert(5)` on an empty tree → size 2.
- [ ] **B2 — `Data_structures/Dynamic_array.java` — shrink condition inverted.** `if (size*4 > capacity) resize(size*2)` fires when *more* than quarter-full: a remove can grow capacity (10 → 16 at size 8), most removes trigger a resize, and a below-¼-full array never shrinks. Intended: `size*4 < capacity → shrink`. Bonus in the same method: `remove(int)` nulls `arr[size]` instead of the vacated `arr[size-1]` (loitering reference).
- [ ] **B3 — `Data_structures/Singly_linked_list.java` — stale `tail` after removing the last element.** `removeFirst` on a one-element list nulls `head` but not `tail`; `getLast()` on the now-empty list returns the removed value instead of throwing.
- [ ] **B4 — `Algorithms/Topological_sort_dfs.java` — cycle detection incomplete.** Only cycles returning to the DFS launch vertex are caught; `0→1→2→1` silently returns `[0,1,2]`. Fix: three-color / on-recursion-stack detection. (`Dag_shortest_longest_path` inherits this — it can "sort" a cyclic graph.)
- [ ] **B5 — `Algorithms/Dag_shortest_longest_path.java` — `longestPaths` missing the unreachable guard.** No `continue` when `dist[u] == UNREACHABLE_LONGEST`; `Long.MIN_VALUE + weight` wraps and the garbage propagates between unreachable vertices. Repro: source 0, disconnected edge 1→2 → vertex 2 reports −9223372036854775803. (`shortestPaths` has the guard — copy it.)
- [ ] **B6 — `Algorithms/Bellman_ford.java` — overflow in the unguarded passes.** `shortestPaths` phase 2 has no `UNREACHABLE` guard → a merely-unreachable vertex fed by another unreachable vertex is falsely marked `NEGATIVE_INF`. `hasNegativeCycle` has the mirror problem (phase 1 unguarded), and the two methods disagree on the same graph. Same one-line guard in both places.
- [ ] **B7 — `Algorithms/Eulerian_path.java` — circuit start hard-coded to vertex 0.** With balanced degrees, `start = 0` even if vertex 0 is isolated; a valid circuit elsewhere (isolated 0, cycle 1→2→1) is rejected as `[]`. Fix: pick any vertex with an out-edge.
- [ ] **B8 — `Data_structures/Hash_table_double_hashing.java` — two tombstone issues.** (a) `toString` checks slots against `null` only, casts a `TOMBSTONE` to `Entry` → `ClassCastException` after any remove. (b) `put`'s insert-into-null path stamps the previously-*empty* slot as `TOMBSTONE` during the relocation swap — empty slots permanently erode into tombstones, lengthening all probes until resize; place into the remembered tombstone slot and leave the null alone.
- [ ] **B9 — `Algorithms/Floyd_warshall.java` — `hasNegativeCycle` init missing an `else`.** `if (i==j){dp=0} if (hasEdge){…} else {UNREACHABLE}` overwrites a self-loop-free diagonal's 0 with `UNREACHABLE`. `allPairsShortestPaths` has the correct `else if` chain — make them match.
- [ ] **B10 — `Problems/Traveling_salesman.java` — dead tour reconstruction with an inverted comparison.** The reconstruction loop replaces the candidate when the incumbent is strictly *better* (builds a worst tour), and the resulting `tour` array is never returned so no test can catch it. Either fix the comparison and return the tour, or delete the loop.

## Hazards & cleanups (lower priority)

- [ ] **C1 — `Algorithms/Unique_substrings.java`:** `n(n+1)/2` computed in `int` (and the LCP sum via an int stream) despite the `long` return — overflows past n ≈ 65,535. Compute in `long`.
- [ ] **C2 — `Algorithms/Longest_common_substring.java`:** sentinel classification assumes input chars ≥ `'A'` (digits/spaces misread as sentinels); window-min uses the O(n)-remove `Priority_queue`; owner found by scanning each suffix for its first sentinel (O(N²)). Works at test sizes — document or upgrade.
- [ ] **C3 — `Data_structures/Heap.java`:** heapify constructor sifts each non-leaf down *and up*; the sift-up is redundant work.
- [ ] **C4 — `Data_structures/Graph_adjacency_list.java`:** `neighbors` returns insertion order, violating the interface's ascending-order contract (tests currently mask it); weighted re-add on an undirected graph updates only `u`'s copy, not the `v→u` mirror.
- [ ] **C5 — `Data_structures/Union_find*.java`:** `union` does four find-walks (via the `connected` pre-check); compressed `find` never rewrites `x`'s own pointer. Correct, just redundant.
- [ ] **C6 — `Data_structures/Hash_table_double_hashing.java`:** capacities double from 11 (non-prime after growth), so an even probe step shares factor 2 with capacity and covers only half the slots. Keep capacity prime across resizes.
- [ ] **C7 — `Problems/Narrow_art_gallery.java`:** infeasibility sentinel is the literal `10000`; a legal closed-sum above it wins wrongly. Use `Integer.MAX_VALUE/2`.
- [ ] **C8 — `Algorithms/Three_way_min.java`** *(→ `Problems/` after the move)*: right recursion starts at `m2-1`, overlapping the middle segment by one — benign for min, but `m2` is the clean boundary.
- [ ] **C9 — static mutable state** in `Breadth_first_search` (`parent`), `Tarjan_scc`, `Prim_mst`, the flow classes, both topo sorts, several Problems classes — not reentrant; fine solo, refactor only if it ever bites.
- [ ] **C10 — dead code / stragglers:** duplicate `rootTree` inside `Topological_sort_kahn`; unused `visited`/`visited_count` in `Dinics`; `Floyd_warshall`'s never-returned `path[][]` matrix (overlaps B10's theme).
- [ ] **C11 — `Readme.md` drift** — already resolved by the corrected README delivered with the notes.