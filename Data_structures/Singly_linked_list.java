package Data_structures;

import java.util.NoSuchElementException;
import java.util.Objects;

public class Singly_linked_list<E> {

    private static class Node<E> {
        E value;
        Node<E> next;
        Node(E value) { this.value = value; }
    }

    private Node<E> head;
    private Node<E> tail;
    private int size;

    public Singly_linked_list() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public void addFirst(E value) {
        Node<E> new_node = new Node<E>(value);
        if(head == null){
            head = new_node;
            tail = new_node;
        }else{
            new_node.next = head;
            head = new_node;
        }
        this.size += 1;
    }

    public void addLast(E value) {
        Node<E> new_node = new Node<E>(value);
        if(head == null){
            head = new_node;
            tail = new_node;
        }else{
            tail.next = new_node;
            tail = new_node;
        }
        this.size += 1;
    }

    public void add(E value) {
        this.addLast(value);
    }

    public void add(int index, E value) {
        if(index == 0){
            this.addFirst(value);
            return;
        }else if(index == this.size){
            this.addLast(value);
            return;
        }
        if(index < 0 || index > this.size){
            throw new IndexOutOfBoundsException();
        }
        Node<E> new_node = new Node<E>(value);
        Node<E> head_copy = head;
        while(index > 1){
            head_copy = head_copy.next;
            index -= 1;
        }
        new_node.next = head_copy.next;
        head_copy.next = new_node;
        this.size += 1;
    }

    public E getFirst() {
        if(head != null){
            return (E)head.value;
        }
        throw new NoSuchElementException();
    }

    public E getLast() {
        if(tail != null){
            return (E)tail.value;
        }
        throw new NoSuchElementException();
    }

    public E get(int index) {
        if(index < 0 || index >= this.size){
            throw new IndexOutOfBoundsException();
        }
        Node<E> head_copy = head;
        while(index > 0){
            head_copy = head_copy.next;
            index -= 1;
        }
        return (E)head_copy.value;
    }

    public void set(int index, E value) {
        if(index < 0 || index >= this.size){
            throw new IndexOutOfBoundsException();
        }
        Node<E> head_copy = head;
        while(index > 0){
            head_copy = head_copy.next;
            index -= 1;
        }
        head_copy.value = value;
    }

    public E removeFirst() {
        if(head == null){
            throw new NoSuchElementException();
        }else{
            Node<E> return_node = head;
            head = head.next;
            this.size -= 1;
            return (E)return_node.value;
        }
    }

    public E removeLast() {
        if(head == null){
            throw new NoSuchElementException();
        }else if(head.next == null){
            Node<E> return_node = tail;
            head = null;
            tail = null;
            this.size -= 1;
            return (E)return_node.value;
        }else{
            Node<E> return_node = tail;
            Node<E> head_copy = head;
            while(!Objects.equals(head_copy.next, tail)){
                head_copy = head_copy.next;
            }
            tail = head_copy;
            head_copy.next = null;
            this.size -= 1;
            return (E)return_node.value;
        }
    }

    public E remove(int index) {
        if(index == 0){
            return this.removeFirst();
        }else if(index == this.size-1){
            return this.removeLast();
        }else if(index < 0 || index >= this.size){
            throw new IndexOutOfBoundsException();
        }else{
            Node<E> head_copy = head;
            Node<E> head_copy_front = head.next;
            while(index>1){
                head_copy = head_copy.next;
                head_copy_front = head_copy_front.next;
                index -= 1;
            }
            Node<E> return_node = head_copy_front;
            head_copy.next = head_copy_front.next;
            this.size -= 1;
            return (E)return_node.value;
        }
    }

    public boolean remove(E value) {
        if(this.contains(value)){
            this.remove(this.indexOf(value));
            return true;
        }
        return false;
    }

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

    public void reverse() {

        if(this.size < 2){
            return;
        }
        Node<E> head_copy = this.head;
        Node<E> tail_copy = this.tail;
        Node<E> prev = head;
        Node<E> current = head.next;
        Node<E> future = head.next.next;
        while(future != null){
            current.next = prev;
            prev = current;
            current = future;
            future = future.next;
        }
        current.next = prev;
        this.tail = head_copy;
        this.tail.next = null;
        this.head = tail_copy;
    }

    public void clear() {
        this.head = null;
        this.tail = null;
        this.size = 0;

    }

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
}

class Singly_linked_list_Main {
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
        Singly_linked_list<Integer> l = new Singly_linked_list<>();
        checkEquals("new size",       0,    l.size());
        checkEquals("new isEmpty",    true, l.isEmpty());
        checkEquals("new toString",   "[]", l.toString());

        l.add(1); l.add(2); l.add(3);
        checkEquals("size after 3 adds",   3,           l.size());
        checkEquals("toString after adds", "[1, 2, 3]", l.toString());
        checkEquals("getFirst",            1,           l.getFirst());
        checkEquals("getLast",             3,           l.getLast());

        l.addFirst(0);
        checkEquals("after addFirst",    "[0, 1, 2, 3]", l.toString());
        checkEquals("getFirst is new",   0,              l.getFirst());
        checkEquals("getLast unchanged", 3,              l.getLast());

        checkEquals("get(0)", 0, l.get(0));
        checkEquals("get(2)", 2, l.get(2));
        checkEquals("get(3)", 3, l.get(3));
        l.set(2, 99);
        checkEquals("after set(2,99)", "[0, 1, 99, 3]", l.toString());

        final Singly_linked_list<Integer> l2 = l;
        checkThrows("get(4) when size=4",  IndexOutOfBoundsException.class, () -> l2.get(4));
        checkThrows("get(-1)",             IndexOutOfBoundsException.class, () -> l2.get(-1));
        checkThrows("set(4, ..)",          IndexOutOfBoundsException.class, () -> l2.set(4, 0));

        Singly_linked_list<Integer> ins = new Singly_linked_list<>();
        ins.add(1); ins.add(2); ins.add(4);
        ins.add(2, 3);
        checkEquals("insert middle",   "[1, 2, 3, 4]",       ins.toString());
        ins.add(0, 0);
        checkEquals("insert front",    "[0, 1, 2, 3, 4]",    ins.toString());
        ins.add(ins.size(), 5);
        checkEquals("insert at size",  "[0, 1, 2, 3, 4, 5]", ins.toString());
        checkEquals("tail correct after insert at size", 5, ins.getLast());

        final Singly_linked_list<Integer> ins2 = ins;
        checkThrows("insert beyond size", IndexOutOfBoundsException.class, () -> ins2.add(100, 9));
        checkThrows("insert negative",    IndexOutOfBoundsException.class, () -> ins2.add(-1, 9));

        Singly_linked_list<Integer> r = new Singly_linked_list<>();
        r.add(10); r.add(20); r.add(30);
        checkEquals("removeFirst returns",         10,         r.removeFirst());
        checkEquals("after removeFirst",           "[20, 30]", r.toString());
        checkEquals("removeLast returns",          30,         r.removeLast());
        checkEquals("after removeLast",            "[20]",     r.toString());
        checkEquals("getFirst==getLast on size 1", r.getFirst(), r.getLast());
        checkEquals("removeLast on size 1",        20,         r.removeLast());
        checkEquals("empty after",                 "[]",       r.toString());
        checkEquals("isEmpty after",               true,       r.isEmpty());

        r.add(7);
        checkEquals("re-add after empty: head", 7,     r.getFirst());
        checkEquals("re-add after empty: tail", 7,     r.getLast());
        checkEquals("re-add toString",          "[7]", r.toString());

        Singly_linked_list<Integer> e = new Singly_linked_list<>();
        checkThrows("getFirst on empty",    java.util.NoSuchElementException.class, () -> e.getFirst());
        checkThrows("getLast on empty",     java.util.NoSuchElementException.class, () -> e.getLast());
        checkThrows("removeFirst on empty", java.util.NoSuchElementException.class, () -> e.removeFirst());
        checkThrows("removeLast on empty",  java.util.NoSuchElementException.class, () -> e.removeLast());

        Singly_linked_list<Integer> ri = new Singly_linked_list<>();
        ri.add(1); ri.add(2); ri.add(3); ri.add(4);
        checkEquals("remove middle returns",                2,           ri.remove(1));
        checkEquals("after remove middle",                  "[1, 3, 4]", ri.toString());
        checkEquals("remove last index",                    4,           ri.remove(ri.size() - 1));
        checkEquals("after remove tail",                    "[1, 3]",    ri.toString());
        checkEquals("tail updated after removing old tail", 3,           ri.getLast());
        ri.add(5);
        checkEquals("append still works after tail-remove", "[1, 3, 5]", ri.toString());

        Singly_linked_list<Integer> rv = new Singly_linked_list<>();
        rv.add(1); rv.add(2); rv.add(3); rv.add(2);
        checkEquals("remove first occurrence(2)", true,        rv.remove(Integer.valueOf(2)));
        checkEquals("after value remove",         "[1, 3, 2]", rv.toString());
        checkEquals("remove head by value",       true,        rv.remove(Integer.valueOf(1)));
        checkEquals("after head remove",          "[3, 2]",    rv.toString());
        checkEquals("head updated",               3,           rv.getFirst());
        checkEquals("remove tail by value",       true,        rv.remove(Integer.valueOf(2)));
        checkEquals("after tail remove",          "[3]",       rv.toString());
        checkEquals("tail updated",               3,           rv.getLast());
        checkEquals("remove absent",              false,       rv.remove(Integer.valueOf(99)));

        Singly_linked_list<Integer> c = new Singly_linked_list<>();
        c.add(10); c.add(20); c.add(30);
        checkEquals("contains present", true,  c.contains(20));
        checkEquals("contains absent",  false, c.contains(99));
        checkEquals("indexOf present",  2,     c.indexOf(30));
        checkEquals("indexOf absent",   -1,    c.indexOf(99));

        Singly_linked_list<Integer> rev = new Singly_linked_list<>();
        rev.add(1); rev.add(2); rev.add(3); rev.add(4);
        rev.reverse();
        checkEquals("reversed toString",    "[4, 3, 2, 1]",    rev.toString());
        checkEquals("reversed head",        4,                 rev.getFirst());
        checkEquals("reversed tail",        1,                 rev.getLast());
        rev.add(0);
        checkEquals("append after reverse", "[4, 3, 2, 1, 0]", rev.toString());

        Singly_linked_list<Integer> rev0 = new Singly_linked_list<>();
        rev0.reverse();
        checkEquals("reverse empty", "[]", rev0.toString());
        Singly_linked_list<Integer> rev1 = new Singly_linked_list<>();
        rev1.add(42);
        rev1.reverse();
        checkEquals("reverse single",      "[42]", rev1.toString());
        checkEquals("reverse single head", 42,     rev1.getFirst());
        checkEquals("reverse single tail", 42,     rev1.getLast());

        Singly_linked_list<Integer> cl = new Singly_linked_list<>();
        cl.add(1); cl.add(2); cl.add(3);
        cl.clear();
        checkEquals("size after clear",     0,      cl.size());
        checkEquals("isEmpty after clear",  true,   cl.isEmpty());
        checkEquals("toString after clear", "[]",   cl.toString());
        cl.add(42);
        checkEquals("usable after clear",   "[42]", cl.toString());
        checkEquals("head after clear+add", 42,     cl.getFirst());
        checkEquals("tail after clear+add", 42,     cl.getLast());

        Singly_linked_list<String> s = new Singly_linked_list<>();
        s.add("a"); s.add("b"); s.add("c");
        checkEquals("string toString", "[a, b, c]", s.toString());
        checkEquals("string get(1)",   "b",         s.get(1));

        Singly_linked_list<String> ns = new Singly_linked_list<>();
        ns.add("x"); ns.add(null); ns.add("z");
        checkEquals("null contains",     true,           ns.contains(null));
        checkEquals("null indexOf",      1,              ns.indexOf(null));
        checkEquals("null toString",     "[x, null, z]", ns.toString());
        checkEquals("remove null",       true,           ns.remove((String) null));
        checkEquals("after remove null", "[x, z]",       ns.toString());

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}