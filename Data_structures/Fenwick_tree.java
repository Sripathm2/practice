package Data_structures;
import java.util.Objects;

// Fenwick tree (Binary Indexed Tree) over long values, fixed size.
// External indices are 0-based; the internal tree is 1-based and hidden.
// Supports point update and prefix/range sum, each O(log n); O(n) construction.
public class Fenwick_tree {
    private long[] tree;   // 1-indexed; tree[0] unused
    private int n;

    // Construct a tree of n zeros.
    // Throw IllegalArgumentException if n < 0.
    public Fenwick_tree(int n) {
        if(n < 0){
            throw new IllegalArgumentException();
        }
        this.n = n+1;
        this.tree = new long[this.n];
    }

    // Construct a tree from values (0-indexed) in O(n).
    // Throw NullPointerException if values is null.
    public Fenwick_tree(long[] values) {
        this.n = values.length+1;
        this.tree = new long[this.n];
        for(int index = 0; index < values.length; index++){
            this.tree[index+1] = values[index];
        }
        for(int index = 0; index < this.n; index ++){
            if(index+this.LSB(index) < this.n){
                this.tree[index+this.LSB(index)] +=  this.tree[index];
            }
        }
    }

    // Return the number of elements.
    public int size() {
        return this.n-1;
    }

    private int LSB(int i){
        return i & (-i);
    }

    // Add delta to the element at index i (0-indexed).
    // Throw IndexOutOfBoundsException if i not in [0, n).
    public void update(int i, long delta) {
        if(i < 0 || i >= this.n-1){
            throw new IndexOutOfBoundsException();
        }
        i += 1;
        while(i < this.n){
            this.tree[i] += delta;
            i += LSB(i);
        }
    }

    // Set the element at index i (0-indexed) to value.
    // Throw IndexOutOfBoundsException if i not in [0, n).
    public void set(int i, long value) {
        long delta = value - this.rangeSum(i, i);
        this.update(i, delta);
    }

    // Return the sum of elements at indices 0..i inclusive (0-indexed).
    // Throw IndexOutOfBoundsException if i not in [0, n).
    public long prefixSum(int i) {
        if(i < 0 || i >= this.n -1 ){
            throw new IndexOutOfBoundsException();
        }
        long sum = 0;
        i += 1;
        while(i > 0){
            sum += this.tree[i];
            i -= this.LSB(i);
        }
        return sum;
    }

    // Return the sum of elements at indices l..r inclusive (0-indexed).
    // Throw IndexOutOfBoundsException if l < 0 or r >= n.
    // Throw IllegalArgumentException if l > r.
    public long rangeSum(int l, int r) {
        if(l<0 || l >= this.n || r<0 || r >= this.n){
            throw new IndexOutOfBoundsException();
        }else if(l>r){
            throw new IllegalArgumentException();
        }
        if(l==0){
            return this.prefixSum(r);
        }
        return this.prefixSum(r) - this.prefixSum(l-1);
    }

    // Return "[a0, a1, ..., a(n-1)]" of the logical element values.
    @Override
    public String toString() {
        String output = "[";

        for(int index = 0; index < this.size(); index ++){
            output += this.rangeSum(index, index);
            output += ", ";
        }
        if(this.size() > 0){
            output = output.substring(0, output.length()-2);
        }
        output += "]";
        return output;
    }
}

class Fenwick_tree_Main {
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

    // brute-force references over a plain mirror array
    private static long brutePrefix(long[] a, int i) {
        long s = 0;
        for (int k = 0; k <= i; k++) s += a[k];
        return s;
    }
    private static long bruteRange(long[] a, int l, int r) {
        long s = 0;
        for (int k = l; k <= r; k++) s += a[k];
        return s;
    }

    public static void main(String[] args) {
        // --- All zeros ---
        Fenwick_tree z = new Fenwick_tree(5);
        checkEquals("zeros size",       5,   z.size());
        checkEquals("zeros prefix 0",   0L,  z.prefixSum(0));
        checkEquals("zeros prefix 4",   0L,  z.prefixSum(4));
        checkEquals("zeros range 0..4", 0L,  z.rangeSum(0, 4));
        checkEquals("zeros toString",   "[0, 0, 0, 0, 0]", z.toString());

        // --- Build from values, validate every prefix and a few ranges ---
        long[] a = {3, 2, -1, 6, 5, 4, -3, 3};
        Fenwick_tree ft = new Fenwick_tree(a);
        checkEquals("build size", 8, ft.size());
        boolean prefixOk = true;
        for (int i = 0; i < a.length; i++) {
            if (ft.prefixSum(i) != brutePrefix(a, i)) { prefixOk = false; break; }
        }
        checkTrue("all prefixes match brute force", prefixOk);
        checkEquals("range 2..5", bruteRange(a, 2, 5), ft.rangeSum(2, 5));
        checkEquals("range 0..7", bruteRange(a, 0, 7), ft.rangeSum(0, 7));
        checkEquals("single elem range 3..3", a[3], ft.rangeSum(3, 3));
        checkEquals("full prefix == total",   19L, ft.prefixSum(7));

        // --- Point update: add 10 at index 3 ---
        ft.update(3, 10);
        a[3] += 10;
        boolean afterUpdate = true;
        for (int i = 0; i < a.length; i++) {
            if (ft.prefixSum(i) != brutePrefix(a, i)) { afterUpdate = false; break; }
        }
        checkTrue("prefixes match after update", afterUpdate);
        checkEquals("updated single 3..3", a[3], ft.rangeSum(3, 3));

        // --- set: overwrite index 0 to 100 ---
        ft.set(0, 100);
        a[0] = 100;
        checkEquals("set elem 0..0", 100L, ft.rangeSum(0, 0));
        checkEquals("set new total",  brutePrefix(a, 7), ft.prefixSum(7));

        // --- negative set and update ---
        ft.update(6, -7);
        a[6] += -7;
        checkEquals("range after negative update", bruteRange(a, 4, 7), ft.rangeSum(4, 7));

        // --- Bounds ---
        final Fenwick_tree fb = new Fenwick_tree(4);
        checkThrows("update -1",      IndexOutOfBoundsException.class, () -> fb.update(-1, 1));
        checkThrows("update n",       IndexOutOfBoundsException.class, () -> fb.update(4, 1));
        checkThrows("set n",          IndexOutOfBoundsException.class, () -> fb.set(4, 1));
        checkThrows("prefix -1",      IndexOutOfBoundsException.class, () -> fb.prefixSum(-1));
        checkThrows("prefix n",       IndexOutOfBoundsException.class, () -> fb.prefixSum(4));
        checkThrows("range r>=n",     IndexOutOfBoundsException.class, () -> fb.rangeSum(0, 4));
        checkThrows("range l<0",      IndexOutOfBoundsException.class, () -> fb.rangeSum(-1, 2));
        checkThrows("range l>r",      IllegalArgumentException.class,  () -> fb.rangeSum(2, 1));

        // --- Constructor guards ---
        checkThrows("negative size",  IllegalArgumentException.class, () -> new Fenwick_tree(-1));
        checkThrows("null values",    NullPointerException.class,     () -> new Fenwick_tree((long[]) null));

        // --- n = 1 edge ---
        Fenwick_tree one = new Fenwick_tree(new long[]{42});
        checkEquals("n=1 size",       1,   one.size());
        checkEquals("n=1 prefix",     42L, one.prefixSum(0));
        checkEquals("n=1 range",      42L, one.rangeSum(0, 0));
        one.update(0, 8);
        checkEquals("n=1 after update", 50L, one.rangeSum(0, 0));

        // --- n = 0 edge ---
        Fenwick_tree zero = new Fenwick_tree(0);
        checkEquals("n=0 size", 0, zero.size());

        // --- Stress: random updates vs brute-force mirror ---
        java.util.Random rng = new java.util.Random(42);
        int M = 500;
        long[] mirror = new long[M];
        Fenwick_tree big = new Fenwick_tree(M);
        for (int op = 0; op < 5000; op++) {
            int idx = rng.nextInt(M);
            long delta = rng.nextInt(201) - 100;   // [-100, 100]
            big.update(idx, delta);
            mirror[idx] += delta;
        }
        boolean stressPrefix = true;
        for (int i = 0; i < M; i++) {
            if (big.prefixSum(i) != brutePrefix(mirror, i)) { stressPrefix = false; break; }
        }
        checkTrue("stress: all prefixes match", stressPrefix);
        boolean stressRange = true;
        for (int t = 0; t < 200; t++) {
            int l = rng.nextInt(M), r = rng.nextInt(M);
            if (l > r) { int tmp = l; l = r; r = tmp; }
            if (big.rangeSum(l, r) != bruteRange(mirror, l, r)) { stressRange = false; break; }
        }
        checkTrue("stress: sampled ranges match", stressRange);

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}