package Algorithms;
import java.util.Objects;

// Iterate an array using recursion instead of a loop: process one element,
// recurse on the rest. Two examples of the same pattern — sum all, and sum
// only the odd-valued elements.
public class Recursive_list_sum {

    // Return the sum of all elements, computed recursively. Empty array -> 0.
    // Throw NullPointerException if arr is null.
    public static long sum(int[] arr) {
        return sum(0, arr);
    }

    private static long sum(int index, int[] arr){
        if(index == arr.length){
            return 0;
        }
        return sum(index + 1, arr) + arr[index];
    }


    // Return the sum of the odd-valued elements, computed recursively. Empty -> 0.
    // Throw NullPointerException if arr is null.
    public static long sumOdd(int[] arr) {
        return sumOdd(0, arr);
    }

    private static long sumOdd(int index, int[] arr){
        if(index == arr.length){
            return 0;
        }
        if(arr[index] % 2 == 0){
            return sumOdd(index + 1, arr);
        }else{
            return sumOdd(index + 1, arr) + arr[index];
        }
        
    }

}

class Recursive_list_sum_Main {
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

    private static long bruteSum(int[] a) {
        long s = 0;
        for (int x : a) s += x;
        return s;
    }
    private static long bruteSumOdd(int[] a) {
        long s = 0;
        for (int x : a) if (x % 2 != 0) s += x;
        return s;
    }

    public static void main(String[] args) {
        // --- Empty ---
        checkEquals("sum empty",    0L, Recursive_list_sum.sum(new int[]{}));
        checkEquals("sumOdd empty", 0L, Recursive_list_sum.sumOdd(new int[]{}));

        // --- Single ---
        checkEquals("sum {5}",     5L, Recursive_list_sum.sum(new int[]{5}));
        checkEquals("sumOdd {5}",  5L, Recursive_list_sum.sumOdd(new int[]{5}));
        checkEquals("sum {4}",     4L, Recursive_list_sum.sum(new int[]{4}));
        checkEquals("sumOdd {4}",  0L, Recursive_list_sum.sumOdd(new int[]{4}));

        // --- Mixed ---
        int[] m = {1, 2, 3, 4, 5};
        checkEquals("sum {1..5}",    15L, Recursive_list_sum.sum(m));
        checkEquals("sumOdd {1..5}", 9L,  Recursive_list_sum.sumOdd(m));   // 1+3+5

        int[] evens = {2, 4, 6, 8};
        checkEquals("sum evens",    20L, Recursive_list_sum.sum(evens));
        checkEquals("sumOdd evens", 0L,  Recursive_list_sum.sumOdd(evens));

        // --- Negatives (odd test must hold for negatives: -3 is odd) ---
        int[] neg = {-1, -2, -3, -4};
        checkEquals("sum negatives",    -10L, Recursive_list_sum.sum(neg));
        checkEquals("sumOdd negatives", -4L,  Recursive_list_sum.sumOdd(neg));  // -1 + -3

        // --- Null safety ---
        checkThrows("sum null",    NullPointerException.class, () -> Recursive_list_sum.sum(null));
        checkThrows("sumOdd null", NullPointerException.class, () -> Recursive_list_sum.sumOdd(null));

        // --- Brute-force cross-check on random arrays ---
        java.util.Random rng = new java.util.Random(11);
        boolean ok = true;
        for (int t = 0; t < 200 && ok; t++) {
            int[] a = new int[rng.nextInt(50)];
            for (int i = 0; i < a.length; i++) a[i] = rng.nextInt(2001) - 1000;
            if (Recursive_list_sum.sum(a) != bruteSum(a))       ok = false;
            if (Recursive_list_sum.sumOdd(a) != bruteSumOdd(a)) ok = false;
        }
        if (ok) { passed++; System.out.println("PASS: brute-force cross-check"); }
        else    { failed++; System.out.println("FAIL: brute-force cross-check"); }

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}