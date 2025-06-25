package graphs;

import java.util.*;
import graphs.create_the_graph;
import graphs.Edge;


public class GraphAlgorithms {
    public static List<String> bfs(create_the_graph graph, String start) {
        List<String> visited = new ArrayList<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> seen = new HashSet<>();

        queue.add(start);
        seen.add(start);

        while (!queue.isEmpty()) {
            String node = queue.poll();
            visited.add(node);

            for (Edge edge : graph.getNeighbors(node)) {
                if (!seen.contains(edge.target)) {
                    seen.add(edge.target);
                    queue.add(edge.target);
                }
            }
        }

        return visited;
    }

    public static List<String> dfs(create_the_graph graph, String start) {
        List<String> visited = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        dfsHelper(graph, start, visited, seen);
        return visited;
    }

    private static void dfsHelper(create_the_graph graph, String node, List<String> visited, Set<String> seen) {
        visited.add(node);
        seen.add(node);

        for (Edge edge : graph.getNeighbors(node)) {
            if (!seen.contains(edge.target)) {
                dfsHelper(graph, edge.target, visited, seen);
            }
        }
    }
}
