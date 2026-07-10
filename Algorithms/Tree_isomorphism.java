package Algorithms;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import Algorithms.Tree_center;
import Data_structures.Tree_node;
import Algorithms.Root_tree;

// Tree isomorphism: are two undirected trees the same shape, ignoring labels?
// The AHU approach: root each tree at its center, then encode each rooted tree
// canonically (a leaf is "()"; an internal node is "(" + its children's encodings,
// sorted, concatenated + ")"). Two rooted trees are isomorphic iff their encodings
// match. For unrooted trees, root at the center — trying both centers on one side
// when a tree has two — and compare the encodings.
public class Tree_isomorphism {

    // Return true iff the two undirected trees are isomorphic.
    // adj.get(u) lists u's neighbors; both graphs are assumed to be trees.
    // Two empty trees (size 0) are isomorphic; trees of different sizes are not.
    // Throw NullPointerException if either adjacency list is null.
    public static boolean areIsomorphic(List<List<Integer>> a, List<List<Integer>> b) {
        if(a == null || b == null){
            throw new NullPointerException();
        }
        if(a.size() != b.size()){
            return false;
        }
        if(a.size() == 0 && b.size() == 0){
            return true;
        }

        int[] centera = Tree_center.center(a);
        Tree_node roota = Root_tree.rootTree(a, centera[0]);
        String encodea = encode(roota);

        int[] centerb = Tree_center.center(b);

        for(int i =0;i<centerb.length;i++){
            Tree_node rootb = Root_tree.rootTree(b, centerb[i]);
            String encodeb = encode(rootb);
            if(encodea.equals(encodeb)){
                return true;
            }
        }

        return false;
    }


    private static String encode(Tree_node node){
        if(node == null)
            return "";
        if(node.isLeaf())
            return "()";
        else{
            ArrayList<String> en = new ArrayList<String>();
            for(int j =0;j<node.children.size();j++){
                en.add(encode(node.children.get(j)));
            }
            en.sort(null);
            String result = String.join("", en);
            result = "(" + result + ")";
            return result;
        }
    }

}

class Tree_isomorphism_Main {
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
        // A and B are the same shape with relabeled vertices (verified via AHU)
        List<List<Integer>> A = tree(5, new int[][]{{0,1},{0,2},{1,3},{1,4}});
        List<List<Integer>> B = tree(5, new int[][]{{4,2},{4,0},{2,1},{2,3}});
        checkEquals("A ~ B isomorphic", true, Tree_isomorphism.areIsomorphic(A, B));

        // A vs a 5-node path: different shape
        List<List<Integer>> P = tree(5, new int[][]{{0,1},{1,2},{2,3},{3,4}});
        checkEquals("A not ~ path", false, Tree_isomorphism.areIsomorphic(A, P));

        // star K1,3 vs path of 4: not isomorphic
        List<List<Integer>> star = tree(4, new int[][]{{0,1},{0,2},{0,3}});
        List<List<Integer>> path4 = tree(4, new int[][]{{0,1},{1,2},{2,3}});
        checkEquals("star not ~ path4", false, Tree_isomorphism.areIsomorphic(star, path4));

        // Different sizes
        checkEquals("size mismatch", false, Tree_isomorphism.areIsomorphic(A, star));

        // Single nodes are isomorphic
        checkEquals("single ~ single", true,
                Tree_isomorphism.areIsomorphic(tree(1, new int[][]{}), tree(1, new int[][]{})));

        // Two empty trees
        checkEquals("empty ~ empty", true,
                Tree_isomorphism.areIsomorphic(new ArrayList<>(), new ArrayList<>()));

        // Same tree, symmetric case
        checkEquals("identical trees", true, Tree_isomorphism.areIsomorphic(A, tree(5, new int[][]{{0,1},{0,2},{1,3},{1,4}})));

        // --- Errors ---
        checkThrows("null a", NullPointerException.class,
                () -> Tree_isomorphism.areIsomorphic(null, star));
        checkThrows("null b", NullPointerException.class,
                () -> Tree_isomorphism.areIsomorphic(star, null));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}