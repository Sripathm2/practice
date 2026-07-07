package Problems;
import java.util.Objects;

// Domino tiling of a 3 x n board with 1x2 dominoes (each domino covers two
// adjacent cells, horizontally or vertically). Count the number of full tilings.
//
// Two facts drive the DP:
//  - A 3 x n board has 3n cells; a domino covers 2, so tiling is only possible
//    when 3n is even, i.e. n is even. Odd n => 0 ways.
public class Domino_tiling_3xn {

    // Return the number of ways to tile a 3 x n board with 1x2 dominoes.
    // Throw IllegalArgumentException if n < 0.
    public static long countWays(int n) {
        if(n < 0){
            throw new IllegalArgumentException();
        }
        long[][] slot_count = new long[n+3][8];
        slot_count[0][7] = 1;
        for(int i = 1; i < n+1; i++){
            slot_count[i][0] += slot_count[i-1][7]; 

            slot_count[i][1] += slot_count[i-1][6];  

            slot_count[i][2] += slot_count[i-1][5];  

            slot_count[i][3] += slot_count[i-1][7];  
            slot_count[i][3] += slot_count[i-1][4];

            slot_count[i][4] += slot_count[i-1][3];  

            slot_count[i][5] += slot_count[i-1][2];  

            slot_count[i][6] += slot_count[i-1][7];
            slot_count[i][6] += slot_count[i-1][1];

            slot_count[i][7] += slot_count[i-1][0]; 
            slot_count[i][7] += slot_count[i-1][3]; 
            slot_count[i][7] += slot_count[i-1][6]; 
        }
        return slot_count[n][7];
    }
}

class Domino_tiling_3xn_Main {
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

    // Ground truth: exhaustive backtracking that actually lays dominoes.
    private static long bruteCount(int n) {
        if (n == 0) return 1;
        return fill(new boolean[3][n], 3, n);
    }
    private static long fill(boolean[][] g, int rows, int cols) {
        int r = -1, c = -1;
        outer:
        for (int j = 0; j < cols; j++)
            for (int i = 0; i < rows; i++)
                if (!g[i][j]) { r = i; c = j; break outer; }
        if (r == -1) return 1;   // board full
        long count = 0;
        // vertical domino (r,c)-(r+1,c)
        if (r + 1 < rows && !g[r + 1][c]) {
            g[r][c] = g[r + 1][c] = true;
            count += fill(g, rows, cols);
            g[r][c] = g[r + 1][c] = false;
        }
        // horizontal domino (r,c)-(r,c+1)
        if (c + 1 < cols && !g[r][c + 1]) {
            g[r][c] = g[r][c + 1] = true;
            count += fill(g, rows, cols);
            g[r][c] = g[r][c + 1] = false;
        }
        return count;
    }

    public static void main(String[] args) {
        // --- Known values: f = 1, 0, 3, 0, 11, 0, 41, 0, 153, 0, 571, 0, 2131 ---
        checkEquals("n=0", 1L,    Domino_tiling_3xn.countWays(0));
        checkEquals("n=1", 0L,    Domino_tiling_3xn.countWays(1));   // odd -> impossible
        checkEquals("n=2", 3L,    Domino_tiling_3xn.countWays(2));
        checkEquals("n=3", 0L,    Domino_tiling_3xn.countWays(3));
        checkEquals("n=4", 11L,   Domino_tiling_3xn.countWays(4));
        checkEquals("n=5", 0L,    Domino_tiling_3xn.countWays(5));
        checkEquals("n=6", 41L,   Domino_tiling_3xn.countWays(6));
        checkEquals("n=8", 153L,  Domino_tiling_3xn.countWays(8));
        checkEquals("n=10", 571L, Domino_tiling_3xn.countWays(10));
        checkEquals("n=12", 2131L, Domino_tiling_3xn.countWays(12));

        // --- Cross-check against backtracking for small n ---
        boolean ok = true;
        for (int n = 0; n <= 8 && ok; n++)
            if (Domino_tiling_3xn.countWays(n) != bruteCount(n)) ok = false;
        if (ok) { passed++; System.out.println("PASS: brute-force cross-check [0,8]"); }
        else    { failed++; System.out.println("FAIL: brute-force cross-check [0,8]"); }

        // --- Error ---
        checkThrows("negative", IllegalArgumentException.class,
                () -> Domino_tiling_3xn.countWays(-1));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}