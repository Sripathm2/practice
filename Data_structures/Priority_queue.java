package Data_structures;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Priority_queue<E> {

    // Wrapper that lets the underlying min-heap order elements by an arbitrary Comparator.
    // The Heap requires Comparable; we satisfy that by delegating compareTo to the Comparator.
    private static class Entry<E> implements Comparable<Entry<E>> {
        final E value;
        final Comparator<? super E> cmp;
        Entry(E value, Comparator<? super E> cmp) {
            this.value = value;
            this.cmp = cmp;
        }
        @Override
        public int compareTo(Entry<E> other) {
            return cmp.compare(this.value, other.value);
        }
    }

    private Heap<Entry<E>> heap;
    private Comparator<? super E> cmp;

    // Construct an empty priority queue using natural ordering (min-priority).
    // Throws ClassCastException at insert time if E does not implement Comparable.
    public Priority_queue() {

    }

    // Construct an empty priority queue ordered by the given Comparator.
    // For a max-priority queue, pass Comparator.reverseOrder().
    public Priority_queue(Comparator<? super E> cmp) {

    }

    // Construct a priority queue from the given values using O(n) heapify, natural ordering.
    public Priority_queue(E[] values) {

    }

    // Construct a priority queue from the given values using O(n) heapify, given Comparator.
    public Priority_queue(E[] values, Comparator<? super E> cmp) {

    }

    // Return the number of elements.
    public int size() {
        return 0;
    }

    // Return true if empty.
    public boolean isEmpty() {
        return false;
    }

    // Add value. O(log n). Throw NullPointerException if value is null.
    public void offer(E value) {

    }

    // Return (without removing) the highest-priority element. O(1).
    // Throw NoSuchElementException if empty.
    public E peek() {
        return null;
    }

    // Remove and return the highest-priority element. O(log n).
    // Throw NoSuchElementException if empty.
    public E poll() {
        return null;
    }

    // Return true if value is present anywhere. O(n).
    public boolean contains(E value) {
        return false;
    }

    // Remove the first occurrence of value. Return true if removed, false if not present.
    // O(n) to find, O(log n) to restore order.
    public boolean remove(E value) {
        return false;
    }

    // Remove all elements.
    public void clear() {

    }

    // Return "[v0, v1, ..., v_{size-1}]" of the underlying heap's level-order layout
    // (unwrapping Entry to value). Not sorted order.
    @Override
    public String toString() {
        return "";
    }
}

class Priority_queue_Main {
    private static int passed = 0;
    private static int failed = 0;

    private static void checkEquals(String name, Object expected, Object actual) {
        if (Objects.equals(expected, actual)) {
            passed++;
            System.out.println("PASS: " + name);
        } else {
            failed++;
            System.out.println("FAIL: " + name + " — expected <" + expected + ">, got <" + actual + ">");
        }
    }

    private static void checkThrows(String name, Class<? extends Throwable> expected, Runnable r) {
        try {
            r.run();
            failed++;
            System.out.println("FAIL: " + name + " — expected " + expected.getSimpleName() + ", none thrown");
        } catch (Throwable t) {
            if (expected.isInstance(t)) {
                passed++;
                System.out.println("PASS: " + name);
            } else {
                failed++;
                System.out.println("FAIL: " + name + " — expected " + expected.getSimpleName()
                        + ", got " + t.getClass().getSimpleName());
            }
        }
    }

    public static void main(String[] args) {
        // --- Min-priority via natural ordering ---
        Priority_queue<Integer> pq = new Priority_queue<>();
        checkEquals("new size",    0,    pq.size());
        checkEquals("new isEmpty", true, pq.isEmpty());

        pq.offer(5); pq.offer(2); pq.offer(8); pq.offer(1); pq.offer(9);
        checkEquals("size after 5 offers", 5, pq.size());
        checkEquals("peek is min",         1, pq.peek());
        checkEquals("size unchanged",      5, pq.size());

        int[] minOrder = {1, 2, 5, 8, 9};
        for (int i = 0; i < minOrder.length; i++) {
            checkEquals("min poll #" + i, minOrder[i], pq.poll());
        }
        checkEquals("empty after drain", true, pq.isEmpty());

        // --- Empty access throws ---
        final Priority_queue<Integer> empty = pq;
        checkThrows("peek on empty", NoSuchElementException.class, () -> empty.peek());
        checkThrows("poll on empty", NoSuchElementException.class, () -> empty.poll());

        // --- Max-priority via reverseOrder ---
        Priority_queue<Integer> max = new Priority_queue<>(Comparator.reverseOrder());
        for (int v : new int[]{5, 2, 8, 1, 9}) max.offer(v);
        checkEquals("max peek", 9, max.peek());
        int[] maxOrder = {9, 8, 5, 2, 1};
        for (int i = 0; i < maxOrder.length; i++) {
            checkEquals("max poll #" + i, maxOrder[i], max.poll());
        }

        // --- Custom Comparator: order strings by length, then lex ---
        Comparator<String> byLenThenLex = Comparator
                .comparingInt(String::length)
                .thenComparing(Comparator.naturalOrder());
        Priority_queue<String> byLen = new Priority_queue<>(byLenThenLex);
        for (String s : new String[]{"banana", "kiwi", "fig", "apple", "pear"}) byLen.offer(s);
        checkEquals("len-pq poll 1", "fig",    byLen.poll());   // 3
        checkEquals("len-pq poll 2", "kiwi",   byLen.poll());   // 4
        checkEquals("len-pq poll 3", "pear",   byLen.poll());   // 4 (tie broken by lex)
        checkEquals("len-pq poll 4", "apple",  byLen.poll());   // 5
        checkEquals("len-pq poll 5", "banana", byLen.poll());   // 6

        // --- Heapify constructors (O(n) build) ---
        Integer[] init = {9, 4, 7, 1, 3, 8, 2, 6, 5};
        Priority_queue<Integer> built = new Priority_queue<>(init);
        checkEquals("heapify size", 9, built.size());
        checkEquals("heapify peek", 1, built.peek());
        int[] sorted = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        for (int i = 0; i < sorted.length; i++) {
            checkEquals("heapify poll #" + i, sorted[i], built.poll());
        }

        Priority_queue<Integer> builtMax = new Priority_queue<>(init, Comparator.reverseOrder());
        checkEquals("heapify max peek", 9, builtMax.peek());
        int[] sortedDesc = {9, 8, 7, 6, 5, 4, 3, 2, 1};
        for (int i = 0; i < sortedDesc.length; i++) {
            checkEquals("heapify max poll #" + i, sortedDesc[i], builtMax.poll());
        }

        // --- Duplicates ---
        Priority_queue<Integer> dup = new Priority_queue<>();
        dup.offer(2); dup.offer(1); dup.offer(2); dup.offer(1); dup.offer(3);
        checkEquals("dup poll 1", 1, dup.poll());
        checkEquals("dup poll 2", 1, dup.poll());
        checkEquals("dup poll 3", 2, dup.poll());
        checkEquals("dup poll 4", 2, dup.poll());
        checkEquals("dup poll 5", 3, dup.poll());

        // --- contains / remove ---
        Priority_queue<Integer> rm = new Priority_queue<>();
        for (int v : new int[]{5, 3, 8, 1, 9, 2, 7, 4, 6}) rm.offer(v);
        checkEquals("contains present", true,  rm.contains(7));
        checkEquals("contains absent",  false, rm.contains(99));

        checkEquals("remove min(1)",       true,  rm.remove(1));
        checkEquals("size after remove",   8,     rm.size());
        checkEquals("peek after remove",   2,     rm.peek());

        checkEquals("remove middle(5)",    true,  rm.remove(5));
        checkEquals("remove absent",       false, rm.remove(999));
        checkEquals("size after",          7,     rm.size());

        int[] remaining = {2, 3, 4, 6, 7, 8, 9};
        for (int i = 0; i < remaining.length; i++) {
            checkEquals("post-remove poll #" + i, remaining[i], rm.poll());
        }

        // --- Null rejected ---
        Priority_queue<Integer> nn = new Priority_queue<>();
        checkThrows("offer(null) throws NPE", NullPointerException.class, () -> nn.offer(null));
        checkEquals("size still 0", 0, nn.size());

        // --- Clear ---
        Priority_queue<Integer> cl = new Priority_queue<>();
        cl.offer(1); cl.offer(2); cl.offer(3);
        cl.clear();
        checkEquals("size after clear",    0,    cl.size());
        checkEquals("isEmpty after clear", true, cl.isEmpty());
        cl.offer(42);
        checkEquals("usable after clear",  42,   cl.peek());

        // --- Stress: heap-sort style drain ---
        int N = 500;
        Priority_queue<Integer> big = new Priority_queue<>();
        java.util.Random rng = new java.util.Random(42);
        int[] vals = new int[N];
        for (int i = 0; i < N; i++) {
            vals[i] = rng.nextInt(10000);
            big.offer(vals[i]);
        }
        java.util.Arrays.sort(vals);
        for (int i = 0; i < N; i++) {
            int got = big.poll();
            if (got != vals[i]) {
                failed++;
                System.out.println("FAIL: stress poll #" + i + " expected " + vals[i] + " got " + got);
                break;
            }
        }
        checkEquals("stress empty after drain", true, big.isEmpty());

        // --- Object with custom comparator (priorities decoupled from value) ---
        // Tasks ordered by priority field, lower = more urgent.
        class Task {
            final String name;
            final int prio;
            Task(String name, int prio) { this.name = name; this.prio = prio; }
        }
        Priority_queue<Task> tasks = new Priority_queue<>(Comparator.comparingInt(t -> t.prio));
        tasks.offer(new Task("backup",  5));
        tasks.offer(new Task("alert",   1));
        tasks.offer(new Task("cleanup", 3));
        tasks.offer(new Task("report",  2));
        checkEquals("task poll 1", "alert",   tasks.poll().name);
        checkEquals("task poll 2", "report",  tasks.poll().name);
        checkEquals("task poll 3", "cleanup", tasks.poll().name);
        checkEquals("task poll 4", "backup",  tasks.poll().name);

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}