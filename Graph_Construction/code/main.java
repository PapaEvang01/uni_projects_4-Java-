package graphs;

import java.io.*;
import java.util.*;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;

/**
 * Main class for generating, analyzing, and visualizing graphs.
 * 
 * This program performs the following:
 * - Generates a random undirected weighted graph.
 * - Visualizes and exports the graph using Graphviz.
 * - Runs Breadth-First Search (BFS), Depth-First Search (DFS), Dijkstra's algorithm, and Prim's Minimum Spanning Tree.
 * - Visualizes each result with a different color and exports each as an image.
 * - Combines all generated images into one summary image.
 */
public class main {
    public static void main(String[] args) {
        // Create a new undirected graph and populate it with random nodes and edges
        create_the_graph graph = new create_the_graph(false);
        graph.generateRandomGraph();
        graph.printGraph();

        // Export and render the initial raw graph
        graph.exportToDot("graph.dot", new ArrayList<>(), "black");
        renderGraph("graph.dot", "graph.png");

        // Wait for user input before running graph algorithms
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nGraph generated. Press ENTER to run BFS, DFS, and Dijkstra...");
        scanner.nextLine();

        // Select the first node as the starting point for all algorithms
        String startNode = graph.getNodes().iterator().next();
        System.out.println("Starting node: " + startNode);

        // Run Breadth-First Search (BFS)
        List<String> bfsPath = GraphAlgorithms.bfs(graph, startNode);
        System.out.println("BFS Path: " + bfsPath);
        graph.exportToDot("graph_bfs.dot", bfsPath, "blue");
        renderGraph("graph_bfs.dot", "graph_bfs.png");

        // Run Depth-First Search (DFS)
        List<String> dfsPath = GraphAlgorithms.dfs(graph, startNode);
        System.out.println("DFS Path: " + dfsPath);
        graph.exportToDot("graph_dfs.dot", dfsPath, "red");
        renderGraph("graph_dfs.dot", "graph_dfs.png");

        // Run Dijkstra's Algorithm for shortest paths
        Dijkstra.DijkstraResult result = Dijkstra.compute(graph, startNode);

        System.out.println("Dijkstra shortest distances from " + startNode + ":");
        for (Map.Entry<String, Integer> entry : result.distances.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }

        // Build Dijkstra path edges (for visualization)
        List<String> dijkstraEdges = new ArrayList<>();
        for (Map.Entry<String, String> entry : result.previous.entrySet()) {
            dijkstraEdges.add(entry.getValue());
            dijkstraEdges.add(entry.getKey());
        }

        List<String> dijkstraEdgePath = new ArrayList<>();
        for (int i = 0; i < dijkstraEdges.size(); i += 2) {
            dijkstraEdgePath.add(dijkstraEdges.get(i));
            dijkstraEdgePath.add(dijkstraEdges.get(i + 1));
        }

        graph.exportToDot("graph_dijkstra.dot", dijkstraEdgePath, "green");
        renderGraph("graph_dijkstra.dot", "graph_dijkstra.png");

        // Run Prim's algorithm for Minimum Spanning Tree (MST)
        MST.MSTResult mstResult = MST.prim(graph, startNode);

        System.out.println("Minimum Spanning Tree (Prim) edges:");
        List<String> mstEdgePath = new ArrayList<>();
        for (MST.EdgeConnection edge : mstResult.edges) {
            System.out.println("  " + edge.from + " -- " + edge.to + " (" + edge.weight + ")");
            mstEdgePath.add(edge.from);
            mstEdgePath.add(edge.to);
        }

        System.out.println("Total MST cost: " + mstResult.totalCost);

        graph.exportToDot("graph_mst.dot", mstEdgePath, "orange");
        renderGraph("graph_mst.dot", "graph_mst.png");

        scanner.close();

        // Combine all result images into a single summary image
        combineImagesVertically(
            List.of("graph.png", "graph_bfs.png", "graph_dfs.png", "graph_dijkstra.png", "graph_mst.png"),
            "summary_graph.png"
        );
    }

    /**
     * Renders a DOT file into a PNG image using Graphviz and opens the result.
     */
    private static void renderGraph(String dotFile, String outputImage) {
        try {
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", dotFile, "-o", outputImage);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null)
                System.out.println(line);

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Graph image saved as: " + outputImage);
                new ProcessBuilder("cmd", "/c", "start", outputImage).start();
            } else {
                System.out.println("Graphviz rendering failed.");
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error rendering graph: " + e.getMessage());
        }
    }

    /**
     * Combines multiple PNG images into a single vertically stacked image.
     */
    private static void combineImagesVertically(List<String> imagePaths, String outputPath) {
        try {
            List<BufferedImage> images = new ArrayList<>();
            int width = 0, height = 0;

            // Load all images and compute final dimensions
            for (String path : imagePaths) {
                BufferedImage img = ImageIO.read(new File(path));
                images.add(img);
                width = Math.max(width, img.getWidth());
                height += img.getHeight();
            }

            // Create a new image with the total height
            BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = combined.createGraphics();

            int y = 0;
            for (BufferedImage img : images) {
                g.drawImage(img, 0, y, null);
                y += img.getHeight();
            }
            g.dispose();

            // Save the final combined image
            ImageIO.write(combined, "png", new File(outputPath));
            System.out.println("Combined image saved as: " + outputPath);
            new ProcessBuilder("cmd", "/c", "start", outputPath).start();

        } catch (IOException e) {
            System.err.println("Error combining images: " + e.getMessage());
        }
    }
}
