package Problems;
import java.util.Objects;

// Generalized tiling: count the ways to fill a 1 x n strip using tiles of
// various lengths, where a given length may come in several colors.
//
// Tiles are described by two parallel arrays:
//   lengths[i] = a tile length that is available
//   colors[i]  = how many distinct colors exist for that length
// A tiling is a left-to-right sequence of colored tiles that exactly fills n.
//
// Recurrence (look at the last tile placed):
//   ways(0) = 1
//   ways(m) = sum over i of  colors[i] * ways(m - lengths[i])   for lengths[i] <= m
public class Tiling_general {

    // Return the number of ways to tile n slots given the available tile types.
    // Throw NullPointerException if lengths or colors is null.
    // Throw IllegalArgumentException if n < 0, lengths.length != colors.length,
    // any length < 1, or any color count < 1.
    public static long countWays(int n, int[] lengths, int[] colors) {
        if(n < 0 || lengths.length!=colors.length){
            throw new IllegalArgumentException();
        }
        for(int i = 0;i<lengths.length;i++){
            if(lengths[i] < 1 || colors[i]<1){
                throw new IllegalArgumentException();
            }
        }
        long[] slot_count = new long[n+3];
        slot_count[0] = 1;

        for(int i =1; i < slot_count.length; i++){
            long sum = 0;
            for(int j=0;j<lengths.length;j++){
                if((i-lengths[j]) >= 0 ){
                    sum += slot_count[i-lengths[j]]*colors[j];
                }
            }
            slot_count[i] = sum;
        }


        return slot_count[n];
    }
}

class Tiling_general_Main {
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

    // Ground truth: exponential recursion over the same recurrence.
    private static long brute(int n, int[] L, int[] C) {
        if (n == 0) return 1;
        if (n < 0)  return 0;
        long s = 0;
        for (int i = 0; i < L.length; i++) s += (long) C[i] * brute(n - L[i], L, C);
        return s;
    }

    public static void main(String[] args) {
        int[] L12 = {1, 2};

        // --- Reduces to classic Fibonacci tiling when each length has 1 color ---
        int[] one1 = {1, 1};
        checkEquals("classic n=0", 1L, Tiling_general.countWays(0, L12, one1));
        checkEquals("classic n=1", 1L, Tiling_general.countWays(1, L12, one1));
        checkEquals("classic n=5", 8L, Tiling_general.countWays(5, L12, one1));
        checkEquals("classic n=10", 89L, Tiling_general.countWays(10, L12, one1));

        // --- Colors: two colors of length-1, one of length-2 ---
        int[] col = {2, 1};
        checkEquals("colored n=1", 2L,  Tiling_general.countWays(1, L12, col));   // r,b
        checkEquals("colored n=2", 5L,  Tiling_general.countWays(2, L12, col));   // rr,rb,br,bb,g
        checkEquals("colored n=3", 12L, Tiling_general.countWays(3, L12, col));   // 2*5 + 1*2

        // --- Single length, k colors -> k^n ---
        checkEquals("3 colors len1, n=0", 1L,  Tiling_general.countWays(0, new int[]{1}, new int[]{3}));
        checkEquals("3 colors len1, n=1", 3L,  Tiling_general.countWays(1, new int[]{1}, new int[]{3}));
        checkEquals("3 colors len1, n=3", 27L, Tiling_general.countWays(3, new int[]{1}, new int[]{3}));

        // --- Single length 2 only: tileable only for even n ---
        checkEquals("len2 only n=1", 0L, Tiling_general.countWays(1, new int[]{2}, new int[]{1}));
        checkEquals("len2 only n=2", 1L, Tiling_general.countWays(2, new int[]{2}, new int[]{1}));
        checkEquals("len2 only n=3", 0L, Tiling_general.countWays(3, new int[]{2}, new int[]{1}));
        checkEquals("len2 only n=4", 1L, Tiling_general.countWays(4, new int[]{2}, new int[]{1}));

        // --- Three lengths, one color each -> Tribonacci ---
        int[] L123 = {1, 2, 3}, one3 = {1, 1, 1};
        checkEquals("tribonacci n=6", 24L, Tiling_general.countWays(6, L123, one3)); // 1,1,2,4,7,13,24

        // --- No tiles available: only n=0 is tileable ---
        checkEquals("no tiles n=0", 1L, Tiling_general.countWays(0, new int[]{}, new int[]{}));
        checkEquals("no tiles n=1", 0L, Tiling_general.countWays(1, new int[]{}, new int[]{}));

        // --- Cross-check against brute over varied tile sets and n ---
        java.util.Random rng = new java.util.Random(61);
        int[][] sets = {
            {1, 2}, {1, 1},
            {1, 2, 3}, {2, 1, 3},
            {2, 3}, {1, 1},
            {1, 3}, {4, 2},
        };
        boolean ok = true;
        for (int s = 0; s < sets.length && ok; s += 2) {
            int[] L = sets[s], C = sets[s + 1];
            for (int n = 0; n <= 15 && ok; n++)
                if (Tiling_general.countWays(n, L, C) != brute(n, L, C)) ok = false;
        }
        if (ok) { passed++; System.out.println("PASS: brute-force cross-check"); }
        else    { failed++; System.out.println("FAIL: brute-force cross-check"); }

        // --- Validation ---
        checkThrows("null lengths", NullPointerException.class,
                () -> Tiling_general.countWays(3, null, one1));
        checkThrows("null colors", NullPointerException.class,
                () -> Tiling_general.countWays(3, L12, null));
        checkThrows("negative n", IllegalArgumentException.class,
                () -> Tiling_general.countWays(-1, L12, one1));
        checkThrows("length mismatch", IllegalArgumentException.class,
                () -> Tiling_general.countWays(3, L12, new int[]{1}));
        checkThrows("length < 1", IllegalArgumentException.class,
                () -> Tiling_general.countWays(3, new int[]{0, 2}, new int[]{1, 1}));
        checkThrows("color < 1", IllegalArgumentException.class,
                () -> Tiling_general.countWays(3, L12, new int[]{1, 0}));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}