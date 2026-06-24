package Data_structures;

import java.util.Objects;

public class Dynamic_array<E> {
    private static final int DEFAULT_CAPACITY = 10;
    private Object[] arr;
    private int size;
    private int capacity;

    // TODO: initialize with DEFAULT_CAPACITY
    public Dynamic_array() {
        this(DEFAULT_CAPACITY);
    }

    // TODO: initialize with given capacity (must be >= 0)
    public Dynamic_array(int capacity) {
        if(capacity < 0){
            capacity = 0;
        }
        this.capacity = capacity;
        this.size = 0;
        this.arr = new Object[this.capacity];
    }

    // TODO: number of elements stored
    public int size() {
        return this.size;
    }

    // TODO: backing array length (exposed so tests can observe resize)
    public int capacity() {
        return this.capacity;
    }

    // TODO: true if size == 0
    public boolean isEmpty() {
        if(this.size == 0){
            return true;
        }
        return false;
    }

    // TODO: bounds check against size (NOT capacity); throw IndexOutOfBoundsException
    @SuppressWarnings("unchecked")
    public E get(int index) {
        if(index < 0 || index >= this.size){
            throw new IndexOutOfBoundsException();
        }
        return (E)this.arr[index];
    }

    // TODO: bounds check against size; replace existing slot
    public void set(int index, E value) {
        if(index < 0 || index >= this.size){
            throw new IndexOutOfBoundsException();
        }
        this.arr[index] = value;
    }

    // TODO: append. If size == capacity, grow first (double; if cap==0, set to 1). Amortized O(1).
    public void add(E value) {
        if(this.size == this.capacity){
            if(this.capacity == 0){
                this.capacity = 1;
            }
            this.resize(this.capacity*2);
        }

        this.arr[size] = value;
        this.size += 1;
    }

    // TODO: insert at index (valid range 0..size inclusive). Shift right. Grow if needed. O(n).
    public void add(int index, E value) {
        if(index < 0 || index > this.size){
            throw new IndexOutOfBoundsException();
        }

        if(this.size == this.capacity){
            this.resize(this.capacity*2);
        }

        // Shift by one
        for(int array_index = this.size ; array_index > index; array_index--){
            this.arr[array_index] = this.arr[array_index-1];
        }
        this.arr[index] = value;
        this.size += 1;
    }

    // TODO: remove and return element at index (0..size-1). Shift left. Null the freed slot.
    //       Optional shrink: if size < capacity / 4 after removal, halve capacity. O(n).
    @SuppressWarnings("unchecked")
    public E remove(int index) {
        if(index < 0 || index >= this.size){
            throw new IndexOutOfBoundsException();
        }
        Object return_value  = this.arr[index];

        for(int array_index = index; array_index < this.size-1; array_index++){
            this.arr[array_index] = this.arr[array_index+1];
        }
        this.arr[this.size] = null;
        this.size -= 1;
        if(this.size*4 > this.capacity){
            this.resize(this.size*2);
        }
        return (E)return_value;
    }

    // TODO: remove first occurrence by value; return true if removed (use indexOf + remove(int))
    public boolean remove(E value) {
        int index = this.indexOf(value);
        if(index >=0){
            this.remove(index);
            return true;
        }
        return false;
    }

    // TODO: linear search; Objects.equals for null-safety
    @SuppressWarnings("unchecked")
    public boolean contains(E value) {
        for(int array_index = 0; array_index < this.size; array_index++){
            if(Objects.equals(value, (E)this.arr[array_index])){
                return true;
            }
        }
        return false;
    }

    // TODO: first index of value or -1
    @SuppressWarnings("unchecked")
    public int indexOf(E value) {
        for(int array_index = 0; array_index < this.size; array_index++){
            if(Objects.equals(value, (E)this.arr[array_index])){
                return array_index;
            }
        }
        return -1;
    }

    // TODO: reset size to 0, null out slots (for GC), reset capacity to DEFAULT_CAPACITY
    public void clear() {
        this.resize(DEFAULT_CAPACITY);
        for(int array_index = 0; array_index < this.size; array_index++){
            this.arr[array_index] = null;
        }
        this.size = 0;
    }

    // TODO: return "[a, b, c]" using ONLY the first `size` elements, not the full backing array
    @Override
    public String toString() {
        String output = "[";
        for(int array_index = 0; array_index < this.size; array_index++){
            if(this.arr[array_index] == null){
                output += "null";
            }else{
                output += this.arr[array_index].toString();
            }
            output += ", ";
        }
        if(this.size > 0){
            output = output.substring(0, output.length()-2);
        }
        output += "]";
        return output;
    }

    // TODO (private helper): allocate new Object[newCap], copy first `size` elements, swap
    private void resize(int newCap) {
        int array_index = 0;
        Object[] new_arr = new Object[newCap];
        for(array_index = 0; array_index < this.size; array_index++){
            new_arr[array_index] = this.arr[array_index];
        }
        this.arr = new_arr;
        this.capacity = newCap;
    }
}

class Dynamic_array_Main {
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
        Dynamic_array<Integer> a = new Dynamic_array<>();
        checkEquals("new size",              0,    a.size());
        checkEquals("new capacity default",  10,   a.capacity());
        checkEquals("new isEmpty",           true, a.isEmpty());
        checkEquals("new toString",          "[]", a.toString());

        // --- Add / size / toString ---
        a.add(1); a.add(2); a.add(3);
        checkEquals("size after 3 adds",     3,           a.size());
        checkEquals("toString after adds",   "[1, 2, 3]", a.toString());
        checkEquals("isEmpty after adds",    false,       a.isEmpty());

        // --- Get / set ---
        checkEquals("get(0)",        1,  a.get(0));
        checkEquals("get(2)",        3,  a.get(2));
        a.set(1, 99);
        checkEquals("set then get",  99, a.get(1));
        checkEquals("toString after set", "[1, 99, 3]", a.toString());

        // --- Bounds use size, NOT capacity (common bug) ---
        final Dynamic_array<Integer> a2 = a;
        checkThrows("get(3) when size=3",     IndexOutOfBoundsException.class, () -> a2.get(3));
        checkThrows("set(3, ..) when size=3", IndexOutOfBoundsException.class, () -> a2.set(3, 0));
        checkThrows("get(-1)",                IndexOutOfBoundsException.class, () -> a2.get(-1));

        // --- Grow ---
        Dynamic_array<Integer> small = new Dynamic_array<>(2);
        checkEquals("small capacity start",    2, small.capacity());
        small.add(10); small.add(20);
        checkEquals("capacity before resize",  2, small.capacity());
        small.add(30);  // triggers grow
        checkEquals("capacity doubled",        4, small.capacity());
        checkEquals("size after grow",         3, small.size());
        checkEquals("elements preserved",      "[10, 20, 30]", small.toString());

        // Grow from capacity 0 (edge case)
        Dynamic_array<Integer> zero = new Dynamic_array<>(0);
        checkEquals("zero-cap start", 0, zero.capacity());
        zero.add(7);
        checkTrue("zero-cap grew on add",      zero.capacity() >= 1);
        checkEquals("zero-cap holds value",    7, zero.get(0));

        // --- Insert at index ---
        Dynamic_array<Integer> ins = new Dynamic_array<>();
        ins.add(1); ins.add(2); ins.add(4);
        ins.add(2, 3);  // between 2 and 4
        checkEquals("insert middle",  "[1, 2, 3, 4]",    ins.toString());
        ins.add(0, 0);  // at front
        checkEquals("insert front",   "[0, 1, 2, 3, 4]", ins.toString());
        ins.add(ins.size(), 5);  // at end == append
        checkEquals("insert at size", "[0, 1, 2, 3, 4, 5]", ins.toString());

        final Dynamic_array<Integer> ins2 = ins;
        checkThrows("insert beyond size", IndexOutOfBoundsException.class, () -> ins2.add(100, 9));
        checkThrows("insert negative",    IndexOutOfBoundsException.class, () -> ins2.add(-1, 9));

        // --- Remove by index ---
        Dynamic_array<Integer> rem = new Dynamic_array<>();
        rem.add(10); rem.add(20); rem.add(30); rem.add(40);
        checkEquals("remove middle returns value", 20,           rem.remove(1));
        checkEquals("after remove middle",         "[10, 30, 40]", rem.toString());
        checkEquals("size after remove",           3,             rem.size());
        checkEquals("remove first",                10,            rem.remove(0));
        checkEquals("remove last",                 40,            rem.remove(rem.size() - 1));
        checkEquals("after multiple removes",      "[30]",        rem.toString());

        final Dynamic_array<Integer> rem2 = rem;
        checkThrows("remove out of bounds", IndexOutOfBoundsException.class, () -> rem2.remove(5));

        // --- Remove by value ---
        Dynamic_array<Integer> rv = new Dynamic_array<>();
        rv.add(1); rv.add(2); rv.add(3); rv.add(2);
        checkEquals("remove first occurrence", true,        rv.remove(Integer.valueOf(2)));
        checkEquals("after value remove",      "[1, 3, 2]", rv.toString());
        checkEquals("remove absent value",     false,       rv.remove(Integer.valueOf(99)));

        // --- Shrink (size < capacity / 4) ---
        Dynamic_array<Integer> sh = new Dynamic_array<>(2);
        for (int i = 0; i < 20; i++) sh.add(i);
        int capBefore = sh.capacity();
        while (sh.size() > 1) sh.remove(sh.size() - 1);
        checkTrue("capacity shrank after removes", sh.capacity() < capBefore);
        checkEquals("shrink preserved remaining",  "[0]", sh.toString());

        // --- Clear ---
        Dynamic_array<Integer> c = new Dynamic_array<>();
        c.add(1); c.add(2); c.add(3);
        c.clear();
        checkEquals("size after clear",     0,    c.size());
        checkEquals("isEmpty after clear",  true, c.isEmpty());
        checkEquals("toString after clear", "[]", c.toString());
        c.add(42);  // still usable after clear
        checkEquals("usable after clear", "[42]", c.toString());

        // --- Generics with String ---
        Dynamic_array<String> s = new Dynamic_array<>();
        s.add("a"); s.add("b"); s.add("c");
        checkEquals("string toString", "[a, b, c]", s.toString());
        checkEquals("string get",      "b",         s.get(1));
        checkEquals("string indexOf",  2,           s.indexOf("c"));

        // --- Null storage ---
        Dynamic_array<String> ns = new Dynamic_array<>();
        ns.add("x"); ns.add(null); ns.add("z");
        checkEquals("null contains",  true,           ns.contains(null));
        checkEquals("null indexOf",   1,              ns.indexOf(null));
        checkEquals("null toString",  "[x, null, z]", ns.toString());

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}