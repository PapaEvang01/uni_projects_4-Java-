package graphs;

import java.util.*;
import java.nio.file.*;

/**
 * This class represents a graph data structure using an adjacency list.
 * It supports both directed and undirected graphs, edge weights, random graph generation,
 * and exporting to Graphviz DOT format for visualization.
 */
public class create_the_graph {
    private Map<String, List<Edge>> adjList; // adjacency list
    private boolean isDirected;              // flag for directed or undirected graph

    /**
     * Constructor to initialize the graph type.
     * @param isDirected true for directed graph, false for undirected
     */
    public create_the_graph(boolean isDirected) {
        this.adjList = new HashMap<>();
        this.isDirected = isDirected;
    }

    /**
     * Adds a node to the graph if it doesn't exist.
     */
    public void addNode(String node) {
        adjList.putIfAbsent(node, new ArrayList<>());
    }

    /**
     * Adds a weighted edge between two nodes.
     */
    public void addEdge(String src, String dest, int weight) {
        addNode(src);
        addNode(dest);

        if (!hasEdge(src, dest)) {
            adjList.get(src).add(new Edge(dest, weight));
            if (!isDirected) {
                adjList.get(dest).add(new Edge(src, weight));
            }
        }
    }

    /**
     * Checks if an edge already exists.
     */
    private boolean hasEdge(String src, String dest) {
        return adjList.getOrDefault(src, new ArrayList<>())
                     .stream().anyMatch(e -> e.target.equals(dest));
    }

    /**
     * Prints the graph in adjacency list format.
     */
    public void printGraph() {
        System.out.println("Graph:");
        for (String node : adjList.keySet()) {
            System.out.print(node + " -> ");
            System.out.println(adjList.get(node));
        }
    }

    /**
     * Randomly generates a graph with 5–10 nodes and a random number of edges.
     * Edge weights range from 1 to 10.
     */
    public void generateRandomGraph() {
        Random rand = new Random();

        int numNodes = rand.nextInt(6) + 5; // between 5 and 10
        int maxEdges = (numNodes * (numNodes - 1)) / 2;
        int numEdges = rand.nextInt(maxEdges - numNodes + 1) + numNodes;

        System.out.println("Generating graph with " + numNodes + " nodes and " + numEdges + " edges");

        List<String> nodes = new ArrayList<>();
        for (int i = 0; i < numNodes; i++) {
            String name = getAlphabetName(i);
            nodes.add(name);
            addNode(name);
        }

        Set<String> usedEdges = new HashSet<>();
        int edgesAdded = 0;

        while (edgesAdded < numEdges) {
            String src = nodes.get(rand.nextInt(numNodes));
            String dest = nodes.get(rand.nextInt(numNodes));

            if (!src.equals(dest)) {
                String edgeKey = isDirected ? src + "->" + dest
                        : (src.compareTo(dest) < 0 ? src + "-" + dest : dest + "-" + src);

                if (!usedEdges.contains(edgeKey)) {
                    int weight = rand.nextInt(10) + 1; // weight from 1 to 10
                    addEdge(src, dest, weight);

                    usedEdges.add(edgeKey);
                    edgesAdded++;
                }
            }
        }
    }

    /**
     * Generates a name like A, B, ..., Z, AA, AB, etc.
     */
    private String getAlphabetName(int index) {
        StringBuilder name = new StringBuilder();
        while (index >= 0) {
            name.insert(0, (char) ('A' + (index % 26)));
            index = index / 26 - 1;
        }
        return name.toString();
    }

    /**
     * Returns the set of node names in the graph.
     */
    public Set<String> getNodes() {
        return adjList.keySet();
    }

    /**
     * Returns a list of neighbors (edges) of a given node.
     */
    public List<Edge> getNeighbors(String node) {
        return adjList.getOrDefault(node, new ArrayList<>());
    }

    /**
     * Exports the graph to a DOT file for visualization.
     * If a path is provided, highlights the path in the given color.
     */
    public void exportToDot(String filename, List<String> highlightPath, String color) {
        try {
            StringBuilder sb = new StringBuilder();
            String connector = isDirected ? " -> " : " -- ";

            sb.append(isDirected ? "digraph G {\n" : "graph G {\n");

            Set<String> added = new HashSet<>();
            Set<String> highlightedEdges = new HashSet<>();

            // Collect highlighted edge keys from path
            for (int i = 0; i < highlightPath.size() - 1; i++) {
                String a = highlightPath.get(i);
                String b = highlightPath.get(i + 1);
                String key = isDirected ? a + "->" + b
                        : a.compareTo(b) < 0 ? a + "-" + b : b + "-" + a;
                highlightedEdges.add(key);
            }

            // Output edges and labels
            for (String node : adjList.keySet()) {
                for (Edge edge : adjList.get(node)) {
                    String neighbor = edge.target;
                    String edgeKey = isDirected ? node + "->" + neighbor
                            : node.compareTo(neighbor) < 0 ? node + "-" + neighbor : neighbor + "-" + node;

                    if (!added.contains(edgeKey)) {
                        sb.append("  ").append(node).append(connector).append(neighbor)
                          .append(" [label=").append(edge.weight);
                        if (highlightedEdges.contains(edgeKey)) {
                            sb.append(", color=").append(color);
                            sb.append(", fontcolor=").append(color);
                            sb.append(", penwidth=2.5");
                        }
                        sb.append("];\n");
                        added.add(edgeKey);
                    }
                }
            }

            sb.append("}");
            Files.write(Paths.get(filename), sb.toString().getBytes());
            System.out.println("DOT file written to: " + filename);
        } catch (Exception e) {
            System.err.println("Error writing DOT file: " + e.getMessage());
        }
    }
}
