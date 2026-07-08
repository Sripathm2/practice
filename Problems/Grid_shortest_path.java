package Problems;
import java.util.Objects;

import Data_structures.Queue;

// Grid Shortest Path (BFS on an implicit graph).
// Given a rectangular grid where 0 = open and 1 = wall, find the fewest 4-directional
// moves (up/down/left/right) from a start cell to a target cell, stepping only on
// open cells. The cells are the graph's vertices and adjacencies between open cells
// are its edges; since every move costs 1, BFS gives the shortest path.
public class Grid_shortest_path {

    // Return the minimum number of moves from (sr,sc) to (tr,tc), or -1 if the
    // target is unreachable (including when the start or target cell is a wall).
    // A start equal to the target (and open) costs 0.
    // Throw NullPointerException if grid or any row is null.
    // Throw IllegalArgumentException if grid is empty or not rectangular.
    // Throw IndexOutOfBoundsException if a start/target coordinate is off the grid.
    public static int shortestPath(int[][] grid, int sr, int sc, int tr, int tc) {
        if(grid == null){
            throw new NullPointerException();
        }

        if(grid.length == 0 || grid[0].length == 0){
            throw new IllegalArgumentException();
        }

        for(int i=0; i < grid.length; i++){
            if(grid[i] == null)
                throw new NullPointerException();
            if(grid[i].length != grid[0].length)
                throw new IllegalArgumentException();
        }
        if(sr < 0 || sc < 0 || tr < 0 || tc < 0 || sr >= grid.length || tr >= grid.length || sc >= grid[0].length || tc >= grid[0].length){
            throw new IndexOutOfBoundsException();
        }

        if(grid[sr][sc] == 1 || grid[tr][tc] == 1){
            return -1;
        }

        Queue<Integer> r_index = new Queue<Integer>();
        Queue<Integer> c_index = new Queue<Integer>();

        int[] moves_r = new int[]{-1, 1, 0, 0};
        int[] moves_c = new int[]{0, 0, 1, -1};

        boolean[][] visited = new boolean[grid.length][grid[0].length];
        int[][] dist = new int[grid.length][grid[0].length];
        
        for(int i =0;i <dist.length; i++){
            for(int j =0; j < dist[0].length; j++){
                dist[i][j] = -1;
            }
        }

        r_index.enqueue(sr);
        c_index.enqueue(sc);
        dist[sr][sc] = 0;

        while(!r_index.isEmpty()){
            int r = (int)r_index.dequeue();
            int c = (int)c_index.dequeue();
            if(!visited[r][c]){
                visited[r][c] = true;

                for(int i = 0; i < moves_r.length; i++){
                    int tempr = r + moves_r[i];
                    int tempc = c + moves_c[i];
                    if(tempr < 0 || tempc < 0 || tempr >= grid.length || tempc >= grid[0].length || grid[tempr][tempc] == 1){
                        continue;
                    }

                    if(dist[tempr][tempc] == -1){
                        dist[tempr][tempc]= dist[r][c]+1;
                    }
                    r_index.enqueue(tempr);
                    c_index.enqueue(tempc);
                }
            }
        }

        if(dist[tr][tc] == -1){
            return -1;
        }        
        
        return dist[tr][tc];
    }
}

class Grid_shortest_path_Main {
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

    // Independent ground truth: repeated relaxation (Bellman-Ford style, unit
    // edges). Different code path from a BFS queue, so it cross-checks the answer.
    private static int relaxSolve(int[][] g, int sr, int sc, int tr, int tc) {
        int R = g.length, C = g[0].length;
        if (g[sr][sc] == 1 || g[tr][tc] == 1) return -1;
        int INF = Integer.MAX_VALUE / 2;
        int[][] dist = new int[R][C];
        for (int[] row : dist) java.util.Arrays.fill(row, INF);
        dist[sr][sc] = 0;
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        for (int iter = 0; iter < R * C; iter++) {
            boolean changed = false;
            for (int r = 0; r < R; r++)
                for (int c = 0; c < C; c++) {
                    if (g[r][c] == 1 || dist[r][c] == INF) continue;
                    for (int[] d : dirs) {
                        int nr = r + d[0], nc = c + d[1];
                        if (nr < 0 || nr >= R || nc < 0 || nc >= C || g[nr][nc] == 1) continue;
                        if (dist[r][c] + 1 < dist[nr][nc]) { dist[nr][nc] = dist[r][c] + 1; changed = true; }
                    }
                }
            if (!changed) break;
        }
        return dist[tr][tc] >= INF ? -1 : dist[tr][tc];
    }

    public static void main(String[] args) {
        // --- Open 3x3: (0,0) -> (2,2) is 4 moves ---
        int[][] open = {{0,0,0},{0,0,0},{0,0,0}};
        checkEquals("open corner-to-corner", 4, Grid_shortest_path.shortestPath(open, 0,0, 2,2));

        // --- Wall forces a detour: down is blocked, go around ---
        int[][] wall = {
            {0,0,0},
            {1,1,0},
            {0,0,0},
        };
        checkEquals("detour around wall", 6, Grid_shortest_path.shortestPath(wall, 0,0, 2,0));

        // --- Unreachable: start boxed in ---
        int[][] boxed = {
            {0,1,0},
            {1,1,0},
            {0,1,0},
        };
        checkEquals("unreachable", -1, Grid_shortest_path.shortestPath(boxed, 0,0, 0,2));

        // --- start == target ---
        checkEquals("same cell", 0, Grid_shortest_path.shortestPath(open, 1,1, 1,1));

        // --- start / target on a wall ---
        checkEquals("start is wall", -1, Grid_shortest_path.shortestPath(wall, 1,0, 2,2));
        checkEquals("target is wall", -1, Grid_shortest_path.shortestPath(wall, 0,0, 1,1));

        // --- single cell ---
        checkEquals("1x1 open", 0, Grid_shortest_path.shortestPath(new int[][]{{0}}, 0,0, 0,0));

        // --- Cross-check vs relaxation on random small grids ---
        java.util.Random rng = new java.util.Random(121);
        boolean ok = true;
        for (int t = 0; t < 400 && ok; t++) {
            int R = 1 + rng.nextInt(5), C = 1 + rng.nextInt(5);
            int[][] g = new int[R][C];
            for (int r = 0; r < R; r++)
                for (int c = 0; c < C; c++)
                    g[r][c] = rng.nextInt(4) == 0 ? 1 : 0;   // ~25% walls
            int sr = rng.nextInt(R), sc = rng.nextInt(C), tr = rng.nextInt(R), tc = rng.nextInt(C);
            int expected = relaxSolve(g, sr, sc, tr, tc);
            if (Grid_shortest_path.shortestPath(g, sr, sc, tr, tc) != expected) ok = false;
        }
        if (ok) { passed++; System.out.println("PASS: cross-check vs relaxation"); }
        else    { failed++; System.out.println("FAIL: cross-check vs relaxation"); }

        // --- Validation ---
        checkThrows("null grid", NullPointerException.class,
                () -> Grid_shortest_path.shortestPath(null, 0,0, 0,0));
        checkThrows("null row", NullPointerException.class,
                () -> Grid_shortest_path.shortestPath(new int[][]{{0,0}, null}, 0,0, 0,1));
        checkThrows("empty grid", IllegalArgumentException.class,
                () -> Grid_shortest_path.shortestPath(new int[][]{}, 0,0, 0,0));
        checkThrows("ragged grid", IllegalArgumentException.class,
                () -> Grid_shortest_path.shortestPath(new int[][]{{0,0},{0}}, 0,0, 1,0));
        checkThrows("start off grid", IndexOutOfBoundsException.class,
                () -> Grid_shortest_path.shortestPath(open, -1,0, 2,2));
        checkThrows("target off grid", IndexOutOfBoundsException.class,
                () -> Grid_shortest_path.shortestPath(open, 0,0, 3,3));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}