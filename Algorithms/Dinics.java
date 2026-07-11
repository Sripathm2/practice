package Algorithms;
import java.util.Objects;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

// Dinic's algorithm: a fast max-flow method. Repeat in phases: (1) BFS from the
// source to assign each vertex a LEVEL (its distance in the residual graph), forming
// a "level graph" of edges that go strictly one level deeper; (2) find a BLOCKING
// flow in that level graph with DFS, only advancing along level+1 edges. Each phase
// strictly increases the source-sink distance, so there are O(V) phases -> O(V^2*E)
// overall (and O(E*sqrt(V)) on unit-capacity / bipartite-matching graphs).
public class Dinics {

    private static final int INF = Integer.MAX_VALUE/2;
    private static int max_flow;
    private static int[] visited;
    private static int[] level;
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
                }
            }
        }
        if(source == sink){
            throw new IllegalArgumentException();
        }
        if(source < 0 || sink < 0 || source >= capacity.length || sink >= capacity.length){
            throw new IndexOutOfBoundsException();
        }

        max_flow = 0;
        visited_count = 1;
        visited = new int[capacity.length];
        level = new int[capacity.length];
        s = source;
        t = sink;

        int[] next = new int[capacity.length];
        while(bfs(capacity)){
            Arrays.fill(next, 0);
            for(int f = dfs(capacity, s,INF, next); f != 0; f = dfs(capacity, s,INF, next)){
                visited_count += 1;
                max_flow += f;
            }
        }
        return max_flow;
    }


    private static int dfs(int[][] capacity, int node, int flow, int[] next){
        if(node == t) return flow;

        final int numedges = capacity.length;

        for(;next[node]< numedges; next[node]++){
            if(capacity[node][next[node]]>0 && level[node]+1 == level[next[node]]){
                int dfflow = dfs(capacity, next[node], Math.min(flow,capacity[node][next[node]]), next);
                if(dfflow > 0){
                    capacity[node][next[node]] -= dfflow;
                    capacity[next[node]][node] += dfflow;
                    return dfflow;
                }
            }
        }
        return 0;
    }

    private static boolean bfs(int[][] capacity){
        Arrays.fill(level, -1);
        Queue<Integer> queue = new ArrayDeque<Integer>();
        queue.offer(s);
        level[s] = 0;
        while(!queue.isEmpty()){
            int node = queue.poll();
            for(int i=0;i<capacity.length;i++){
                if(level[i] == -1 && capacity[node][i]>0){
                    level[i] = level[node] + 1;
                    queue.offer(i);
                }
            }
        }
        return level[t] != -1;
    }
}

class Dinics_Main {
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
        checkEquals("CLRS max flow", 23, Dinics.maxFlow(deep(clrs), 0, 5));
        checkEquals("diamond", 5, Dinics.maxFlow(net(4, new int[][]{{0,1,3},{0,2,2},{1,3,2},{2,3,3},{1,2,1}}), 0, 3));
        checkEquals("single path", 3, Dinics.maxFlow(net(3, new int[][]{{0,1,5},{1,2,3}}), 0, 2));
        checkEquals("disconnected", 0, Dinics.maxFlow(net(3, new int[][]{{0,1,5}}), 0, 2));
        checkEquals("needs residual reroute", 2,
                Dinics.maxFlow(net(4, new int[][]{{0,1,1},{0,2,1},{1,2,1},{1,3,1},{2,3,1}}), 0, 3));
        // Unit-capacity bipartite-style graph (Dinic's is especially fast here)
        checkEquals("bipartite matching",
                2, Dinics.maxFlow(net(6, new int[][]{{0,1,1},{0,2,1},{1,3,1},{1,4,1},{2,4,1},{3,5,1},{4,5,1}}), 0, 5));

        java.util.Random rng = new java.util.Random(171);
        boolean ok = true;
        for (int t = 0; t < 300 && ok; t++) {
            int n = 2 + rng.nextInt(6); int[][] cap = new int[n][n];
            for (int u = 0; u < n; u++) for (int v = 0; v < n; v++) if (u != v && rng.nextInt(2) == 0) cap[u][v] = rng.nextInt(10);
            if (Dinics.maxFlow(deep(cap), 0, n - 1) != truth(cap, 0, n - 1)) ok = false;
        }
        if (ok) { passed++; System.out.println("PASS: cross-check"); } else { failed++; System.out.println("FAIL: cross-check"); }

        checkThrows("null capacity", NullPointerException.class, () -> Dinics.maxFlow(null, 0, 1));
        checkThrows("not square", IllegalArgumentException.class, () -> Dinics.maxFlow(new int[][]{{0,1,0},{0,0,0}}, 0, 1));
        checkThrows("negative", IllegalArgumentException.class, () -> Dinics.maxFlow(new int[][]{{0,-1},{0,0}}, 0, 1));
        checkThrows("source==sink", IllegalArgumentException.class, () -> Dinics.maxFlow(new int[][]{{0,1},{0,0}}, 0, 0));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}