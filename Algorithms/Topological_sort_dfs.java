package Algorithms;
import Data_structures.Graph;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

// Topological sort via DFS. Run DFS over a directed acyclic graph; record each
// vertex when it FINISHES (post-order). The reverse of the finish order is a
// topological ordering — every edge u->v then points from an earlier vertex to a
// later one. Detects cycles (a back-edge to a vertex still on the recursion stack).
public class Topological_sort_dfs {

    private static ArrayList<Integer> order;
    private static boolean[] visited;

    // Return a topological ordering of g's vertices.
    // Throw NullPointerException if g is null.
    // Throw IllegalArgumentException if g contains a cycle (no ordering exists).
    public static int[] sort(Graph g) {
        if(g == null){
            throw new NullPointerException();
        }

        order = new ArrayList<Integer>();
        visited = new boolean[g.vertexCount()];

        for(int i=0; i<g.vertexCount(); i++){
            if(!visited[i]){
                dfs(g, i, i);
            }
        }
        return order.stream().mapToInt(i -> i).toArray();
    }

    public static int[] sort_with_start_node(Graph g, int start) {
        if(g == null){
            throw new NullPointerException();
        }

        order = new ArrayList<Integer>();
        visited = new boolean[g.vertexCount()];

        dfs(g,start,start);

        for(int i=0; i<g.vertexCount(); i++){
            if(!visited[i]){
                dfs(g, i, i);
            }
        }
        return order.stream().mapToInt(i -> i).toArray();
    }

    public static void dfs(Graph g, int current, int start) {
        if(start == current && visited[current] == true){
            throw new IllegalArgumentException();
        }
        if(visited[current]){
            return;
        }
        
        visited[current] = true;
        for(int neigh: g.neighbors(current)){
            dfs(g, neigh, start);
        }
        order.add(0, current);
    }

}

class Topological_sort_dfs_Main {
    private static int passed = 0;
    private static int failed = 0;

    private static void checkTrue(String name, boolean cond) {
        if (cond) { passed++; System.out.println("PASS: " + name); }
        else      { failed++; System.out.println("FAIL: " + name); }
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

    private static final class DiGraph implements Graph {
        private final int n;
        private final List<List<Integer>> adj;
        DiGraph(int n) { this.n = n; adj = new ArrayList<>(); for (int i = 0; i < n; i++) adj.add(new ArrayList<>()); }
        void addEdge(int u, int v) { adj.get(u).add(v); }
        public int vertexCount() { return n; }
        public boolean isDirected() { return true; }
        public boolean hasEdge(int u, int v) { return adj.get(u).contains(v); }
        public int weight(int u, int v) { if (!hasEdge(u, v)) throw new java.util.NoSuchElementException(); return 1; }
        public int[] neighbors(int v) {
            List<Integer> ns = new ArrayList<>(adj.get(v)); Collections.sort(ns);
            int[] out = new int[ns.size()];
            for (int i = 0; i < out.length; i++) out[i] = ns.get(i);
            return out;
        }
    }

    // Is `order` a valid topological ordering of g? (permutation + every edge respected)
    static boolean validTopo(Graph g, int[] order) {
        int n = g.vertexCount();
        if (order.length != n) return false;
        int[] pos = new int[n]; boolean[] seen = new boolean[n];
        for (int i = 0; i < n; i++) {
            int v = order[i];
            if (v < 0 || v >= n || seen[v]) return false;
            seen[v] = true; pos[v] = i;
        }
        for (int u = 0; u < n; u++)
            for (int w : g.neighbors(u))
                if (pos[u] > pos[w]) return false;   // edge u->w must go forward
        return true;
    }

    public static void main(String[] args) {
        // DAG: 0->1, 0->2, 1->3, 2->3, 3->4
        DiGraph g1 = new DiGraph(5);
        g1.addEdge(0,1); g1.addEdge(0,2); g1.addEdge(1,3); g1.addEdge(2,3); g1.addEdge(3,4);
        checkTrue("diamond DAG valid topo", validTopo(g1, Topological_sort_dfs.sort(g1)));

        // Chain 4->3->2->1->0 (reverse-labeled)
        DiGraph g2 = new DiGraph(5);
        g2.addEdge(4,3); g2.addEdge(3,2); g2.addEdge(2,1); g2.addEdge(1,0);
        checkTrue("chain valid topo", validTopo(g2, Topological_sort_dfs.sort(g2)));

        // Disconnected DAG: two components 0->1 and 2->3, plus isolated 4
        DiGraph g3 = new DiGraph(5);
        g3.addEdge(0,1); g3.addEdge(2,3);
        checkTrue("disconnected DAG valid topo", validTopo(g3, Topological_sort_dfs.sort(g3)));

        // No edges: any permutation is valid
        checkTrue("edgeless valid topo", validTopo(new DiGraph(4), Topological_sort_dfs.sort(new DiGraph(4))));

        // --- Cycle detection ---
        DiGraph cyc = new DiGraph(3);
        cyc.addEdge(0,1); cyc.addEdge(1,2); cyc.addEdge(2,0);
        checkThrows("3-cycle throws", IllegalArgumentException.class, () -> Topological_sort_dfs.sort(cyc));
        DiGraph self = new DiGraph(2);
        self.addEdge(0,0);
        checkThrows("self-loop throws", IllegalArgumentException.class, () -> Topological_sort_dfs.sort(self));

        // --- Null ---
        checkThrows("null graph", NullPointerException.class, () -> Topological_sort_dfs.sort(null));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}