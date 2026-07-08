package Data_structures;

// Common contract for graph representations, so algorithms (DFS, BFS, Dijkstra,
// ...) can run on any storage backend. Vertices are integers 0..vertexCount()-1.
// Edges are directed u->v; an undirected representation stores each edge both
// ways so neighbors() and hasEdge() behave symmetrically.
public interface Graph {

    // Number of vertices; valid vertex ids are 0..vertexCount()-1.
    int vertexCount();

    // True if this graph treats edges as directed.
    boolean isDirected();

    // True if there is an edge u -> v.
    boolean hasEdge(int u, int v);

    // Weight of the edge u -> v (1 for unweighted edges).
    // Throw NoSuchElementException if there is no such edge.
    int weight(int u, int v);

    // Out-neighbors of v (vertices w with an edge v -> w), in ascending order.
    int[] neighbors(int v);
}