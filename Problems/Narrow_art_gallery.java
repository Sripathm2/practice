package Problems;
import java.util.Objects;

// Narrow Art Gallery (Kattis "narrowartgallery", 2014 NAQ).
// A gallery has N rows of 2 rooms (left, right). Exactly k rooms must be closed.
// To keep the gallery navigable you may NOT close two rooms in the same row, nor
// two rooms in adjacent rows that touch diagonally (left in one row and right in
// the next, or vice versa). Choose the k rooms to close so that the total value
// of the rooms left OPEN is maximized; return that maximum open value.
//
// DP over rows with state = which side (if any) was closed in the previous row:
//   none  -> next row may close none / left / right
//   left  -> next row may close none / left   (not right: diagonal block)
//   right -> next row may close none / right   (not left: diagonal block)
public class Narrow_art_gallery {

    // rooms[i][0] = left value, rooms[i][1] = right value (0 <= v).
    // Return the maximum total value of open rooms after closing exactly k rooms.
    // Throw NullPointerException if rooms or any row is null.
    // Throw IllegalArgumentException if any row's length != 2, k < 0, or k > rooms.length.
    public static int maxOpenValue(int[][] rooms, int k) {
        return 0;
    }
}

class Narrow_art_gallery_Main {
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

    // Independent brute force: try every way to pick a closed side per row
    // (none / left / right) with exactly k closures, enforcing the constraints.
    private static int brute(int[][] rooms, int k) {
        return rec(rooms, k, 0, -1);   // prevSide: -1 none, 0 left, 1 right
    }
    private static int rec(int[][] rooms, int remaining, int row, int prevSide) {
        int n = rooms.length;
        if (row == n) return remaining == 0 ? 0 : Integer.MIN_VALUE;
        int best = Integer.MIN_VALUE;
        // close none this row -> both open
        int a = rec(rooms, remaining, row + 1, -1);
        if (a != Integer.MIN_VALUE) best = Math.max(best, rooms[row][0] + rooms[row][1] + a);
        if (remaining > 0) {
            // close left (prev must be none or left) -> right open
            if (prevSide != 1) {
                int b = rec(rooms, remaining - 1, row + 1, 0);
                if (b != Integer.MIN_VALUE) best = Math.max(best, rooms[row][1] + b);
            }
            // close right (prev must be none or right) -> left open
            if (prevSide != 0) {
                int c = rec(rooms, remaining - 1, row + 1, 1);
                if (c != Integer.MIN_VALUE) best = Math.max(best, rooms[row][0] + c);
            }
        }
        return best;
    }

    public static void main(String[] args) {
        // --- The three Kattis samples (outputs verified via DP + subset brute) ---
        int[][] s1 = {{3,1},{2,1},{1,2},{1,3},{3,3},{0,0}};
        checkEquals("sample 1 (N=6,k=4)", 17, Narrow_art_gallery.maxOpenValue(s1, 4));
        int[][] s2 = {{3,4},{1,1},{1,1},{5,6}};
        checkEquals("sample 2 (N=4,k=3)", 17, Narrow_art_gallery.maxOpenValue(s2, 3));
        int[][] s3 = {{7,8},{4,9},{3,7},{5,9},{7,2},{10,3},{0,10},{3,2},{6,3},{7,9}};
        checkEquals("sample 3 (N=10,k=5)", 102, Narrow_art_gallery.maxOpenValue(s3, 5));

        // --- k = 0 -> nothing closed, all open ---
        int[][] g = {{1,2},{3,4},{5,6}};
        checkEquals("k=0 -> total", 21, Narrow_art_gallery.maxOpenValue(g, 0));

        // --- k = N -> exactly one per row, same-side-adjacency forced ---
        int[][] two = {{3,1},{1,3}};
        checkEquals("k=N same-side", 4, Narrow_art_gallery.maxOpenValue(two, 2));  // both-left or both-right
        checkEquals("k=1 close cheapest", 7, Narrow_art_gallery.maxOpenValue(two, 1));

        // --- Brute-force cross-check on random small galleries ---
        java.util.Random rng = new java.util.Random(71);
        boolean ok = true;
        for (int t = 0; t < 400 && ok; t++) {
            int n = 1 + rng.nextInt(7);
            int[][] rooms = new int[n][2];
            for (int i = 0; i < n; i++) { rooms[i][0] = rng.nextInt(10); rooms[i][1] = rng.nextInt(10); }
            int k = rng.nextInt(n + 1);
            if (Narrow_art_gallery.maxOpenValue(rooms, k) != brute(rooms, k)) ok = false;
        }
        if (ok) { passed++; System.out.println("PASS: brute-force cross-check"); }
        else    { failed++; System.out.println("FAIL: brute-force cross-check"); }

        // --- Validation ---
        checkThrows("null rooms", NullPointerException.class,
                () -> Narrow_art_gallery.maxOpenValue(null, 0));
        checkThrows("null row", NullPointerException.class,
                () -> Narrow_art_gallery.maxOpenValue(new int[][]{{1,2}, null}, 0));
        checkThrows("row length != 2", IllegalArgumentException.class,
                () -> Narrow_art_gallery.maxOpenValue(new int[][]{{1,2,3}}, 0));
        checkThrows("k < 0", IllegalArgumentException.class,
                () -> Narrow_art_gallery.maxOpenValue(g, -1));
        checkThrows("k > N", IllegalArgumentException.class,
                () -> Narrow_art_gallery.maxOpenValue(g, 4));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}