package Data_structures;

import java.util.NoSuchElementException;
import java.util.Objects;

public class Queue<E> {

    private Doubly_linked_list<E> list;

    // Construct an empty queue.
    public Queue() {
        this.list = new Doubly_linked_list<E>();
    }

    // Return the number of elements in the queue.
    public int size() {
        return this.list.size();
    }

    // Return true if the queue has no elements.
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    // Add value to the back of the queue. O(1).
    public void enqueue(E value) {
        this.list.addLast(value);
    }

    // Remove and return the value at the front. O(1). Throw NoSuchElementException if empty.
    public E dequeue() {
        if(this.list.isEmpty()){
            throw new NoSuchElementException();
        }
        return this.list.removeFirst();
    }

    // Return (without removing) the value at the front. O(1). Throw NoSuchElementException if empty.
    public E peek() {
        if(this.list.isEmpty()){
            throw new NoSuchElementException();
        }
        return this.list.getFirst();
    }

    // Return (without removing) the value at the back. O(1). Throw NoSuchElementException if empty.
    public E peekLast() {
        if(this.list.isEmpty()){
            throw new NoSuchElementException();
        }
        return this.list.getLast();
    }

    // Return true if value is present anywhere in the queue (by equals, null-safe).
    public boolean contains(E value) {
        return this.list.contains(value);
    }

    // Remove all elements. After this call, size is 0 and the queue is usable again.
    public void clear() {
        this.list.clear();
    }

    // Return "[front, ..., back]" — leftmost is the front, rightmost is the back.
    @Override
    public String toString() {
        return this.list.toString();
    }
}

class Queue_Main {
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
        Queue<Integer> q = new Queue<>();
        checkEquals("new size",     0,    q.size());
        checkEquals("new isEmpty",  true, q.isEmpty());
        checkEquals("new toString", "[]", q.toString());

        q.enqueue(1);
        checkEquals("size after enqueue",    1,     q.size());
        checkEquals("isEmpty after enqueue", false, q.isEmpty());
        checkEquals("peek after enqueue",    1,     q.peek());
        checkEquals("peekLast after enqueue", 1,    q.peekLast());
        checkEquals("toString one elem",     "[1]", q.toString());

        q.enqueue(2);
        q.enqueue(3);
        checkEquals("size after 3 enqueues",   3,           q.size());
        checkEquals("peek is front",           1,           q.peek());
        checkEquals("peekLast is back",        3,           q.peekLast());
        checkEquals("toString front to back",  "[1, 2, 3]", q.toString());

        checkEquals("peek does not dequeue",      1, q.peek());
        checkEquals("size unchanged after peek",  3, q.size());
        checkEquals("peekLast does not dequeue",  3, q.peekLast());

        checkEquals("dequeue returns front", 1,           q.dequeue());
        checkEquals("size after dequeue",    2,           q.size());
        checkEquals("toString after dequeue", "[2, 3]",   q.toString());
        checkEquals("peek after dequeue",    2,           q.peek());
        checkEquals("peekLast unchanged",    3,           q.peekLast());

        checkEquals("contains front",   true,  q.contains(2));
        checkEquals("contains back",    true,  q.contains(3));
        checkEquals("contains absent",  false, q.contains(99));

        checkEquals("dequeue again",                 2,    q.dequeue());
        checkEquals("dequeue to single",             3,    q.dequeue());
        checkEquals("isEmpty after dequeuing all",   true, q.isEmpty());
        checkEquals("toString when empty",           "[]", q.toString());

        final Queue<Integer> empty = q;
        checkThrows("dequeue on empty",  java.util.NoSuchElementException.class, () -> empty.dequeue());
        checkThrows("peek on empty",     java.util.NoSuchElementException.class, () -> empty.peek());
        checkThrows("peekLast on empty", java.util.NoSuchElementException.class, () -> empty.peekLast());

        q.enqueue(42);
        checkEquals("reusable after emptying", "[42]", q.toString());
        checkEquals("peek after re-enqueue",   42,     q.peek());
        checkEquals("peekLast after re-enqueue", 42,   q.peekLast());

        Queue<Integer> fifo = new Queue<>();
        fifo.enqueue(1); fifo.enqueue(2); fifo.enqueue(3); fifo.enqueue(4);
        checkEquals("FIFO dequeue 1", 1, fifo.dequeue());
        checkEquals("FIFO dequeue 2", 2, fifo.dequeue());
        checkEquals("FIFO dequeue 3", 3, fifo.dequeue());
        checkEquals("FIFO dequeue 4", 4, fifo.dequeue());
        checkEquals("empty after FIFO drain", true, fifo.isEmpty());

        Queue<Integer> mix = new Queue<>();
        mix.enqueue(1); mix.enqueue(2);
        checkEquals("mix dequeue 1",   1,        mix.dequeue());
        mix.enqueue(3); mix.enqueue(4);
        checkEquals("mix toString",    "[2, 3, 4]", mix.toString());
        checkEquals("mix peek",        2,        mix.peek());
        checkEquals("mix peekLast",    4,        mix.peekLast());
        checkEquals("mix dequeue 2",   2,        mix.dequeue());
        checkEquals("mix dequeue 3",   3,        mix.dequeue());
        mix.enqueue(5);
        checkEquals("mix toString end", "[4, 5]", mix.toString());

        Queue<Integer> cl = new Queue<>();
        cl.enqueue(1); cl.enqueue(2); cl.enqueue(3);
        cl.clear();
        checkEquals("size after clear",     0,    cl.size());
        checkEquals("isEmpty after clear",  true, cl.isEmpty());
        checkEquals("toString after clear", "[]", cl.toString());
        cl.enqueue(7);
        checkEquals("usable after clear",       "[7]", cl.toString());
        checkEquals("peek after clear+enqueue", 7,     cl.peek());

        Queue<String> str = new Queue<>();
        str.enqueue("a"); str.enqueue("b"); str.enqueue("c");
        checkEquals("string toString", "[a, b, c]", str.toString());
        checkEquals("string peek",     "a",         str.peek());
        checkEquals("string peekLast", "c",         str.peekLast());
        checkEquals("string dequeue",  "a",         str.dequeue());

        Queue<String> ns = new Queue<>();
        ns.enqueue("x"); ns.enqueue(null); ns.enqueue("z");
        checkEquals("null contains",   true,           ns.contains(null));
        checkEquals("null toString",   "[x, null, z]", ns.toString());
        checkEquals("dequeue front",   "x",            ns.dequeue());
        checkEquals("dequeue null",    null,           ns.dequeue());
        checkEquals("after dequeue null", "[z]",       ns.toString());

        Queue<Integer> deep = new Queue<>();
        for (int i = 0; i < 1000; i++) deep.enqueue(i);
        checkEquals("deep size",     1000, deep.size());
        checkEquals("deep peek",     0,    deep.peek());
        checkEquals("deep peekLast", 999,  deep.peekLast());
        for (int i = 0; i < 1000; i++) {
            int got = deep.dequeue();
            if (got != i) {
                failed++;
                System.out.println("FAIL: deep dequeue order at i=" + i + " — got " + got);
                break;
            }
        }
        checkEquals("deep empty after drain", true, deep.isEmpty());

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}