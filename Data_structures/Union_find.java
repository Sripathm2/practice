package Data_structures;

import java.util.Objects;

public class Union_find {

    private int[] parent;
    private int[] rank;
    private int count;     // number of disjoint components
    private int n;         // total elements

    // Construct a Union_find with n elements (ids 0..n-1), each in its own component.
    public Union_find(int n) {
        this.n = n;
        this.parent = new int[n];
        this.rank = new int[n];
        this.count = n;
        for(int index = 0; index < n; index++){
            this.parent[index] = index;
            this.rank[index] = 0;
        }
    }

    // Return the total number of elements.
    public int size() {
        return this.n;
    }

    // Return the number of disjoint components.
    public int components() {
        return this.count;
    }

    // Return the representative (root) of x's component, applying path compression.
    // Throw IndexOutOfBoundsException if x is not in [0, n).
    public int find(int x) {
        if(x < 0 || x >= n){
            throw new IndexOutOfBoundsException();
        }
        int node_value = this.parent[x];
        while(node_value != this.parent[node_value]){
            node_value = this.parent[node_value];
        }
        return node_value;
    }

    // Merge the components containing x and y using union by rank.
    // Return true if a merge happened, false if they were already in the same component.
    public boolean union(int x, int y) {
        if(this.connected(x, y)){
            return false;
        }else{
            int root_x = this.find(x);
            int root_y = this.find(y);
            
            if(this.rank[root_x] < this.rank[root_y]){
                this.parent[root_x] = root_y;
            }else if(this.rank[root_x] > this.rank[root_y]){
                this.parent[root_y] = root_x;
            }else{
                this.parent[root_y] = root_x;
                this.rank[root_x] += 1;
            }
            this.count -= 1;
            return true;
        }
    }

    // Return true if x and y are in the same component.
    public boolean connected(int x, int y) {
        return this.find(x) == this.find(y);
    }

    // Return the size (number of elements) of the component containing x.
    // Note: this is O(n) unless you maintain a componentSize[] array. Keep it simple for now.
    public int componentSize(int x) {
        int root_value = this.find(x);
        int counter = 0;
        for(int index = 0; index < this.n; index++){
            if(root_value == this.find(index)){
                counter += 1;
            }
        }
        return counter;
    }

    // Return "[root_of_0, root_of_1, ..., root_of_n-1]" after fully resolving each element's root.
    // Useful for tests to verify component membership regardless of internal tree shape.
    @Override
    public String toString() {
        String output = "[";
        for(int index = 0; index < n; index++){
            output += this.find(index);
            output += ", ";
        }
        if(n > 0){
            output = output.substring(0, output.length()-2);
        }
        output += "]";
        return output;
    }
}

class Union_find_Main {
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
        // --- Construction ---
        Union_find uf = new Union_find(5);
        checkEquals("size",          5, uf.size());
        checkEquals("components new", 5, uf.components());
        for (int i = 0; i < 5; i++) {
            checkEquals("self is own root: " + i, i, uf.find(i));
        }
        for (int i = 0; i < 5; i++) {
            checkEquals("componentSize=1: " + i, 1, uf.componentSize(i));
        }

        // --- Basic union ---
        checkEquals("union(0,1) new",          true,  uf.union(0, 1));
        checkEquals("components after 1 union", 4,    uf.components());
        checkEquals("connected(0,1)",          true,  uf.connected(0, 1));
        checkEquals("connected(0,2)",          false, uf.connected(0, 2));
        checkEquals("find(0)==find(1)", uf.find(0), uf.find(1));
        checkEquals("componentSize after merge", 2,   uf.componentSize(0));
        checkEquals("componentSize symmetric",   2,   uf.componentSize(1));

        // --- Redundant union returns false ---
        checkEquals("union(1,0) redundant",        false, uf.union(1, 0));
        checkEquals("components unchanged",        4,     uf.components());

        // --- Chained unions ---
        uf.union(2, 3);
        uf.union(3, 4);
        checkEquals("components after chain", 2,    uf.components());
        checkEquals("connected(2,4) chain",   true, uf.connected(2, 4));
        checkEquals("connected(0,4)",         false, uf.connected(0, 4));
        checkEquals("componentSize 2..4",     3,    uf.componentSize(2));
        checkEquals("componentSize 0..1",     2,    uf.componentSize(0));

        // --- Merge two components ---
        uf.union(1, 4);
        checkEquals("components all merged", 1,    uf.components());
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                checkTrue("all connected (" + i + "," + j + ")", uf.connected(i, j));
            }
        }
        checkEquals("componentSize full", 5, uf.componentSize(0));

        // --- Bounds ---
        final Union_find uf2 = uf;
        checkThrows("find(-1)",  IndexOutOfBoundsException.class, () -> uf2.find(-1));
        checkThrows("find(5)",   IndexOutOfBoundsException.class, () -> uf2.find(5));
        checkThrows("union(-1,0)", IndexOutOfBoundsException.class, () -> uf2.union(-1, 0));
        checkThrows("union(0,5)",  IndexOutOfBoundsException.class, () -> uf2.union(0, 5));

        // --- Edge: n = 1 ---
        Union_find one = new Union_find(1);
        checkEquals("n=1 components",      1,    one.components());
        checkEquals("n=1 self-find",       0,    one.find(0));
        checkEquals("n=1 union self",      false, one.union(0, 0));
        checkEquals("n=1 connected self",  true, one.connected(0, 0));

        // --- Edge: n = 0 ---
        Union_find zero = new Union_find(0);
        checkEquals("n=0 size",        0, zero.size());
        checkEquals("n=0 components",  0, zero.components());

        // --- Union by rank: trees stay shallow ---
        // After unioning 0..15 in a chain, find should still be fast and all roots equal.
        Union_find tall = new Union_find(16);
        for (int i = 0; i < 15; i++) tall.union(i, i + 1);
        checkEquals("chain components", 1, tall.components());
        int root = tall.find(0);
        for (int i = 0; i < 16; i++) {
            checkEquals("chain find=" + i, root, tall.find(i));
        }

        // --- Path compression: after find, repeated finds are O(1) ---
        // We can't time directly, but we can verify that every element points directly at root
        // after enough finds. Run find on every element, then check parent[i] == root for all i.
        // (This relies on the toString contract — every entry should equal the same root.)
        String reprs = tall.toString();
        String expected = "[";
        for (int i = 0; i < 16; i++) expected += (i == 0 ? "" : ", ") + root;
        expected += "]";
        checkEquals("toString all point to root", expected, reprs);

        // --- Connectivity counting (classic LeetCode pattern) ---
        // Edges: (0,1), (1,2), (3,4) on n=6 → 3 components: {0,1,2}, {3,4}, {5}
        Union_find conn = new Union_find(6);
        conn.union(0, 1);
        conn.union(1, 2);
        conn.union(3, 4);
        checkEquals("3 components",         3,    conn.components());
        checkEquals("0-2 connected",        true, conn.connected(0, 2));
        checkEquals("3-4 connected",        true, conn.connected(3, 4));
        checkEquals("2-3 not connected",    false, conn.connected(2, 3));
        checkEquals("5 alone",              false, conn.connected(0, 5));
        checkEquals("size of {0,1,2}",      3,    conn.componentSize(0));
        checkEquals("size of {3,4}",        2,    conn.componentSize(3));
        checkEquals("size of {5}",          1,    conn.componentSize(5));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}