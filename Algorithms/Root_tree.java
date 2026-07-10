package Algorithms;
import Data_structures.Tree_node;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import Data_structures.Stack;

// Rooting a tree: given an undirected tree as an adjacency list and a chosen root,
// build a rooted tree (parent/children links) by a DFS that walks away from the root.
public class Root_tree {

    // Root the undirected tree `adj` at vertex `root` and return its Tree_node.
    // adj.get(u) lists u's neighbors; the graph is assumed to be a tree.
    // Throw NullPointerException if adj is null.
    // Throw IllegalArgumentException if adj is empty.
    // Throw IndexOutOfBoundsException if root is not in [0, adj.size()).
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

class Root_tree_Main {
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

    // Canonical description of a rooted tree: one line per node,
    // "id(parentId)->[sorted child ids]", lines sorted by id.
    private static String describe(Tree_node root) {
        if (root == null) return "<null>";
        List<String> lines = new ArrayList<>();
        collect(root, lines);
        Collections.sort(lines);
        return String.join(" ", lines);
    }
    private static void collect(Tree_node node, List<String> lines) {
        List<Integer> kids = new ArrayList<>();
        for (Tree_node c : node.children) kids.add(c.id);
        Collections.sort(kids);
        int p = (node.parent == null) ? -1 : node.parent.id;
        lines.add(node.id + "(" + p + ")->" + kids);
        for (Tree_node c : node.children) collect(c, lines);
    }

    public static void main(String[] args) {
        // Undirected tree: 0-1, 0-2, 1-3, 2-4, 2-5
        List<List<Integer>> t = tree(6, new int[][]{{0,1},{0,2},{1,3},{2,4},{2,5}});

        Tree_node r0 = Root_tree.rootTree(t, 0);
        checkEquals("root id", 0, r0 == null ? null : r0.id);
        checkEquals("rooted at 0",
                "0(-1)->[1, 2] 1(0)->[3] 2(0)->[4, 5] 3(1)->[] 4(2)->[] 5(2)->[]",
                describe(r0));

        // Re-root the same tree at 3 -> different parent/child structure
        Tree_node r3 = Root_tree.rootTree(t, 3);
        checkEquals("rooted at 3",
                "0(1)->[2] 1(3)->[0] 2(0)->[4, 5] 3(-1)->[1] 4(2)->[] 5(2)->[]",
                describe(r3));

        // Single-node tree
        Tree_node one = Root_tree.rootTree(tree(1, new int[][]{}), 0);
        checkEquals("single node", "0(-1)->[]", describe(one));

        // --- Errors ---
        checkThrows("null adj", NullPointerException.class, () -> Root_tree.rootTree(null, 0));
        checkThrows("empty adj", IllegalArgumentException.class,
                () -> Root_tree.rootTree(new ArrayList<>(), 0));
        checkThrows("root oob", IndexOutOfBoundsException.class, () -> Root_tree.rootTree(t, 6));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}