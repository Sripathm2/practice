package Algorithms;
import java.util.Arrays;

// Merge sort on a 1D array (divide and conquer): split the array in half,
// recursively sort each half, then merge the two sorted halves.
// Non-destructive: returns a new sorted array; the input is not modified.
public class Merge_sort {

    // Return a new array holding arr's elements in ascending order.
    // The input array is not modified.
    // Throw NullPointerException if arr is null.
    public static int[] sort(int[] arr) {
        if(arr == null){
            throw new NullPointerException();
        } if(arr.length == 0){
            return new int[0];
        }
        return mergesort(0,arr.length-1, arr);
    }

    private static int[] mergesort(int lo, int hi, int[] arr){
        if(lo == hi){
            int[] returnv = new int[1];
            returnv[0] = arr[lo];
            return returnv;
        }

        int mid = (lo+hi)/2;

        int[] left = mergesort(lo, mid, arr);
        int[] right = mergesort(mid+1, hi, arr);

        return merge(left,right);
    }

    private static int[] merge(int[] left, int[] right){
        int [] merged = new int[left.length+right.length];
        int mindex = 0;
        int lindex = 0;
        int rindex = 0;
        while(mindex < merged.length){
            if(lindex == left.length){
                merged[mindex] = right[rindex];
                rindex += 1;
            }else if(rindex == right.length){
                merged[mindex] = left[lindex];
                lindex += 1;
            }else if(left[lindex]<=right[rindex]){
                merged[mindex] = left[lindex];
                lindex += 1;
            }else{
                merged[mindex] = right[rindex];
                rindex += 1;
            }
            mindex += 1;
        }
        return merged;
    }
}

class Merge_sort_Main {
    private static int passed = 0;
    private static int failed = 0;

    private static void checkArrayEquals(String name, int[] expected, int[] actual) {
        if (Arrays.equals(expected, actual)) {
            passed++;
            System.out.println("PASS: " + name);
        } else {
            failed++;
            System.out.println("FAIL: " + name + " — expected " + Arrays.toString(expected)
                    + ", got " + Arrays.toString(actual));
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
        // --- Base cases ---
        checkArrayEquals("empty",  new int[]{},  Merge_sort.sort(new int[]{}));
        checkArrayEquals("single", new int[]{5}, Merge_sort.sort(new int[]{5}));

        // --- Small ---
        checkArrayEquals("two swap",   new int[]{1, 2}, Merge_sort.sort(new int[]{2, 1}));
        checkArrayEquals("two sorted", new int[]{1, 2}, Merge_sort.sort(new int[]{1, 2}));

        // --- General ---
        checkArrayEquals("unsorted",   new int[]{1, 2, 3, 4, 5}, Merge_sort.sort(new int[]{3, 1, 4, 5, 2}));
        checkArrayEquals("reverse",    new int[]{1, 2, 3, 4, 5}, Merge_sort.sort(new int[]{5, 4, 3, 2, 1}));
        checkArrayEquals("already sorted", new int[]{1, 2, 3, 4}, Merge_sort.sort(new int[]{1, 2, 3, 4}));

        // --- Duplicates ---
        checkArrayEquals("duplicates", new int[]{1, 1, 2, 3, 3}, Merge_sort.sort(new int[]{3, 1, 2, 3, 1}));
        checkArrayEquals("all equal",  new int[]{7, 7, 7},       Merge_sort.sort(new int[]{7, 7, 7}));

        // --- Negatives / mixed ---
        checkArrayEquals("negatives", new int[]{-9, -5, -1, 0, 3, 8},
                Merge_sort.sort(new int[]{3, -1, -9, 8, 0, -5}));

        // --- Odd and even lengths ---
        checkArrayEquals("odd length",  new int[]{1, 2, 3, 4, 5, 6, 7}, Merge_sort.sort(new int[]{7, 3, 5, 1, 6, 2, 4}));
        checkArrayEquals("even length", new int[]{1, 2, 3, 4, 5, 6},    Merge_sort.sort(new int[]{6, 2, 4, 1, 5, 3}));

        // --- Input must NOT be modified ---
        int[] orig = {4, 2, 5, 1, 3};
        int[] snapshot = orig.clone();
        Merge_sort.sort(orig);
        checkArrayEquals("input unchanged", snapshot, orig);

        // --- Null ---
        checkThrows("null", NullPointerException.class, () -> Merge_sort.sort(null));

        // --- Brute-force cross-check vs Arrays.sort on random arrays ---
        java.util.Random rng = new java.util.Random(41);
        boolean ok = true, unmodified = true;
        for (int t = 0; t < 500 && ok; t++) {
            int[] a = new int[rng.nextInt(80)];
            for (int i = 0; i < a.length; i++) a[i] = rng.nextInt(401) - 200;
            int[] before = a.clone();
            int[] expected = a.clone();
            Arrays.sort(expected);
            int[] got = Merge_sort.sort(a);
            if (!Arrays.equals(expected, got)) ok = false;
            if (!Arrays.equals(before, a)) unmodified = false;   // input preserved
        }
        if (ok) { passed++; System.out.println("PASS: brute-force cross-check"); }
        else    { failed++; System.out.println("FAIL: brute-force cross-check"); }
        if (unmodified) { passed++; System.out.println("PASS: brute-force input preserved"); }
        else            { failed++; System.out.println("FAIL: brute-force input preserved"); }

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}