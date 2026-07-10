package Algorithms;
import java.util.Objects;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

// Center of an undirected tree: the 1 or 2 vertices in the middle of the tree's
// longest path. Found by "peeling" — repeatedly strip away all current leaves,
// layer by layer, until only 1 or 2 vertices remain. Those are the center(s).
public class Tree_center {

    // Return the center vertex ids (exactly 1 or 2), in ascending order.
    // adj.get(u) lists u's neighbors; the graph is assumed to be a tree.
    // Throw NullPointerException if adj is null.
    // Throw IllegalArgumentException if adj is empty.
    public static int[] center(List<List<Integer>> adj) {
        if(adj == null){
            throw new NullPointerException();
        }
        if(adj.size() == 0){
            throw new IllegalArgumentException();
        }

        int[] connectedto = new int[adj.size()];
        ArrayList<Integer> leaves = new ArrayList<Integer>();
        for(int i = 0; i < adj.size(); i++){
            connectedto[i] = adj.get(i).size();
            if(connectedto[i] == 0 || connectedto[i] == 1){
                leaves.add(i);
            }
        }
        int n = leaves.size();
        boolean [] processed = new boolean[adj.size()];
        while(n < adj.size()){
            ArrayList<Integer> newleaves = new ArrayList<Integer>();
            for(int leaf: leaves){
                processed[leaf] = true;
                for(int neigh: adj.get(leaf)){
                    connectedto[neigh] -= 1;
                    if((connectedto[neigh] == 0 || connectedto[neigh] == 1) && !processed[neigh] && !newleaves.contains(neigh)){
                        newleaves.add(neigh);
                    }
                }
            }
            leaves = newleaves;

            n += leaves.size();
            
        }

        return leaves.stream()
                .mapToInt(Integer::intValue)
                .toArray();
    }
}

class Tree_center_Main {
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
        // odd path 0-1-2-3-4 -> single center 2
        checkEquals("path 5 -> [2]", Arrays.toString(new int[]{2}),
                Arrays.toString(Tree_center.center(tree(5, new int[][]{{0,1},{1,2},{2,3},{3,4}}))));

        // even path 0-1-2-3 -> two centers 1,2
        checkEquals("path 4 -> [1,2]", Arrays.toString(new int[]{1,2}),
                Arrays.toString(Tree_center.center(tree(4, new int[][]{{0,1},{1,2},{2,3}}))));

        // star: hub 0 -> center 0
        checkEquals("star -> [0]", Arrays.toString(new int[]{0}),
                Arrays.toString(Tree_center.center(tree(4, new int[][]{{0,1},{0,2},{0,3}}))));

        // branching tree 0-1,0-2,1-3,2-4,2-5 -> center 0
        checkEquals("branchy -> [0]", Arrays.toString(new int[]{0}),
                Arrays.toString(Tree_center.center(tree(6, new int[][]{{0,1},{0,2},{1,3},{2,4},{2,5}}))));

        // single node -> [0]
        checkEquals("single -> [0]", Arrays.toString(new int[]{0}),
                Arrays.toString(Tree_center.center(tree(1, new int[][]{}))));

        // two nodes -> both centers [0,1]
        checkEquals("two -> [0,1]", Arrays.toString(new int[]{0,1}),
                Arrays.toString(Tree_center.center(tree(2, new int[][]{{0,1}}))));

        // --- Errors ---
        checkThrows("null adj", NullPointerException.class, () -> Tree_center.center(null));
        checkThrows("empty adj", IllegalArgumentException.class,
                () -> Tree_center.center(new ArrayList<>()));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}