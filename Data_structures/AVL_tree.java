package Data_structures;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;

// AVL tree: a self-balancing BST. Each node stores its height and balance
// factor (BF = height(right) - height(left)); every insert/remove rebalances
// via rotations so |BF| <= 1 at every node. No duplicates.
public class AVL_tree<E extends Comparable<E>> {
    private static class Node<E> {
        E value;
        Node<E> left;
        Node<E> right;
        int height;   // edges to furthest leaf; leaf = 0
        int bf;       // height(right) - height(left)
        Node(E value) { this.value = value; }
    }

    private Node<E> root;
    private int size;


    public AVL_tree(){
        this.root = null;
        this.size = 0;
    }

    // --- helpers (private) ---

    // Height of a subtree; null = -1, leaf = 0.
    private int height(Node<E> node) {
        if(node == null){
            return -1;
        } 
        int left = node.left == null? -1: node.left.height;
        int right = node.right == null? -1: node.right.height;
        node.height = 1 + Math.max(left, right);
        return node.height;
    }

    // Recompute node.height and node.bf from its children.
    private void update(Node<E> node) {
        int left = node.left == null? -1: node.left.height;
        int right = node.right == null? -1: node.right.height;
        node.height = 1 + Math.max(left, right);
        node.bf = this.height(node.right) - this.height(node.left);
    }

    // Left rotation around node; return the new subtree root.
    private Node<E> rotateLeft(Node<E> node) {
        Node<E> A = node;
        Node<E> B = A.right;
        A.right = B.left;
        B.left = A;
        this.update(A);
        this.update(B);
        return B;
    }

    // Right rotation around node; return the new subtree root.
    private Node<E> rotateRight(Node<E> node) {
        Node<E> A = node;
        Node<E> B = A.left;
        A.left = B.right;
        B.right = A;
        this.update(A);
        this.update(B);
        return B;
    }

    // Rebalance node if |bf| == 2 (four cases); return the new subtree root.
    private Node<E> balance(Node<E> node) {
        if (node.bf == -2) {
            if (node.left.bf <= 0) return rotateRight(node);
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        if (node.bf == 2) {
            if (node.right.bf >= 0) return rotateLeft(node);
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
        return node;
    }

    // --- public API ---

    // Insert value. Return true if inserted, false if already present.
    // Throw NullPointerException if value is null.
    public boolean insert(E value) {
        if (value == null) throw new NullPointerException();
        if (contains(value)) return false;
        root = insert(root, value);
        size++;
        return true;
    }

    private Node<E> insert(Node<E> node, E value){
        if(node == null) return new Node<E>(value);

        int compare = value.compareTo(node.value);
        
        if(compare < 0){
            node.left = insert(node.left, value);
        }else {
            node.right = insert(node.right, value);
        }

        
        update(node);
        return balance(node);
    }



    // Return true if value is in the tree.
    // Throw NullPointerException if value is null.
    public boolean contains(E value) {
        if(value == null){
            throw new NullPointerException();
        } if(this.size == 0){
            return false;
        }else{
            Node<E> root_copy = this.root;
            while(root_copy!= null){
                if(value.compareTo(root_copy.value)==0){
                    return true;
                } else if(value.compareTo(root_copy.value)>0){
                    root_copy = root_copy.right;
                } else{
                    root_copy = root_copy.left;
                }
            }
        }
        return false;
    }

    // Remove value. Return true if removed, false if not present.
    // Throw NullPointerException if value is null.
    public boolean remove(E value) {
        if (value == null) throw new NullPointerException();
        if (!contains(value)) return false;
        root = remove(root, value);
        size--;
        return true;
    }

    private Node<E> remove(Node<E> node, E value){
        if(node == null) return null;
        if(value.compareTo(node.value)<0){
            node.left = remove(node.left, value);
            this.update(node);
            return this.balance(node);
        } else if(value.compareTo(node.value)>0){
            node.right = remove(node.right, value);
            this.update(node);
            return this.balance(node);
        }else{
            if(node.left == null && node.right == null){
                return null;
            }else if(node.left != null && node.right == null){
                return node.left;
            }else if(node.left == null && node.right != null){
                return node.right;
            } else {
                E succ = right_smallest_child(node.right);
                node.value = succ;
                node.right = remove(node.right, succ);
                this.update(node);
                return this.balance(node);
            }
        }
    }

    public E right_smallest_child(Node<E> node){
        Node<E> root_copy = node;
        if(root_copy.left == null){
            return root_copy.value;
        }
        root_copy = root_copy.left;
        while(root_copy.left!= null){
            root_copy = root_copy.left;
        }
        return root_copy.value;
    }

    // Number of elements.
    public int size() {
        return this.size;
    }

    // True if empty.
    public boolean isEmpty() {
        return this.size == 0;
    }

    // Smallest value. Throw NoSuchElementException if empty.
    public E min() {
        if(this.root == null){
            throw new NoSuchElementException();
        }
        Node<E> root_copy = this.root;
        while(root_copy.left!= null){
            root_copy = root_copy.left;
        }
        return root_copy.value;
    }

    // Largest value. Throw NoSuchElementException if empty.
    public E max() {
        if(this.root == null){
            throw new NoSuchElementException();
        }
        Node<E> root_copy = this.root;
        while(root_copy.right!= null){
            root_copy = root_copy.right;
        }
        return root_copy.value;
    }

    // Height in edges: empty = -1, single node = 0.
    public int height() {
        if(this.size ==0){
            return -1;
        }
        return this.height(this.root);
    }

    // Values in sorted (in-order) order.
    public List<E> inorder() {
        Stack<Node<E>> stack = new Stack<Node<E>>();
        ArrayList<E> order = new ArrayList<>();
        Node<E> currnode  = this.root;
        while(currnode != null || !stack.isEmpty()){
            while(currnode!= null){
                stack.push(currnode);
                currnode = currnode.left;
            }
            currnode = stack.pop();
            order.add(currnode.value);
            currnode = currnode.right;
        }
        return order;
    }

    // Values in pre-order (node, left, right).
    public List<E> preorder() {
        Stack<Node<E>> stack = new Stack<Node<E>>();
        stack.push(this.root);
        ArrayList<E> order = new ArrayList<>();
        
        while(!stack.isEmpty()){
            Node<E> currnode  = stack.pop();
            order.add(currnode.value);
            if(currnode.right!= null){
                stack.push(currnode.right);
            }
            if(currnode.left!= null){
                stack.push(currnode.left);
            }
        }
        return order;
    }

    // Values in post-order (left, right, node).
    public List<E> postorder() {
        ArrayList<E> order = new ArrayList<>();
        this.postorder(this.root, order);
        return order;
    }

    public void postorder(Node<E> node, ArrayList<E> order) {
        if(node.left != null){
            this.postorder(node.left, order);
        }
        if(node.right != null){
            this.postorder(node.right, order);
        }
        order.add(node.value);
    }

    // Diagnostic: true iff the AVL invariant holds at every node —
    // |bf| <= 1, stored height == 1 + max(child heights) (null = -1),
    // and stored bf == height(right) - height(left). Empty tree is balanced.
    public boolean isBalanced() {
        if(root == null){
            return true;
        }
        boolean returnv = true;
        Stack<Node<E>> stack = new Stack<Node<E>>();
        Node<E> currnode  = this.root;
        while(currnode != null || !stack.isEmpty()){
            while(currnode!= null){
                stack.push(currnode);
                currnode = currnode.left;
            }
            currnode = stack.pop();
            if(currnode.bf < -1 || currnode.bf > 1){
                returnv = false;
            }
            currnode = currnode.right;
        }
        return returnv;
    }

    // "[v0, v1, ..., vn]" in in-order (sorted) order.
    @Override
    public String toString() {
        String output = "[";
        Stack<Node<E>> stack = new Stack<Node<E>>();
        Node<E> currnode  = this.root;
        while(currnode != null || !stack.isEmpty()){
            while(currnode!= null){
                stack.push(currnode);
                currnode = currnode.left;
            }
            currnode = stack.pop();
            output += currnode.value.toString() + ", ";
            currnode = currnode.right;
        }
        if(this.size > 0){
            output = output.substring(0, output.length()-2);
        }
        output += "]";
        return output;
    }
}

class AVL_tree_Main {
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

    private static <T extends Comparable<T>> boolean isSorted(List<T> xs) {
        for (int i = 1; i < xs.size(); i++)
            if (xs.get(i - 1).compareTo(xs.get(i)) >= 0) return false;
        return true;
    }

    // loose upper bound on AVL height for n nodes: ~1.4405 * log2(n+2)
    private static int maxAVLHeight(int n) {
        if (n <= 0) return -1;
        return (int) Math.floor(1.4405 * (Math.log(n + 2) / Math.log(2)));
    }

    public static void main(String[] args) {
        // --- Empty ---
        AVL_tree<Integer> t = new AVL_tree<>();
        checkEquals("empty size",     0,    t.size());
        checkTrue ("empty isEmpty",         t.isEmpty());
        checkEquals("empty height",  -1,    t.height());
        checkEquals("empty contains", false, t.contains(1));
        checkEquals("empty toString", "[]", t.toString());
        checkTrue ("empty balanced",        t.isBalanced());
        checkThrows("empty min", NoSuchElementException.class, t::min);
        checkThrows("empty max", NoSuchElementException.class, t::max);

        // --- Basic inserts + order ---
        for (int v : new int[]{5, 3, 8, 1, 4, 7, 9}) checkTrue("insert " + v, t.insert(v));
        checkEquals("insert dup", false, t.insert(5));
        checkEquals("size", 7, t.size());
        checkEquals("inorder sorted", Arrays.asList(1,3,4,5,7,8,9), t.inorder());
        checkEquals("min", 1, t.min());
        checkEquals("max", 9, t.max());
        checkTrue ("balanced after inserts", t.isBalanced());
        checkTrue ("contains 7", t.contains(7));
        checkEquals("contains 6", false, t.contains(6));

        // --- Worst case for a plain BST: sorted inserts must NOT chain ---
        AVL_tree<Integer> seq = new AVL_tree<>();
        final int N = 1000;
        for (int i = 1; i <= N; i++) seq.insert(i);
        checkEquals("sorted-insert size", N, seq.size());
        checkTrue ("sorted-insert sorted", isSorted(seq.inorder()));
        checkTrue ("sorted-insert balanced", seq.isBalanced());
        checkTrue ("sorted-insert height bounded (" + seq.height() + " <= " + maxAVLHeight(N) + ")",
                seq.height() <= maxAVLHeight(N));

        // --- Rotation-triggering shapes ---
        // left-left
        AVL_tree<Integer> ll = new AVL_tree<>();
        for (int v : new int[]{3,2,1}) ll.insert(v);
        checkTrue("LL balanced", ll.isBalanced());
        checkEquals("LL inorder", Arrays.asList(1,2,3), ll.inorder());
        checkEquals("LL height", 1, ll.height());
        // left-right
        AVL_tree<Integer> lr = new AVL_tree<>();
        for (int v : new int[]{3,1,2}) lr.insert(v);
        checkTrue("LR balanced", lr.isBalanced());
        checkEquals("LR inorder", Arrays.asList(1,2,3), lr.inorder());
        // right-right
        AVL_tree<Integer> rr = new AVL_tree<>();
        for (int v : new int[]{1,2,3}) rr.insert(v);
        checkTrue("RR balanced", rr.isBalanced());
        // right-left
        AVL_tree<Integer> rl = new AVL_tree<>();
        for (int v : new int[]{1,3,2}) rl.insert(v);
        checkTrue("RL balanced", rl.isBalanced());

        // --- Deletion keeps balance + order ---
        AVL_tree<Integer> d = new AVL_tree<>();
        for (int i = 1; i <= 50; i++) d.insert(i);
        checkTrue("pre-delete balanced", d.isBalanced());
        // remove a spread of values (leaf, one-child, two-child cases)
        for (int v : new int[]{25, 1, 50, 30, 7, 42, 13}) checkTrue("remove " + v, d.remove(v));
        checkEquals("remove absent", false, d.remove(25));
        checkTrue ("post-delete sorted", isSorted(d.inorder()));
        checkTrue ("post-delete balanced", d.isBalanced());
        checkEquals("post-delete size", 43, d.size());

        // --- Delete down toward empty, checking invariant throughout ---
        AVL_tree<Integer> shrink = new AVL_tree<>();
        for (int i = 0; i < 100; i++) shrink.insert(i);
        boolean stayedBalanced = true, stayedSorted = true;
        for (int i = 0; i < 100; i++) {
            shrink.remove(i);
            if (!shrink.isBalanced()) { stayedBalanced = false; break; }
            if (!isSorted(shrink.inorder())) { stayedSorted = false; break; }
        }
        checkTrue("balanced through full teardown", stayedBalanced);
        checkTrue("sorted through full teardown", stayedSorted);
        checkTrue("empty after teardown", shrink.isEmpty());

        // --- Null safety ---
        final AVL_tree<Integer> tn = new AVL_tree<>();
        checkThrows("insert null", NullPointerException.class, () -> tn.insert(null));
        checkThrows("contains null", NullPointerException.class, () -> tn.contains(null));
        checkThrows("remove null", NullPointerException.class, () -> tn.remove(null));

        // --- Generic over String ---
        AVL_tree<String> ts = new AVL_tree<>();
        for (String s : new String[]{"dog","cat","bird","fish","ant","elk","cow"}) ts.insert(s);
        checkEquals("string inorder",
                Arrays.asList("ant","bird","cat","cow","dog","elk","fish"), ts.inorder());
        checkTrue ("string balanced", ts.isBalanced());
        checkEquals("string min", "ant", ts.min());
        checkEquals("string max", "fish", ts.max());

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}