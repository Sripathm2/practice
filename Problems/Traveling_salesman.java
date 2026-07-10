package Problems;
import java.util.Objects;

// Traveling Salesman Problem (Held-Karp bitmask DP).
// Given a complete directed graph as an n x n distance matrix (dist[i][j] = cost
// to go from city i to city j), find the minimum-cost tour that starts at city 0,
// visits every city exactly once, and returns to city 0.
//
// dp[mask][i] = min cost of a path that starts at 0, visits exactly the cities in
// `mask`, and currently sits at city i (i must be in mask).
//   base:       dp[{0}][0] = 0
//   transition: dp[mask | (1<<j)][j] = min over i in mask of dp[mask][i] + dist[i][j]
//   answer:     min over i of dp[fullMask][i] + dist[i][0]
public class Traveling_salesman {

    private static final int MAX_VALUE = Integer.MAX_VALUE/2;

    // Return the minimum cost of a tour visiting all cities once and returning to
    // the start. A single city has cost 0.
    // Throw NullPointerException if dist or any row is null.
    // Throw IllegalArgumentException if dist is empty or not square (n x n).
    public static int tspMinCost(int[][] dist) {
        
        if(dist == null){
            throw new NullPointerException();
        }
        if(dist.length==0){
            throw new IllegalArgumentException();
        }
        for(int i=0;i<dist.length; i++){
            if(dist[i] == null){
                throw new NullPointerException();
            }
            if(dist[i].length == 0 || dist[i].length != dist[0].length || dist[i].length != dist.length){
                throw new IllegalArgumentException();
            }
        }

        if(dist.length == 1){
            return 0;
        }

        int n = dist.length;
        int start = 0;
        int[][] memo = new int[n][(int)Math.pow(2, n)];

        for(int i=0;i<n;i++){
            if(i==start){
                continue;
            }
            memo[i][1<<start | 1<<i] = dist[start][i];
        }

        for(int r = 3; r <= n;r++){
            for (int mask = 0; mask < (1 << n); mask++) {
                if (Integer.bitCount(mask) == r) {
                    // use mask
                    if(notIN(start, mask)) continue;
                    for(int next =0;next < n; next ++){
                        if(next == start || notIN(next, mask)) continue;
                        int state = mask ^ (1<<next);
                        int mindist = MAX_VALUE;
                        for(int end = 0;end < n; end ++){
                            if(end == start || end == next || notIN(end, mask)) continue;
                            int newdist = memo[end][state] + dist[end][next];
                            if(newdist < mindist){
                                mindist = newdist;
                            }
                        }
                        memo[next][mask] = mindist;
                    }
                }
            }
        }

        int END_state = (1<<n)-1;

        int mintourcost = MAX_VALUE;

        for(int end = 0; end < n; end++){
            if(end==start)continue;
            int tourcost = memo[end][END_state] + dist[end][start];
            if(tourcost < mintourcost){
                mintourcost = tourcost;
            }
        }

        ///// path extra we dont use it
        
        int lastindex= start;
        int state = (1<<n)-1;
        
        int[] tour = new int[n+1];

        for(int i=n-1; i>=1; i--){
            int index = -1;
            for(int j=0;j<n; j++){
                if(j==start||notIN(j, state))continue;
                if(index ==-1) index =j;
                if(memo[index][state] + dist[index][lastindex] < memo[j][state]+dist[j][lastindex]){
                    index = j;
                }
            }
            tour[i] = index;
            state = state ^ (1<<index);
            lastindex = index;
        }
        tour[0] = tour[n] = start;

        return mintourcost;
    }

    private static boolean notIN(int i, int subset){
        return ((1<<i)&subset) == 0;
    }


}

class Traveling_salesman_Main {
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

    // Independent brute force: try every ordering of cities 1..n-1, fix start 0.
    private static int bestBrute;
    private static int bruteTSP(int[][] d) {
        int n = d.length;
        if (n <= 1) return 0;
        int[] cities = new int[n - 1];
        for (int i = 0; i < n - 1; i++) cities[i] = i + 1;
        bestBrute = Integer.MAX_VALUE;
        permute(d, cities, 0);
        return bestBrute;
    }
    private static void permute(int[][] d, int[] a, int idx) {
        if (idx == a.length) {
            int cost = d[0][a[0]];
            for (int i = 0; i + 1 < a.length; i++) cost += d[a[i]][a[i + 1]];
            cost += d[a[a.length - 1]][0];
            bestBrute = Math.min(bestBrute, cost);
            return;
        }
        for (int i = idx; i < a.length; i++) {
            int t = a[idx]; a[idx] = a[i]; a[i] = t;
            permute(d, a, idx + 1);
            t = a[idx]; a[idx] = a[i]; a[i] = t;
        }
    }

    public static void main(String[] args) {
        // --- Classic symmetric 4-city: optimal tour 0-1-3-2-0 = 10+25+30+15 = 80 ---
        int[][] sym = {
            { 0, 10, 15, 20},
            {10,  0, 35, 25},
            {15, 35,  0, 30},
            {20, 25, 30,  0},
        };
        checkEquals("symmetric 4-city", 80, Traveling_salesman.tspMinCost(sym));

        // --- Asymmetric 3-city: 0-1-2-0 = 1+1+1 = 3 (vs 0-2-1-0 = 15) ---
        int[][] asym = {
            {0, 1, 5},
            {5, 0, 1},
            {1, 5, 0},
        };
        checkEquals("asymmetric 3-city", 3, Traveling_salesman.tspMinCost(asym));

        // --- Trivial sizes ---
        checkEquals("single city", 0, Traveling_salesman.tspMinCost(new int[][]{{0}}));
        checkEquals("two cities", 7, Traveling_salesman.tspMinCost(new int[][]{{0,3},{4,0}})); // 3+4

        // --- Brute-force cross-check on random small complete graphs ---
        java.util.Random rng = new java.util.Random(101);
        boolean ok = true;
        for (int t = 0; t < 200 && ok; t++) {
            int n = 1 + rng.nextInt(7);   // 1..7 cities
            int[][] d = new int[n][n];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    d[i][j] = (i == j) ? 0 : 1 + rng.nextInt(50);
            if (Traveling_salesman.tspMinCost(d) != bruteTSP(d)) ok = false;
        }
        if (ok) { passed++; System.out.println("PASS: brute-force cross-check"); }
        else    { failed++; System.out.println("FAIL: brute-force cross-check"); }

        // --- Validation ---
        checkThrows("null dist", NullPointerException.class,
                () -> Traveling_salesman.tspMinCost(null));
        checkThrows("null row", NullPointerException.class,
                () -> Traveling_salesman.tspMinCost(new int[][]{{0,1}, null}));
        checkThrows("empty", IllegalArgumentException.class,
                () -> Traveling_salesman.tspMinCost(new int[][]{}));
        checkThrows("not square", IllegalArgumentException.class,
                () -> Traveling_salesman.tspMinCost(new int[][]{{0,1,2},{3,4,5}}));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}
