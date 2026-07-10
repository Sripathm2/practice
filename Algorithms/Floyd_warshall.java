package Algorithms;
import Data_structures.Graph;
import java.util.Objects;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

// Floyd-Warshall: ALL-PAIRS shortest paths. For each intermediate vertex k, try to
// improve every pair (i, j) by routing through k: dist[i][j] = min(dist[i][j],
// dist[i][k] + dist[k][j]). Handles negative edges; a negative value on the
// diagonal afterward signals a negative cycle.
public class Floyd_warshall {

    public static final long UNREACHABLE  = Long.MAX_VALUE;
    public static final long NEGATIVE_INF = Long.MIN_VALUE;

    // dist[i][j] = shortest distance from i to j: finite, UNREACHABLE if no path,
    // or NEGATIVE_INF if the path can be made arbitrarily short via a negative cycle.
    // dist[i][i] = 0 unless i lies on a negative cycle.
    // Throw NullPointerException if g is null.
    public static long[][] allPairsShortestPaths(Graph g) {
        if(g == null){
            throw new NullPointerException();
        }
        int[][] path = new int[g.vertexCount()][g.vertexCount()];
        long[][] dp = new long[g.vertexCount()][g.vertexCount()];
        for(int i=0;i<dp.length;i++){
            for(int j=0;j<dp.length;j++){
                if(i==j){
                    dp[i][j] = 0;
                    path[i][j] = j;
                }else if(g.hasEdge(i, j)){
                    dp[i][j] = g.weight(i, j);
                    path[i][j] = j;
                }else{
                    dp[i][j] = UNREACHABLE;
                }
            }
        }
        
        for(int k = 0; k < dp.length; k++){
            for(int i = 0;i < dp.length; i++){
                for(int j=0; j < dp.length; j++){
                    if(dp[i][k] != UNREACHABLE && dp[k][j] != UNREACHABLE && dp[i][k] + dp[k][j] < dp[i][j]){
                        dp[i][j] = dp[i][k] + dp[k][j];
                        path[i][j] = path[i][k];
                    }
                }
            }
        }
        for(int k = 0; k < dp.length; k++){
            for(int i = 0;i < dp.length; i++){
                for(int j=0; j < dp.length; j++){
                    if(dp[i][k] != UNREACHABLE && dp[k][j] != UNREACHABLE && dp[i][k] + dp[k][j] < dp[i][j]){
                        dp[i][j] = NEGATIVE_INF;
                        path[i][j] = -1;
                    }
                }
            }
        }
        return dp;
    }

    // True if the graph contains any negative cycle.
    // Throw NullPointerException if g is null.
    public static boolean hasNegativeCycle(Graph g) {
        if(g == null){
            throw new NullPointerException();
        }
        long[][] dp = new long[g.vertexCount()][g.vertexCount()];
        int[][] path = new int[g.vertexCount()][g.vertexCount()];
        for(int i=0;i<dp.length;i++){
            for(int j=0;j<dp[0].length;j++){
                if(i==j){
                    dp[i][j] = 0;
                    path[i][j] = j;
                }if(g.hasEdge(i, j)){
                    dp[i][j] = g.weight(i, j);
                    path[i][j] = j;
                }else{
                    dp[i][j] = UNREACHABLE;
                }
            }
        }
        
        for(int k = 0; k < dp.length; k++){
            for(int i = 0;i < dp[k].length; i++){
                for(int j=0; j < dp[k].length; j++){
                    if(dp[i][k] != UNREACHABLE && dp[k][j] != UNREACHABLE && dp[i][k] + dp[k][j] < dp[i][j]){
                        dp[i][j] = dp[i][k] + dp[k][j];
                        path[i][j] = path[i][k];
                    }
                }
            }
        }
        for(int k = 0; k < dp.length; k++){
            for(int i = 0;i < dp[k].length; i++){
                for(int j=0; j < dp[k].length; j++){
                    if(dp[i][k] != UNREACHABLE && dp[k][j] != UNREACHABLE && dp[i][k] + dp[k][j] < dp[i][j]){
                        dp[i][j] = NEGATIVE_INF;
                        path[i][j] = -1;
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

class Floyd_warshall_Main {
    private static int passed = 0;
    private static int failed = 0;

    private static void checkEquals(String name, Object expected, Object actual) {
        if (Objects.equals(expected, actual)) { passed++; System.out.println("PASS: " + name); }
        else { failed++; System.out.println("FAIL: " + name + " — expected <" + expected + ">, got <" + actual + ">"); }
    }
    private static void checkThrows(String name, Class<? extends Throwable> ex, Runnable r) {
        try { r.run(); failed++; System.out.println("FAIL: " + name + " — none thrown"); }
        catch (Throwable t) {
            if (ex.isInstance(t)) { passed++; System.out.println("PASS: " + name); }
            else { failed++; System.out.println("FAIL: " + name + " — got " + t.getClass().getSimpleName()); }
        }
    }

    static final class W implements Graph {
        final int n; final List<List<int[]>> adj;
        W(int n) { this.n = n; adj = new ArrayList<>(); for (int i = 0; i < n; i++) adj.add(new ArrayList<>()); }
        void add(int u, int v, int w) { adj.get(u).add(new int[]{v, w}); }
        public int vertexCount() { return n; }
        public boolean isDirected() { return true; }
        public boolean hasEdge(int u, int v) { for (int[] e : adj.get(u)) if (e[0] == v) return true; return false; }
        public int weight(int u, int v) { for (int[] e : adj.get(u)) if (e[0] == v) return e[1]; throw new java.util.NoSuchElementException(); }
        public int[] neighbors(int v) {
            List<Integer> ns = new ArrayList<>(); for (int[] e : adj.get(v)) ns.add(e[0]);
            java.util.Collections.sort(ns);
            int[] out = new int[ns.size()]; for (int i = 0; i < out.length; i++) out[i] = ns.get(i); return out;
        }
    }

    public static void main(String[] args) {
        // 0->1(4) 0->2(1) 2->1(2) 1->3(1) 2->3(5) ; vertex 4 isolated
        W g = new W(5);
        g.add(0,1,4); g.add(0,2,1); g.add(2,1,2); g.add(1,3,1); g.add(2,3,5);
        long[][] d = Floyd_warshall.allPairsShortestPaths(g);
        long U = Floyd_warshall.UNREACHABLE;
        checkEquals("row 0", Arrays.toString(new long[]{0, 3, 1, 4, U}),
                (d.length > 0 ? Arrays.toString(d[0]) : "null"));
        checkEquals("row 2", Arrays.toString(new long[]{U, 2, 0, 3, U}),
                (d.length > 2 ? Arrays.toString(d[2]) : "null"));
        checkEquals("row 4 (isolated)", Arrays.toString(new long[]{U, U, U, U, 0}),
                (d.length > 4 ? Arrays.toString(d[4]) : "null"));
        checkEquals("no negative cycle", false, Floyd_warshall.hasNegativeCycle(g));

        // Negative cycle present: 0->1(1) 1->2(-3) 2->0(1) -> cycle 0->1->2->0 = -1
        W c = new W(3);
        c.add(0,1,1); c.add(1,2,-3); c.add(2,0,1);
        checkEquals("detects negative cycle", true, Floyd_warshall.hasNegativeCycle(c));

        checkThrows("null graph", NullPointerException.class,
                () -> Floyd_warshall.allPairsShortestPaths(null));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}
