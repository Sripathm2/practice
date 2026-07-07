package Problems;
import java.util.Objects;

// Tiling: count the number of ways to completely fill a 1 x n strip of slots
// using tiles of length 1 and length 2. (Classic Fibonacci-shaped DP.)
public class Tiling {

    // Return the number of distinct ways to tile n slots with size-1 and size-2 tiles.
    // n == 0 has exactly one tiling (the empty tiling). Returns long.
    // Throw IllegalArgumentException if n < 0.
    public static long countWays(int n) {
        if(n < 0){
            throw new IllegalArgumentException();
        }
        long[] slot_count = new long[n+3];
        slot_count[0] = 1;
        slot_count[1] = 1;
        slot_count[2] = 2;
        for(int i = 3; i < n + 1; i++){
            slot_count[i] = slot_count[i-1] + slot_count[i-2];
        }
        return slot_count[n];
    }
}

class Tiling_Main {
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

    // Ground-truth: plain (exponential) recursion, for small n.
    private static long bruteWays(int n) {
        if (n < 0) return 0;
        if (n == 0) return 1;
        if (n == 1) return 1;
        return bruteWays(n - 1) + bruteWays(n - 2);
    }

    public static void main(String[] args) {
        // --- Small known values (1,1,2,3,5,8,13,21,34,55,89,...) ---
        checkEquals("n=0", 1L,  Tiling.countWays(0));
        checkEquals("n=1", 1L,  Tiling.countWays(1));
        checkEquals("n=2", 2L,  Tiling.countWays(2));   // [1,1] [2]
        checkEquals("n=3", 3L,  Tiling.countWays(3));   // [1,1,1] [1,2] [2,1]
        checkEquals("n=4", 5L,  Tiling.countWays(4));
        checkEquals("n=5", 8L,  Tiling.countWays(5));
        checkEquals("n=10", 89L, Tiling.countWays(10));

        // --- Cross-check against brute recursion for a range ---
        boolean ok = true;
        for (int n = 0; n <= 25 && ok; n++)
            if (Tiling.countWays(n) != bruteWays(n)) ok = false;
        if (ok) { passed++; System.out.println("PASS: brute-force cross-check [0,25]"); }
        else    { failed++; System.out.println("FAIL: brute-force cross-check [0,25]"); }

        // --- Larger value that needs long (fits: n=90 is within long range) ---
        // ways(90) = Fib(91) = 4660046610375530309
        checkEquals("n=90 (long range)", 4660046610375530309L, Tiling.countWays(90));

        // --- Error ---
        checkThrows("negative", IllegalArgumentException.class, () -> Tiling.countWays(-1));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}