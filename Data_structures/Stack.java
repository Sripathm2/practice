package Data_structures;

import java.util.NoSuchElementException;
import java.util.Objects;

public class Stack<E> {

    private Doubly_linked_list<E> list;

    // Construct an empty stack.
    public Stack() {
        this.list = new Doubly_linked_list<E>();
    }

    // Return the number of elements in the stack.
    public int size() {
        return this.list.size();
    }

    // Return true if the stack has no elements.
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    // Push value onto the top of the stack. O(1).
    public void push(E value) {
        this.list.addFirst(value);
    }

    // Remove and return the top value. O(1). Throw NoSuchElementException if empty.
    public E pop() {
        if(this.list.isEmpty()){
            throw new NoSuchElementException();
        }
        return this.list.removeFirst();
    }

    // Return (without removing) the top value. O(1). Throw NoSuchElementException if empty.
    public E peek() {
        if(this.list.isEmpty()){
            throw new NoSuchElementException();
        }
        return this.list.getFirst();
    }

    // Return true if value is present anywhere in the stack (by equals, null-safe).
    public boolean contains(E value) {
        return this.list.contains(value);
    }

    // Remove all elements. After this call, size is 0 and the stack is usable again.
    public void clear() {
        this.list.clear();
    }

    // Return "[bottom, ..., top]" — leftmost is the bottom, rightmost is the top.
    @Override
    public String toString() {
        return this.list.toStringReverse();
    }
}

class Stack_Main {
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

    public static void main(String[] args) {
        Stack<Integer> s = new Stack<>();
        checkEquals("new size",     0,    s.size());
        checkEquals("new isEmpty",  true, s.isEmpty());
        checkEquals("new toString", "[]", s.toString());

        s.push(1);
        checkEquals("size after push",     1,     s.size());
        checkEquals("isEmpty after push",  false, s.isEmpty());
        checkEquals("peek after push",     1,     s.peek());
        checkEquals("toString one elem",   "[1]", s.toString());

        s.push(2);
        s.push(3);
        checkEquals("size after 3 pushes",     3,           s.size());
        checkEquals("peek is top",             3,           s.peek());
        checkEquals("toString bottom to top",  "[1, 2, 3]", s.toString());

        checkEquals("peek does not pop",       3, s.peek());
        checkEquals("size unchanged after peek", 3, s.size());

        checkEquals("pop returns top",   3,        s.pop());
        checkEquals("size after pop",    2,        s.size());
        checkEquals("toString after pop", "[1, 2]", s.toString());
        checkEquals("peek after pop",    2,        s.peek());

        checkEquals("contains present", true,  s.contains(1));
        checkEquals("contains top",     true,  s.contains(2));
        checkEquals("contains absent",  false, s.contains(99));

        checkEquals("pop again",                 2,     s.pop());
        checkEquals("pop to single",             1,     s.pop());
        checkEquals("isEmpty after popping all", true,  s.isEmpty());
        checkEquals("toString when empty",       "[]",  s.toString());

        final Stack<Integer> empty = s;
        checkThrows("pop on empty",  java.util.NoSuchElementException.class, () -> empty.pop());
        checkThrows("peek on empty", java.util.NoSuchElementException.class, () -> empty.peek());

        s.push(42);
        checkEquals("reusable after emptying", "[42]", s.toString());
        checkEquals("peek after re-push",      42,     s.peek());

        Stack<Integer> lifo = new Stack<>();
        lifo.push(1); lifo.push(2); lifo.push(3); lifo.push(4);
        checkEquals("LIFO pop 1", 4, lifo.pop());
        checkEquals("LIFO pop 2", 3, lifo.pop());
        checkEquals("LIFO pop 3", 2, lifo.pop());
        checkEquals("LIFO pop 4", 1, lifo.pop());
        checkEquals("empty after LIFO drain", true, lifo.isEmpty());

        Stack<Integer> cl = new Stack<>();
        cl.push(1); cl.push(2); cl.push(3);
        cl.clear();
        checkEquals("size after clear",    0,    cl.size());
        checkEquals("isEmpty after clear", true, cl.isEmpty());
        checkEquals("toString after clear", "[]", cl.toString());
        cl.push(7);
        checkEquals("usable after clear",  "[7]", cl.toString());
        checkEquals("peek after clear+push", 7,   cl.peek());

        Stack<String> str = new Stack<>();
        str.push("a"); str.push("b"); str.push("c");
        checkEquals("string toString", "[a, b, c]", str.toString());
        checkEquals("string peek",     "c",         str.peek());
        checkEquals("string pop",      "c",         str.pop());

        Stack<String> ns = new Stack<>();
        ns.push("x"); ns.push(null); ns.push("z");
        checkEquals("null contains",  true,           ns.contains(null));
        checkEquals("null toString",  "[x, null, z]", ns.toString());
        checkEquals("pop top with null below", "z",   ns.pop());
        checkEquals("pop null value",          null,  ns.pop());
        checkEquals("after popping null",      "[x]", ns.toString());

        Stack<Integer> deep = new Stack<>();
        for (int i = 0; i < 1000; i++) deep.push(i);
        checkEquals("deep size",   1000, deep.size());
        checkEquals("deep peek",   999,  deep.peek());
        for (int i = 999; i >= 0; i--) {
            int got = deep.pop();
            if (got != i) {
                failed++;
                System.out.println("FAIL: deep pop order at i=" + i + " — got " + got);
                break;
            }
        }
        checkEquals("deep empty after drain", true, deep.isEmpty());

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}