package Data_structures;

import java.util.NoSuchElementException;
import java.util.Objects;

public class Doubly_linked_list<E> {

    private static class Node<E> {
        E value;
        Node<E> prev;
        Node<E> next;
        Node(E value) { this.value = value; }
    }

    private Node<E> head;
    private Node<E> tail;
    private int size;

    // Construct an empty list.
    public Doubly_linked_list() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    // Return the number of elements in the list.
    public int size() {
        return this.size;
    }

    // Return true if the list has no elements.
    public boolean isEmpty() {
        return this.size == 0;
    }

    // Insert value at the front of the list. O(1).
    public void addFirst(E value) {
        Node<E> node = new Node<E>(value);
        if(head == null){
            this.head = node;
            this.tail = node;
        }else {
            node.next = head;
            head.prev = node;
            node.prev = null;
            head = node;
        }
        this.size += 1;
    }

    // Insert value at the end of the list. O(1).
    public void addLast(E value) {
        Node<E> node = new Node<E>(value);
        if(head == null){
            this.head = node;
            this.tail = node;
        }else {
            tail.next = node;
            node.prev = tail;
            node.next = null;
            tail = node;
        }
        this.size += 1;
    }

    // Convenience alias for addLast.
    public void add(E value) {
        this.addLast(value);
    }

    // Insert value at the given index (valid range 0..size inclusive).
    // Throw IndexOutOfBoundsException for invalid indices.
    public void add(int index, E value) {
        if(index < 0 || index > this.size){
            throw new IndexOutOfBoundsException();
        }else if(index == 0){
            this.addFirst(value);
        }else if(index == this.size){
            this.addLast(value);
        }else{
            Node<E> head_copy = head;
            Node<E> node = new Node<E>(value);
            while(index > 1){
                index -= 1; 
                head_copy = head_copy.next;
            }
            node.next = head_copy.next;
            node.next.prev = node;
            node.prev = head_copy;
            head_copy.next = node;
            this.size += 1;
        }
    }

    // Return the value at the head. Throw NoSuchElementException if empty.
    public E getFirst() {
        if(head == null){
            throw new NoSuchElementException();
        }
        return head.value;
    }

    // Return the value at the tail. Throw NoSuchElementException if empty.
    public E getLast() {
        if(head == null){
            throw new NoSuchElementException();
        }
        return tail.value;
    }

    // Return the value at index (0..size-1). Throw IndexOutOfBoundsException for invalid indices.
    public E get(int index) {
        if(index < 0 || index >= this.size){
            throw new IndexOutOfBoundsException();
        }else if(index == 0){
            return head.value;
        }else if(index == this.size){
            return tail.value;
        }else{
            Node<E> head_copy = head;
            while(index > 0){
                index -= 1; 
                head_copy = head_copy.next;
            }
            return head_copy.value;
        }
    }

    // Replace the value at index (0..size-1). Throw IndexOutOfBoundsException for invalid indices.
    public void set(int index, E value) {
        if(index < 0 || index >= this.size){
            throw new IndexOutOfBoundsException();
        }else if(index == 0){
            head.value = value;
        }else if(index == this.size){
            tail.value = value;
        }else{
            Node<E> head_copy = head;
            while(index > 0){
                index -= 1; 
                head_copy = head_copy.next;
            }
            head_copy.value = value;
        }
    }

    // Remove and return the head value. O(1). Throw NoSuchElementException if empty.
    public E removeFirst() {
        if(this.isEmpty()){
            throw new NoSuchElementException();
        }else{
            Node<E> return_node = head;
            head = head.next;
            this.size -= 1;
            if(this.size == 0){
                this.clear();
            }else{
                head.prev = null;
            }
            
            return return_node.value;
        }
    }

    // Remove and return the tail value. O(1). Throw NoSuchElementException if empty.
    public E removeLast() {
        if(this.isEmpty()){
            throw new NoSuchElementException();
        }else{
            Node<E> return_node = tail;
            tail = tail.prev;
            this.size -= 1;
            if(this.size == 0){
                this.clear();
            }else{
                tail.next = null;
            }
            
            return return_node.value;
        }
    }

    // Remove and return the value at index (0..size-1).
    // Throw IndexOutOfBoundsException for invalid indices.
    public E remove(int index) {
        if(index < 0 || index >= this.size){
            throw new IndexOutOfBoundsException();
        } else if(index == 0){
            return this.removeFirst();
        } else if(index == this.size-1){
            return this.removeLast();
        }else{
            Node<E> head_copy = head;
            while(index > 0){
                index -= 1; 
                head_copy = head_copy.next;
            }
            Node<E> return_node = head_copy;
            head_copy.prev.next = head_copy.next;
            head_copy.next.prev = head_copy.prev;
            this.size -= 1;
            return return_node.value;

        }
    }

    // Remove the first occurrence of value (by equals). Return true if removed, false if not found.
    public boolean remove(E value) {
        if(this.contains(value)){
            this.remove(this.indexOf(value));
            return true;
        }
        return false;
    }

    // Return true if value is present (by equals, null-safe).
    public boolean contains(E value) {
        Node<E> head_copy = head;
        while(head_copy != null){
            if(Objects.equals(value, head_copy.value)){
                return true;
            }
            head_copy = head_copy.next;
        }
        return false;
    }

    // Return the first index of value, or -1 if not found.
    public int indexOf(E value) {
        Node<E> head_copy = head;
        int index = 0;
        while(head_copy != null){
            if(Objects.equals(value, head_copy.value)){
                return index;
            }
            head_copy = head_copy.next;
            index += 1;
        }
        return -1;
    }

    // Reverse the list in place. Head becomes tail and vice versa.
    public void reverse() {
        if(this.size < 2){
            return;
        }
        Node<E> head_copy = this.head;
        Node<E> tail_copy = this.tail;
        Node<E> temp = null;
        Node<E> current = head;
        while(current != null){
            temp = current.next;
            current.next = current.prev;
            current.prev = temp;
            current = current.prev;
        }
        this.tail = head_copy;
        this.tail.next = null;
        this.head = tail_copy;
        this.head.prev = null;
    }

    // Remove all elements. After this call, size is 0 and the list is usable again.
    public void clear() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    // Return "[a, b, c]" walking from head to tail via next pointers.
    @Override
    public String toString() {
        String output = "[";
        Node<E> head_copy = head;

        while(head_copy != null && head_copy.next != null){
            if(head_copy.value == null){
                output += "null";
            }else{
                output += head_copy.value.toString();
            }
            output += ", ";
            head_copy = head_copy.next;
        }
        if(this.size > 0 &&  head_copy.value != null){
            output += head_copy.value.toString();
        }else if(this.size > 0 &&  head_copy.value == null){
            output += "null";
        }
        
        output += "]";
        return output;
    }

    // Return "[c, b, a]" walking from tail to head via prev pointers.
    // Used by tests to verify prev pointers are maintained correctly.
    public String toStringReverse() {
        String output = "[";
        Node<E> tail_copy = tail;

        while(tail_copy != null && tail_copy.prev != null){
            if(tail_copy.value == null){
                output += "null";
            }else{
                output += tail_copy.value.toString();
            }
            output += ", ";
            tail_copy = tail_copy.prev;
        }
        if(this.size > 0 &&  tail_copy.value != null){
            output += tail_copy.value.toString();
        }else if(this.size > 0 &&  tail_copy.value == null){
            output += "null";
        }
        
        output += "]";
        return output;
    }
}

class Doubly_linked_list_Main {
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
        Doubly_linked_list<Integer> l = new Doubly_linked_list<>();
        checkEquals("new size",     0,    l.size());
        checkEquals("new isEmpty",  true, l.isEmpty());
        checkEquals("new toString", "[]", l.toString());
        checkEquals("new toStringReverse", "[]", l.toStringReverse());

        l.add(1); l.add(2); l.add(3);
        checkEquals("size after 3 adds",      3,           l.size());
        checkEquals("toString after adds",    "[1, 2, 3]", l.toString());
        checkEquals("toStringReverse after adds", "[3, 2, 1]", l.toStringReverse());
        checkEquals("getFirst", 1, l.getFirst());
        checkEquals("getLast",  3, l.getLast());

        l.addFirst(0);
        checkEquals("after addFirst toString",        "[0, 1, 2, 3]", l.toString());
        checkEquals("after addFirst toStringReverse", "[3, 2, 1, 0]", l.toStringReverse());
        checkEquals("getFirst is new",   0, l.getFirst());
        checkEquals("getLast unchanged", 3, l.getLast());

        checkEquals("get(0)", 0, l.get(0));
        checkEquals("get(2)", 2, l.get(2));
        checkEquals("get(3)", 3, l.get(3));
        l.set(2, 99);
        checkEquals("after set(2,99) toString",        "[0, 1, 99, 3]", l.toString());
        checkEquals("after set(2,99) toStringReverse", "[3, 99, 1, 0]", l.toStringReverse());

        final Doubly_linked_list<Integer> l2 = l;
        checkThrows("get(4) when size=4", IndexOutOfBoundsException.class, () -> l2.get(4));
        checkThrows("get(-1)",            IndexOutOfBoundsException.class, () -> l2.get(-1));
        checkThrows("set(4, ..)",         IndexOutOfBoundsException.class, () -> l2.set(4, 0));

        Doubly_linked_list<Integer> ins = new Doubly_linked_list<>();
        ins.add(1); ins.add(2); ins.add(4);
        ins.add(2, 3);
        checkEquals("insert middle",                "[1, 2, 3, 4]", ins.toString());
        checkEquals("insert middle reverse",        "[4, 3, 2, 1]", ins.toStringReverse());
        ins.add(0, 0);
        checkEquals("insert front",                 "[0, 1, 2, 3, 4]", ins.toString());
        checkEquals("insert front reverse",         "[4, 3, 2, 1, 0]", ins.toStringReverse());
        ins.add(ins.size(), 5);
        checkEquals("insert at size",               "[0, 1, 2, 3, 4, 5]", ins.toString());
        checkEquals("insert at size reverse",       "[5, 4, 3, 2, 1, 0]", ins.toStringReverse());
        checkEquals("tail correct after insert at size", 5, ins.getLast());

        final Doubly_linked_list<Integer> ins2 = ins;
        checkThrows("insert beyond size", IndexOutOfBoundsException.class, () -> ins2.add(100, 9));
        checkThrows("insert negative",    IndexOutOfBoundsException.class, () -> ins2.add(-1, 9));

        Doubly_linked_list<Integer> r = new Doubly_linked_list<>();
        r.add(10); r.add(20); r.add(30);
        checkEquals("removeFirst returns",         10,         r.removeFirst());
        checkEquals("after removeFirst",           "[20, 30]", r.toString());
        checkEquals("after removeFirst reverse",   "[30, 20]", r.toStringReverse());
        checkEquals("removeLast returns",          30,         r.removeLast());
        checkEquals("after removeLast",            "[20]",     r.toString());
        checkEquals("after removeLast reverse",    "[20]",     r.toStringReverse());
        checkEquals("getFirst==getLast on size 1", r.getFirst(), r.getLast());
        checkEquals("removeLast on size 1",        20,         r.removeLast());
        checkEquals("empty after",                 "[]",       r.toString());
        checkEquals("empty after reverse",         "[]",       r.toStringReverse());
        checkEquals("isEmpty after",               true,       r.isEmpty());

        r.add(7);
        checkEquals("re-add after empty: head",            7,     r.getFirst());
        checkEquals("re-add after empty: tail",            7,     r.getLast());
        checkEquals("re-add toString",                     "[7]", r.toString());
        checkEquals("re-add toStringReverse",              "[7]", r.toStringReverse());
        r.addFirst(6);
        checkEquals("addFirst after re-add",               "[6, 7]", r.toString());
        checkEquals("addFirst after re-add reverse",       "[7, 6]", r.toStringReverse());

        Doubly_linked_list<Integer> e = new Doubly_linked_list<>();
        checkThrows("getFirst on empty",    java.util.NoSuchElementException.class, () -> e.getFirst());
        checkThrows("getLast on empty",     java.util.NoSuchElementException.class, () -> e.getLast());
        checkThrows("removeFirst on empty", java.util.NoSuchElementException.class, () -> e.removeFirst());
        checkThrows("removeLast on empty",  java.util.NoSuchElementException.class, () -> e.removeLast());

        Doubly_linked_list<Integer> ri = new Doubly_linked_list<>();
        ri.add(1); ri.add(2); ri.add(3); ri.add(4);
        checkEquals("remove middle returns",                2,           ri.remove(1));
        checkEquals("after remove middle",                  "[1, 3, 4]", ri.toString());
        checkEquals("after remove middle reverse",          "[4, 3, 1]", ri.toStringReverse());
        checkEquals("remove last index",                    4,           ri.remove(ri.size() - 1));
        checkEquals("after remove tail",                    "[1, 3]",    ri.toString());
        checkEquals("after remove tail reverse",            "[3, 1]",    ri.toStringReverse());
        checkEquals("tail updated after removing old tail", 3,           ri.getLast());
        ri.add(5);
        checkEquals("append still works after tail-remove",         "[1, 3, 5]", ri.toString());
        checkEquals("append still works after tail-remove reverse", "[5, 3, 1]", ri.toStringReverse());

        Doubly_linked_list<Integer> rv = new Doubly_linked_list<>();
        rv.add(1); rv.add(2); rv.add(3); rv.add(2);
        checkEquals("remove first occurrence(2)",   true,        rv.remove(Integer.valueOf(2)));
        checkEquals("after value remove",           "[1, 3, 2]", rv.toString());
        checkEquals("after value remove reverse",   "[2, 3, 1]", rv.toStringReverse());
        checkEquals("remove head by value",         true,        rv.remove(Integer.valueOf(1)));
        checkEquals("after head remove",            "[3, 2]",    rv.toString());
        checkEquals("after head remove reverse",    "[2, 3]",    rv.toStringReverse());
        checkEquals("head updated",                 3,           rv.getFirst());
        checkEquals("remove tail by value",         true,        rv.remove(Integer.valueOf(2)));
        checkEquals("after tail remove",            "[3]",       rv.toString());
        checkEquals("after tail remove reverse",    "[3]",       rv.toStringReverse());
        checkEquals("tail updated",                 3,           rv.getLast());
        checkEquals("remove absent",                false,       rv.remove(Integer.valueOf(99)));

        Doubly_linked_list<Integer> c = new Doubly_linked_list<>();
        c.add(10); c.add(20); c.add(30);
        checkEquals("contains present", true,  c.contains(20));
        checkEquals("contains absent",  false, c.contains(99));
        checkEquals("indexOf present",  2,     c.indexOf(30));
        checkEquals("indexOf absent",   -1,    c.indexOf(99));

        Doubly_linked_list<Integer> rev = new Doubly_linked_list<>();
        rev.add(1); rev.add(2); rev.add(3); rev.add(4);
        rev.reverse();
        checkEquals("reversed toString",         "[4, 3, 2, 1]", rev.toString());
        checkEquals("reversed toStringReverse",  "[1, 2, 3, 4]", rev.toStringReverse());
        checkEquals("reversed head",             4,              rev.getFirst());
        checkEquals("reversed tail",             1,              rev.getLast());
        rev.add(0);
        checkEquals("append after reverse",         "[4, 3, 2, 1, 0]", rev.toString());
        checkEquals("append after reverse reverse", "[0, 1, 2, 3, 4]", rev.toStringReverse());
        rev.addFirst(5);
        checkEquals("addFirst after reverse",         "[5, 4, 3, 2, 1, 0]", rev.toString());
        checkEquals("addFirst after reverse reverse", "[0, 1, 2, 3, 4, 5]", rev.toStringReverse());

        Doubly_linked_list<Integer> rev0 = new Doubly_linked_list<>();
        rev0.reverse();
        checkEquals("reverse empty",         "[]", rev0.toString());
        checkEquals("reverse empty reverse", "[]", rev0.toStringReverse());
        Doubly_linked_list<Integer> rev1 = new Doubly_linked_list<>();
        rev1.add(42);
        rev1.reverse();
        checkEquals("reverse single",         "[42]", rev1.toString());
        checkEquals("reverse single reverse", "[42]", rev1.toStringReverse());
        checkEquals("reverse single head",    42,     rev1.getFirst());
        checkEquals("reverse single tail",    42,     rev1.getLast());

        Doubly_linked_list<Integer> cl = new Doubly_linked_list<>();
        cl.add(1); cl.add(2); cl.add(3);
        cl.clear();
        checkEquals("size after clear",            0,      cl.size());
        checkEquals("isEmpty after clear",         true,   cl.isEmpty());
        checkEquals("toString after clear",        "[]",   cl.toString());
        checkEquals("toStringReverse after clear", "[]",   cl.toStringReverse());
        cl.add(42);
        checkEquals("usable after clear",          "[42]", cl.toString());
        checkEquals("usable after clear reverse",  "[42]", cl.toStringReverse());
        checkEquals("head after clear+add",        42,     cl.getFirst());
        checkEquals("tail after clear+add",        42,     cl.getLast());

        Doubly_linked_list<String> s = new Doubly_linked_list<>();
        s.add("a"); s.add("b"); s.add("c");
        checkEquals("string toString",        "[a, b, c]", s.toString());
        checkEquals("string toStringReverse", "[c, b, a]", s.toStringReverse());
        checkEquals("string get(1)",          "b",         s.get(1));

        Doubly_linked_list<String> ns = new Doubly_linked_list<>();
        ns.add("x"); ns.add(null); ns.add("z");
        checkEquals("null contains",             true,           ns.contains(null));
        checkEquals("null indexOf",              1,              ns.indexOf(null));
        checkEquals("null toString",             "[x, null, z]", ns.toString());
        checkEquals("null toStringReverse",      "[z, null, x]", ns.toStringReverse());
        checkEquals("remove null",               true,           ns.remove((String) null));
        checkEquals("after remove null",         "[x, z]",       ns.toString());
        checkEquals("after remove null reverse", "[z, x]",       ns.toStringReverse());

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}