package Algorithms;
import java.util.Objects;

// Find the maximum value in a 2D array AND its (row, col) position, recursively.
// Ties resolve to the first occurrence in ROW-MAJOR order (row 0 left-to-right,
// then row 1, ...). Assumes a rectangular, non-empty grid.
public class Recursive_max_2d {

    // Bundles the three values returned together.
    public static final class Result {
        public final int value;
        public final int row;
        public final int col;
        public Result(int value, int row, int col) { this.value = value; this.row = row; this.col = col; }
        @Override public boolean equals(Object o) {
            if (!(o instanceof Result)) return false;
            Result r = (Result) o;
            return value == r.value && row == r.row && col == r.col;
        }
        @Override public int hashCode() { return Objects.hash(value, row, col); }
        @Override public String toString() { return "(value=" + value + ", row=" + row + ", col=" + col + ")"; }
    }

    // Return the max value and its first row-major position.
    // Throw NullPointerException if grid or any row is null.
    // Throw IllegalArgumentException if the grid has no elements.
    public static Result maxWithIndex(int[][] grid) {
        if(grid == null){
            throw new NullPointerException();
        } if (grid.length == 0 || grid[0].length == 0){
            throw new  IllegalArgumentException();
        }
        for(int index = 0; index <grid.length; index ++){
            if(grid[index] == null){
                throw new NullPointerException();
            }
        }
        return maxWithIndex(0, 0, grid);
    }

    private static Result maxWithIndex(int i, int j, int[][] grid){
        if(i >= grid.length){
            return new Result(Integer.MIN_VALUE, -1, -1);
        }else{
            int newi = i;
            int newj = j+1;
            if(newj == grid[0].length){
                newj = 0;
                newi += 1;
            }
            Result returnv = maxWithIndex(newi, newj, grid);
            if(returnv.value <= grid[i][j]){
                return new Result(grid[i][j], i, j);
            }else{
                return returnv;
            }
        }
    }


}

class Recursive_max_2d_Main {
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

    private static Recursive_max_2d.Result res(int v, int r, int c) {
        return new Recursive_max_2d.Result(v, r, c);
    }

    public static void main(String[] args) {
        // --- Single cell ---
        checkEquals("single", res(7, 0, 0),
                Recursive_max_2d.maxWithIndex(new int[][]{{7}}));

        // --- Max at corners ---
        checkEquals("max bottom-right", res(4, 1, 1),
                Recursive_max_2d.maxWithIndex(new int[][]{{1, 2}, {3, 4}}));
        checkEquals("max top-left", res(4, 0, 0),
                Recursive_max_2d.maxWithIndex(new int[][]{{4, 3}, {2, 1}}));

        // --- Max in the interior ---
        checkEquals("max interior", res(9, 1, 0),
                Recursive_max_2d.maxWithIndex(new int[][]{{1, 2, 3}, {9, 5, 6}, {7, 8, 4}}));

        // --- Ties resolve row-major first ---
        checkEquals("tie across rows -> earlier row", res(5, 0, 0),
                Recursive_max_2d.maxWithIndex(new int[][]{{5, 1}, {5, 2}}));
        checkEquals("tie within a row -> earlier col", res(5, 0, 1),
                Recursive_max_2d.maxWithIndex(new int[][]{{1, 5, 5}}));

        // --- Negatives ---
        checkEquals("negatives", res(-1, 0, 1),
                Recursive_max_2d.maxWithIndex(new int[][]{{-3, -1}, {-2, -5}}));

        // --- Non-square (rectangular) ---
        checkEquals("wide grid", res(8, 0, 3),
                Recursive_max_2d.maxWithIndex(new int[][]{{2, 4, 6, 8}, {1, 3, 5, 7}}));
        checkEquals("tall grid", res(9, 2, 0),
                Recursive_max_2d.maxWithIndex(new int[][]{{1, 2}, {3, 4}, {9, 5}}));

        // --- Errors ---
        checkThrows("null grid", NullPointerException.class,
                () -> Recursive_max_2d.maxWithIndex(null));
        checkThrows("null row", NullPointerException.class,
                () -> Recursive_max_2d.maxWithIndex(new int[][]{{1}, null}));
        checkThrows("no rows", IllegalArgumentException.class,
                () -> Recursive_max_2d.maxWithIndex(new int[][]{}));
        checkThrows("empty row (no elements)", IllegalArgumentException.class,
                () -> Recursive_max_2d.maxWithIndex(new int[][]{{}}));

        // --- Brute-force cross-check on random rectangular grids ---
        java.util.Random rng = new java.util.Random(21);
        boolean ok = true;
        for (int t = 0; t < 300 && ok; t++) {
            int rows = 1 + rng.nextInt(8), cols = 1 + rng.nextInt(8);
            int[][] g = new int[rows][cols];
            int bv = Integer.MIN_VALUE, br = 0, bc = 0;
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++) {
                    g[r][c] = rng.nextInt(201) - 100;
                    if (g[r][c] > bv) { bv = g[r][c]; br = r; bc = c; }  // '>' = row-major first
                }
            if (!res(bv, br, bc).equals(Recursive_max_2d.maxWithIndex(g))) ok = false;
        }
        if (ok) { passed++; System.out.println("PASS: brute-force cross-check"); }
        else    { failed++; System.out.println("FAIL: brute-force cross-check"); }

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}