package graphs;

import java.util.*;

/**
 * Implements Dijkstra's algorithm to compute the shortest paths from a single source
 * node to all other nodes in a weighted graph with non-negative edge weights.
 */
public class Dijkstra {

    /**
     * A class to store the result of Dijkstra's algorithm.
     * Includes the shortest distances and the previous-node map for path reconstruction.
     */
    public static class DijkstraResult {
        public Map<String, Integer> distances; // Shortest distances from the start node
        public Map<String, String> previous;   // Previous node for each node in the shortest path

        public DijkstraResult(Map<String, Integer> distances, Map<String, String> previous) {
            this.distances = distances;
            this.previous = previous;
        }
    }

    /**
     * Computes the shortest paths from a starting node using Dijkstra's algorithm.
     *
     * @param graph The input graph
     * @param start The starting node
     * @return A DijkstraResult containing distances and the shortest path tree
     */
    public static DijkstraResult compute(create_the_graph graph, String start) {
        Map<String, Integer> distances = new HashMap<>();      // Distance from start to each node
        Map<String, String> previous = new HashMap<>();        // Previous node for path reconstruction
        PriorityQueue<NodeDist> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));
        Set<String> visited = new HashSet<>();

        // Initialize all distances to "infinity"
        for (String node : graph.getNodes()) {
            distances.put(node, Integer.MAX_VALUE);
        }

        // Distance to the start node is 0
        distances.put(start, 0);
        queue.add(new NodeDist(start, 0));

        while (!queue.isEmpty()) {
            NodeDist current = queue.poll();
            String node = current.node;

            if (visited.contains(node)) continue;
            visited.add(node);

            // Explore neighbors
            for (Edge edge : graph.getNeighbors(node)) {
                int newDist = distances.get(node) + edge.weight;
                if (newDist < distances.get(edge.target)) {
                    distances.put(edge.target, newDist);
                    previous.put(edge.target, node); // Remember how we reached this node
                    queue.add(new NodeDist(edge.target, newDist));
                }
            }
        }

        return new DijkstraResult(distances, previous);
    }

    /**
     * Helper class used in the priority queue to store nodes and their tentative distances.
     */
    private static class NodeDist {
        String node;
        int distance;

        NodeDist(String node, int distance) {
            this.node = node;
            this.distance = distance;
        }
    }
}
