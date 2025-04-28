import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Contains the network flow algorithm implementation
 * Task 3 & 4 Implementation: File parsing and Ford-Fulkerson algorithm
 * - Uses BFS to find augmenting paths (Edmonds-Karp implementation)
 * - Time Complexity: O(VE²) where V is number of vertices and E is number of edges
 * - Space Complexity: O(V + E) for the residual graph
 * Student: [Your Name]
 * ID: [Your Student ID]
 */
public class NetworkFlow {
    /**
     * Parses a network description from an input file
     * Task 3 Implementation: File format parser
     * @param filename name of the input file
     * @return constructed flow network
     * @throws FileNotFoundException if input file is not found
     */
    public static FlowNetwork parseInputFile(String filename) throws FileNotFoundException {
        System.out.println("Processing File: " + filename);
        Scanner scanner = new Scanner(new File(filename));
        
        // Read number of nodes
        int numNodes = scanner.nextInt();
        System.out.println("\nTotal Nodes: " + numNodes);
        FlowNetwork network = new FlowNetwork(numNodes);

        // Read edges until end of file
        while (scanner.hasNextInt()) {
            int from = scanner.nextInt();
            int to = scanner.nextInt();
            int capacity = scanner.nextInt();
            network.addEdge(from, to, capacity);
        }
        scanner.close();
        return network;
    }

    /**
     * Computes maximum flow using Ford-Fulkerson algorithm with BFS (Edmonds-Karp)
     * Task 4 Implementation: Maximum flow computation
     * @param network the flow network to analyze
     * @return the maximum flow value
     */
    public static int maxFlow(FlowNetwork network) {
        long startTime = System.nanoTime();
        int source = 0;
        int sink = network.getNumNodes() - 1;
        int maxFlow = 0;
        int pathCount = 1;

        // Create residual graph for flow computation
        FlowNetwork residual = createResidualGraph(network);

        // Main Ford-Fulkerson algorithm loop
        while (true) {
            // Find augmenting path using BFS
            Edge[] parent = new Edge[network.getNumNodes()];
            Queue<Integer> queue = new LinkedList<>();
            queue.add(source);

            // BFS to find shortest augmenting path
            while (!queue.isEmpty() && parent[sink] == null) {
                int current = queue.poll();
                for (Edge edge : residual.getAdjacentEdges(current)) {
                    if (parent[edge.to] == null && edge.to != source && edge.capacity > edge.flow) {
                        parent[edge.to] = edge;
                        queue.add(edge.to);
                    }
                }
            }

            // No more augmenting paths found
            if (parent[sink] == null) {
                System.out.println("No more augmenting paths.");
                break;
            }

            // Find bottleneck capacity and construct path
            int pathFlow = Integer.MAX_VALUE;
            List<Integer> path = new ArrayList<>();
            for (Edge edge = parent[sink]; edge != null; edge = parent[edge.from]) {
                pathFlow = Math.min(pathFlow, edge.capacity - edge.flow);
                path.add(0, edge.to);
            }
            path.add(0, source);

            // Print augmenting path and bottleneck
            System.out.print("Augmenting Path " + pathCount + ": ");
            for (int i = 0; i < path.size(); i++) {
                System.out.print(path.get(i));
                if (i < path.size() - 1) System.out.print(" → ");
            }
            System.out.println(" | Bottleneck = " + pathFlow);

            // Update flows along the augmenting path
            for (Edge edge = parent[sink]; edge != null; edge = parent[edge.from]) {
                edge.flow += pathFlow;
                // Update reverse edge in residual graph
                for (Edge reverse : residual.getAdjacentEdges(edge.to)) {
                    if (reverse.to == edge.from) {
                        reverse.flow -= pathFlow;
                        break;
                    }
                }
            }

            maxFlow += pathFlow;
            pathCount++;
        }

        // Print final results
        System.out.println("\nFinal flow through each edge:");
        updateOriginalNetwork(network, residual);
        for (int i = 0; i < network.getNumNodes(); i++) {
            for (Edge edge : network.getAdjacentEdges(i)) {
                System.out.println("Edge from " + edge.from + " to " + edge.to + 
                                 " | Capacity = " + edge.capacity + 
                                 " | Final Flow = " + edge.flow);
            }
        }

        // Print performance metrics
        long endTime = System.nanoTime();
        double timeInMs = (endTime - startTime) / 1_000_000.0;
        System.out.println("\nTotal Maximum Flow: " + maxFlow);
        System.out.printf("Total Time Taken: %.2f ms%n", timeInMs);
        
        return maxFlow;
    }

    /**
     * Creates a residual graph for flow computations
     * @param network original network
     * @return residual graph with forward and backward edges
     */
    private static FlowNetwork createResidualGraph(FlowNetwork network) {
        FlowNetwork residual = new FlowNetwork(network.getNumNodes());
        
        // Add forward edges from original network
        for (int i = 0; i < network.getNumNodes(); i++) {
            for (Edge edge : network.getAdjacentEdges(i)) {
                residual.addEdge(edge.from, edge.to, edge.capacity);
            }
        }
        
        // Add backward edges for flow reversal
        for (int i = 0; i < network.getNumNodes(); i++) {
            for (Edge edge : network.getAdjacentEdges(i)) {
                residual.addEdge(edge.to, edge.from, 0);
            }
        }
        
        return residual;
    }

    /**
     * Updates the original network with final flows from residual graph
     * @param original original network to update
     * @param residual residual graph containing final flows
     */
    private static void updateOriginalNetwork(FlowNetwork original, FlowNetwork residual) {
        for (int i = 0; i < original.getNumNodes(); i++) {
            for (Edge originalEdge : original.getAdjacentEdges(i)) {
                for (Edge residualEdge : residual.getAdjacentEdges(originalEdge.from)) {
                    if (residualEdge.to == originalEdge.to) {
                        originalEdge.flow = residualEdge.flow;
                        break;
                    }
                }
            }
        }
    }
}