package graphs;

/**
 * Represents a weighted edge to a target node in a graph.
 * This is a basic data structure used in adjacency lists.
 */
public class Edge {
    public String target;  // The node this edge connects to
    public int weight;     // The weight (cost) of the edge

    /**
     * Constructs an edge to a target node with a specific weight.
     *
     * @param target The destination node
     * @param weight The weight of the edge
     */
    public Edge(String target, int weight) {
        this.target = target;
        this.weight = weight;
    }

    /**
     * Returns a string representation of the edge in the form: Target(weight)
     */
    @Override
    public String toString() {
        return target + "(" + weight + ")";
    }
}
