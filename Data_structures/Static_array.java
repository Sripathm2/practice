package Data_structures;


import java.util.Objects;

public class Static_array<E> {
    private Object[] arr;
    private int capacity;

    // TODO: initialize array with given capacity
    public Static_array(int capacity) {
        if(capacity < 0){
            throw new IndexOutOfBoundsException();
        }
        this.capacity = capacity;
        this.arr = new Object[this.capacity];

    }

    // TODO: return element at index; throw IndexOutOfBoundsException if invalid
    @SuppressWarnings("unchecked")
    public E get(int index) {
        if(index < 0 || index >= this.capacity){
            throw new IndexOutOfBoundsException();
        }
        return (E)this.arr[index];
    }

    // TODO: set element at index; throw IndexOutOfBoundsException if invalid
    public void set(int index, E value) {
        if(index < 0 || index >= this.capacity){
            throw new IndexOutOfBoundsException();
        }
        this.arr[index] = value;
    }

    // TODO: return capacity (fixed size)
    public int size() {
        return this.capacity;
    }

    // TODO: linear search; return true if value present (use Objects.equals for null-safety)
    @SuppressWarnings("unchecked")
    public boolean contains(E value) {
        for(Object item : this.arr){
            if(Objects.equals(value, (E)item)){
                return true;
            }
        }
        return false;
    }

    // TODO: return first index of value, or -1 if not found
    @SuppressWarnings("unchecked")
    public int indexOf(E value) {
        for(int index = 0; index < this.capacity; index++){
            Object item = this.arr[index];
            if(Objects.equals(value, (E)item)){
                return index;
            }
        }
        return -1;
    }

    // TODO: return string like "[1, 2, 3, 4]" or "[a, b, null, d]"
    @Override
    public String toString() {
        String output = "[";

        for(Object item : this.arr){
            if(item == null){
                output += "null";
            }else{
                output += item.toString();
            }
            output += ", ";
        }
        if(this.capacity > 0){
            output = output.substring(0, output.length()-2);
        }
        output += "]";
        return output;
    }
}

class Static_array_Main {
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
        // --- Integer array ---
        Static_array<Integer> arr = new Static_array<>(5);
        checkEquals("size of capacity-5 array", 5, arr.size());

        arr.set(0, 10);
        arr.set(1, 20);
        arr.set(4, 50);
        checkEquals("get(0) after set(0,10)", 10, arr.get(0));
        checkEquals("get(1) after set(1,20)", 20, arr.get(1));
        checkEquals("get(4) after set(4,50)", 50, arr.get(4));

        checkEquals("toString format", "[10, 20, null, null, 50]", arr.toString());

        checkEquals("contains(20) present", true,  arr.contains(20));
        checkEquals("contains(99) absent",  false, arr.contains(99));

        checkEquals("indexOf(50) present",  4,  arr.indexOf(50));
        checkEquals("indexOf(99) absent",  -1,  arr.indexOf(99));

        final Static_array<Integer> a6 = arr;
        checkThrows("get(10) on size-5 throws OOB",   IndexOutOfBoundsException.class, () -> a6.get(10));
        checkThrows("set(-1,5) on size-5 throws OOB", IndexOutOfBoundsException.class, () -> a6.set(-1, 5));

        // --- Edge: capacity 1 ---
        Static_array<Integer> single = new Static_array<>(1);
        single.set(0, 42);
        checkEquals("capacity-1 get(0)", 42, single.get(0));

        // --- Edge: capacity 0 ---
        Static_array<Integer> empty = new Static_array<>(0);
        checkEquals("capacity-0 size",        0,     empty.size());
        checkEquals("capacity-0 toString",    "[]",  empty.toString());
        checkEquals("capacity-0 contains(0)", false, empty.contains(0));

        // --- String array (exercises generics) ---
        Static_array<String> words = new Static_array<>(3);
        words.set(0, "alpha");
        words.set(1, "beta");
        words.set(2, "gamma");
        checkEquals("string get(1)",        "beta",                   words.get(1));
        checkEquals("string contains",      true,                     words.contains("gamma"));
        checkEquals("string indexOf",       0,                        words.indexOf("alpha"));
        checkEquals("string toString",      "[alpha, beta, gamma]",   words.toString());

        // --- Null handling (must use Objects.equals, not ==) ---
        Static_array<String> withNull = new Static_array<>(3);
        withNull.set(0, "x");
        withNull.set(2, "z");  // index 1 stays null
        checkEquals("contains(null) present", true,  withNull.contains(null));
        checkEquals("indexOf(null) present",  1,     withNull.indexOf(null));
        checkEquals("null toString",          "[x, null, z]", withNull.toString());

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}