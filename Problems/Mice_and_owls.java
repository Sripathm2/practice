package Problems;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Queue;

import Algorithms.Ford_fulkerson;

// Mice and Owls (bipartite max flow).
// Mice must flee into holes before owls strike. A mouse can enter a hole only if
// the straight-line distance to it is within the distance the mouse can travel;
// each hole holds a limited number of mice. The number of mice that can be saved is
// the maximum flow of the network:
//     source -> each mouse            capacity 1
//     mouse  -> each reachable hole   capacity 1
//     hole   -> sink                  capacity = that hole's capacity
//
// distance() and canReach() are provided. You implement miceSaved(): build that
// network and run it through max flow.
public class Mice_and_owls {

    public static final class Mouse {
        public final double x, y;
        public Mouse(double x, double y) { this.x = x; this.y = y; }
    }

    public static final class Hole {
        public final double x, y;
        public final int capacity;
        public Hole(double x, double y, int capacity) { this.x = x; this.y = y; this.capacity = capacity; }
    }

    // Euclidean distance between (x1, y1) and (x2, y2). [provided]
    public static double distance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2, dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // True if the mouse can reach the hole while travelling at most maxDistance
    // (reachable when distance <= maxDistance). [provided]
    public static boolean canReach(Mouse m, Hole h, double maxDistance) {
        return distance(m.x, m.y, h.x, h.y) <= maxDistance;
    }

    // Maximum number of mice that can be saved. Build the flow network described
    // above (source, one node per mouse, one node per hole, sink), connect a mouse
    // to a hole exactly when canReach(mouse, hole, maxDistance), and return the
    // max flow from source to sink. [YOU IMPLEMENT]
    // Throw NullPointerException if mice or holes is null.
    // Throw IllegalArgumentException if maxDistance < 0.
    public static int miceSaved(List<Mouse> mice, List<Hole> holes, double maxDistance) {
        if(mice == null || holes == null){
            throw new NullPointerException();
        }
        if(maxDistance < 0){
            throw new IllegalArgumentException();
        }

        int[][] graph = new int[mice.size()+holes.size()+2][mice.size()+holes.size()+2];        

        for(int i=0;i< mice.size(); i++){
            graph[mice.size()+holes.size()][i] = 1;
            for(int j=0;j<holes.size();j++){
                if(canReach(mice.get(i), holes.get(j), maxDistance)){
                    graph[i][mice.size()+j] = 1;
                }
            }
        }

        for(int j=0;j<holes.size();j++){
            graph[mice.size()+j][mice.size()+holes.size()+1] = holes.get(j).capacity;
        }

        int micesaved = Ford_fulkerson.maxFlow(graph, mice.size()+holes.size(), mice.size()+holes.size()+1);


        return micesaved;
    }
}

class Mice_and_owls_Main {
    private static int passed = 0;
    private static int failed = 0;

    private static void checkClose(String name, double expected, double actual) {
        if (Math.abs(expected - actual) < 1e-9) { passed++; System.out.println("PASS: " + name); }
        else { failed++; System.out.println("FAIL: " + name + " — expected " + expected + ", got " + actual); }
    }
    private static void checkEquals(String name, Object expected, Object actual) {
        if (Objects.equals(expected, actual)) { passed++; System.out.println("PASS: " + name); }
        else { failed++; System.out.println("FAIL: " + name + " — expected <" + expected + ">, got <" + actual + ">"); }
    }
    private static void checkThrows(String name, Class<? extends Throwable> ex, Runnable r) {
        try { r.run(); failed++; System.out.println("FAIL: " + name + " — none thrown"); }
        catch (Throwable t) {
            if (ex.isInstance(t)) { passed++; System.out.println("PASS: " + name); }
            else { failed++; System.out.println("FAIL: " + name + " — got " + t.getClass().getSimpleName()); }
        }
    }

    private static Mice_and_owls.Mouse mouse(double x, double y) { return new Mice_and_owls.Mouse(x, y); }
    private static Mice_and_owls.Hole hole(double x, double y, int c) { return new Mice_and_owls.Hole(x, y, c); }

    // Independent ground truth: build the same network and run Edmonds-Karp.
    private static int truthSaved(List<Mice_and_owls.Mouse> mice, List<Mice_and_owls.Hole> holes, double md) {
        int M = mice.size(), H = holes.size(), n = M + H + 2, s = 0, t = n - 1;
        int[][] cap = new int[n][n];
        for (int i = 0; i < M; i++) cap[s][1 + i] = 1;
        for (int j = 0; j < H; j++) cap[M + 1 + j][t] = holes.get(j).capacity;
        for (int i = 0; i < M; i++)
            for (int j = 0; j < H; j++)
                if (Mice_and_owls.canReach(mice.get(i), holes.get(j), md)) cap[1 + i][M + 1 + j] = 1;
        return edmondsKarp(cap, s, t);
    }
    private static int edmondsKarp(int[][] cap, int s, int t) {
        int n = cap.length; int[][] res = new int[n][]; for (int i = 0; i < n; i++) res[i] = cap[i].clone();
        int flow = 0;
        while (true) {
            int[] par = new int[n]; java.util.Arrays.fill(par, -1); par[s] = s;
            Queue<Integer> q = new ArrayDeque<>(); q.add(s);
            while (!q.isEmpty()) { int u = q.poll(); for (int v = 0; v < n; v++) if (par[v] == -1 && res[u][v] > 0) { par[v] = u; q.add(v); } }
            if (par[t] == -1) break;
            int b = Integer.MAX_VALUE; for (int v = t; v != s; v = par[v]) b = Math.min(b, res[par[v]][v]);
            for (int v = t; v != s; v = par[v]) { res[par[v]][v] -= b; res[v][par[v]] += b; }
            flow += b;
        }
        return flow;
    }

    private static List<Mice_and_owls.Mouse> mice(Mice_and_owls.Mouse... ms) { List<Mice_and_owls.Mouse> l = new ArrayList<>(); for (var m : ms) l.add(m); return l; }
    private static List<Mice_and_owls.Hole> holes(Mice_and_owls.Hole... hs) { List<Mice_and_owls.Hole> l = new ArrayList<>(); for (var h : hs) l.add(h); return l; }

    public static void main(String[] args) {
        // distance() / canReach() are provided, so these should pass immediately.
        checkClose("distance 3-4-5", 5.0, Mice_and_owls.distance(0, 0, 3, 4));
        checkEquals("canReach within", true,  Mice_and_owls.canReach(mouse(0, 0), hole(3, 4, 1), 5.0));
        checkEquals("canReach out", false, Mice_and_owls.canReach(mouse(0, 0), hole(3, 4, 1), 4.9));

        // Scenarios (expected values verified via independent max flow):
        var s1m = mice(mouse(0, 0), mouse(1, 1), mouse(5, 5));
        var s1h = holes(hole(0, 1, 1), hole(5, 4, 2));
        checkEquals("scenario 1 (hole cap limits)", 2, Mice_and_owls.miceSaved(s1m, s1h, 2));

        checkEquals("scenario 2 (shared big hole)", 2,
                Mice_and_owls.miceSaved(mice(mouse(0, 0), mouse(0, 0)), holes(hole(0, 0, 5)), 1));

        checkEquals("scenario 3 (none reachable)", 0,
                Mice_and_owls.miceSaved(mice(mouse(0, 0)), holes(hole(10, 10, 1)), 1));

        checkEquals("scenario 4 (capacity 2 caps it)", 2,
                Mice_and_owls.miceSaved(mice(mouse(0, 0), mouse(0, 0), mouse(0, 0)), holes(hole(0, 0, 2)), 1));

        checkEquals("scenario 5 (all saved)", 3,
                Mice_and_owls.miceSaved(mice(mouse(0, 0), mouse(3, 0), mouse(0, 4)),
                                        holes(hole(1, 0, 1), hole(2, 0, 1), hole(0, 2, 1)), 5));

        checkEquals("no mice", 0, Mice_and_owls.miceSaved(mice(), holes(hole(0, 0, 3)), 5));
        checkEquals("no holes", 0, Mice_and_owls.miceSaved(mice(mouse(0, 0)), holes(), 5));

        // Cross-check vs independent max flow on random scenarios
        java.util.Random rng = new java.util.Random(181);
        boolean ok = true;
        for (int t = 0; t < 300 && ok; t++) {
            List<Mice_and_owls.Mouse> ms = new ArrayList<>();
            List<Mice_and_owls.Hole> hs = new ArrayList<>();
            int M = rng.nextInt(6), H = rng.nextInt(5);
            for (int i = 0; i < M; i++) ms.add(mouse(rng.nextInt(10), rng.nextInt(10)));
            for (int j = 0; j < H; j++) hs.add(hole(rng.nextInt(10), rng.nextInt(10), 1 + rng.nextInt(3)));
            double md = 1 + rng.nextInt(6);
            if (Mice_and_owls.miceSaved(ms, hs, md) != truthSaved(ms, hs, md)) ok = false;
        }
        if (ok) { passed++; System.out.println("PASS: cross-check vs max flow"); }
        else    { failed++; System.out.println("FAIL: cross-check vs max flow"); }

        // Validation
        checkThrows("null mice", NullPointerException.class,
                () -> Mice_and_owls.miceSaved(null, holes(hole(0, 0, 1)), 1));
        checkThrows("null holes", NullPointerException.class,
                () -> Mice_and_owls.miceSaved(mice(mouse(0, 0)), null, 1));
        checkThrows("negative distance", IllegalArgumentException.class,
                () -> Mice_and_owls.miceSaved(mice(mouse(0, 0)), holes(hole(0, 0, 1)), -1));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}