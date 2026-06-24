package Data_structures;

import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Binary_search_tree<E extends Comparable<E>> {
    private static class Node<E> {
        E value;
        Node<E> left;
        Node<E> right;
        Node(E value) { this.value = value; }
    }

    private Node<E> root;
    private int size;

    public Binary_search_tree(){
        this.root = null;
        this.size = 0;
    }

    // Insert value. Return true if inserted, false if value already present (no duplicates).
    // Throw NullPointerException if value is null.
    public boolean insert(E value) {
        if(value == null){
            throw new NullPointerException();
        } if( this.root == null){
            this.root = new Node<E>(value);
        } else {
            Node<E> root_copy = this.root;
            while(root_copy.left != null || root_copy.right != null){
                if(value.compareTo(root_copy.value)==0){
                    return false;
                } else if(root_copy.right != null && value.compareTo(root_copy.value)>0){
                    root_copy = root_copy.right;
                } else if(root_copy.left != null && value.compareTo(root_copy.value)<0){
                    root_copy = root_copy.left;
                } else{
                    break;
                }
            }
            if(value.compareTo(root_copy.value)>0){
                root_copy.right = new Node<E>(value);
            }else{
                root_copy.left = new Node<E>(value);
            }
        }
        this.size += 1;
        return true;
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
            return node;
        } else if(value.compareTo(node.value)>0){
            node.right = remove(node.right, value);
            return node;
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
                return node;
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


    // Return number of elements.
    public int size() {
        return this.size;
    }

    // Return true if empty.
    public boolean isEmpty() {
        return this.size == 0;
    }

    // Return smallest value. Throw NoSuchElementException if empty.
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

    // Return largest value. Throw NoSuchElementException if empty.
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

    // Return height: edges on longest root-to-leaf path. Empty tree = -1, single node = 0.
    public int height() {
        if(this.size ==0){
            return -1;
        }
        return this.height(this.root);
    }

    public int height(Node<E> node){
        if(node.left == null && node.right == null){
            return 0;
        }else if(node.left != null && node.right == null){
            return 1+this.height(node.left);
        }else if(node.left == null && node.right != null){
            return 1+this.height(node.right);
        }else{
            return 1+Math.max(this.height(node.left), this.height(node.right));
        }
    }

    // Return values in sorted (in-order) order as a List.
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

        // Return values in pre-order (root, left, right) as a List.
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

    // Return values in post-order (left, right, root) as a List.
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

    // Return values in post-order (left, right, root) as a List.
    public List<E> levelOrder() {
        Queue<Node<E>> queue = new Queue<Node<E>>();
        ArrayList<E> order = new ArrayList<>();
        queue.enqueue(this.root);
        while(!queue.isEmpty()){
            Node<E> currnode  = queue.dequeue();
            order.add(currnode.value);
            if(currnode.left!=null) queue.enqueue(currnode.left);
            if(currnode.right!=null) queue.enqueue(currnode.right);
        }
        return order;
    }


    // Return "[v0, v1, ..., vn]" in in-order (sorted) order.
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

class Binary_search_tree_Main {
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
        // --- Empty tree ---
        Binary_search_tree<Integer> t = new Binary_search_tree<>();
        checkEquals("empty size",     0,    t.size());
        checkTrue ("empty isEmpty",         t.isEmpty());
        checkEquals("empty height",  -1,    t.height());
        checkEquals("empty contains", false, t.contains(1));
        checkEquals("empty toString", "[]", t.toString());
        checkThrows("empty min", java.util.NoSuchElementException.class, t::min);
        checkThrows("empty max", java.util.NoSuchElementException.class, t::max);

        // --- Insert ---
        checkEquals("insert 5",  true,  t.insert(5));
        checkEquals("insert 3",  true,  t.insert(3));
        checkEquals("insert 8",  true,  t.insert(8));
        checkEquals("insert 1",  true,  t.insert(1));
        checkEquals("insert 4",  true,  t.insert(4));
        checkEquals("insert 7",  true,  t.insert(7));
        checkEquals("insert 9",  true,  t.insert(9));
        checkEquals("insert dup 5", false, t.insert(5));
        checkEquals("size after inserts", 7, t.size());
        checkTrue ("not empty", !t.isEmpty());

        // tree shape:
        //        5
        //      /   \
        //     3     8
        //    / \   / \
        //   1   4 7   9

        // --- Order / structure ---
        checkEquals("inorder sorted", java.util.Arrays.asList(1,3,4,5,7,8,9), t.inorder());
        checkEquals("preorder", java.util.Arrays.asList(5,3,1,4,8,7,9), t.preorder());
        checkEquals("postorder", java.util.Arrays.asList(1,4,3,7,9,8,5), t.postorder());
        checkEquals("levelorder", java.util.Arrays.asList(5,3,8,1,4,7,9), t.levelOrder());
        checkEquals("toString sorted", "[1, 3, 4, 5, 7, 8, 9]", t.toString());
        checkEquals("min", 1, t.min());
        checkEquals("max", 9, t.max());
        checkEquals("height", 2, t.height());

        // --- Contains ---
        checkTrue ("contains 7",  t.contains(7));
        checkTrue ("contains 1",  t.contains(1));
        checkEquals("contains 6", false, t.contains(6));
        checkEquals("contains 10", false, t.contains(10));

        // --- Remove leaf ---
        checkEquals("remove leaf 1", true, t.remove(1));
        checkEquals("after leaf remove", java.util.Arrays.asList(3,4,5,7,8,9), t.inorder());
        checkEquals("size after leaf remove", 6, t.size());
        checkEquals("remove absent", false, t.remove(1));

        // --- Remove node with one child (8 has 7 and 9; first remove 7 to make 8 have one child) ---
        checkEquals("remove leaf 7", true, t.remove(7));
        checkEquals("after remove 7", java.util.Arrays.asList(3,4,5,8,9), t.inorder());
        checkEquals("remove one-child 8", true, t.remove(8));
        checkEquals("after remove 8", java.util.Arrays.asList(3,4,5,9), t.inorder());

        // --- Remove node with two children (3 has children 4 ... and root 5) ---
        Binary_search_tree<Integer> t2 = new Binary_search_tree<>();
        for (int v : new int[]{5,3,8,1,4,7,9,2}) t2.insert(v);
        // remove 3 (two children: 1-subtree and 4)
        checkEquals("remove two-child 3", true, t2.remove(3));
        checkEquals("after remove two-child", java.util.Arrays.asList(1,2,4,5,7,8,9), t2.inorder());
        checkEquals("size after two-child remove", 7, t2.size());
        checkTrue ("BST still valid sorted",
                isSorted(t2.inorder()));

        // --- Remove root ---
        checkEquals("remove root 5", true, t2.remove(5));
        checkEquals("after remove root", java.util.Arrays.asList(1,2,4,7,8,9), t2.inorder());
        checkTrue ("sorted after root remove", isSorted(t2.inorder()));

        // --- Remove down to empty ---
        Binary_search_tree<Integer> t3 = new Binary_search_tree<>();
        t3.insert(10);
        checkEquals("single height", 0, t3.height());
        checkEquals("remove only", true, t3.remove(10));
        checkTrue ("empty after remove", t3.isEmpty());
        checkEquals("empty toString again", "[]", t3.toString());

        // --- Null safety ---
        final Binary_search_tree<Integer> tn = new Binary_search_tree<>();
        checkThrows("insert null", NullPointerException.class, () -> tn.insert(null));
        checkThrows("contains null", NullPointerException.class, () -> tn.contains(null));
        checkThrows("remove null", NullPointerException.class, () -> tn.remove(null));

        // --- Strings (generic check) ---
        Binary_search_tree<String> ts = new Binary_search_tree<>();
        for (String s : new String[]{"dog","cat","bird","fish","ant"}) ts.insert(s);
        checkEquals("string inorder",
                java.util.Arrays.asList("ant","bird","cat","dog","fish"), ts.inorder());
        checkEquals("string min", "ant", ts.min());
        checkEquals("string max", "fish", ts.max());

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }

    private static <T extends Comparable<T>> boolean isSorted(List<T> xs) {
        for (int i = 1; i < xs.size(); i++) {
            if (xs.get(i - 1).compareTo(xs.get(i)) >= 0) return false;
        }
        return true;
    }
}