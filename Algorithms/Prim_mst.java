package Algorithms;
import Data_structures.Graph;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

// Prim's algorithm: minimum spanning tree of a connected, weighted, UNDIRECTED
// graph. Grow a tree from a start vertex; at each step add the cheapest edge that
// connects a tree vertex to a not-yet-in-tree vertex, using a priority queue to
// always pull the minimum such edge.
public class Prim_mst {

    public static final long NO_SPANNING_TREE = Long.MAX_VALUE;   // graph is disconnected
    private static ArrayList<Pair> mst;
    private static long mstweight;

    private static class Pair {
        int first;
        int second;
        int weight;


        Pair(int first, int second, int weight) {
            this.first = first;
            this.second = second;
            this.weight = weight;
        }
    }

    // Total weight of a minimum spanning tree, or NO_SPANNING_TREE if the graph is
    // disconnected (no tree spans all vertices). A 0- or 1-vertex graph has weight 0.
    // Throw NullPointerException if g is null.
    public static long minimumSpanningWeight(Graph g) {
        minimumSpanningEdges(g);
        return mstweight;
    }

    // The MST edges as rows {u, v, weight} with u < v, sorted ascending; an empty
    // array if the graph is disconnected. (Any one MST is acceptable on ties.)
    // Throw NullPointerException if g is null.
    public static int[][] minimumSpanningEdges(Graph g) {
        if(g == null){
            throw new NullPointerException(); 
        }
        mst = new ArrayList<Pair>();
        mstweight = 0;
        if(g.vertexCount() == 0 || g.vertexCount() == 1){
            return new int[0][0];
        }
        boolean[] visited = new boolean[g.vertexCount()];
        PriorityQueue<Pair> pq = new PriorityQueue<Pair>(Comparator.comparingInt(p -> p.weight));
        for(int neigh: g.neighbors(0)){
            pq.add(new Pair(0,neigh, g.weight(0, neigh)));
        }
        visited[0] = true;

        while(!pq.isEmpty()){
            Pair process = pq.poll();
            if(visited[process.second]) continue;
            else{
                visited[process.second] = true;
                mst.add(process);
                mstweight += process.weight;
                for(int neigh: g.neighbors(process.second)){
                    if(!visited[neigh]) pq.add(new Pair(process.second,neigh, g.weight(process.second, neigh)));
                }
            }
        }

        for(int i=0;i<visited.length;i++){
            if(visited[i] == false){
                mstweight = NO_SPANNING_TREE;
                return new int[0][0];
            }
        }

        mst.sort(Comparator.comparingInt((Pair p) -> p.first).thenComparingInt(p -> p.second));

        ArrayList<Pair> fair = new ArrayList<Pair>();
        for(Pair in: mst){
            if(in.first < in.second){
                fair.add(in);
            }else{
                fair.add(new Pair(in.second, in.first, in.weight));
            }
        }
        mst = fair;
        int[][] returnarr = new int[mst.size()][3];
        int index  = 0;
        for(Pair in: mst){
            returnarr[index][0] = in.first;
            returnarr[index][1] = in.second;
            returnarr[index][2] = in.weight;
            index += 1;
        }

        return returnarr;
    }
}

class Prim_mst_Main {
    private static int passed = 0;
    private static int failed = 0;

    private static void checkEquals(String name, Object expected, Object actual) {
        if (Objects.equals(expected, actual)) { passed++; System.out.println("PASS: " + name); }
        else { failed++; System.out.println("FAIL: " + name + " — expected <" + expected + ">, got <" + actual + ">"); }
    }
    private static void checkTrue(String name, boolean c) {
        if (c) { passed++; System.out.println("PASS: " + name); } else { failed++; System.out.println("FAIL: " + name); }
    }
    private static void checkThrows(String name, Class<? extends Throwable> ex, Runnable r) {
        try { r.run(); failed++; System.out.println("FAIL: " + name + " — none thrown"); }
        catch (Throwable t) {
            if (ex.isInstance(t)) { passed++; System.out.println("PASS: " + name); }
            else { failed++; System.out.println("FAIL: " + name + " — got " + t.getClass().getSimpleName()); }
        }
    }

    // Undirected weighted graph.
    static final class WU implements Graph {
        final int n; final List<List<int[]>> adj;   // {to, weight}
        WU(int n) { this.n = n; adj = new ArrayList<>(); for (int i = 0; i < n; i++) adj.add(new ArrayList<>()); }
        void add(int u, int v, int w) { adj.get(u).add(new int[]{v, w}); adj.get(v).add(new int[]{u, w}); }
        public int vertexCount() { return n; }
        public boolean isDirected() { return false; }
        public boolean hasEdge(int u, int v) { for (int[] e : adj.get(u)) if (e[0] == v) return true; return false; }
        public int weight(int u, int v) { for (int[] e : adj.get(u)) if (e[0] == v) return e[1]; throw new java.util.NoSuchElementException(); }
        public int[] neighbors(int v) {
            List<Integer> ns = new ArrayList<>(); for (int[] e : adj.get(v)) ns.add(e[0]);
            java.util.Collections.sort(ns);
            int[] out = new int[ns.size()]; for (int i = 0; i < out.length; i++) out[i] = ns.get(i); return out;
        }
    }

    // Independent ground truth: Kruskal with union-find.
    private static int find(int[] p, int x) { while (p[x] != x) { p[x] = p[p[x]]; x = p[x]; } return x; }
    private static long kruskalWeight(Graph g) {
        int n = g.vertexCount();
        List<int[]> edges = new ArrayList<>();
        for (int u = 0; u < n; u++) for (int v : g.neighbors(u)) if (v > u) edges.add(new int[]{g.weight(u, v), u, v});
        edges.sort((a, b) -> Integer.compare(a[0], b[0]));
        int[] p = new int[n]; for (int i = 0; i < n; i++) p[i] = i;
        long total = 0; int used = 0;
        for (int[] e : edges) { int a = find(p, e[1]), b = find(p, e[2]); if (a != b) { p[a] = b; total += e[0]; used++; } }
        return (n <= 1 || used == n - 1) ? total : Prim_mst.NO_SPANNING_TREE;
    }

    // Do the returned edges form a spanning tree whose total equals the MST weight?
    private static boolean validMST(Graph g, int[][] edges, long mstWeight) {
        int n = g.vertexCount();
        if (mstWeight == Prim_mst.NO_SPANNING_TREE) return edges.length == 0;
        if (n <= 1) return edges.length == 0;
        if (edges.length != n - 1) return false;
        int[] p = new int[n]; for (int i = 0; i < n; i++) p[i] = i;
        long sum = 0;
        for (int[] e : edges) {
            int u = e[0], v = e[1], w = e[2];
            if (u >= v || !g.hasEdge(u, v) || g.weight(u, v) != w) return false;   // real edge, canonical
            int a = find(p, u), b = find(p, v);
            if (a == b) return false;   // no cycles
            p[a] = b; sum += w;
        }
        return sum == mstWeight;   // spans (n-1 acyclic edges) and matches weight
    }

    public static void main(String[] args) {
        // MST weight 16 (verified via Kruskal)
        WU g = new WU(5);
        g.add(0,1,2); g.add(0,3,6); g.add(1,2,3); g.add(1,3,8); g.add(1,4,5); g.add(2,4,7); g.add(3,4,9);
        checkEquals("MST weight", 16L, Prim_mst.minimumSpanningWeight(g));
        checkTrue("MST edges valid", validMST(g, Prim_mst.minimumSpanningEdges(g), 16L));

        // Disconnected -> no spanning tree
        WU disc = new WU(4);
        disc.add(0,1,1); disc.add(2,3,3);
        checkEquals("disconnected weight", Prim_mst.NO_SPANNING_TREE, Prim_mst.minimumSpanningWeight(disc));
        checkEquals("disconnected edges empty", 0, Prim_mst.minimumSpanningEdges(disc).length);

        // Two vertices, one edge
        WU two = new WU(2); two.add(0,1,7);
        checkEquals("two-vertex weight", 7L, Prim_mst.minimumSpanningWeight(two));

        // Single vertex -> 0
        checkEquals("single vertex weight", 0L, Prim_mst.minimumSpanningWeight(new WU(1)));

        // Cross-check vs Kruskal on random connected-ish graphs
        java.util.Random rng = new java.util.Random(131);
        boolean ok = true;
        for (int t = 0; t < 300 && ok; t++) {
            int n = 1 + rng.nextInt(8);
            WU r = new WU(n);
            for (int i = 1; i < n; i++) r.add(i, rng.nextInt(i), 1 + rng.nextInt(20));   // spanning backbone (connected)
            for (int e = 0; e < rng.nextInt(6); e++) {                                     // extra random edges
                int u = rng.nextInt(n), v = rng.nextInt(n);
                if (u != v) r.add(u, v, 1 + rng.nextInt(20));
            }
            long prim = Prim_mst.minimumSpanningWeight(r);
            long krus = kruskalWeight(r);
            if (prim != krus) ok = false;
            if (!validMST(r, Prim_mst.minimumSpanningEdges(r), prim)) ok = false;
        }
        if (ok) { passed++; System.out.println("PASS: cross-check vs Kruskal"); }
        else    { failed++; System.out.println("FAIL: cross-check vs Kruskal"); }

        checkThrows("null graph", NullPointerException.class, () -> Prim_mst.minimumSpanningWeight(null));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}