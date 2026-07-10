package Algorithms;
import Data_structures.Graph;
import java.util.Objects;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

// Bellman-Ford: single-source shortest paths that tolerates NEGATIVE edge weights
// and detects negative cycles. Relax every edge V-1 times (that suffices for any
// shortest path, which has at most V-1 edges); then relax V-1 more times — any
// vertex that still improves is reachable from a negative cycle, so its shortest
// distance is -infinity.
public class Bellman_ford {

    public static final long UNREACHABLE  = Long.MAX_VALUE;   // no path from source
    public static final long NEGATIVE_INF = Long.MIN_VALUE;   // reachable via a negative cycle

    // dist[v] = shortest distance from source to v: a finite value, UNREACHABLE if
    // no path exists, or NEGATIVE_INF if v is reachable through a negative cycle.
    // Throw NullPointerException if g is null.
    // Throw IndexOutOfBoundsException if source is not in [0, vertexCount).
    public static long[] shortestPaths(Graph g, int source) {
        if(g == null){
            throw new NullPointerException();
        }
        if(source < 0 || source >= g.vertexCount()){
            throw new IndexOutOfBoundsException();
        }
        
        long[] distance = new long[g.vertexCount()];
        for(int i=0;i<distance.length;i++){
            distance[i] = UNREACHABLE;
        }
        distance[source] = 0;

        for(int k = 0;k <g.vertexCount()-1; k++){
            for(int i=0;i<g.vertexCount();i++){
                for(int neigh: g.neighbors(i)){
                    if(distance[i] != UNREACHABLE && distance[i]+g.weight(i, neigh) < distance[neigh]){
                        distance[neigh] = distance[i]+g.weight(i, neigh);
                    }
                }
            }
        }
        for(int k = 0;k <g.vertexCount()-1; k++){
            for(int i=0;i<g.vertexCount();i++){
                for(int neigh: g.neighbors(i)){
                    if(distance[i] == NEGATIVE_INF || distance[i]+g.weight(i, neigh) < distance[neigh]){
                        distance[neigh] = NEGATIVE_INF;
                    }
                }
            }
        }
        return distance;
    }

    // True if any vertex reachable from source lies on / beyond a negative cycle.
    // Throw NullPointerException if g is null.
    // Throw IndexOutOfBoundsException if source is not in [0, vertexCount).
    public static boolean hasNegativeCycle(Graph g, int source) {
        if(g == null){
            throw new NullPointerException();
        }
        if(source < 0 || source >= g.vertexCount()){
            throw new IndexOutOfBoundsException();
        }
        
        long[] distance = new long[g.vertexCount()];
        for(int i=0;i<distance.length;i++){
            distance[i] = UNREACHABLE;
        }
        distance[source] = 0;
        for(int k = 0;k <g.vertexCount()-1; k++){
            for(int i=0;i<g.vertexCount();i++){
                for(int neigh: g.neighbors(i)){
                    if(distance[i]+g.weight(i, neigh) < distance[neigh]){
                        distance[neigh] = distance[i]+g.weight(i, neigh);
                    }
                }
            }
        }
        boolean returnv = false;
        for(int k = 0;k <g.vertexCount()-1; k++){
            for(int i=0;i<g.vertexCount();i++){
                for(int neigh: g.neighbors(i)){
                    if(distance[i] == NEGATIVE_INF ||  distance[i]+g.weight(i, neigh) < distance[neigh]){
                        distance[neigh] = NEGATIVE_INF;
                        returnv = true;
                    }
                }
            }
        }
        return returnv;
    }
}

class Bellman_ford_Main {
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
        // Negative edge, no negative cycle: 0->1(4) 0->2(5) 1->2(-3) 2->3(2)
        W g = new W(4);
        g.add(0,1,4); g.add(0,2,5); g.add(1,2,-3); g.add(2,3,2);
        checkEquals("dist with negative edge",
                Arrays.toString(new long[]{0, 4, 1, 3}),
                Arrays.toString(Bellman_ford.shortestPaths(g, 0)));
        checkEquals("no negative cycle", false, Bellman_ford.hasNegativeCycle(g, 0));

        // Negative cycle: 0->1(1) 1->2(1) 2->1(-3) 2->3(1). Cycle 1->2->1 = -2.
        W c = new W(4);
        c.add(0,1,1); c.add(1,2,1); c.add(2,1,-3); c.add(2,3,1);
        checkEquals("negative-cycle distances",
                Arrays.toString(new long[]{0, Bellman_ford.NEGATIVE_INF, Bellman_ford.NEGATIVE_INF, Bellman_ford.NEGATIVE_INF}),
                Arrays.toString(Bellman_ford.shortestPaths(c, 0)));
        checkEquals("has negative cycle", true, Bellman_ford.hasNegativeCycle(c, 0));

        checkThrows("null graph", NullPointerException.class, () -> Bellman_ford.shortestPaths(null, 0));
        checkThrows("source oob", IndexOutOfBoundsException.class, () -> Bellman_ford.shortestPaths(g, 4));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}
