package Data_structures;

import java.util.NoSuchElementException;
import java.util.Objects;

public class Heap<E extends Comparable<E>> {

    private Object[] arr;
    private int size;

    private static final int DEFAULT_CAPACITY = 16;

    // Construct an empty min-heap with default capacity.
    public Heap() {
        this.size = 0;
        this.arr = new Object[DEFAULT_CAPACITY];
    }

    // Construct an empty min-heap with the given initial backing-array capacity.
    public Heap(int capacity) {
        this.size = 0;
        this.arr = new Object[capacity];
    }

    // Construct a min-heap from the given elements using O(n) bottom-up heapify
    // (not n calls to add, which would be O(n log n)).
    public Heap(E[] values) {
        this.arr = new Object[values.length];
        this.size = 0;
        for(int index = 0; index <values.length; index ++ ){
            this.arr[index] = values[index];
            this.size += 1;
        }
        for(int index = ((this.size/2)-1); index >=0; index--){
            this.shift_down(index);
            this.shift_up(index);
        }
    }

    @SuppressWarnings("unchecked")
    private void shift_up(int i){
        int curr_index = i;
        int parent_index = (curr_index-1)/2;
        E curr = (E)this.arr[curr_index];
        E parent = (E)this.arr[parent_index];

        while(curr_index > 0 && parent.compareTo(curr)>0){
            E temp = (E)this.arr[curr_index];
            this.arr[curr_index] = this.arr[parent_index];
            this.arr[parent_index] = temp;
            curr_index = parent_index;
            parent_index = (curr_index-1)/2;
            curr = (E)this.arr[curr_index];
            parent = (E)this.arr[parent_index];
        }
    }

    @SuppressWarnings("unchecked")
    private void shift_down(int i){
        int curr_index = i;
        int child_indexleft = 2*curr_index+1;
        int child_indexright = 2*curr_index+2;
        if(child_indexleft >= this.size){
            return;
        }else if(child_indexright >= size){
            child_indexright = child_indexleft;
        }
        E curr = (E)this.arr[curr_index];
        E childleft = (E)this.arr[child_indexleft];
        E childright = (E)this.arr[child_indexright];
        E chosechild = childleft.compareTo(childright) < 0 ? childleft : childright;
        int choose_child_index = childleft.compareTo(chosechild) == 0 ? child_indexleft:child_indexright;

        while(chosechild.compareTo(curr)<0){
            E temp = (E)this.arr[curr_index];
            this.arr[curr_index] = this.arr[choose_child_index];
            this.arr[choose_child_index] = temp;

            curr_index = choose_child_index; 
            child_indexleft = 2*curr_index+1;
            child_indexright = 2*curr_index+2;
            if(child_indexleft >= this.size){
                break;
            }else if(child_indexright >= size){
                child_indexright = child_indexleft;
            }

            curr = (E)this.arr[curr_index];
            childleft = (E)this.arr[child_indexleft];
            childright = (E)this.arr[child_indexright];
            chosechild = childleft.compareTo(childright) < 0 ? childleft : childright;
            choose_child_index = childleft.compareTo(chosechild) == 0 ? child_indexleft:child_indexright;
        }
    }

    private void grow() {
        int newCap = this.arr.length == 0 ? 1 : this.arr.length * 2;
        Object[] newArr = new Object[newCap];
        for (int i = 0; i < this.size; i++) {
            newArr[i] = this.arr[i];
        }
        this.arr = newArr;
    }

    // Return the number of elements in the heap.
    public int size() {
        return this.size;
    }

    // Return true if the heap has no elements.
    public boolean isEmpty() {
        return this.size == 0;
    }

    // Return (without removing) the minimum element. O(1).
    // Throw NoSuchElementException if empty.
    @SuppressWarnings("unchecked")
    public E peek() {
        if(this.size ==0){
            throw new NoSuchElementException();
        }
        return (E)this.arr[0];
    }

    // Remove and return the minimum element. O(log n).
    // Throw NoSuchElementException if empty.
    @SuppressWarnings("unchecked")
    public E poll() {
        if(this.isEmpty()){
            throw new NoSuchElementException();
        }
        E returnValue = (E)this.arr[0];
        this.arr[0] = this.arr[this.size-1];
        this.size -= 1;
        if(this.size>0){
            this.shift_down(0);
        }
        return returnValue;
    }

    // Insert value into the heap. O(log n). Throw NullPointerException if value is null
    // (matches java.util.PriorityQueue behavior).
    @SuppressWarnings("unchecked")
    public void add(E value) {
        if(value == null){
            throw new NullPointerException();
        }if(this.size == this.arr.length){
            this.grow();
        }
        this.arr[this.size] = value;
        this.size += 1;
        this.shift_up(this.size-1);
        
    }

    

    // Return true if value is present anywhere in the heap. O(n) linear scan.
    // contains(null) returns false.
    public boolean contains(E value) {
        if(value == null){
            return false;
        }else{
            for(int index = 0; index < this.size; index++){
                if(Objects.equals(value, this.arr[index])){
                    return true;
                }
            }
            return false;
        }
    }

    // Remove the first occurrence of value. Return true if removed, false if not present or null.
    // O(n) — linear scan to find, O(log n) to restore heap property.
    public boolean remove(E value) {
        if(value == null){
            return false;
        }else{
            for(int index = 0; index < this.size; index++){
                if(Objects.equals(value, this.arr[index])){
                    E temp = (E)this.arr[this.size-1];
                    this.arr[this.size-1] = this.arr[index];
                    this.arr[index] = temp;
                    this.size -= 1;
                    this.shift_up(index);
                    this.shift_down(index);
                    return true;
                }
            }
            return false;
        }
    }

    // Remove all elements. After this call, size is 0 and the heap is usable again.
    public void clear() {
        for(int index = 0; index < this.size; index++){
            this.arr[index] = null;
        }
        this.size = 0;
    }

    // Return "[e0, e1, ..., e_{size-1}]" using the internal array (level-order) layout,
    // NOT sorted order. Useful for verifying heap structure during debugging.
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
}



class Heap_Main {
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
        Heap<Integer> h = new Heap<>();
        checkEquals("new size",     0,    h.size());
        checkEquals("new isEmpty",  true, h.isEmpty());
        checkEquals("new toString", "[]", h.toString());

        // --- Single add / peek / poll ---
        h.add(5);
        checkEquals("size after add",    1,     h.size());
        checkEquals("isEmpty after add", false, h.isEmpty());
        checkEquals("peek single",       5,     h.peek());
        checkEquals("poll single",       5,     h.poll());
        checkEquals("empty after poll",  true,  h.isEmpty());

        // --- Peek does not modify ---
        h.add(3);
        h.add(1);
        h.add(7);
        checkEquals("peek is min",       1, h.peek());
        checkEquals("size unchanged",    3, h.size());
        checkEquals("peek again is min", 1, h.peek());

        // --- Poll order is ascending (min-heap invariant) ---
        Heap<Integer> ord = new Heap<>();
        int[] scrambled = {5, 3, 8, 1, 9, 2, 7, 4, 6};
        for (int v : scrambled) ord.add(v);
        checkEquals("size before drain", 9, ord.size());
        int[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        for (int i = 0; i < expected.length; i++) {
            checkEquals("poll #" + i, expected[i], ord.poll());
        }
        checkEquals("empty after full drain", true, ord.isEmpty());

        // --- Duplicates preserved ---
        Heap<Integer> dup = new Heap<>();
        dup.add(2); dup.add(1); dup.add(2); dup.add(1); dup.add(3);
        checkEquals("dup size", 5, dup.size());
        checkEquals("dup poll 1", 1, dup.poll());
        checkEquals("dup poll 2", 1, dup.poll());
        checkEquals("dup poll 3", 2, dup.poll());
        checkEquals("dup poll 4", 2, dup.poll());
        checkEquals("dup poll 5", 3, dup.poll());

        // --- Heapify constructor (O(n) build) ---
        Integer[] init = {9, 4, 7, 1, 3, 8, 2, 6, 5};
        Heap<Integer> built = new Heap<>(init);
        checkEquals("heapify size", 9, built.size());
        checkEquals("heapify peek", 1, built.peek());
        int[] sortedFromBuilt = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        for (int i = 0; i < sortedFromBuilt.length; i++) {
            checkEquals("heapify poll #" + i, sortedFromBuilt[i], built.poll());
        }

        // --- Heapify with empty input ---
        Heap<Integer> heapifyEmpty = new Heap<>(new Integer[]{});
        checkEquals("heapify empty size",     0,    heapifyEmpty.size());
        checkEquals("heapify empty isEmpty",  true, heapifyEmpty.isEmpty());

        // --- Heapify with single ---
        Heap<Integer> heapifyOne = new Heap<>(new Integer[]{42});
        checkEquals("heapify single size", 1,  heapifyOne.size());
        checkEquals("heapify single peek", 42, heapifyOne.peek());

        // --- Empty heap throws ---
        Heap<Integer> empty = new Heap<>();
        checkThrows("peek on empty", NoSuchElementException.class, () -> empty.peek());
        checkThrows("poll on empty", NoSuchElementException.class, () -> empty.poll());

        // --- Reusable after full drain ---
        Heap<Integer> reuse = new Heap<>();
        reuse.add(3); reuse.add(1); reuse.add(2);
        reuse.poll(); reuse.poll(); reuse.poll();
        checkEquals("drained isEmpty", true, reuse.isEmpty());
        reuse.add(99);
        checkEquals("reuse size", 1,  reuse.size());
        checkEquals("reuse peek", 99, reuse.peek());

        // --- contains ---
        Heap<Integer> c = new Heap<>();
        c.add(10); c.add(20); c.add(30); c.add(15);
        checkEquals("contains min",     true,  c.contains(10));
        checkEquals("contains middle",  true,  c.contains(20));
        checkEquals("contains last",    true,  c.contains(15));
        checkEquals("contains absent",  false, c.contains(99));
        checkEquals("contains(null)",   false, c.contains(null));

        // --- remove(value): heap property preserved ---
        Heap<Integer> rm = new Heap<>();
        int[] rmVals = {5, 3, 8, 1, 9, 2, 7, 4, 6};
        for (int v : rmVals) rm.add(v);
        checkEquals("remove min(1)",       true,  rm.remove(1));
        checkEquals("size after remove",   8,     rm.size());
        checkEquals("peek after removing min", 2, rm.peek());

        checkEquals("remove middle(5)",   true,  rm.remove(5));
        checkEquals("size after remove",  7,     rm.size());

        checkEquals("remove absent",      false, rm.remove(999));
        checkEquals("size unchanged",     7,     rm.size());

        checkEquals("remove(null)",       false, rm.remove(null));

        // After two removes, drain and verify what's left is still sorted.
        int[] remaining = {2, 3, 4, 6, 7, 8, 9};
        for (int i = 0; i < remaining.length; i++) {
            checkEquals("post-remove poll #" + i, remaining[i], rm.poll());
        }

        // --- Null disallowed on add ---
        Heap<Integer> nn = new Heap<>();
        checkThrows("add(null) throws NPE", NullPointerException.class, () -> nn.add(null));
        checkEquals("size still 0 after failed add", 0, nn.size());

        // --- clear ---
        Heap<Integer> cl = new Heap<>();
        cl.add(1); cl.add(2); cl.add(3);
        cl.clear();
        checkEquals("size after clear",     0,    cl.size());
        checkEquals("isEmpty after clear",  true, cl.isEmpty());
        checkEquals("toString after clear", "[]", cl.toString());
        cl.add(42);
        checkEquals("usable after clear",   1,    cl.size());
        checkEquals("peek after clear+add", 42,   cl.peek());

        // --- Generics with String (lexicographic via Comparable) ---
        Heap<String> s = new Heap<>();
        s.add("banana"); s.add("apple"); s.add("cherry"); s.add("date");
        checkEquals("string peek lex-min", "apple",  s.peek());
        checkEquals("string poll 1",       "apple",  s.poll());
        checkEquals("string poll 2",       "banana", s.poll());
        checkEquals("string poll 3",       "cherry", s.poll());
        checkEquals("string poll 4",       "date",   s.poll());

        // --- Growth: add past default capacity ---
        Heap<Integer> grow = new Heap<>(2);
        for (int i = 100; i >= 1; i--) grow.add(i);
        checkEquals("grow size", 100, grow.size());
        for (int i = 1; i <= 100; i++) {
            int got = grow.poll();
            if (got != i) {
                failed++;
                System.out.println("FAIL: grow poll #" + i + " — got " + got);
                break;
            }
        }
        checkEquals("grow empty after drain", true, grow.isEmpty());

        // --- Heap property holds after every operation (interleaved) ---
        Heap<Integer> mix = new Heap<>();
        mix.add(5); mix.add(2); mix.add(8);
        checkEquals("mix peek 1", 2, mix.peek());
        mix.poll();
        checkEquals("mix peek 2", 5, mix.peek());
        mix.add(1);
        checkEquals("mix peek 3", 1, mix.peek());
        mix.add(6); mix.add(3);
        checkEquals("mix peek 4", 1, mix.peek());
        checkEquals("mix poll 1", 1, mix.poll());
        checkEquals("mix poll 2", 3, mix.poll());
        checkEquals("mix poll 3", 5, mix.poll());
        checkEquals("mix poll 4", 6, mix.poll());
        checkEquals("mix poll 5", 8, mix.poll());

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}