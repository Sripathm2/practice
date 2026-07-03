package Algorithms;
import java.util.Objects;

// Find the minimum value in a 1D array using a THREE-WAY divide-and-conquer
// split: divide the range into three parts, recursively find each part's min,
// and combine by taking the min of the three.
public class Three_way_min {

    // Return the minimum value in arr.
    // Throw NullPointerException if arr is null.
    // Throw IllegalArgumentException if arr is empty.
    public static int min(int[] arr) {
        if(arr == null){
            throw new NullPointerException();
        }else if (arr.length < 1){
            throw new IllegalArgumentException();
        }
        return min(0, arr.length-1, arr);
    }

    private static int min(int l, int r, int []arr){
        if(l==r){
            return arr[l];
        }else if(l+1 == r){
            return Math.min(arr[l], arr[r]);
        }else{
            int len = r-l+1;
            int m1 = l + (len/3);
            int m2 = l + 2*(len/3);
            int left = min(l, m1-1, arr);
            int mid = min(m1, m2-1, arr);
            int right = min(m2-1, r, arr);
            if(left <= mid && left <= right){
                return left;
            }else if(mid <= left && mid <= right){
                return mid;
            }else{
                return right;
            }
        }
    }
}

class Three_way_min_Main {
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

    private static int bruteMin(int[] a) {
        int m = a[0];
        for (int x : a) if (x < m) m = x;
        return m;
    }

    public static void main(String[] args) {
        // --- Single ---
        checkEquals("single", 5, Three_way_min.min(new int[]{5}));

        // --- Small sizes (exercise the base cases around the 3-way split) ---
        checkEquals("two",   2, Three_way_min.min(new int[]{9, 2}));
        checkEquals("three", 1, Three_way_min.min(new int[]{3, 2, 1}));
        checkEquals("four",  1, Three_way_min.min(new int[]{4, 1, 3, 2}));

        // --- Min located in each third ---
        checkEquals("min in first third",  0, Three_way_min.min(new int[]{0, 8, 7, 6, 5, 4}));
        checkEquals("min in middle third", 0, Three_way_min.min(new int[]{9, 8, 0, 6, 5, 4}));
        checkEquals("min in last third",   0, Three_way_min.min(new int[]{9, 8, 7, 6, 5, 0}));

        // --- Sorted both ways ---
        checkEquals("ascending",  1, Three_way_min.min(new int[]{1, 2, 3, 4, 5}));
        checkEquals("descending", 1, Three_way_min.min(new int[]{5, 4, 3, 2, 1}));

        // --- Duplicates, negatives, all-equal ---
        checkEquals("duplicates", 1, Three_way_min.min(new int[]{2, 2, 1, 1, 3}));
        checkEquals("negatives", -7, Three_way_min.min(new int[]{-3, -7, -1, -2}));
        checkEquals("all equal",  4, Three_way_min.min(new int[]{4, 4, 4, 4}));

        // --- Size not divisible by 3 ---
        checkEquals("size 7", 1, Three_way_min.min(new int[]{5, 3, 8, 1, 9, 2, 7}));
        checkEquals("size 8", -4, Three_way_min.min(new int[]{5, 3, 8, 1, 9, 2, 7, -4}));

        // --- Errors ---
        checkThrows("null",  NullPointerException.class,     () -> Three_way_min.min(null));
        checkThrows("empty", IllegalArgumentException.class, () -> Three_way_min.min(new int[]{}));

        // --- Brute-force cross-check on random arrays of varied lengths ---
        java.util.Random rng = new java.util.Random(31);
        boolean ok = true;
        for (int t = 0; t < 500 && ok; t++) {
            int[] a = new int[1 + rng.nextInt(60)];
            for (int i = 0; i < a.length; i++) a[i] = rng.nextInt(2001) - 1000;
            if (Three_way_min.min(a) != bruteMin(a)) ok = false;
        }
        if (ok) { passed++; System.out.println("PASS: brute-force cross-check"); }
        else    { failed++; System.out.println("FAIL: brute-force cross-check"); }

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}