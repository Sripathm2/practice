package Problems;
import java.util.Objects;

// Tiling Dominoes and Trominoes (LeetCode 790).
// Count the ways to fully tile a 2 x n board using 2x1 dominoes and L-shaped
// trominoes (a tromino covers 3 cells: a 2x2 square minus one corner, in any of
// its 4 orientations). Answer is taken modulo 1_000_000_007, since the count
// grows exponentially.
public class Domino_tromino_tiling {

    public static final long MOD = 1_000_000_007L;
    public static long [][] slot_count; 
    public static int n; 

    // Return the number of ways to tile a 2 x n board with dominoes and
    // trominoes, modulo 1_000_000_007.
    // Throw IllegalArgumentException if n < 0.
    public static long countWays(int input) {
        if(input < 0){
            throw new IllegalArgumentException();
        }
        n = input;
        slot_count = new long[n+1][4];
        for(int i = 0; i < slot_count.length; i++){
            for(int j = 0; j < slot_count[0].length; j++){
                slot_count[i][j] = -1;
            }
        }
        return countWaysRecursive(0,true, true);
    }

    public static long countWaysRecursive(int i, boolean t1, boolean t2){
        if(i==n){
            return 1;
        }
        int state = -1;
        if(t1 && t2){
            state = 3;
        }else if(!t1 && !t2){
            state = 0;
        }else if(t1 && !t2){
            state = 1;
        }else{
            state = 2;
        }
        if(slot_count[i][state] != -1){
            return slot_count[i][state];
        }
        boolean t3 = i+1 < n ? true : false;
        boolean t4 = i+1 < n ? true : false;
        long count  = 0;
        if(state == 3 && t3) count += countWaysRecursive(i+1, false, true);
        if(state == 3 && t4) count += countWaysRecursive(i+1, true, false);
        if(state == 1 && t3 && t4) count += countWaysRecursive(i+1, false, false);
        if(state == 2 && t3 && t4) count += countWaysRecursive(i+1, false, false);
        if(state == 3) count += countWaysRecursive(i+1, true, true);
        if(state == 3  && t3 && t4) count += countWaysRecursive(i+1, false, false);
        if(state == 1 && t3) count += countWaysRecursive(i+1, false, true);
        if(state == 2 && t4) count += countWaysRecursive(i+1, true, false);
        if(state == 0) count += countWaysRecursive(i+1, true, true);
        slot_count[i][state] = count % MOD;
        return slot_count[i][state];
    }
}

class Domino_tromino_tiling_Main {
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

    // Ground truth: exhaustive backtracking over a 2 x n board.
    // grid[c][r] = filled?  (2 rows, n columns)
    private static int N;
    private static boolean[][] grid;

    private static long bruteCount(int n) {
        if (n == 0) return 1;
        N = n;
        grid = new boolean[n][2];
        return rec();
    }
    private static long rec() {
        int fr = -1, fc = -1;
        outer:
        for (int c = 0; c < N; c++)
            for (int r = 0; r < 2; r++)
                if (!grid[c][r]) { fr = r; fc = c; break outer; }
        if (fr == -1) return 1;   // full

        // candidate footprints; each is a list of {r,c} pairs that must include (fr,fc)
        int[][][] cands = {
            {{0, fc}, {1, fc}},                        // vertical domino
            {{fr, fc}, {fr, fc + 1}},                  // horizontal domino
            {{1, fc}, {0, fc + 1}, {1, fc + 1}},       // L: block minus (0,c)
            {{0, fc}, {0, fc + 1}, {1, fc + 1}},       // L: block minus (1,c)
            {{0, fc}, {1, fc}, {1, fc + 1}},           // L: block minus (0,c+1)
            {{0, fc}, {1, fc}, {0, fc + 1}},           // L: block minus (1,c+1)
        };
        long total = 0;
        for (int[][] cells : cands) {
            boolean coversFirst = false, valid = true;
            for (int[] cell : cells) {
                int r = cell[0], c = cell[1];
                if (r == fr && c == fc) coversFirst = true;
                if (c < 0 || c >= N || r < 0 || r > 1 || grid[c][r]) { valid = false; break; }
            }
            if (!coversFirst || !valid) continue;
            for (int[] cell : cells) grid[cell[1]][cell[0]] = true;
            total += rec();
            for (int[] cell : cells) grid[cell[1]][cell[0]] = false;
        }
        return total;
    }

    public static void main(String[] args) {
        // --- Verified values: 1,1,2,5,11,24,53,117,258,569,1255 ---
        long[] known = {1, 1, 2, 5, 11, 24, 53, 117, 258, 569, 1255};
        for (int n = 0; n < known.length; n++)
            checkEquals("n=" + n, known[n], Domino_tromino_tiling.countWays(n));

        // --- Cross-check against exhaustive backtracking (values well under MOD) ---
        boolean ok = true;
        for (int n = 0; n <= 12 && ok; n++)
            if (Domino_tromino_tiling.countWays(n) != bruteCount(n)) ok = false;
        if (ok) { passed++; System.out.println("PASS: brute-force cross-check [0,12]"); }
        else    { failed++; System.out.println("FAIL: brute-force cross-check [0,12]"); }

        // --- Modulo behaviour: large n must return a value in [0, MOD) without overflow ---
        long big = Domino_tromino_tiling.countWays(1000);
        checkEquals("n=1000 in range [0,MOD)", true, big >= 0 && big < Domino_tromino_tiling.MOD);

        // --- Error ---
        checkThrows("negative", IllegalArgumentException.class,
                () -> Domino_tromino_tiling.countWays(-1));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}