package Problems;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

// Magical Cows (dynamic programming).
// A farm holds cows, up to a capacity C. Every night a farm with v cows doubles
// to 2v; if 2v <= C it becomes ONE farm of 2v, otherwise it splits into TWO
// farms of v cows each. Given the initial farms and a set of query days, report
// the total number of farms after each queried number of nights.
//
// Key idea: don't track individual farms (they explode). Track cnt[v] = how many
// farms currently hold v cows, for v in 1..C, and advance that vector one night
// at a time in O(C).
public class Magical_cows {

    // For each day in queryDays, return the total number of farms after that many nights.
    // Throw NullPointerException if initialCows or queryDays is null.
    // Throw IllegalArgumentException if capacity < 1, any initial count is < 1 or > capacity,
    // or any query day is < 0.
    public static long[] solve(int capacity, int[] initialCows, int[] queryDays) {
        if(initialCows == null || queryDays == null){
            throw new NullPointerException();
        }else if(capacity < 1){
            throw new IllegalArgumentException();
        }
        
        int max_query_day = -1;
        for(int index = 0;index < queryDays.length; index++){
            if(queryDays[index] < 0){
                throw new IllegalArgumentException();
            }
            if(queryDays[index] > max_query_day){
                max_query_day = queryDays[index];
            }
        }

        long [][] farm_count = new long[max_query_day + 1][capacity+1]; 
        for(int index = 0;index < initialCows.length; index++){
            if(initialCows[index] < 1 || initialCows[index] > capacity){
                throw new IllegalArgumentException();
            }
            farm_count[0][initialCows[index]] += 1;
        }

        for(int i = 0; i + 1 < farm_count.length; i++){
            for(int j = 1; j < farm_count[0].length; j++){
                if(j*2 <= capacity){
                    farm_count[i+1][j*2] += farm_count[i][j];
                }else{
                    farm_count[i+1][j] += farm_count[i][j]*2;
                }
            }
        }

        long[] returnv = new long[queryDays.length];
        for(int i = 0; i < queryDays.length ; i++){
            long sum = 0;
            for(int j=0; j<farm_count[0].length; j++){
                sum += farm_count[queryDays[i]][j];
            }
            returnv[i] = sum;
        }

        return returnv;
    }
}

class Magical_cows_Main {
    private static int passed = 0;
    private static int failed = 0;

    private static void checkArrayEquals(String name, long[] expected, long[] actual) {
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

    // Direct (exponential) simulation for small cases — the ground truth.
    private static long bruteFarms(int capacity, int[] initial, int days) {
        List<Integer> farms = new ArrayList<>();
        for (int v : initial) farms.add(v);
        for (int d = 0; d < days; d++) {
            List<Integer> next = new ArrayList<>();
            for (int v : farms) {
                int nv = 2 * v;
                if (nv <= capacity) next.add(nv);
                else { next.add(v); next.add(v); }
            }
            farms = next;
        }
        return farms.size();
    }
    private static long[] bruteSolve(int capacity, int[] initial, int[] days) {
        long[] out = new long[days.length];
        for (int i = 0; i < days.length; i++) out[i] = bruteFarms(capacity, initial, days[i]);
        return out;
    }

    public static void main(String[] args) {
        // --- C=1: everything splits every night, farms double each day ---
        // {1} -> day0 1, day1 2, day2 4, day3 8
        checkArrayEquals("C=1 doubling",
                new long[]{1, 2, 4, 8},
                Magical_cows.solve(1, new int[]{1}, new int[]{0, 1, 2, 3}));

        // --- C=2, {1}: 1->2 (grows), 2->split ---
        // day0 {1}=1, day1 {2}=1, day2 {2,2}=2, day3 =4
        checkArrayEquals("C=2 grow then split",
                new long[]{1, 1, 2, 4},
                Magical_cows.solve(2, new int[]{1}, new int[]{0, 1, 2, 3}));

        // --- Multiple farms ---
        checkArrayEquals("multiple farms C=4",
                bruteSolve(4, new int[]{1, 2, 3, 4}, new int[]{0, 1, 2, 3, 4, 5}),
                Magical_cows.solve(4, new int[]{1, 2, 3, 4}, new int[]{0, 1, 2, 3, 4, 5}));

        // --- Day 0 returns the initial farm count ---
        checkArrayEquals("day 0 = initial count",
                new long[]{3},
                Magical_cows.solve(10, new int[]{5, 5, 5}, new int[]{0}));

        // --- Queries out of order / repeated ---
        checkArrayEquals("unordered queries",
                bruteSolve(3, new int[]{1, 1}, new int[]{3, 0, 2, 2}),
                Magical_cows.solve(3, new int[]{1, 1}, new int[]{3, 0, 2, 2}));

        // --- Validation ---
        checkThrows("null initial", NullPointerException.class,
                () -> Magical_cows.solve(5, null, new int[]{0}));
        checkThrows("null queries", NullPointerException.class,
                () -> Magical_cows.solve(5, new int[]{1}, null));
        checkThrows("capacity < 1", IllegalArgumentException.class,
                () -> Magical_cows.solve(0, new int[]{1}, new int[]{0}));
        checkThrows("cow count > capacity", IllegalArgumentException.class,
                () -> Magical_cows.solve(3, new int[]{4}, new int[]{0}));
        checkThrows("cow count < 1", IllegalArgumentException.class,
                () -> Magical_cows.solve(3, new int[]{0}, new int[]{0}));
        checkThrows("negative day", IllegalArgumentException.class,
                () -> Magical_cows.solve(3, new int[]{1}, new int[]{-1}));

        // --- Brute-force cross-check on small random instances ---
        java.util.Random rng = new java.util.Random(51);
        boolean ok = true;
        for (int t = 0; t < 300 && ok; t++) {
            int C = 2 + rng.nextInt(5);                 // 2..6
            int f = 1 + rng.nextInt(4);                 // 1..4 farms
            int[] init = new int[f];
            for (int i = 0; i < f; i++) init[i] = 1 + rng.nextInt(C);   // 1..C
            int[] days = new int[1 + rng.nextInt(4)];
            for (int i = 0; i < days.length; i++) days[i] = rng.nextInt(9);  // 0..8
            if (!Arrays.equals(bruteSolve(C, init, days), Magical_cows.solve(C, init, days))) ok = false;
        }
        if (ok) { passed++; System.out.println("PASS: brute-force cross-check"); }
        else    { failed++; System.out.println("FAIL: brute-force cross-check"); }

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}