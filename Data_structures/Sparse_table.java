package Data_structures;
import java.util.Objects;
import java.util.function.BinaryOperator;

// Sparse table for range queries on a STATIC array (no updates).
// The combining function F is supplied as a BinaryOperator<T>: F.apply(a, b).
//
// Two query methods:
//   queryOverlap  - O(1), correct ONLY if F is idempotent/overlap-friendly
//                   (F(x,x) == x): min, max, gcd, bitwise and/or.
//   queryDisjoint - O(log n), correct for any ASSOCIATIVE F (sum, product, ...),
//                   and also works for the idempotent ones.
public class Sparse_table<T> {
    private Object[][] table;   // table[i][j] = F over [j, j + 2^i)
    private int[][] index_table;   // table[i][j] = F over [j, j + 2^i)
    private int[] log;          // log[len] = floor(log2(len))
    private BinaryOperator<T> op;
    private int n;
    private int P;

    // Build a sparse table over arr using op as the combining function.
    // Throw NullPointerException if arr or op is null.
    @SuppressWarnings("unchecked")
    public Sparse_table(T[] arr, BinaryOperator<T> op) {
        this.n = arr.length;
        this.P = (int) (Math.log(this.n)/Math.log(2));

        this.table = new Object[this.P +1][this.n];
        this.index_table = new int[this.P +1][this.n];

        for(int index = 0; index < this.n ; index++){
            this.table[0][index] = arr[index];
            this.index_table[0][index] = index;
        }

        this.op = op;

        this.log = new int[this.n+1];
        for(int index = 2; index <= n; index ++){
            this.log[index] = (int)this.log[index/2] + 1;
        }

        for(int row = 1; row < this.P +1; row ++){
            for(int col = 0; col + (1<<row) <= this.n; col++){
                T left_interval_value = (T)this.table[row-1][col];
                T right_interval_value = (T)this.table[row-1][col+(1<<(row-1))];
                this.table[row][col] = this.op.apply(left_interval_value, right_interval_value);

                if(left_interval_value instanceof Integer){
                    if((int)left_interval_value <= (int)right_interval_value){
                        index_table[row][col] = index_table[row-1][col];
                    }else{
                        index_table[row][col] = index_table[row-1][col + (1<<(row-1))];
                    }
                }
            }
        }
    }

    // Number of elements.
    public int size() {
        return this.n;
    }

    // Range query over [l, r] inclusive, assuming F is idempotent (O(1)).
    // Throw IndexOutOfBoundsException if l < 0 or r >= n.
    // Throw IllegalArgumentException if l > r.
    @SuppressWarnings("unchecked")
    public T queryOverlap(int l, int r) {
        if(l < 0 || r >= this.n){
            throw new IndexOutOfBoundsException();
        }else if(l > r){
            throw new IllegalArgumentException();
        }
        int p = this.log[r-l+1];
        int k = 1 << p;
        T left_interval_value = (T)this.table[p][l];
        T right_interval_value = (T)this.table[p][r-k+1];
        return this.op.apply(left_interval_value, right_interval_value);
    }

    public int queryOverlap_index_min_operation_only(int l, int r) {
        if(l < 0 || r >= this.n){
            throw new IndexOutOfBoundsException();
        }else if(l > r){
            throw new IllegalArgumentException();
        }
        int p = this.log[r-l+1];
        int k = 1 << p;
        int left_interval_value = (int)this.table[p][l];
        int right_interval_value = (int)this.table[p][r-k+1];

        if(left_interval_value <= right_interval_value){
            return this.index_table[p][l];
        }else{
            return this.index_table[p][r-k+1];
        }
    }

    // Range query over [l, r] inclusive for any associative F (O(log n)).
    // Throw IndexOutOfBoundsException if l < 0 or r >= n.
    // Throw IllegalArgumentException if l > r.
    @SuppressWarnings("unchecked")
    public T queryDisjoint(int l, int r) {
        if(l < 0 || r >= this.n){
            throw new IndexOutOfBoundsException();
        }else if(l > r){
            throw new IllegalArgumentException();
        }
        T returnv = null;
        for(int p = this.log[this.n]; p >=0 ; p--){
            if((l + (1<<p)) - 1 <= r){
                if(returnv == null){
                    returnv = (T) this.table[p][l];
                } else{
                    returnv = this.op.apply(returnv, (T) this.table[p][l]);
                }
                l += (1<<p);
            }
        }
        return returnv;
    }
}

class Sparse_table_Main {
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

    // Safe check for an Integer-returning query: a null or thrown result from an
    // unimplemented stub records a clean FAIL instead of crashing the runner.
    private static void checkIntEquals(String name, int expected, java.util.function.Supplier<Integer> actual) {
        Integer got;
        try { got = actual.get(); }
        catch (Throwable t) { failed++; System.out.println("FAIL: " + name + " — threw " + t.getClass().getSimpleName()); return; }
        if (got != null && got == expected) { passed++; System.out.println("PASS: " + name); }
        else { failed++; System.out.println("FAIL: " + name + " — expected <" + expected + ">, got <" + got + ">"); }
    }

    // brute-force references
    private static int bruteMin(int[] a, int l, int r) {
        int m = a[l];
        for (int i = l + 1; i <= r; i++) m = Math.min(m, a[i]);
        return m;
    }
    private static int bruteMax(int[] a, int l, int r) {
        int m = a[l];
        for (int i = l + 1; i <= r; i++) m = Math.max(m, a[i]);
        return m;
    }
    private static long bruteSum(int[] a, int l, int r) {
        long s = 0;
        for (int i = l; i <= r; i++) s += a[i];
        return s;
    }

    public static void main(String[] args) {
        int[] raw = {5, 2, 9, 1, 7, 3, 8, 4, 6, 0};
        Integer[] arr = new Integer[raw.length];
        for (int i = 0; i < raw.length; i++) arr[i] = raw[i];

        // --- MIN table (idempotent): both query methods must agree with brute min ---
        Sparse_table<Integer> mn = new Sparse_table<>(arr, Integer::min);
        checkEquals("size", raw.length, mn.size());
        checkIntEquals("min [0,9] overlap", bruteMin(raw,0,9), () -> mn.queryOverlap(0,9));
        checkIntEquals("min [2,6] overlap", bruteMin(raw,2,6), () -> mn.queryOverlap(2,6));
        checkIntEquals("min [4,4] overlap", bruteMin(raw,4,4), () -> mn.queryOverlap(4,4));
        checkIntEquals("min [3,3] single",  1,                 () -> mn.queryOverlap(3,3));
        checkIntEquals("min [2,6] disjoint",bruteMin(raw,2,6), () -> mn.queryDisjoint(2,6));
        boolean minAll = true;
        try {
            for (int l = 0; l < raw.length && minAll; l++)
                for (int r = l; r < raw.length; r++)
                    if (!Objects.equals(mn.queryOverlap(l,r), bruteMin(raw,l,r))
                     || !Objects.equals(mn.queryDisjoint(l,r), bruteMin(raw,l,r))) { minAll = false; break; }
        } catch (Throwable t) { minAll = false; }
        checkTrue("min: all ranges, both methods", minAll);

        // --- MAX table (idempotent) ---
        Sparse_table<Integer> mx = new Sparse_table<>(arr, Integer::max);
        boolean maxAll = true;
        try {
            for (int l = 0; l < raw.length && maxAll; l++)
                for (int r = l; r < raw.length; r++)
                    if (!Objects.equals(mx.queryOverlap(l,r), bruteMax(raw,l,r))) { maxAll = false; break; }
        } catch (Throwable t) { maxAll = false; }
        checkTrue("max: all ranges overlap", maxAll);

        // --- SUM table (NOT idempotent): only queryDisjoint is correct ---
        Sparse_table<Integer> sm = new Sparse_table<>(arr, Integer::sum);
        checkIntEquals("sum [0,9] disjoint", (int) bruteSum(raw,0,9), () -> sm.queryDisjoint(0,9));
        checkIntEquals("sum [2,6] disjoint", (int) bruteSum(raw,2,6), () -> sm.queryDisjoint(2,6));
        checkIntEquals("sum [5,5] disjoint", raw[5],                  () -> sm.queryDisjoint(5,5));
        boolean sumAll = true;
        try {
            for (int l = 0; l < raw.length && sumAll; l++)
                for (int r = l; r < raw.length; r++)
                    if (!Objects.equals(sm.queryDisjoint(l,r), (int) bruteSum(raw,l,r))) { sumAll = false; break; }
        } catch (Throwable t) { sumAll = false; }
        checkTrue("sum: all ranges disjoint", sumAll);

        // --- Bounds ---
        final Sparse_table<Integer> b = mn;
        checkThrows("l < 0",  IndexOutOfBoundsException.class, () -> b.queryOverlap(-1, 3));
        checkThrows("r >= n", IndexOutOfBoundsException.class, () -> b.queryOverlap(0, 10));
        checkThrows("l > r",  IllegalArgumentException.class,  () -> b.queryOverlap(5, 2));
        checkThrows("disjoint r >= n", IndexOutOfBoundsException.class, () -> b.queryDisjoint(0, 10));
        checkThrows("disjoint l > r",  IllegalArgumentException.class,  () -> b.queryDisjoint(5, 2));

        // --- Null guards ---
        checkThrows("null arr", NullPointerException.class,
                () -> new Sparse_table<Integer>(null, Integer::min));
        checkThrows("null op", NullPointerException.class,
                () -> new Sparse_table<>(arr, null));

        // --- Single-element array ---
        Integer[] one = {42};
        Sparse_table<Integer> o = new Sparse_table<>(one, Integer::min);
        checkEquals("single size", 1, o.size());
        checkIntEquals("single overlap",  42, () -> o.queryOverlap(0,0));
        checkIntEquals("single disjoint", 42, () -> o.queryDisjoint(0,0));

        // --- Generic over String (lexicographic min, idempotent) ---
        String[] words = {"delta","alpha","charlie","bravo","echo"};
        Sparse_table<String> ws = new Sparse_table<>(words, (a, c) -> a.compareTo(c) <= 0 ? a : c);
        checkEquals("string min [0,4]", "alpha", ws.queryOverlap(0,4));
        checkEquals("string min [2,4]", "bravo", ws.queryOverlap(2,4));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}