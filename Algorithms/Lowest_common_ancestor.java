package Algorithms;
import java.util.Objects;

import Data_structures.Sparse_table;
import Data_structures.Tree_node;
import java.util.List;
import java.util.ArrayList;

import java.util.Hashtable;

// Lowest Common Ancestor in a rooted tree. Build once from an undirected tree and
// a chosen root (precomputing each node's parent and depth), then answer LCA
// queries by lifting the deeper node to the shallower one's depth and walking both
// up in lockstep until they meet.
public class Lowest_common_ancestor {
    private ArrayList<Tree_node> nodes;   // parent[v], -1 for the root
    private ArrayList<Integer> depth;    // depth[v], 0 for the root
    private int n;
    private Hashtable<Integer,Integer> last;
    private Sparse_table<Integer> sparse;

    // Preprocess the undirected tree `adj` rooted at `root`.
    // Throw NullPointerException if adj is null.
    // Throw IllegalArgumentException if adj is empty.
    // Throw IndexOutOfBoundsException if root is not in [0, adj.size()).
    public Lowest_common_ancestor(List<List<Integer>> adj, int root) {
        if(adj == null){
            throw new NullPointerException();
        }
        if(adj.size() == 0){
            throw new IllegalArgumentException();
        } 
        if(root <0 || root >=adj.size()){
            throw new IndexOutOfBoundsException();
        }

        Tree_node rootnode = Root_tree.rootTree(adj, root);

        nodes = new ArrayList<Tree_node>();
        depth = new ArrayList<Integer>();
        last = new Hashtable<Integer,Integer>();
        n = adj.size();

        dfs(rootnode, root);

        sparse = new Sparse_table<Integer>(depth.toArray(new Integer[0]), Integer::min);
    }

    public void dfs(Tree_node node, int node_depth){
        if(node == null){
            return;
        }
        visit(node, node_depth);
        for(Tree_node child: node.children){
            dfs(child, node_depth+1);
            visit(node, node_depth);
        }
    }


    public void visit(Tree_node node, int node_depth){
        nodes.add(node);
        depth.add(node_depth);
        last.put(node.id, nodes.size()-1);
    }


    // Return the lowest common ancestor of u and v.
    // Throw IndexOutOfBoundsException if u or v is not in [0, n).
    public int lca(int u, int v) {
        if(u < 0 || v <0  || u >= n || v >= n){
            throw new IndexOutOfBoundsException();
        }
        int l = Math.min(last.get(u), last.get(v));
        int r = Math.max(last.get(u), last.get(v));
        int i = sparse.queryOverlap_index_min_operation_only(l, r);
        return nodes.get(i).id;
    }
}

class Lowest_common_ancestor_Main {
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

    private static List<List<Integer>> tree(int n, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) { adj.get(e[0]).add(e[1]); adj.get(e[1]).add(e[0]); }
        return adj;
    }

    public static void main(String[] args) {
        //         0
        //       /   \
        //      1     2
        //     / \     \
        //    3   4     5
        //         \
        //          6
        List<List<Integer>> t = tree(7, new int[][]{{0,1},{0,2},{1,3},{1,4},{2,5},{4,6}});
        Lowest_common_ancestor lca = new Lowest_common_ancestor(t, 0);

        checkEquals("lca(3,4)=1", 1, lca.lca(3, 4));
        checkEquals("lca(3,6)=1", 1, lca.lca(3, 6));
        checkEquals("lca(6,5)=0", 0, lca.lca(6, 5));
        checkEquals("lca(4,6)=4 (ancestor)", 4, lca.lca(4, 6));
        checkEquals("lca(3,5)=0", 0, lca.lca(3, 5));
        checkEquals("lca(2,5)=2", 2, lca.lca(2, 5));
        checkEquals("lca(6,6)=6 (self)", 6, lca.lca(6, 6));
        checkEquals("lca(0,6)=0 (root)", 0, lca.lca(0, 6));

        // Rooting elsewhere changes ancestry: root at 6
        Lowest_common_ancestor lca6 = new Lowest_common_ancestor(t, 6);
        checkEquals("root6 lca(3,5)=1", 1, lca6.lca(3, 5));
        checkEquals("root6 lca(0,6)=6 (6 is the root)", 6, lca6.lca(0, 6));

        // --- Errors ---
        checkThrows("null adj", NullPointerException.class,
                () -> new Lowest_common_ancestor(null, 0));
        checkThrows("empty adj", IllegalArgumentException.class,
                () -> new Lowest_common_ancestor(new ArrayList<>(), 0));
        checkThrows("root oob", IndexOutOfBoundsException.class,
                () -> new Lowest_common_ancestor(t, 7));
        final Lowest_common_ancestor l = lca;
        checkThrows("query oob", IndexOutOfBoundsException.class, () -> l.lca(0, 7));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}