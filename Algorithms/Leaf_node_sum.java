package Algorithms;
import Data_structures.Tree_node;
import java.util.Objects;

// Sum of leaf nodes: given a rooted tree, sum the ids of its leaves (nodes with no
// children). A recursive post-order walk: a leaf contributes its id; an internal
// node contributes the sum from its subtrees.
public class Leaf_node_sum {

    // Return the sum of the ids of all leaf nodes in the tree rooted at `root`.
    // A single-node tree's root is itself a leaf.
    // Throw NullPointerException if root is null.
    public static long leafSum(Tree_node root) {
        if(root == null){
            throw new NullPointerException();
        }
        return leafsum(root);
    }

    private static long leafsum(Tree_node node){
        if(node.isLeaf()){
            return node.id;
        }else{
            long sum = 0;
            for(int i=0;i<node.children.size();i++){
                sum += leafsum(node.children.get(i));
            }
            return sum;
        }
    }

}

class Leaf_node_sum_Main {
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

    // Build a rooted tree by hand: link child under parent.
    private static Tree_node node(int id, Tree_node parent) {
        Tree_node n = new Tree_node(id, parent);
        if (parent != null) parent.children.add(n);
        return n;
    }

    public static void main(String[] args) {
        //        0
        //      /   \
        //     1     2
        //    /     / \
        //   3     4   5     leaves: 3,4,5  -> 12
        Tree_node r = node(0, null);
        Tree_node a = node(1, r);
        Tree_node b = node(2, r);
        node(3, a);
        node(4, b);
        node(5, b);
        checkEquals("leaf sum 3+4+5", 12L, Leaf_node_sum.leafSum(r));

        // Single node is itself a leaf
        checkEquals("single node leaf", 7L, Leaf_node_sum.leafSum(new Tree_node(7, null)));

        // A path 0-10-20-30 : only the deepest node (30) is a leaf
        Tree_node p0 = node(0, null);
        Tree_node p1 = node(10, p0);
        Tree_node p2 = node(20, p1);
        node(30, p2);
        checkEquals("path leaf = deepest", 30L, Leaf_node_sum.leafSum(p0));

        // --- Error ---
        checkThrows("null root", NullPointerException.class, () -> Leaf_node_sum.leafSum(null));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}