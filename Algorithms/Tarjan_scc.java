package Algorithms;
import Data_structures.Graph;
import Data_structures.Stack;

import java.util.Objects;
import java.util.List;
import java.util.ArrayList;

// Tarjan's algorithm: find strongly connected components (SCCs) of a directed graph
// in a single DFS. Each vertex gets a DFS index and a "low-link" = the smallest
// index reachable from its subtree (including via one back-edge to a vertex still
// on the stack). When a vertex's low-link equals its own index, it's the root of an
// SCC, and everything above it on the stack forms that component.
public class Tarjan_scc {

    private static int count;
    private static final int UNVISITED = -1;
    private static int[] ids;
    private static int[] low;
    private static boolean[] onStack;
    private static Stack<Integer> st;
    private static int idgiver;
    // component[v] = id of the SCC containing v. Vertices in the same SCC share an
    // id; ids lie in [0, sccCount). The exact id values are implementation-defined,
    // only the grouping is guaranteed.
    // Throw NullPointerException if g is null.
    public static int[] sccIds(Graph g) {
        if(g == null){
            throw new NullPointerException();
        }
        count =0;
        idgiver = 0;
        ids = new int[g.vertexCount()];
        low = new int[g.vertexCount()];
        onStack = new boolean[g.vertexCount()];
        st = new Stack<Integer>();

        for(int i=0;i<ids.length;i++){
            ids[i] = UNVISITED;
        }
        for(int i=0;i<ids.length;i++){
            if(ids[i] == UNVISITED){
                dfs(g, i);
            }
        }
        return low;
    }

    private static void dfs(Graph g, int node){
        ids[node] = low[node] = idgiver++;
        onStack[node] = true;
        st.push(node);
        for(int neigh: g.neighbors(node)){
            if(ids[neigh] == UNVISITED){
                dfs(g,neigh);
            }
            if(onStack[neigh]){
                low[node] = Math.min(low[node],low[neigh]);
            }
        }

        if(ids[node] == low[node]){
            while(true){
                int tempid = st.pop();
                low[tempid] = low[node];
                onStack[tempid] = false;
                if(tempid == node){
                    break;
                }
            }
            count += 1;
        }
    }

    // Number of strongly connected components.
    // Throw NullPointerException if g is null.
    public static int sccCount(Graph g) {
        sccIds(g);
        return count;
    }
}

class Tarjan_scc_Main {
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

    static final class D implements Graph {
        final int n; final List<List<Integer>> adj;
        D(int n) { this.n = n; adj = new ArrayList<>(); for (int i = 0; i < n; i++) adj.add(new ArrayList<>()); }
        void add(int u, int v) { adj.get(u).add(v); }
        public int vertexCount() { return n; }
        public boolean isDirected() { return true; }
        public boolean hasEdge(int u, int v) { return adj.get(u).contains(v); }
        public int weight(int u, int v) { if (!hasEdge(u, v)) throw new java.util.NoSuchElementException(); return 1; }
        public int[] neighbors(int v) {
            List<Integer> ns = new ArrayList<>(adj.get(v)); java.util.Collections.sort(ns);
            int[] out = new int[ns.size()]; for (int i = 0; i < out.length; i++) out[i] = ns.get(i); return out;
        }
    }

    // Do two vertices share a component id?
    private static boolean same(int[] comp, int a, int b) { return comp.length > Math.max(a, b) && comp[a] == comp[b]; }

    public static void main(String[] args) {
        // SCCs: {0,1,2} (cycle), {3,4} (cycle), {5} isolated ; edge 2->3 links them
        D g = new D(6);
        g.add(0,1); g.add(1,2); g.add(2,0); g.add(3,4); g.add(4,3); g.add(2,3);
        checkEquals("scc count", 3, Tarjan_scc.sccCount(g));

        int[] comp = Tarjan_scc.sccIds(g);
        checkTrue("0,1,2 same comp", same(comp, 0, 1) && same(comp, 1, 2));
        checkTrue("3,4 same comp", same(comp, 3, 4));
        checkTrue("0 and 3 differ", comp.length == 6 && comp[0] != comp[3]);
        checkTrue("5 is its own comp", comp.length == 6 && comp[5] != comp[0] && comp[5] != comp[3]);

        // DAG (no cycles): every vertex its own SCC
        D dag = new D(3);
        dag.add(0,1); dag.add(1,2);
        checkEquals("DAG scc count", 3, Tarjan_scc.sccCount(dag));

        // One big cycle: single SCC
        D ring = new D(4);
        ring.add(0,1); ring.add(1,2); ring.add(2,3); ring.add(3,0);
        checkEquals("ring scc count", 1, Tarjan_scc.sccCount(ring));

        checkThrows("null graph", NullPointerException.class, () -> Tarjan_scc.sccCount(null));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}
