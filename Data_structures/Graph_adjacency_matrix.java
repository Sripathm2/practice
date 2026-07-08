package Data_structures;
import java.util.Objects;
import java.util.NoSuchElementException;
import java.util.Arrays;

// Graph stored as an adjacency matrix: an n x n grid where cell (u, v) records
// whether edge u -> v exists and its weight. Great for dense graphs and O(1)
// edge/weight lookup; costs O(n^2) memory and O(n) to list a vertex's neighbors.
public class Graph_adjacency_matrix implements Graph {
    private int n;
    private boolean directed;
    private boolean[][] present;   // present[u][v] = is there an edge u->v
    private int[][] w;             // w[u][v] = weight of edge u->v (valid iff present)
    private int edges;             // number of edges (undirected counted once)

    // Create a graph with n vertices (0..n-1). If directed is false, every edge
    // is stored both ways.
    // Throw IllegalArgumentException if n < 0.
    public Graph_adjacency_matrix(int n, boolean directed) {
        if(n < 0){
            throw new IllegalArgumentException();
        }
        this.n = n;
        this.directed = directed;
        this.edges = 0;
        this.present = new boolean[n][n];
        this.w = new int[n][n];
    }

    // Add an unweighted edge (weight 1) between u and v.
    public void addEdge(int u, int v) {
        if(u < 0  || v < 0 || u >= this.n || v >= this.n){
            throw new IndexOutOfBoundsException();
        }
        if(this.present[u][v] == false){
            this.edges += 1;
        }
        this.present[u][v] = true;
        this.w[u][v] = 1;
        if(!directed){
            this.present[v][u] = true;
            this.w[v][u] = 1;
        }
    }

    // Add an edge u -> v (and v -> u if undirected) with the given weight.
    // Re-adding an existing edge updates its weight (does not double-count).
    // Throw IndexOutOfBoundsException if u or v is not in [0, n).
    public void addEdge(int u, int v, int weight) {
        if(u < 0  || v < 0 || u >= this.n || v >= this.n){
            throw new IndexOutOfBoundsException();
        }
        if(this.present[u][v] == false){
            this.edges += 1;
        }
        this.present[u][v] = true;
        this.w[u][v] = weight;
        if(!directed){
            this.present[v][u] = true;
            this.w[v][u] = weight;
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
        return this.present[u][v];
    }

    @Override
    public int weight(int u, int v) {
        if(!this.present[u][v]){
            throw new NoSuchElementException();
        }
        return this.w[u][v];
    }

    @Override
    public int[] neighbors(int v) {
        if(v < 0 || v >= this.n){
            throw new IndexOutOfBoundsException();
        }

        int ncount = 0;
        for(int i = 0; i < this.present[v].length; i++){
            if(this.present[v][i]){
                ncount += 1;
            }
        }

        int [] returnv = new int[ncount];
        int index = 0;
        for(int i = 0; i < this.present[v].length; i++){
            if(this.present[v][i]){
                returnv[index] = i;
                index += 1;
            }
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

class Graph_adjacency_matrix_Main {
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
        Graph_adjacency_matrix g = new Graph_adjacency_matrix(5, false);
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
        checkEquals("neighbors(0)", Arrays.toString(new int[]{1, 2}), Arrays.toString(g.neighbors(0)));
        checkEquals("neighbors(2)", Arrays.toString(new int[]{0, 1}), Arrays.toString(g.neighbors(2)));
        checkEquals("edgeCount", 3, g.edgeCount());

        // update weight, no double count
        g.addEdge(0, 1, 9);
        checkEquals("weight updated", 9, g.weight(0, 1));
        checkEquals("edgeCount unchanged", 3, g.edgeCount());

        // --- Directed ---
        Graph_adjacency_matrix d = new Graph_adjacency_matrix(3, true);
        d.addEdge(0, 1, 4);
        checkTrue ("dir has 0->1", d.hasEdge(0, 1));
        checkEquals("dir no 1->0", false, d.hasEdge(1, 0));
        checkEquals("dir neighbors(0)", Arrays.toString(new int[]{1}), Arrays.toString(d.neighbors(0)));
        checkEquals("dir neighbors(1)", Arrays.toString(new int[]{}), Arrays.toString(d.neighbors(1)));
        checkEquals("dir edgeCount", 1, d.edgeCount());

        // --- Errors ---
        final Graph_adjacency_matrix e = g;
        checkThrows("addEdge oob lo", IndexOutOfBoundsException.class, () -> e.addEdge(-1, 0));
        checkThrows("addEdge oob hi", IndexOutOfBoundsException.class, () -> e.addEdge(0, 5));
        checkThrows("weight absent", NoSuchElementException.class, () -> e.weight(0, 4));
        checkThrows("negative n", IllegalArgumentException.class, () -> new Graph_adjacency_matrix(-1, false));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}