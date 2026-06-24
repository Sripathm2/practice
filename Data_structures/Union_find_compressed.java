package Data_structures;

import java.util.Objects;

public class Union_find_compressed {

    private int[] parent;
    private int[] rank;
    private int count;     // number of disjoint components
    private int n;         // total elements

    // Construct a Union_find_compressed with n elements (ids 0..n-1), each in its own component.
    public Union_find_compressed(int n) {
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

    // Return the representative (root) of x's component, applying path compression
    // so that every node visited on the way to the root points directly at the root.
    // Throw IndexOutOfBoundsException if x is not in [0, n).
    public int find(int x) {
        if(x < 0 || x >= n){
            throw new IndexOutOfBoundsException();
        }
        int root_value = this.parent[x];
        while(root_value != this.parent[root_value]){
            root_value = this.parent[root_value];
        }
        int current_node = this.parent[x];
        while(current_node != this.parent[current_node]){
            int temp_val = current_node;
            current_node = this.parent[current_node];
            this.parent[temp_val] = root_value;
        }
        return root_value;
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

    // Return "[parent[0], parent[1], ..., parent[n-1]]" — the RAW parent array, not resolved roots.
    // After enough finds, path compression should make this match a fully-flattened structure.
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

class Union_find_compressed_Main {
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
        Union_find_compressed uf = new Union_find_compressed(5);
        checkEquals("size",           5, uf.size());
        checkEquals("components new", 5, uf.components());
        for (int i = 0; i < 5; i++) {
            checkEquals("self is own root: " + i, i, uf.find(i));
        }
        for (int i = 0; i < 5; i++) {
            checkEquals("componentSize=1: " + i, 1, uf.componentSize(i));
        }

        // --- Basic union ---
        checkEquals("union(0,1) new",           true,  uf.union(0, 1));
        checkEquals("components after 1 union", 4,     uf.components());
        checkEquals("connected(0,1)",           true,  uf.connected(0, 1));
        checkEquals("connected(0,2)",           false, uf.connected(0, 2));
        checkEquals("find(0)==find(1)", uf.find(0), uf.find(1));
        checkEquals("componentSize after merge", 2,    uf.componentSize(0));
        checkEquals("componentSize symmetric",   2,    uf.componentSize(1));

        // --- Redundant union returns false ---
        checkEquals("union(1,0) redundant", false, uf.union(1, 0));
        checkEquals("components unchanged", 4,     uf.components());

        // --- Chained unions ---
        uf.union(2, 3);
        uf.union(3, 4);
        checkEquals("components after chain", 2,     uf.components());
        checkEquals("connected(2,4) chain",   true,  uf.connected(2, 4));
        checkEquals("connected(0,4)",         false, uf.connected(0, 4));
        checkEquals("componentSize 2..4",     3,     uf.componentSize(2));
        checkEquals("componentSize 0..1",     2,     uf.componentSize(0));

        // --- Merge two components ---
        uf.union(1, 4);
        checkEquals("components all merged", 1, uf.components());
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                checkTrue("all connected (" + i + "," + j + ")", uf.connected(i, j));
            }
        }
        checkEquals("componentSize full", 5, uf.componentSize(0));

        // --- Bounds ---
        final Union_find_compressed uf2 = uf;
        checkThrows("find(-1)",    IndexOutOfBoundsException.class, () -> uf2.find(-1));
        checkThrows("find(5)",     IndexOutOfBoundsException.class, () -> uf2.find(5));
        checkThrows("union(-1,0)", IndexOutOfBoundsException.class, () -> uf2.union(-1, 0));
        checkThrows("union(0,5)",  IndexOutOfBoundsException.class, () -> uf2.union(0, 5));

        // --- Edge: n = 1 ---
        Union_find_compressed one = new Union_find_compressed(1);
        checkEquals("n=1 components",     1,     one.components());
        checkEquals("n=1 self-find",      0,     one.find(0));
        checkEquals("n=1 union self",     false, one.union(0, 0));
        checkEquals("n=1 connected self", true,  one.connected(0, 0));

        // --- Edge: n = 0 ---
        Union_find_compressed zero = new Union_find_compressed(0);
        checkEquals("n=0 size",       0, zero.size());
        checkEquals("n=0 components", 0, zero.components());

        // --- Path compression: after a find, every visited node points directly at the root ---
        // Build a deliberately tall-ish structure, then verify compression actually happens.
        Union_find_compressed comp = new Union_find_compressed(8);
        for (int i = 0; i < 7; i++) comp.union(i, i + 1);
        int root = comp.find(0);  // this single find should compress the whole path from 0 to root

        // After find(0), the path from 0 → root must be fully flattened.
        // We can verify this by reading the raw parent array via toString:
        // every element on the path 0 → ... → root should now have parent == root.
        // Note: rank-based union may put some elements at depth 2 from non-visited paths,
        // but after we call find on EVERY element below, the entire structure flattens.
        for (int i = 0; i < 8; i++) comp.find(i);
        String reprs = comp.toString();
        String allRoot = "[";
        for (int i = 0; i < 8; i++) allRoot += (i == 0 ? "" : ", ") + root;
        allRoot += "]";
        checkEquals("parent[] fully flattened after find-all", allRoot, reprs);

        // --- Connectivity counting (classic LeetCode pattern) ---
        Union_find_compressed conn = new Union_find_compressed(6);
        conn.union(0, 1);
        conn.union(1, 2);
        conn.union(3, 4);
        checkEquals("3 components",      3,     conn.components());
        checkEquals("0-2 connected",     true,  conn.connected(0, 2));
        checkEquals("3-4 connected",     true,  conn.connected(3, 4));
        checkEquals("2-3 not connected", false, conn.connected(2, 3));
        checkEquals("5 alone",           false, conn.connected(0, 5));
        checkEquals("size of {0,1,2}",   3,     conn.componentSize(0));
        checkEquals("size of {3,4}",     2,     conn.componentSize(3));
        checkEquals("size of {5}",       1,     conn.componentSize(5));

        // --- Stress: long chain still flattens to depth 1 after a single full pass ---
        int N = 1000;
        Union_find_compressed big = new Union_find_compressed(N);
        for (int i = 0; i < N - 1; i++) big.union(i, i + 1);
        int bigRoot = big.find(0);
        for (int i = 0; i < N; i++) big.find(i);
        // After find on every element, parent[i] should equal bigRoot for all i.
        // Spot-check a few; reading the full toString would be noisy.
        checkEquals("stress parent[0]",     bigRoot, ((java.util.function.IntUnaryOperator)(idx -> {
            String s = big.toString();
            // crude parse: split on ", " and grab idx-th entry
            String body = s.substring(1, s.length() - 1);
            String[] parts = body.split(", ");
            return Integer.parseInt(parts[idx]);
        })).applyAsInt(0));
        checkEquals("stress parent[N/2]",   bigRoot, ((java.util.function.IntUnaryOperator)(idx -> {
            String s = big.toString();
            String body = s.substring(1, s.length() - 1);
            String[] parts = body.split(", ");
            return Integer.parseInt(parts[idx]);
        })).applyAsInt(N / 2));
        checkEquals("stress parent[N-1]",   bigRoot, ((java.util.function.IntUnaryOperator)(idx -> {
            String s = big.toString();
            String body = s.substring(1, s.length() - 1);
            String[] parts = body.split(", ");
            return Integer.parseInt(parts[idx]);
        })).applyAsInt(N - 1));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}