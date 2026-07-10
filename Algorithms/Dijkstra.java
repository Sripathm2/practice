package Algorithms;
import Data_structures.Graph;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

// Dijkstra's algorithm: single-source shortest paths on a graph with
// NON-NEGATIVE edge weights. Greedily settle the closest unsettled vertex using a
// priority queue; once a vertex is popped with its final distance, that distance
// can never improve (which is exactly why non-negative weights are required).
public class Dijkstra {

    public static final long UNREACHABLE = Long.MAX_VALUE;

    private static class Pair {
        int first;
        long second;

        Pair(int first, long second) {
            this.first = first;
            this.second = second;
        }
    }

    // dist[v] = shortest-path distance from source to v, or UNREACHABLE if no path.
    // Assumes all edge weights are >= 0.
    // Throw NullPointerException if g is null.
    // Throw IndexOutOfBoundsException if source is not in [0, vertexCount).
    public static long[] shortestPaths(Graph g, int source) {
        if(g == null){
            throw new NullPointerException();
        }
        if(source < 0 || source >= g.vertexCount()){
            throw new IndexOutOfBoundsException();
        }

        PriorityQueue<Pair> pq = new PriorityQueue<Pair>(Comparator.comparingLong(p -> p.second));
        boolean[] visited = new boolean[g.vertexCount()];
        long[] distance = new long[g.vertexCount()];
        for(int i=0;i<distance.length;i++){
            distance[i] = UNREACHABLE;
        }
        distance[source] = 0;
        pq.add(new Pair(source, 0));

        while(!pq.isEmpty()){
            Pair top = pq.poll();
            visited[top.first] = true;
            if(distance[top.first] < top.second){
                continue;
            }
            for(int neigh: g.neighbors(top.first)){
                if(visited[neigh]) continue;
                long weight = g.weight(top.first, neigh);
                if(distance[top.first]+weight < distance[neigh]){
                    distance[neigh] = distance[top.first]+weight;
                    pq.add(new Pair(neigh, distance[neigh]));
                }
            }
        }

        return distance;
    }
}

class Dijkstra_Main {
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

    // Weighted directed graph for testing.
    static final class W implements Graph {
        final int n; final List<List<int[]>> adj;   // adj.get(u) = list of {to, weight}
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
        checkEquals("dist from 0",
                Arrays.toString(new long[]{0, 3, 1, 4, Dijkstra.UNREACHABLE}),
                Arrays.toString(Dijkstra.shortestPaths(g, 0)));
        checkEquals("dist from 2",
                Arrays.toString(new long[]{Dijkstra.UNREACHABLE, 2, 0, 3, Dijkstra.UNREACHABLE}),
                Arrays.toString(Dijkstra.shortestPaths(g, 2)));
        checkEquals("dist from isolated 4",
                Arrays.toString(new long[]{Dijkstra.UNREACHABLE, Dijkstra.UNREACHABLE, Dijkstra.UNREACHABLE, Dijkstra.UNREACHABLE, 0}),
                Arrays.toString(Dijkstra.shortestPaths(g, 4)));

        checkThrows("null graph", NullPointerException.class, () -> Dijkstra.shortestPaths(null, 0));
        checkThrows("source oob", IndexOutOfBoundsException.class, () -> Dijkstra.shortestPaths(g, 5));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}