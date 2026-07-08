package Algorithms;
import Data_structures.Graph;
import Data_structures.Queue;
import Data_structures.Stack;

import java.util.Objects;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

// Depth-first search on any Graph. DFS dives as deep as possible along each
// branch before backtracking, marking vertices visited so each is processed once.
// Neighbors are visited in ascending order (Graph.neighbors is ascending), so the
// traversal order below is deterministic.
public class Depth_first_search {


    // Vertices in the order DFS first visits them, starting from `start`
    // (only vertices reachable from start are included).
    // Throw NullPointerException if g is null.
    // Throw IndexOutOfBoundsException if start is not in [0, vertexCount).
    public static int[] preorder(Graph g, int start) {
        if(g == null){
            throw new NullPointerException();
        } 
        if(start <0 || start >=g.vertexCount()){
            throw new IndexOutOfBoundsException();
        }

        ArrayList<Integer> order = new ArrayList<Integer>();
        boolean [] visit = new boolean[g.vertexCount()];
        Stack<Integer> queue = new Stack<Integer>();
        queue.push(start);
        while(!queue.isEmpty()){
            int node = (int)queue.pop();
            if(!visit[node]){
                order.add(node);
                visit[node] = true;
                int [] neighbors = g.neighbors(node);
                for(int i = neighbors.length-1; i >= 0; i--){
                    queue.push(neighbors[i]);
                }
            }
        }
        return order.stream().mapToInt(i -> i).toArray();
    }

    // reachable[v] == true iff v is reachable from `start`.
    // Throw NullPointerException if g is null.
    // Throw IndexOutOfBoundsException if start is not in [0, vertexCount).
    public static boolean[] reachable(Graph g, int start) {
        boolean [] visit = new boolean[g.vertexCount()];
        int[] connect = preorder(g, start);
        for(int i = 0; i <connect.length; i++){
            visit[connect[i]] = true;
        }
        return visit;
    }

    // Number of connected components: run DFS from each not-yet-visited vertex and
    // count how many DFS trees result. (For an undirected graph this is the number
    // of connected components.)
    // Throw NullPointerException if g is null.
    public static int componentCount(Graph g) {
        int numcount = 0;
        boolean [] visit = new boolean[g.vertexCount()];
        int start = 0;
        while(true){
            int[] connect = preorder(g, start);
            numcount += 1;
            for(int i = 0; i <connect.length; i++){
                visit[connect[i]] = true;
            }
            start = -1;
            for(int i = 0; i <visit.length; i++){
                if(!visit[i]){
                    start = i;
                }
            }
            if(start == -1){
                break;
            }
        }
        return numcount;
    }
}

class Depth_first_search_Main {
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

    // Self-contained Graph for testing DFS without depending on the big
    // representation classes. neighbors() returns ascending order.
    private static final class TestGraph implements Graph {
        private final int n;
        private final boolean directed;
        private final List<List<Integer>> adj;
        TestGraph(int n, boolean directed) {
            this.n = n; this.directed = directed;
            adj = new ArrayList<>();
            for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        }
        void addEdge(int u, int v) {
            adj.get(u).add(v);
            if (!directed) adj.get(v).add(u);
        }
        public int vertexCount() { return n; }
        public boolean isDirected() { return directed; }
        public boolean hasEdge(int u, int v) { return adj.get(u).contains(v); }
        public int weight(int u, int v) {
            if (!hasEdge(u, v)) throw new java.util.NoSuchElementException();
            return 1;
        }
        public int[] neighbors(int v) {
            List<Integer> ns = new ArrayList<>(adj.get(v));
            Collections.sort(ns);
            int[] out = new int[ns.size()];
            for (int i = 0; i < out.length; i++) out[i] = ns.get(i);
            return out;
        }
    }

    public static void main(String[] args) {
        // Undirected graph:
        //   0-1, 0-2, 1-3, 2-3   (component {0,1,2,3})
        //   4-5                  (component {4,5})
        //   6 isolated           (component {6})
        TestGraph u = new TestGraph(7, false);
        u.addEdge(0, 1); u.addEdge(0, 2); u.addEdge(1, 3); u.addEdge(2, 3); u.addEdge(4, 5);

        checkEquals("preorder from 0", Arrays.toString(new int[]{0, 1, 3, 2}),
                Arrays.toString(Depth_first_search.preorder(u, 0)));
        checkEquals("preorder from 4", Arrays.toString(new int[]{4, 5}),
                Arrays.toString(Depth_first_search.preorder(u, 4)));
        checkEquals("preorder from 6 (isolated)", Arrays.toString(new int[]{6}),
                Arrays.toString(Depth_first_search.preorder(u, 6)));

        boolean[] r0 = Depth_first_search.reachable(u, 0);
        checkEquals("reachable(0) set",
                Arrays.toString(new boolean[]{true, true, true, true, false, false, false}),
                Arrays.toString(r0));

        checkEquals("component count", 3, Depth_first_search.componentCount(u));

        // Directed graph: 0->1, 1->2, 2->0 (cycle), 3->1
        TestGraph d = new TestGraph(4, true);
        d.addEdge(0, 1); d.addEdge(1, 2); d.addEdge(2, 0); d.addEdge(3, 1);
        checkEquals("dir preorder from 0", Arrays.toString(new int[]{0, 1, 2}),
                Arrays.toString(Depth_first_search.preorder(d, 0)));
        checkEquals("dir reachable from 3",
                Arrays.toString(new boolean[]{true, true, true, true}),
                Arrays.toString(Depth_first_search.reachable(d, 3)));
        checkEquals("dir reachable from 0",
                Arrays.toString(new boolean[]{true, true, true, false}),
                Arrays.toString(Depth_first_search.reachable(d, 0)));

        // --- Errors ---
        checkThrows("null graph", NullPointerException.class,
                () -> Depth_first_search.preorder(null, 0));
        checkThrows("start oob", IndexOutOfBoundsException.class,
                () -> Depth_first_search.preorder(u, 7));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}