package Algorithms;

import Data_structures.Graph;
import Data_structures.Queue;
import Data_structures.Stack;
import Data_structures.Tree_node;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

// Topological sort via Kahn's algorithm (BFS on in-degrees). Compute every
// vertex's in-degree; start with all in-degree-0 vertices; repeatedly remove one,
// append it to the order, and decrement its out-neighbors' in-degrees, enqueuing
// any that hit 0. If fewer than n vertices come out, the graph has a cycle.
public class Topological_sort_kahn {

    private static ArrayList<Integer> order;
    private static int[] incoming;

    // Return a topological ordering of g's vertices.
    // Throw NullPointerException if g is null.
    // Throw IllegalArgumentException if g contains a cycle (no ordering exists).
    public static int[] sort(Graph g) {
        if(g == null){
            throw new NullPointerException();
        }

        order = new ArrayList<Integer>();
        incoming = new int[g.vertexCount()];

        for(int i=0; i<g.vertexCount(); i++){
            for(int neigh: g.neighbors(i)){
                incoming[neigh] += 1;
            }
        }

        Queue<Integer> queue = new Queue<Integer>();

        for(int i=0; i<g.vertexCount(); i++){
            if(incoming[i] == 0){
                queue.enqueue(i);
            }
        }

        while(!queue.isEmpty()){
            int node = (int)queue.dequeue();
            order.add(node);
            for(int neigh: g.neighbors(node)){
                incoming[neigh] -= 1;
                if(incoming[neigh] == 0){
                    queue.enqueue(neigh);
                }
            }
        }

        if(order.size() != g.vertexCount()){
            throw new IllegalArgumentException();
        }

        return order.stream().mapToInt(i -> i).toArray();
    }

    public static Tree_node rootTree(List<List<Integer>> adj, int root) {
        if(adj == null){
            throw new NullPointerException();
        }
        if(adj.size() == 0){
            throw new IllegalArgumentException();
        }
        if(root < 0 || root >= adj.size()){
            throw new IndexOutOfBoundsException();
        }
        Tree_node rootNode = new Tree_node(root, null);
        Stack<Tree_node> stack = new Stack<>();
        stack.push(rootNode);
        boolean[] visited = new boolean[adj.size()];
        while(!stack.isEmpty()){
            Tree_node currnode = stack.pop();
            int id = currnode.id;
            if(!visited[id]){
                visited[id] = true;
                List<Integer> neighbors = adj.get(id);
                for(int i = 0; i < neighbors.size(); i++){
                    if(currnode.parent != null && neighbors.get(i) == currnode.parent.id){
                        continue;
                    }
                    Tree_node newnode = new Tree_node(neighbors.get(i), currnode);
                    currnode.children.add(newnode);
                    stack.push(newnode);
                }
            }
        }
        return rootNode;
    }
}

class Topological_sort_kahn_Main {
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
                if (pos[u] > pos[w]) return false;
        return true;
    }

    public static void main(String[] args) {
        DiGraph g1 = new DiGraph(5);
        g1.addEdge(0,1); g1.addEdge(0,2); g1.addEdge(1,3); g1.addEdge(2,3); g1.addEdge(3,4);
        checkTrue("diamond DAG valid topo", validTopo(g1, Topological_sort_kahn.sort(g1)));

        DiGraph g2 = new DiGraph(5);
        g2.addEdge(4,3); g2.addEdge(3,2); g2.addEdge(2,1); g2.addEdge(1,0);
        checkTrue("chain valid topo", validTopo(g2, Topological_sort_kahn.sort(g2)));

        DiGraph g3 = new DiGraph(5);
        g3.addEdge(0,1); g3.addEdge(2,3);
        checkTrue("disconnected DAG valid topo", validTopo(g3, Topological_sort_kahn.sort(g3)));

        checkTrue("edgeless valid topo", validTopo(new DiGraph(4), Topological_sort_kahn.sort(new DiGraph(4))));

        // wide DAG: 0 -> {1,2,3}, {1,2,3} -> 4
        DiGraph g4 = new DiGraph(5);
        g4.addEdge(0,1); g4.addEdge(0,2); g4.addEdge(0,3); g4.addEdge(1,4); g4.addEdge(2,4); g4.addEdge(3,4);
        checkTrue("wide DAG valid topo", validTopo(g4, Topological_sort_kahn.sort(g4)));

        // --- Cycle detection ---
        DiGraph cyc = new DiGraph(3);
        cyc.addEdge(0,1); cyc.addEdge(1,2); cyc.addEdge(2,0);
        checkThrows("3-cycle throws", IllegalArgumentException.class, () -> Topological_sort_kahn.sort(cyc));
        // partial cycle: 0->1, 1->2, 2->1 (2 and 1 never reach in-degree 0)
        DiGraph pc = new DiGraph(3);
        pc.addEdge(0,1); pc.addEdge(1,2); pc.addEdge(2,1);
        checkThrows("partial cycle throws", IllegalArgumentException.class, () -> Topological_sort_kahn.sort(pc));

        // --- Null ---
        checkThrows("null graph", NullPointerException.class, () -> Topological_sort_kahn.sort(null));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}