package Data_structures;
import java.util.Objects;
import java.util.NoSuchElementException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

// Graph stored as an adjacency list: each vertex keeps a list of its outgoing
// edges (neighbor + weight). Great for sparse graphs and cheap neighbor
// iteration (O(degree)); edge/weight lookup is O(degree) rather than O(1).
public class Graph_adjacency_list implements Graph {

    private static final class Edge {
        final int to;
        int weight;
        Edge(int to, int weight) { this.to = to; this.weight = weight; }
    }

    private int n;
    private boolean directed;
    private List<List<Edge>> adj;   // adj.get(u) = outgoing edges from u
    private int edges;              // number of edges (undirected counted once)

    // Create a graph with n vertices (0..n-1). If directed is false, every edge
    // is stored in both endpoints' lists.
    // Throw IllegalArgumentException if n < 0.
    public Graph_adjacency_list(int n, boolean directed) {
        if(n < 0){
            throw new IllegalArgumentException();
        }
        this.n = n;
        this.directed = directed;
        this.edges = 0;
        this.adj = new ArrayList<>();

        for(int i =0; i < this.n; i++){
            this.adj.add(new ArrayList<Edge>());
        }
    }

    // Add an unweighted edge (weight 1) between u and v.
    public void addEdge(int u, int v) {
        if(u < 0  || v < 0 || u >= this.n || v >= this.n){
            throw new IndexOutOfBoundsException();
        }
        for (Edge edge : this.adj.get(u)) {
            if(edge.to == v) return;
        }
        this.adj.get(u).add(new Edge(v, 1));
        this.edges += 1;
        if(!this.directed){
            this.adj.get(v).add(new Edge(u, 1));
        }
    }

    // Add an edge u -> v (and v -> u if undirected) with the given weight.
    // Re-adding an existing edge updates its weight (does not double-count).
    // Throw IndexOutOfBoundsException if u or v is not in [0, n).
    public void addEdge(int u, int v, int weight) {
        if(u < 0  || v < 0 || u >= this.n || v >= this.n){
            throw new IndexOutOfBoundsException();
        }
        for (Edge edge : this.adj.get(u)) {
            if(edge.to == v) {
                edge.weight = weight;
                return;
            }
        }
        this.adj.get(u).add(new Edge(v, weight));
        this.edges += 1;
        if(!this.directed){
            this.adj.get(v).add(new Edge(u, weight));
        }
    }

    @Override
    public int vertexCount() {
        return this.n;
    }

    @Override
    public boolean isDirected() {
        return this.directed;
    }

    @Override
    public boolean hasEdge(int u, int v) {
        if(u < 0  || v < 0 || u >= this.n || v >= this.n){
            throw new IndexOutOfBoundsException();
        }
        for (Edge edge : this.adj.get(u)) {
            if(edge.to == v) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int weight(int u, int v) {
        if(u < 0  || v < 0 || u >= this.n || v >= this.n){
            throw new IndexOutOfBoundsException();
        }
        for (Edge edge : this.adj.get(u)) {
            if(edge.to == v) {
                return edge.weight;
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    public int[] neighbors(int v) {
        if(v < 0 || v >= this.n){
            throw new IndexOutOfBoundsException();
        }
        int [] returnv = new int[this.adj.get(v).size()];
        int index = 0;
        for (Edge edge : this.adj.get(v)) {
            returnv[index] = edge.to;
            index += 1;
        }
        return returnv;
    }

    // Number of edges (each undirected edge counted once).
    public int edgeCount() {
        return this.edges;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int v = 0; v < n; v++) {
            sb.append(v).append(" ->");
            int[] ns = neighbors(v);              // ascending, reuse your own method
            for (int i = 0; i < ns.length; i++) {
                sb.append(i == 0 ? " " : ", ").append(ns[i]).append("(").append(weight(v, ns[i])).append(")");
            }
            if (v < n - 1) sb.append("\n");
        }
        return sb.toString();
    }
}

class Graph_adjacency_list_Main {
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

    public static void main(String[] args) {
        // --- Undirected ---
        Graph_adjacency_list g = new Graph_adjacency_list(5, false);
        checkEquals("vertexCount", 5, g.vertexCount());
        checkEquals("isDirected", false, g.isDirected());
        g.addEdge(0, 1);
        g.addEdge(0, 2, 7);
        g.addEdge(1, 2);
        checkTrue ("has 0-1", g.hasEdge(0, 1));
        checkTrue ("has 1-0 (undirected)", g.hasEdge(1, 0));
        checkEquals("no 0-3", false, g.hasEdge(0, 3));
        checkEquals("weight 0-2", 7, g.weight(0, 2));
        checkEquals("default weight 0-1", 1, g.weight(0, 1));
        // neighbors ascending regardless of insertion order
        checkEquals("neighbors(0)", Arrays.toString(new int[]{1, 2}), Arrays.toString(g.neighbors(0)));
        checkEquals("neighbors(2)", Arrays.toString(new int[]{0, 1}), Arrays.toString(g.neighbors(2)));
        checkEquals("edgeCount", 3, g.edgeCount());

        g.addEdge(0, 1, 9);
        checkEquals("weight updated", 9, g.weight(0, 1));
        checkEquals("edgeCount unchanged", 3, g.edgeCount());

        // --- Directed ---
        Graph_adjacency_list d = new Graph_adjacency_list(3, true);
        d.addEdge(0, 1, 4);
        checkTrue ("dir has 0->1", d.hasEdge(0, 1));
        checkEquals("dir no 1->0", false, d.hasEdge(1, 0));
        checkEquals("dir neighbors(0)", Arrays.toString(new int[]{1}), Arrays.toString(d.neighbors(0)));
        checkEquals("dir neighbors(1)", Arrays.toString(new int[]{}), Arrays.toString(d.neighbors(1)));
        checkEquals("dir edgeCount", 1, d.edgeCount());

        // --- Errors ---
        final Graph_adjacency_list e = g;
        checkThrows("addEdge oob lo", IndexOutOfBoundsException.class, () -> e.addEdge(-1, 0));
        checkThrows("addEdge oob hi", IndexOutOfBoundsException.class, () -> e.addEdge(0, 5));
        checkThrows("weight absent", NoSuchElementException.class, () -> e.weight(0, 4));
        checkThrows("negative n", IllegalArgumentException.class, () -> new Graph_adjacency_list(-1, false));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}