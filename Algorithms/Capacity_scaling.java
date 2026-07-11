package Algorithms;
import java.util.Objects;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

// Capacity Scaling: Ford-Fulkerson that only uses augmenting paths whose edges all
// have residual capacity >= a threshold delta. Start delta at the largest power of
// two <= the maximum capacity; saturate all paths at that threshold, then halve
// delta and repeat down to 1. Pushing large amounts first keeps the number of
// augmentations small: O(E^2 log U) where U is the maximum capacity.
public class Capacity_scaling {

    private static final int INF = Integer.MAX_VALUE;
    private static int max_flow;
    private static int[] visited;
    private static int visited_count;
    private static int s;
    private static int t;

    // Return the maximum flow from source to sink. capacity[u][v] is the capacity of
    // edge u->v (0 if none). The input matrix is NOT modified.
    // Throw NullPointerException if capacity or any row is null.
    // Throw IllegalArgumentException if the matrix is not square, has a negative
    //   entry, or source == sink.
    // Throw IndexOutOfBoundsException if source or sink is not in [0, n).
    public static int maxFlow(int[][] capacity, int source, int sink) {
        if(capacity == null){
            throw new NullPointerException();
        }
        int max_capacity  = -1;
        for(int i=0;i<capacity.length; i++){
            if(capacity[i] == null){
                throw new NullPointerException();
            }
            if(capacity[i].length != capacity[0].length || capacity[i].length != capacity.length){
                throw new IllegalArgumentException();
            }
            for(int j: capacity[i]){
                if(j < 0){
                    throw new IllegalArgumentException();
                }else{
                    max_capacity = Math.max(max_capacity, j);
                }
            }
        }
        if(source == sink){
            throw new IllegalArgumentException();
        }
        if(source < 0 || sink < 0 || source >= capacity.length || sink >= capacity.length){
            throw new IndexOutOfBoundsException();
        }

        int[][] copy = new int[capacity.length][];

        for (int i = 0; i < capacity.length; i++) {
            copy[i] = Arrays.copyOf(capacity[i], capacity[i].length);
        }


        max_flow = 0;
        visited_count = 1;
        visited = new int[copy.length];
        s = source;
        t = sink;
        int delta = Integer.highestOneBit(Math.max(max_capacity, 1));
        for (; delta > 0; delta >>= 1) {
            // System.out.println(delta);
            for (int f = dfs(copy, s, INF, delta); f != 0; f = dfs(copy, s, INF, delta)) {
                visited_count++; max_flow += f;
            }
            visited_count++;
        }
        

        return max_flow;
    }

    private static int dfs(int[][] capacity, int node, int flow, int delta){
        if(node == t) return flow;

        visited[node] = visited_count;

        for(int i=0;i<capacity.length;i++){
            if(visited[i]!= visited_count && capacity[node][i]>=delta){
                int dfflow = 0;
                if(capacity[node][i] < flow) dfflow = dfs(capacity, i, capacity[node][i], delta);
                else dfflow = dfs(capacity, i, flow, delta);
                
                if(dfflow > 0){
                    capacity[node][i] -= dfflow;
                    capacity[i][node] += dfflow;
                    return dfflow;
                }
            }
        }
        return 0;
    }
}

class Capacity_scaling_Main {
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
    private static int[][] net(int n, int[][] edges) { int[][] c = new int[n][n]; for (int[] e : edges) c[e[0]][e[1]] = e[2]; return c; }
    private static int[][] deep(int[][] m) { int[][] c = new int[m.length][]; for (int i = 0; i < m.length; i++) c[i] = m[i].clone(); return c; }

    private static int truth(int[][] cap, int s, int t) {
        int n = cap.length; int[][] res = deep(cap); int flow = 0;
        while (true) {
            int[] par = new int[n]; java.util.Arrays.fill(par, -1); par[s] = s;
            Queue<Integer> q = new ArrayDeque<>(); q.add(s);
            while (!q.isEmpty()) { int u = q.poll(); for (int v = 0; v < n; v++) if (par[v] == -1 && res[u][v] > 0) { par[v] = u; q.add(v); } }
            if (par[t] == -1) break;
            int b = Integer.MAX_VALUE; for (int v = t; v != s; v = par[v]) b = Math.min(b, res[par[v]][v]);
            for (int v = t; v != s; v = par[v]) { res[par[v]][v] -= b; res[v][par[v]] += b; }
            flow += b;
        }
        return flow;
    }

    public static void main(String[] args) {
        int[][] clrs = net(6, new int[][]{{0,1,16},{0,2,13},{1,3,12},{2,1,4},{3,2,9},{2,4,14},{4,3,7},{3,5,20},{4,5,4}});
        checkEquals("CLRS max flow", 23, Capacity_scaling.maxFlow(deep(clrs), 0, 5));
        checkEquals("diamond", 5, Capacity_scaling.maxFlow(net(4, new int[][]{{0,1,3},{0,2,2},{1,3,2},{2,3,3},{1,2,1}}), 0, 3));
        checkEquals("single path", 3, Capacity_scaling.maxFlow(net(3, new int[][]{{0,1,5},{1,2,3}}), 0, 2));
        checkEquals("disconnected", 0, Capacity_scaling.maxFlow(net(3, new int[][]{{0,1,5}}), 0, 2));
        // Large capacities: scaling shines here (few augmentations)
        checkEquals("large caps", 2000000,
                Capacity_scaling.maxFlow(net(4, new int[][]{{0,1,1000000},{1,3,1000000},{0,2,1000000},{2,3,1000000}}), 0, 3));
        checkEquals("needs residual reroute", 2,
                Capacity_scaling.maxFlow(net(4, new int[][]{{0,1,1},{0,2,1},{1,2,1},{1,3,1},{2,3,1}}), 0, 3));

        java.util.Random rng = new java.util.Random(161);
        boolean ok = true;
        for (int t = 0; t < 300 && ok; t++) {
            int n = 2 + rng.nextInt(6); int[][] cap = new int[n][n];
            for (int u = 0; u < n; u++) for (int v = 0; v < n; v++) if (u != v && rng.nextInt(2) == 0) cap[u][v] = rng.nextInt(64);
            if (Capacity_scaling.maxFlow(deep(cap), 0, n - 1) != truth(cap, 0, n - 1)) ok = false;
        }
        if (ok) { passed++; System.out.println("PASS: cross-check"); } else { failed++; System.out.println("FAIL: cross-check"); }

        checkThrows("null capacity", NullPointerException.class, () -> Capacity_scaling.maxFlow(null, 0, 1));
        checkThrows("not square", IllegalArgumentException.class, () -> Capacity_scaling.maxFlow(new int[][]{{0,1,0},{0,0,0}}, 0, 1));
        checkThrows("negative", IllegalArgumentException.class, () -> Capacity_scaling.maxFlow(new int[][]{{0,-1},{0,0}}, 0, 1));
        checkThrows("source==sink", IllegalArgumentException.class, () -> Capacity_scaling.maxFlow(new int[][]{{0,1},{0,0}}, 0, 0));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}