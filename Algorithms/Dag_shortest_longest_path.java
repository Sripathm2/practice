package Algorithms;
import java.util.Objects;

import Data_structures.Graph;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

// Single-source shortest / longest path on a weighted DAG.
// Because the graph is acyclic, topologically sort it, then relax edges in that
// order: by the time you reach a vertex, every path into it is already finalized,
// so a single pass suffices. Works with NEGATIVE weights too (a DAG has no cycles,
// hence no negative cycles). Shortest and longest differ only in the comparison
// (and the initial "infinity" sign).
public class Dag_shortest_longest_path {

    public static final long UNREACHABLE_SHORTEST = Long.MAX_VALUE;
    public static final long UNREACHABLE_LONGEST  = Long.MIN_VALUE;

    // dist[v] = weight of the shortest path from source to v, or UNREACHABLE_SHORTEST
    // if v is not reachable. dist[source] == 0. Length == vertexCount().
    // Throw NullPointerException if g is null.
    // Throw IndexOutOfBoundsException if source is not in [0, vertexCount).
    // Throw IllegalArgumentException if g contains a cycle (not a DAG).
    public static long[] shortestPaths(Graph g, int source) {
        if(g == null){
            throw new NullPointerException();
        } 
        if(source <0 || source >=g.vertexCount()){
            throw new IndexOutOfBoundsException();
        }

        int[] topo_order = Topological_sort_dfs.sort_with_start_node(g, source);

        long[] dist = new long[g.vertexCount()];
        for(int i =0;i< dist.length;i++){
            dist[i] = UNREACHABLE_SHORTEST;
        }

        for(int id: topo_order){
            if(source == id){
                dist[id] = 0;
            }
            if(dist[id] == UNREACHABLE_SHORTEST) continue;
            for(int neigh: g.neighbors(id)){
                long weight = g.weight(id, neigh);
                if(dist[id] + weight < dist[neigh]){
                    dist[neigh] = dist[id] + weight;
                }
            }
        }

        return dist;
    }

    // dist[v] = weight of the longest path from source to v, or UNREACHABLE_LONGEST
    // if v is not reachable. dist[source] == 0. Length == vertexCount().
    // Throw NullPointerException if g is null.
    // Throw IndexOutOfBoundsException if source is not in [0, vertexCount).
    // Throw IllegalArgumentException if g contains a cycle (not a DAG).
    public static long[] longestPaths(Graph g, int source) {
        if(g == null){
            throw new NullPointerException();
        } 
        if(source <0 || source >=g.vertexCount()){
            throw new IndexOutOfBoundsException();
        }

        int[] topo_order = Topological_sort_dfs.sort_with_start_node(g, source);

        long[] dist = new long[g.vertexCount()];
        for(int i =0;i< dist.length;i++){
            dist[i] = UNREACHABLE_LONGEST;
        }

        for(int id: topo_order){
            if(source == id){
                dist[id] = 0;
            }
            for(int neigh: g.neighbors(id)){
                long weight = -1 * g.weight(id, neigh);
                if(dist[id] + weight < dist[neigh] || dist[neigh] == UNREACHABLE_LONGEST){
                    dist[neigh] = dist[id] + weight;
                }
            }
        }

        for(int i =0;i< dist.length;i++){
            dist[i] *= -1;
        }

        return dist;
    }
}

class Dag_shortest_longest_path_Main {
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

    // Weighted directed graph for testing.
    private static final class WDiGraph implements Graph {
        private final int n;
        private final List<int[]>[] adj;   // adj[u] = list of {to, weight}
        @SuppressWarnings("unchecked")
        WDiGraph(int n) { this.n = n; adj = new List[n]; for (int i = 0; i < n; i++) adj[i] = new ArrayList<>(); }
        void addEdge(int u, int v, int w) { adj[u].add(new int[]{v, w}); }
        public int vertexCount() { return n; }
        public boolean isDirected() { return true; }
        public boolean hasEdge(int u, int v) { for (int[] e : adj[u]) if (e[0] == v) return true; return false; }
        public int weight(int u, int v) { for (int[] e : adj[u]) if (e[0] == v) return e[1]; throw new java.util.NoSuchElementException(); }
        public int[] neighbors(int v) {
            List<Integer> ns = new ArrayList<>();
            for (int[] e : adj[v]) ns.add(e[0]);
            Collections.sort(ns);
            int[] out = new int[ns.size()];
            for (int i = 0; i < out.length; i++) out[i] = ns.get(i);
            return out;
        }
    }

    public static void main(String[] args) {
        // DAG (7 vertices; 6 is isolated/unreachable):
        // 0->1(2) 0->2(4) 1->2(1) 1->3(7) 2->4(3) 3->4(1) 3->5(5) 4->5(2)
        WDiGraph g = new WDiGraph(7);
        g.addEdge(0,1,2); g.addEdge(0,2,4); g.addEdge(1,2,1); g.addEdge(1,3,7);
        g.addEdge(2,4,3); g.addEdge(3,4,1); g.addEdge(3,5,5); g.addEdge(4,5,2);

        long U = Dag_shortest_longest_path.UNREACHABLE_SHORTEST;
        long L = Dag_shortest_longest_path.UNREACHABLE_LONGEST;

        checkEquals("shortest from 0",
                java.util.Arrays.toString(new long[]{0,2,3,9,6,8,U}),
                java.util.Arrays.toString(Dag_shortest_longest_path.shortestPaths(g, 0)));
        checkEquals("longest from 0",
                java.util.Arrays.toString(new long[]{0,2,4,9,10,14,L}),
                java.util.Arrays.toString(Dag_shortest_longest_path.longestPaths(g, 0)));

        // Negative weights are fine on a DAG: 0->1(5) 0->2(2) 1->3(-4) 2->3(1)
        // shortest to 3 = 0->1->3 = 1 (beats 0->2->3 = 3)
        WDiGraph neg = new WDiGraph(4);
        neg.addEdge(0,1,5); neg.addEdge(0,2,2); neg.addEdge(1,3,-4); neg.addEdge(2,3,1);
        checkEquals("shortest with negatives",
                java.util.Arrays.toString(new long[]{0,5,2,1}),
                java.util.Arrays.toString(Dag_shortest_longest_path.shortestPaths(neg, 0)));
        checkEquals("longest with negatives",   // 0->2->3 = 3 beats 0->1->3 = 1
                java.util.Arrays.toString(new long[]{0,5,2,3}),
                java.util.Arrays.toString(Dag_shortest_longest_path.longestPaths(neg, 0)));

        // Source in the middle: only downstream vertices reachable
        checkEquals("shortest from 3",
                java.util.Arrays.toString(new long[]{U,U,U,0,1,3,U}),
                java.util.Arrays.toString(Dag_shortest_longest_path.shortestPaths(g, 3)));

        // Single vertex
        checkEquals("single vertex",
                java.util.Arrays.toString(new long[]{0}),
                java.util.Arrays.toString(Dag_shortest_longest_path.shortestPaths(new WDiGraph(1), 0)));

        // --- Cycle -> not a DAG ---
        WDiGraph cyc = new WDiGraph(3);
        cyc.addEdge(0,1,1); cyc.addEdge(1,2,1); cyc.addEdge(2,0,1);
        checkThrows("cycle throws", IllegalArgumentException.class,
                () -> Dag_shortest_longest_path.shortestPaths(cyc, 0));

        // --- Errors ---
        checkThrows("null graph", NullPointerException.class,
                () -> Dag_shortest_longest_path.shortestPaths(null, 0));
        checkThrows("source oob", IndexOutOfBoundsException.class,
                () -> Dag_shortest_longest_path.longestPaths(g, 7));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}