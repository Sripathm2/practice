package Data_structures;
import java.util.Objects;
import java.util.NoSuchElementException;

// Indexed priority queue (min-heap), fixed capacity. Keys are integer indices
// in [0, N). Each key maps to a value/priority; the queue supports updating and
// deleting an arbitrary key's priority in O(log n).
//
// Three parallel arrays:
//   values[ki] : the priority of key ki (never moves during swim/sink)
//   pm[ki]     : heap position of key ki, or -1 if absent   (position map)
//   im[pos]    : key index living at heap position pos       (inverse map)
// Invariant: pm[im[pos]] == pos and im[pm[ki]] == ki.
public class Indexed_priority_queue<V extends Comparable<V>> {
    private Object[] values;   // values[ki]
    private int[] pm;          // pm[ki]  = heap position of ki, or -1
    private int[] im;          // im[pos] = ki at heap position pos, or -1
    private int sz;            // number of keys currently in the queue
    private int capacity;      // N

    // Construct an empty IPQ with key domain [0, maxN).
    // Throw IllegalArgumentException if maxN < 0.
    public Indexed_priority_queue(int maxN) {
        if(maxN < 1){
            throw new IllegalArgumentException();
        }
        this.capacity = maxN;
        this.sz = 0;
        this.values = new Object[this.capacity];
        this.pm = new int[this.capacity];
        this.im = new int[this.capacity];

        for(int i =0;i<this.capacity; i++){
            this.im[i] = -1;
            this.pm[i] = -1;
        }
    }

    // --- private heap helpers ---

    // True if the priority at heap position i is less than at position j.
    @SuppressWarnings("unchecked")
    private boolean less(int i, int j) {
        V a = (V) values[im[i]];
        V b = (V) values[im[j]];
        return a.compareTo(b) < 0;
    }

    // Swap heap positions i and j: exchange im[i], im[j] and fix pm for both.
    // Does not touch values.
    private void swap(int i, int j) {
        int temp = im[i];
        im[i] = im[j];
        im[j] = temp;
        pm[im[i]] = i;
        pm[im[j]] = j;
        
    }

    // Float the element at heap position i up while it is smaller than its parent.
    private void swim(int i) {
        int parent = (i-1)/2;
        while(i > 0 && less(i, parent)){
            swap(i, parent);
            i = parent;
            parent = (i-1)/2;
        }
    }

    // Sink the element at heap position i down while a child is smaller.
    private void sink(int i) {
        int child1 = i*2 + 1;
        int child2 = i*2 + 2;
        int selected_child = child1;
        if(child1 < this.sz && child2 < this.sz && less(child2, child1)){
            selected_child = child2;
        }

        while(selected_child < this.sz && less(selected_child, i)){
            swap(i, selected_child);
            i = selected_child;
            child1 = i*2 + 1;
            child2 = i*2 + 2;
            selected_child = child1;
            if(child1 < this.sz && child2 < this.sz && less(child2, child1)){
                selected_child = child2;
            }
        }
    }

    // --- public API (all key indices are 0-based in [0, N)) ---

    // Number of keys currently in the queue.
    public int size() {
        return this.sz;
    }

    // True if empty.
    public boolean isEmpty() {
        return this.sz == 0 ;
    }

    // True if key ki is present.
    // Throw IndexOutOfBoundsException if ki not in [0, N).
    public boolean contains(int ki) {
        if(ki < 0 || ki > this.capacity-1){
            throw new IndexOutOfBoundsException();
        }
        return pm[ki] != -1;
    }

    // Insert key ki with the given value.
    // Throw IndexOutOfBoundsException if ki not in [0, N).
    // Throw IllegalArgumentException if ki is already present.
    // Throw NullPointerException if value is null.
    public void insert(int ki, V value) {
        if(ki < 0 || ki >= this.capacity){
            throw new IndexOutOfBoundsException();
        }else if(contains(ki)){
            throw new IllegalArgumentException();
        }else if(value == null){
            throw new NullPointerException();
        } else {
            this.values[ki] = value;
            this.pm[ki] = this.sz;
            this.im[this.sz] = ki;
            swim(this.sz);
            this.sz += 1;
        }
    }

    // Return the value associated with key ki.
    // Throw IndexOutOfBoundsException if ki not in [0, N).
    // Throw NoSuchElementException if ki is absent.
    @SuppressWarnings("unchecked")
    public V valueOf(int ki) {
        if(ki < 0 || ki >= this.capacity){
            throw new IndexOutOfBoundsException();
        }else if(!contains(ki)){
            throw new NoSuchElementException();
        }
        return (V)this.values[ki];
    }

    // Change the value of an existing key ki.
    // Throw IndexOutOfBoundsException if ki not in [0, N).
    // Throw NoSuchElementException if ki is absent.
    // Throw NullPointerException if value is null.
    public void update(int ki, V value) {
        if(ki < 0 || ki >= this.capacity){
            throw new IndexOutOfBoundsException();
        }else if(!contains(ki)){
            throw new NoSuchElementException();
        }else if(value == null){
            throw new NullPointerException();
        } else {
            this.values[ki] = value;
            final int i = pm[ki];
            sink(i);
            swim(i);
        }
    }

    // Remove key ki and return its value.
    // Throw IndexOutOfBoundsException if ki not in [0, N).
    // Throw NoSuchElementException if ki is absent.
    @SuppressWarnings("unchecked")
    public V delete(int ki) {
        if(ki < 0 || ki >= this.capacity){
            throw new IndexOutOfBoundsException();
        }else if(!contains(ki)){
            throw new NoSuchElementException();
        }
        final int i = pm[ki];
        this.sz -= 1;
        swap(i, sz);
        sink(i);
        swim(i);
        V returnv = (V) this.values[ki];
        this.values[ki] = null;
        pm[ki] = -1;
        im[sz] = -1;
        return returnv;
    }

    // Return the key index with the smallest value.
    // Throw NoSuchElementException if empty.
    public int peekMinKeyIndex() {
        if(this.sz == 0){
            throw new NoSuchElementException();
        }
        return im[0];
    }

    // Return the smallest value.
    // Throw NoSuchElementException if empty.
    @SuppressWarnings("unchecked")
    public V peekMinValue() {
        if(this.sz == 0){
            throw new NoSuchElementException();
        }
        return (V)this.values[im[0]];
    }

    // Remove the key with the smallest value and return its key index.
    // Throw NoSuchElementException if empty.
    public int pollMinKeyIndex() {
        if(this.sz == 0){
            throw new NoSuchElementException();
        }
        int returnv = im[0];
        delete(returnv);
        return returnv;
    }

    // Diagnostic: true iff pm/im are consistent inverses over the current heap
    // and the min-heap order holds (every parent's value <= its children's).
    // Empty queue is valid.
    public boolean isValid() {
        if(this.sz == 0){
            return true;
        }
        for(int i=0; i< this.sz; i++){
            int parent = (i-1)/2;
            if(pm[im[i]] != i){
                return false;
            }
            if(less(i,parent)){
                return false;
            }
        }

        return true;
    }
}

class Indexed_priority_queue_Main {
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

    private static void checkTrue(String name, boolean cond) {
        if (cond) { passed++; System.out.println("PASS: " + name); }
        else      { failed++; System.out.println("FAIL: " + name); }
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
        // --- Empty ---
        Indexed_priority_queue<Integer> e = new Indexed_priority_queue<>(8);
        checkEquals("empty size",     0,     e.size());
        checkTrue ("empty isEmpty",          e.isEmpty());
        checkEquals("empty contains", false, e.contains(0));
        checkTrue ("empty valid",            e.isValid());
        checkThrows("empty peekKey",   NoSuchElementException.class, e::peekMinKeyIndex);
        checkThrows("empty peekVal",   NoSuchElementException.class, e::peekMinValue);
        checkThrows("empty poll",      NoSuchElementException.class, e::pollMinKeyIndex);
        checkThrows("empty valueOf",   NoSuchElementException.class, () -> e.valueOf(0));
        checkThrows("empty delete",    NoSuchElementException.class, () -> e.delete(0));

        // --- Insert + basic queries ---
        // ki -> value:  0:50  1:10  2:30  3:20  4:40
        Indexed_priority_queue<Integer> q = new Indexed_priority_queue<>(8);
        q.insert(0, 50);
        q.insert(1, 10);
        q.insert(2, 30);
        q.insert(3, 20);
        q.insert(4, 40);
        checkEquals("size 5",        5,    q.size());
        checkTrue ("contains 3",           q.contains(3));
        checkEquals("contains 7",    false, q.contains(7));
        checkEquals("valueOf 0",     50,   q.valueOf(0));
        checkEquals("valueOf 3",     20,   q.valueOf(3));
        checkEquals("min key",       1,    q.peekMinKeyIndex());
        checkEquals("min value",     10,   q.peekMinValue());
        checkTrue ("valid after inserts", q.isValid());

        // --- Update: raise key 1 above others; lower key 0 to new min ---
        q.update(1, 100);            // was the min, now the max
        checkEquals("min key after raise", 3, q.peekMinKeyIndex());  // 20
        checkTrue ("valid after raise", q.isValid());
        q.update(0, 5);              // now the smallest
        checkEquals("min key after lower", 0, q.peekMinKeyIndex());
        checkEquals("min value after lower", 5, q.peekMinValue());
        checkTrue ("valid after lower", q.isValid());

        // --- Delete an arbitrary (non-min) key ---
        checkEquals("delete 2 returns value", 30, q.delete(2));
        checkEquals("contains 2 after delete", false, q.contains(2));
        checkEquals("size after delete", 4, q.size());
        checkTrue ("valid after delete", q.isValid());

        // --- Poll drains in ascending value order ---
        // remaining ki:value -> 0:5, 3:20, 4:40, 1:100  => keys 0,3,4,1
        Indexed_priority_queue<Integer> pq = new Indexed_priority_queue<>(8);
        pq.insert(0, 50); pq.insert(1, 10); pq.insert(2, 30); pq.insert(3, 20); pq.insert(4, 40);
        int[] expectedOrder = {1, 3, 2, 4, 0};   // by ascending value
        boolean orderOk = true;
        for (int idx = 0; idx < 5; idx++) {
            if (pq.pollMinKeyIndex() != expectedOrder[idx]) { orderOk = false; break; }
        }
        checkTrue("poll yields ascending-value key order", orderOk);
        checkTrue("empty after draining", pq.isEmpty());

        // --- Bounds / error cases ---
        final Indexed_priority_queue<Integer> b = new Indexed_priority_queue<>(4);
        b.insert(2, 7);
        checkThrows("insert out-of-range hi", IndexOutOfBoundsException.class, () -> b.insert(4, 1));
        checkThrows("insert out-of-range lo", IndexOutOfBoundsException.class, () -> b.insert(-1, 1));
        checkThrows("insert duplicate",       IllegalArgumentException.class,  () -> b.insert(2, 9));
        checkThrows("insert null value",      NullPointerException.class,      () -> b.insert(1, null));
        checkThrows("contains out-of-range",  IndexOutOfBoundsException.class, () -> b.contains(9));
        checkThrows("valueOf absent",         NoSuchElementException.class,    () -> b.valueOf(3));
        checkThrows("update absent",          NoSuchElementException.class,    () -> b.update(3, 1));
        checkThrows("delete absent",          NoSuchElementException.class,    () -> b.delete(3));
        checkThrows("update null value",      NullPointerException.class,      () -> b.update(2, null));

        // --- Fill to capacity, then drain ---
        Indexed_priority_queue<Integer> full = new Indexed_priority_queue<>(6);
        int[] vals = {12, 3, 45, 7, 30, 21};      // value at key index i
        for (int i = 0; i < 6; i++) full.insert(i, vals[i]);
        checkEquals("full size", 6, full.size());
        checkTrue ("full valid", full.isValid());
        // expected key order by ascending value: 3(1),7(3),12(0),21(5),30(4),45(2)
        int[] fullOrder = {1, 3, 0, 5, 4, 2};
        boolean fullOk = true;
        for (int idx = 0; idx < 6; idx++) {
            if (full.pollMinKeyIndex() != fullOrder[idx]) { fullOk = false; break; }
        }
        checkTrue("full drains in order", fullOk);

        // --- Brute-force stress: random insert/update/delete vs a mirror ---
        java.util.Random rng = new java.util.Random(7);
        final int N = 64;
        Indexed_priority_queue<Integer> s = new Indexed_priority_queue<>(N);
        Integer[] mirror = new Integer[N];   // mirror[ki] = value or null if absent
        int present = 0;
        boolean stressOk = true;
        for (int op = 0; op < 4000 && stressOk; op++) {
            int ki = rng.nextInt(N);
            if (mirror[ki] == null) {
                int v = rng.nextInt(1000);
                s.insert(ki, v); mirror[ki] = v; present++;
            } else if (rng.nextBoolean()) {
                int v = rng.nextInt(1000);
                s.update(ki, v); mirror[ki] = v;
            } else {
                s.delete(ki); mirror[ki] = null; present--;
            }
            if (!s.isValid()) { stressOk = false; break; }
            if (present > 0) {
                int min = Integer.MAX_VALUE;
                for (Integer m : mirror) if (m != null && m < min) min = m;
                if (!Objects.equals(min, s.peekMinValue())) { stressOk = false; break; }
            }
            if (s.size() != present) { stressOk = false; break; }
        }
        checkTrue("stress: valid + correct min throughout", stressOk);

        // --- Generic over String values ---
        Indexed_priority_queue<String> str = new Indexed_priority_queue<>(4);
        str.insert(0, "delta"); str.insert(1, "alpha"); str.insert(2, "charlie");
        checkEquals("string min key", 1, str.peekMinKeyIndex());
        checkEquals("string min val", "alpha", str.peekMinValue());
        checkTrue ("string valid", str.isValid());

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}