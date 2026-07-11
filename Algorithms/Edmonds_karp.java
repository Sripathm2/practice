package Algorithms;
import java.util.Objects;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

// Edmonds-Karp: Ford-Fulkerson where each augmenting path is the one with the
// FEWEST edges, found by BFS on the residual graph. Choosing shortest augmenting
// paths bounds the number of augmentations at O(V*E), giving O(V*E^2) overall and
// guaranteeing termination regardless of capacity sizes.
public class Edmonds_karp {

    private static final int INF = Integer.MAX_VALUE/2;
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

        int[][] copy = new int[capacity.length][];

        for (int i = 0; i < capacity.length; i++) {
            copy[i] = Arrays.copyOf(capacity[i], capacity[i].length);
        }

        max_flow = 0;
        visited_count = 1;
        visited = new int[copy.length];
        s = source;
        t = sink;
        for(int f = bfs(copy); f != 0; f = bfs(copy)){
            visited_count += 1;
            max_flow += f;
        }

        return max_flow;
    }

    private static int bfs(int[][] capacity){

        Queue<Integer> queue = new ArrayDeque<Integer>();
        visited[s] = visited_count;
        queue.offer(s);
        
        int [] prev = new int[capacity.length];
        for(int i=0;i<capacity.length;i++){
            prev[i] = -1;
        }
        while(!queue.isEmpty()){
            int node = queue.poll();
            if(node == t) break;

            for(int i=0;i<capacity.length;i++){
                if(visited[i]!= visited_count && capacity[node][i]>0){
                    visited[i] = visited_count;
                    queue.offer(i);
                    prev[i] = node;
                }
            }
        }

        if(prev[t] == -1) return 0;

        int flow_val = INF;

        int pathmaker = t;

        while(pathmaker!= s){
            flow_val = Math.min(flow_val, capacity[prev[pathmaker]][pathmaker]);
            pathmaker = prev[pathmaker];
        }
        pathmaker = t;
        while(pathmaker!= s){
            capacity[prev[pathmaker]][pathmaker] -= flow_val;
            capacity[pathmaker][prev[pathmaker]] += flow_val;
            pathmaker = prev[pathmaker];
        }

        return flow_val;
    }
}

class Edmonds_karp_Main {
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
    private static int[][] net(int n, int[][] edges) {
        int[][] c = new int[n][n];
        for (int[] e : edges) c[e[0]][e[1]] = e[2];
        return c;
    }
    private static int[][] deep(int[][] m) { int[][] c = new int[m.length][]; for (int i = 0; i < m.length; i++) c[i] = m[i].clone(); return c; }

    // Independent ground truth (standard Edmonds-Karp, on its own copy).
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
        checkEquals("CLRS max flow", 23, Edmonds_karp.maxFlow(deep(clrs), 0, 5));
        checkEquals("diamond", 5, Edmonds_karp.maxFlow(net(4, new int[][]{{0,1,3},{0,2,2},{1,3,2},{2,3,3},{1,2,1}}), 0, 3));
        checkEquals("single path", 3, Edmonds_karp.maxFlow(net(3, new int[][]{{0,1,5},{1,2,3}}), 0, 2));
        checkEquals("disconnected", 0, Edmonds_karp.maxFlow(net(3, new int[][]{{0,1,5}}), 0, 2));
        checkEquals("needs residual reroute", 2,
                Edmonds_karp.maxFlow(net(4, new int[][]{{0,1,1},{0,2,1},{1,2,1},{1,3,1},{2,3,1}}), 0, 3));

        java.util.Random rng = new java.util.Random(151);
        boolean ok = true;
        for (int t = 0; t < 300 && ok; t++) {
            int n = 2 + rng.nextInt(6); int[][] cap = new int[n][n];
            for (int u = 0; u < n; u++) for (int v = 0; v < n; v++) if (u != v && rng.nextInt(2) == 0) cap[u][v] = rng.nextInt(10);
            if (Edmonds_karp.maxFlow(deep(cap), 0, n - 1) != truth(cap, 0, n - 1)) ok = false;
        }
        if (ok) { passed++; System.out.println("PASS: cross-check"); } else { failed++; System.out.println("FAIL: cross-check"); }

        checkThrows("null capacity", NullPointerException.class, () -> Edmonds_karp.maxFlow(null, 0, 1));
        checkThrows("not square", IllegalArgumentException.class, () -> Edmonds_karp.maxFlow(new int[][]{{0,1,0},{0,0,0}}, 0, 1));
        checkThrows("negative", IllegalArgumentException.class, () -> Edmonds_karp.maxFlow(new int[][]{{0,-1},{0,0}}, 0, 1));
        checkThrows("source==sink", IllegalArgumentException.class, () -> Edmonds_karp.maxFlow(new int[][]{{0,1},{0,0}}, 0, 0));
        checkThrows("oob", IndexOutOfBoundsException.class, () -> Edmonds_karp.maxFlow(new int[][]{{0,1},{0,0}}, 2, 1));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}