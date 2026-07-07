package Problems;
import java.util.Objects;

// Mountain Scenes (Kattis "scenes", NAIPC 2016).
// A ribbon of length n is cut into w columns, each filled from the bottom to an
// integer height in [0, h]. The total ribbon used (sum of column heights) must be
// at most n. A *mountain* scene must be uneven: if every column has the same
// height it's a "plain", not a mountain. Count the distinct mountain scenes,
// modulo 1_000_000_007.
//
// Plan: count ALL scenes (assignments of heights in [0,h] to w columns with
// sum <= n), then subtract the flat scenes (all columns equal). The flat count
// is min(h, n/w) + 1 (heights k = 0..h with w*k <= n). The total-scenes count is
// a DP over columns and used ribbon, with the ribbon dimension capped at w*h.
public class Mountain_scenes {

    public static final long MOD = 1_000_000_007L;
    public static int N; 
    public static int W;
    public static int H;
    public static long[][] dp; 

    // Return the number of mountain scenes for ribbon length n and a w x h frame,
    // modulo 1_000_000_007.
    // Throw IllegalArgumentException if n < 0, w < 1, or h < 1.
    public static long countMountainScenes(int n1, int w1, int h1) {
        if(n1 < 0 ||  w1 < 1 || h1 < 1){
            throw new IllegalArgumentException();
        }
        N = n1;
        W = w1;
        H = h1;
        dp = new long[W+1][N+1];
        for(int i = 0; i < dp.length; i++){
            for(int j = 0; j < dp[0].length; j++){
                dp[i][j] = -1;
            }
        }

        long ribbonsqs = Math.min(W*H, N);
        long plains  = (ribbonsqs/W) + 1;
        long count = recursive_count(1, N);
        return ((count-plains)+MOD)%MOD;
    }

    public static long recursive_count(int w, int ribbon){
        if(ribbon<0){
            return 0;
        }if(w>W){
            return 1;
        }
        if(dp[w][ribbon] != -1){
            return dp[w][ribbon];
        }
        long count = 0;
        for(int i = 0; i <= H; i++){
            count = (count + recursive_count(w+1, ribbon-i))%MOD;
        }
        dp[w][ribbon] = count;
        return dp[w][ribbon];
    }

}

class Mountain_scenes_Main {
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

    // Brute force: enumerate every height assignment (feasible only for tiny w,h).
    private static long brute(int n, int w, int h) {
        int[] col = new int[w];
        return countRec(col, 0, n, w, h);
    }
    private static long countRec(int[] col, int idx, int n, int w, int h) {
        if (idx == w) {
            int sum = 0, first = col[0];
            boolean allSame = true;
            for (int v : col) { sum += v; if (v != first) allSame = false; }
            return (sum <= n && !allSame) ? 1 : 0;
        }
        long c = 0;
        for (int v = 0; v <= h; v++) { col[idx] = v; c += countRec(col, idx + 1, n, w, h); }
        return c;
    }

    public static void main(String[] args) {
        // --- The four Kattis sample cases ---
        checkEquals("sample 1 (25 5 5)", 7770L, Mountain_scenes.countMountainScenes(25, 5, 5));
        checkEquals("sample 2 (15 5 5)", 6050L, Mountain_scenes.countMountainScenes(15, 5, 5));
        checkEquals("sample 3 (10 10 1)", 1022L, Mountain_scenes.countMountainScenes(10, 10, 1));
        checkEquals("sample 4 (4 2 2)",   6L,    Mountain_scenes.countMountainScenes(4, 2, 2));

        // --- Edge cases ---
        checkEquals("no ribbon -> 0", 0L, Mountain_scenes.countMountainScenes(0, 5, 5));
        checkEquals("width 1 -> 0 (always flat)", 0L, Mountain_scenes.countMountainScenes(50, 1, 100));
        checkEquals("height 1 small", 0L, Mountain_scenes.countMountainScenes(0, 3, 1));

        // --- Ribbon larger than the frame can hold: capped at w*h ---
        // n huge -> every assignment counts -> (h+1)^w - (h+1) flat scenes
        // w=3,h=2 -> 3^3 - 3 = 24
        checkEquals("ribbon exceeds frame (3 2, big n)", 24L,
                Mountain_scenes.countMountainScenes(100000, 3, 2));

        // --- Brute-force cross-check on small frames (values well under MOD) ---
        boolean ok = true;
        int[][] cases = {
            {4, 2, 2}, {6, 3, 2}, {5, 3, 3}, {0, 2, 2}, {3, 1, 5},
            {7, 4, 2}, {9, 3, 4}, {2, 4, 1}, {12, 3, 5}, {1, 2, 3},
        };
        for (int[] c : cases) {
            if (Mountain_scenes.countMountainScenes(c[0], c[1], c[2]) != brute(c[0], c[1], c[2])) {
                ok = false;
                System.out.println("  mismatch at n=" + c[0] + " w=" + c[1] + " h=" + c[2]);
            }
        }
        if (ok) { passed++; System.out.println("PASS: brute-force cross-check"); }
        else    { failed++; System.out.println("FAIL: brute-force cross-check"); }

        // --- Large inputs return a value in range without overflow ---
        long big = Mountain_scenes.countMountainScenes(10000, 100, 100);
        checkEquals("large in [0,MOD)", true, big >= 0 && big < Mountain_scenes.MOD);

        // --- Validation ---
        checkThrows("n < 0", IllegalArgumentException.class,
                () -> Mountain_scenes.countMountainScenes(-1, 5, 5));
        checkThrows("w < 1", IllegalArgumentException.class,
                () -> Mountain_scenes.countMountainScenes(5, 0, 5));
        checkThrows("h < 1", IllegalArgumentException.class,
                () -> Mountain_scenes.countMountainScenes(5, 5, 0));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}