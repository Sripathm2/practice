package Algorithms;
import java.util.Objects;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

// Eulerian Path / Circuit on a DIRECTED graph (Hierholzer's algorithm).
// An Eulerian path uses every edge exactly once; an Eulerian circuit is one that
// starts and ends at the same vertex. The graph is given as an adjacency list where
// adj.get(u) lists the destinations of u's out-edges (duplicates allowed = multi-edges).
//
// A directed Eulerian path exists iff:
//   - at most one vertex has out-in == +1 (the start) and at most one has in-out == +1 (the end),
//     with every other vertex balanced (in == out), and
//   - all edges lie in a single connected piece (Hierholzer must consume them all).
// If start/end degrees are all balanced, the path is a circuit.
public class Eulerian_path {


    private static ArrayList<Integer> order;
    private static int[][] node_count;

    // Return the vertices of an Eulerian path in order (length = edgeCount + 1),
    // or an empty array if the graph has no Eulerian path. A graph with no edges
    // returns an empty array.
    // Throw NullPointerException if adj (or any row) is null.
    public static int[] eulerianPath(List<List<Integer>> adj) {
        if(adj == null){
            throw new NullPointerException();
        }
        int edges = 0;
        node_count = new int[adj.size()][2];
        for(int i=0;i<adj.size();i++){
            List<Integer> neigh = adj.get(i);
            if(neigh == null){
                throw new NullPointerException();
            }
            for(int j: neigh){
                node_count[i][1] += 1;
                node_count[j][0] += 1;
                edges += 1;
            }
        }
        if(edges ==0){
            return new int[0];
        }

        int start = -1;
        int end = -1;
        order = new ArrayList<Integer>();

        for(int i=0;i<node_count.length;i++){
            if(node_count[i][0]+1 == node_count[i][1] && start ==-1){
                start = i;
            }else if(node_count[i][0]-1 == node_count[i][1] && end ==-1){
                end = i;
            }else if(node_count[i][0] == node_count[i][1]){
                continue;
            }else{
                return new int[0];
            }
        }

        if((start == -1 && end != -1) ||  (start != -1 && end == -1)){
            return new int[0];
        }

        if(start == -1){
            start = 0;
        }


        dfs(start, adj);

        for(int i =0;i< node_count.length;i++){
            if(node_count[i][1] > 0){
                return new int[0];
            }
        }

        return order.stream().mapToInt(i -> i).toArray();
    }

    private static void dfs(int node, List<List<Integer>> adj){
        while(node_count[node][1]!= 0){
            int next_node = adj.get(node).get(node_count[node][1]-1);
            node_count[node][1] -= 1;
            dfs(next_node, adj);
        }
        order.add(0, node);
    }
}

class Eulerian_path_Main {
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

    private static List<List<Integer>> graph(int n, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) adj.get(e[0]).add(e[1]);   // directed, duplicates allowed
        return adj;
    }

    private static int edgeCount(List<List<Integer>> adj) {
        int e = 0; for (List<Integer> row : adj) e += row.size(); return e;
    }

    // A returned path is a valid Eulerian trail iff it uses every edge exactly once
    // and each consecutive pair is a real edge.
    private static boolean validEuler(List<List<Integer>> adj, int[] path) {
        int E = edgeCount(adj);
        if (E == 0) return path.length == 0;
        if (path.length != E + 1) return false;
        // multiset of remaining edges
        List<List<Integer>> remaining = new ArrayList<>();
        for (List<Integer> row : adj) remaining.add(new ArrayList<>(row));
        for (int i = 0; i + 1 < path.length; i++) {
            int u = path[i], v = path[i + 1];
            if (u < 0 || u >= adj.size()) return false;
            if (!remaining.get(u).remove((Integer) v)) return false;   // edge must exist & be unused
        }
        for (List<Integer> row : remaining) if (!row.isEmpty()) return false;  // all edges consumed
        return true;
    }

    public static void main(String[] args) {
        // Circuit: 0->1->2->0
        List<List<Integer>> circ = graph(3, new int[][]{{0,1},{1,2},{2,0}});
        int[] p1 = Eulerian_path.eulerianPath(circ);
        checkTrue("circuit valid", validEuler(circ, p1));
        checkTrue("circuit closed (start==end)", p1.length > 0 && p1[0] == p1[p1.length - 1]);

        // Forced-start path: 0->1->2->3  (0 is the unique start, 3 the unique end)
        List<List<Integer>> path = graph(4, new int[][]{{0,1},{1,2},{2,3}});
        checkEquals("forced path exact", Arrays.toString(new int[]{0,1,2,3}),
                Arrays.toString(Eulerian_path.eulerianPath(path)));

        // Multi-edges: 0->1, 1->0, 0->1, 1->0  (balanced, circuit, uses all 4)
        List<List<Integer>> multi = graph(2, new int[][]{{0,1},{1,0},{0,1},{1,0}});
        checkTrue("multi-edge circuit valid", validEuler(multi, Eulerian_path.eulerianPath(multi)));

        // Branching Eulerian path: 1->2,1->3,3->1,2->2 ... use a known-good one:
        // 0->1,1->2,2->0,0->3,3->4,4->0  (each vertex balanced -> circuit through all 6 edges)
        List<List<Integer>> br = graph(5, new int[][]{{0,1},{1,2},{2,0},{0,3},{3,4},{4,0}});
        checkTrue("branching circuit valid", validEuler(br, Eulerian_path.eulerianPath(br)));

        // No path: two disconnected circuits 0<->1 and 2<->3 (locally balanced, but disconnected)
        List<List<Integer>> disc = graph(4, new int[][]{{0,1},{1,0},{2,3},{3,2}});
        checkEquals("disconnected -> none", Arrays.toString(new int[]{}),
                Arrays.toString(Eulerian_path.eulerianPath(disc)));

        // No path: degree imbalance > 1  (0 has out 2, in 0)
        List<List<Integer>> imbal = graph(3, new int[][]{{0,1},{0,2}});
        checkEquals("imbalanced -> none", Arrays.toString(new int[]{}),
                Arrays.toString(Eulerian_path.eulerianPath(imbal)));

        // No edges -> empty
        checkEquals("no edges -> empty", Arrays.toString(new int[]{}),
                Arrays.toString(Eulerian_path.eulerianPath(graph(3, new int[][]{}))));

        // Single edge
        checkEquals("single edge", Arrays.toString(new int[]{0,1}),
                Arrays.toString(Eulerian_path.eulerianPath(graph(2, new int[][]{{0,1}}))));

        // --- Errors ---
        checkThrows("null adj", NullPointerException.class, () -> Eulerian_path.eulerianPath(null));
        checkThrows("null row", NullPointerException.class, () -> {
            List<List<Integer>> a = new ArrayList<>(); a.add(new ArrayList<>()); a.add(null);
            Eulerian_path.eulerianPath(a);
        });

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}