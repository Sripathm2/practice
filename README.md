# Data Structures — Implementation Notes

Positives, negatives, and algorithm thought-process for each structure built from scratch in the `Data_structures` package. No code — contracts and reasoning only.

## Contents
1. [Static_array](#static_array)
2. [Dynamic_array](#dynamic_array)
3. [Singly_linked_list](#singly_linked_list)
4. [Doubly_linked_list](#doubly_linked_list)
5. [Stack](#stack)
6. [Queue](#queue)
7. [Heap](#heap)
8. [Priority_queue](#priority_queue)
9. [Union_find](#union_find)
10. [Union_find_compressed](#union_find_compressed)
11. [Binary_search_tree](#binary_search_tree)

---

## Static_array<E>

Fixed-capacity contiguous backing. Index maps directly to a memory offset.

### Positives
- Access by index O(1) — direct offset, no walking.
- Add into an empty slot is O(1) (no resize ever happens).
- Lowest memory overhead of any structure — no pointers, no spare capacity.
- Cache-friendly: contiguous layout.

### Negatives
- Fixed size. Can't grow; full means full.
- Remove is O(n) — shift everything after the gap down.
- Insert in the middle is O(n) for the same reason.
- Search is O(n) (unsorted) — no index for a value, must scan.

### Algorithm / thought process
Backing is one `Object[]` of fixed length. Index `i` is a direct lookup, that's the whole point — O(1) access and O(1) set.

Removal is the cost center: deleting index `i` leaves a hole, so everything from `i+1` to the end shifts left one slot. Linear work. Same logic for mid-array insert (shift right to open a slot).

No resizing logic at all — capacity is set once at construction. If you need growth, that's a dynamic array.


---

## Dynamic_array<E>

Static array that resizes itself. Tracks `size` (used) separately from `capacity` (allocated).

### Positives
- Access by index O(1) — same direct-offset trick as static.
- Append amortized O(1) — most appends are free, the occasional resize averages out.
- Resizable — no fixed cap; grows on demand.

### Negatives
- Insert/remove in the middle is O(n) — still has to shift.
- Resize cost is O(n) when it triggers (allocate new array, copy everything over).
- Over-allocates: capacity is usually larger than size, so wasted memory.

### Algorithm / thought process
Two numbers matter: `size` and `capacity`. Append writes at index `size`, then `size++`. When `size == capacity`, double the capacity, allocate a new array, copy elements, swap it in.

Why doubling and not +1? Amortization. Doubling means resizes happen at 1, 2, 4, 8, ... — geometrically rare. Total copy work across n appends sums to ~2n, so per-append cost averages to O(1). That's the "amortized" claim, not worst-case.

Shrinking: halve capacity when size drops to ~1/4 (not 1/2). The gap between the grow threshold and shrink threshold stops thrashing — you don't want grow→shrink→grow on repeated add/remove at the boundary. (This is the "only ½ discard → log n" idea: leave hysteresis.)

Remove still shifts, so it's O(n) regardless of the resize machinery.


---

## Singly_linked_list<E>

Chain of nodes, each holding a value and a `next` pointer. Head pointer is the entry point.

### Positives
- Insert/remove at head O(1) — just repoint head.
- Append O(1) if you keep a tail pointer.
- Truly dynamic size, node by node — no resize, no copy.
- Less memory than doubly (one pointer per node).
- Simple.

### Negatives
- Access/search by index O(n) — must walk from head; no random access.
- Can't traverse backward — only one direction.
- Remove tail is O(n) even with a tail pointer — you need the *previous* node to fix its `next`, and there's no back-link to find it.

### Algorithm / thought process
Each node points only forward. You hold `head` (and usually `tail`).

Insert at head: new node's `next` = old head, then head = new node. Constant.

Remove head: head = head.next. Constant.

Tail is the asymmetry. Appending is cheap with a tail pointer, but *removing* the tail means the new tail is the second-to-last node — and to find it you walk the whole list, because no node knows its predecessor. That O(n) tail-removal is exactly what the doubly linked list fixes.

Traversal is one-directional: to "reverse" you'd have to rebuild pointers, you can't just walk back.


---

## Doubly_linked_list<E>

Linked list where each node has both `next` and `prev`. Maintains head and tail.

### Positives
- Traverse in both directions.
- Remove a known node in O(1) — you have its `prev`, so you can splice it out without walking.
- O(1) insert/remove at *both* head and tail (tail removal is cheap here, unlike singly).

### Negatives
- ~2x pointer memory (every node stores `prev` too).
- More bookkeeping — every insert/remove must fix up to four pointers (the node's prev/next and the neighbors' links), easy to get wrong.

### Algorithm / thought process
The `prev` pointer buys two things: backward traversal, and O(1) tail removal. Removing the tail just needs `tail = tail.prev; tail.next = null` — no walk, because the back-link is already there.

The cost is discipline. Any splice touches the node and both neighbors. Insert between A and B: new.prev=A, new.next=B, A.next=new, B.prev=new. Miss one and the list corrupts silently in one direction.

This is why `toStringReverse` (walk via `prev` from tail) is a useful test — it catches a broken back-link that a forward-only `toString` would never reveal.

Stack and Queue are both built on this: head/tail O(1) access in both directions covers LIFO and FIFO cleanly.


---

## Stack<E>

LIFO (last in, first out). Built on Doubly_linked_list: push/pop both hit the head.

### Positives
- push, pop, peek all O(1).
- Dead simple — one access point (the top).
- Natural fit for DFS, recursion unrolling, undo, expression/bracket matching.

### Negatives
- No random access — only the top is reachable.
- No search without destroying the stack (you'd have to pop everything).

### Algorithm / thought process
LIFO means the only interesting end is the top. Mapping onto the doubly linked list: push = add to head, pop = remove head, peek = read head value. All O(1) because head ops are O(1).

Could equally back it with a dynamic array (push = append, pop = remove last) — also amortized O(1). The linked-list version avoids resize cost; the array version is more cache-friendly. Either satisfies the contract.

Why DFS uses it: you push neighbors and pop the most recent, so you dive deep before going wide — that's depth-first by construction.


---

## Queue<E>

FIFO (first in, first out). Built on Doubly_linked_list: add at tail, remove from head.

### Positives
- enqueue, dequeue, peek all O(1).
- Simple — two access points (front and back), each fixed.
- Natural fit for BFS, scheduling, buffering, producer/consumer.

### Negatives
- No random access — only front and back.
- No search without draining it.

### Algorithm / thought process
FIFO means you add at one end and remove from the other. Mapping onto the doubly linked list: enqueue = add to tail, dequeue = remove head. Both O(1) because the list has O(1) ops at both ends — this is exactly why a *doubly* linked backing is natural (a singly linked list would make one of the two ends O(n)).

Why BFS uses it: you enqueue neighbors and dequeue the oldest, so you finish an entire level before descending — breadth-first by construction. Stack→DFS, Queue→BFS is the pairing to remember.


---

## Heap<E extends Comparable<E>>

Binary min-heap stored in an array. Complete binary tree: every level full except the last, which fills left to right, no gaps.

### Positives
- peek min O(1) — it's always at index 0.
- insert and extract-min O(log n) — only one root-to-leaf path moves.
- Array-backed: no node objects, no pointers, cache-friendly. Children are computed, not stored.
- Build from an array (heapify) in O(n), better than n inserts.

### Negatives
- Arbitrary contains/search is O(n) — heap order says nothing about left/right siblings, so you can't binary-search it.
- Not sorted — only the min is positioned; the rest is partially ordered.
- Only the extreme (min, or max) is accessible; no efficient access to the k-th element without extracting.
- Arbitrary remove needs a value→index map to be better than O(n).

### Algorithm / thought process
Index arithmetic replaces pointers (0-based):
- parent(i) = (i - 1) / 2  (integer division)
- left(i)   = 2i + 1
- right(i)  = 2i + 2

Heap invariant (min-heap): every parent ≤ its children. Says nothing about left vs right — that's why search isn't logarithmic.

**Insert:** put the new value at the end of the array (next open leaf), then **sift up** — while it's smaller than its parent, swap with the parent. Walks one path up, O(log n).

**Extract-min:** the answer is index 0. Swap root with the last element, shrink size by one (drop the old root now sitting at the end), then **sift down** from the root — repeatedly swap with the *smaller* of its two children while it's larger than that child. One path down, O(log n).

**Up or down?** After a replacement, you only ever need one direction. A freshly inserted leaf only violates upward → sift up. A new root after extract only violates downward → sift down. Picking the smaller child on the way down is what preserves the min property.

**Heapify (bottom-up):** to turn an arbitrary array into a heap, sift down every non-leaf node from the last parent backward to index 0. Looks like O(n log n) but sums to O(n) because most nodes are near the bottom with short sift paths.

**Arbitrary remove / contains:** to do better than scanning, keep a hashmap from value → index so you can locate a node in O(1), then sift it up or down after removal. Without that map it's O(n).


---

## Priority_queue<E>

A heap ordered by an explicit priority instead of the natural ordering of `E`. Wraps Heap via an Entry wrapper that carries the value plus its comparator-derived priority.

### Positives
- Order by any priority you define (a Comparator), not just `Comparable`.
- insert and poll O(log n); peek O(1) — inherits the heap's costs.
- Decouples "what to store" from "how to rank it."

### Negatives
- Same as the heap underneath: arbitrary search/contains O(n), not fully sorted, only the head is cheap.
- Extra wrapper object per element (the Entry) — small memory and indirection overhead.

### Algorithm / thought process
The heap needs a notion of "smaller." A bare `Heap<E>` uses `E`'s `compareTo`. A priority queue instead takes a `Comparator` and ranks by that. The Entry wrapper bundles the element with whatever the comparator yields, and the heap orders Entries.

Min vs max is just the comparator's direction — to flip a min-PQ into a max-PQ, reverse the comparator (or negate priorities). Nothing else in the heap machinery changes; sift up/down still picks the "smaller" entry, where "smaller" is now whatever the comparator says.

Everything else — insert at end + sift up, poll = swap root with last + sift down — is identical to the underlying heap.


---

## Union_find

Disjoint-set structure over ids 0..n-1. `find` tells which set an element belongs to (its root); `union` merges two sets. This version uses union by rank, no path compression.

### Positives
- Tracks connected components cheaply; near-constant union/find with rank.
- Tiny memory: just a parent array (plus rank, plus a component count).
- The backbone of Kruskal's MST and any "are these connected / how many groups" problem.

### Negatives
- No path compression here, so finds re-walk the tree each time — O(log n) with rank, worse if you skipped rank.
- Can't un-union (no split). Merges are permanent.
- `componentSize` is O(n) unless you maintain a size array.

### Algorithm / thought process
A `parent[]` array. Each element points at its parent; a root points at itself and *is* the representative of its set.

**find(x):** follow `parent` links up until you hit a node that points to itself. That root identifies the set.

**union(x, y):** find both roots. If equal, already merged → return false. Otherwise attach one root under the other. **Union by rank:** attach the shorter tree under the taller one so the result doesn't grow taller than necessary. This keeps trees shallow → find stays O(log n) instead of degrading to an O(n) chain.

**connected(x, y):** find(x) == find(y).

**components:** start at n, decrement on every successful union.

**Why it matters for Kruskal:** sort edges by weight, walk them cheapest-first, and add an edge only if its two endpoints have *different* roots (otherwise it would form a cycle). Each "add" is a union. Union-find is what answers "would this edge close a cycle?" in near-constant time. (MST = minimum set of edges connecting all vertices at lowest total cost; not unique.)


---

## Union_find_compressed

Same disjoint-set structure, plus **path compression** in `find`. With rank + compression, operations run in amortized α(n) — inverse Ackermann, effectively constant for any real input.

### Positives
- Amortized α(n) per operation — the fastest practical disjoint-set; α(n) ≤ 4 for any n you'll ever see.
- Trees flatten over time, so repeated finds on the same elements become O(1).
- Same tiny memory footprint as the uncompressed version.

### Negatives
- A `find` now *writes* (rewrites parent pointers), so it's not a pure read.
- Still no un-union; merges are permanent.
- The α(n) bound is amortized, not per-call worst-case — a single early find can still walk a longer path before it flattens.

### Algorithm / thought process
Identical to plain union-find for `union`/`connected`. The change is in `find`.

**find(x) with path compression:** walk up to the root as before, but then make every node you passed point *directly* at the root. Next time any of them is queried, it's one hop away. The tree gets flatter with use.

Two common ways to implement: a two-pass version (walk up to find the root, then walk up again repointing each node to it), or the one-line recursive `parent[x] = find(parent[x])`. Both achieve the same flattening.

Combined with union by rank, this is what gives the α(n) amortized guarantee — rank keeps trees shallow on the way up, compression collapses them on the way down. Either alone is good; together they're effectively constant.


---

## Binary_search_tree<E extends Comparable<E>>

Ordered tree. At every node: all of left subtree < node < all of right subtree (no duplicates here). Note "BST" ≠ complete tree — it can be any shape.

### Positives
- Search, insert, delete all O(log n) on average.
- In-order traversal gives sorted output for free.
- Dynamic size; supports min, max, successor/predecessor, and range queries naturally.

### Negatives
- O(n) worst case — inserting already-sorted data builds a degenerate chain (a linked list).
- Not self-balancing on its own (AVL / Red-Black fix this; out of scope).
- Pointer overhead per node, and no random access by index.

### Algorithm / thought process
Ordering invariant holds at *every* node, not just the root — that's what makes the binary search work at each step.

**Insert:** walk down comparing — go left when smaller, right when larger — until you fall off the tree, then attach the new node there. Duplicates: decide a policy (reject, or always go one side); here they're rejected.

**Contains:** same walk; found if you land on an equal value, absent if you fall off.

**min / max:** walk all the way left (min) or all the way right (max).

**Remove** — find the node by the same walk, then handle by child count:
- *Leaf:* drop it (return null to the parent).
- *One child:* splice the node out, return its single child to the parent.
- *Two children:* don't delete the node — overwrite its value with its in-order **successor** (the smallest value in the right subtree), then recursively remove that successor from the right subtree. The successor is the leftmost node of the right subtree, so it has no left child → its own removal lands in the leaf or one-child case. No orphans, no infinite recursion. (Predecessor — largest in left subtree — works symmetrically.)

The trick that makes remove clean: recursive helpers take a subtree and **return the new subtree root**, and the caller reassigns its child pointer. That return-and-reassign handles all the relinking, so you never manually null out parent pointers (which would orphan a successor's child).

**Traversals** (same recursion skeleton, only the visit position moves):
- **in-order** (left, node, right) → sorted output; the defining BST traversal.
- **pre-order** (node, left, right) → serialize / copy a tree.
- **post-order** (left, right, node) → process children before parent (safe deletion).
- **Level Order**. This is breadth first search method. We will have a queue were we will add the children as we get to the parent and so on. 

### Interview hooks
- Validate a BST → in-order must be strictly increasing.
- Kth smallest → in-order, stop at k.
- Lowest common ancestor in a BST → walk down comparing both targets to the current node.


---

## Data Structure name

Description

### Positives
- 

### Negatives
- 

### Algorithm / thought process