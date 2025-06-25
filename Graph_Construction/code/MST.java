package graphs;

import java.util.*;

/**
 * This class implements Prim's algorithm to compute the Minimum Spanning Tree (MST)
 * of an undirected, weighted graph.
 * 
 * The result is returned as a list of selected edges and the total cost.
 */
public class MST {

    /**
     * A helper class representing an edge connection used in MST.
     * Contains the source node, destination node, and edge weight.
     */
    public static class EdgeConnection {
        public String from;
        public String to;
        public int weight;

        public EdgeConnection(String from, String to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    /**
     * Represents the result of Prim’s algorithm.
     * Contains the edges used in the MST and the total cost of the tree.
     */
    public static class MSTResult {
        public List<EdgeConnection> edges;
        public int totalCost;

        public MSTResult(List<EdgeConnection> edges, int totalCost) {
            this.edges = edges;
            this.totalCost = totalCost;
        }
    }

    /**
     * Computes the Minimum Spanning Tree (MST) using Prim’s algorithm.
     * 
     * @param graph the input graph (undirected, weighted)
     * @param start the starting node
     * @return an MSTResult object containing the selected edges and total cost
     */
    public static MSTResult prim(create_the_graph graph, String start) {
        Set<String> visited = new HashSet<>();  // Track visited nodes
        PriorityQueue<EdgeConnection> minHeap = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));
        List<EdgeConnection> mstEdges = new ArrayList<>();
        int totalCost = 0;

        // Start from the selected node
        visited.add(start);

        // Add all edges from the starting node to the priority queue
        for (Edge edge : graph.getNeighbors(start)) {
            minHeap.add(new EdgeConnection(start, edge.target, edge.weight));
        }

        // Continue until all reachable nodes are added
        while (!minHeap.isEmpty()) {
            EdgeConnection edge = minHeap.poll();

            if (visited.contains(edge.to)) continue;

            // Accept this edge
            visited.add(edge.to);
            mstEdges.add(edge);
            totalCost += edge.weight;

            // Add edges from the new node to the heap
            for (Edge neighborEdge : graph.getNeighbors(edge.to)) {
                if (!visited.contains(neighborEdge.target)) {
                    minHeap.add(new EdgeConnection(edge.to, neighborEdge.target, neighborEdge.weight));
                }
            }
        }

        return new MSTResult(mstEdges, totalCost);
    }
}
