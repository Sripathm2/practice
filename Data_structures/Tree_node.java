package Data_structures;
import java.util.List;
import java.util.ArrayList;

// A node in a rooted tree: an id, a link to its parent (null for the root), and
// its children. Produced by rooting an undirected tree and consumed by rooted-tree
// algorithms (e.g. summing leaf ids).
public class Tree_node {
    public final int id;
    public Tree_node parent;                       // null for the root
    public final List<Tree_node> children = new ArrayList<>();

    public Tree_node(int id, Tree_node parent) {
        this.id = id;
        this.parent = parent;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }
}