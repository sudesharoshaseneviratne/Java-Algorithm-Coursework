// Student Name: Sudesh Arosha Senaratne
// IIT ID: 20232432
// UoW ID: 2054013
// Module: 5SENG003C.2 Algorithms: Theory, Design and Implementation
// Group: L5 SE Group-4

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/*
 * Contains the network flow algorithm implementation
 * Task 3 & 4 Implementation: File parsing and Ford-Fulkerson algorithm
 * - Uses BFS to find augmenting paths (Edmonds-Karp implementation)
 * - Time Complexity: O(VE²) where V is number of vertices and E is number ofedges
 * - Space Complexity: O(V + E) for the residual graph
 */

public class NetworkFlow {
    /*
     * Parses a network description from an input file
     * Task 3 Implementation: File format parser
     * Format of the input file:
     * - First line: integer n (number of nodes in the network)
     * - Following lines: each containing three integers (from, to, capacity)
     * representing a directed edge from node 'from' to node 'to' with capacity
     * - Node 0 is considered the source and node (n-1) is the sink
     * @param filename name of the input file
     * @return constructed flow network
     * @throws FileNotFoundException if input file is not found
     */

    public static FlowNetwork parseInputFile(String filename) throws FileNotFoundException {
        System.out.println("\n -------------------------------------------");
        System.out.println("| Processing File : " + filename + " |");
        System.out.println(" -------------------------------------------");

        Scanner scanner = new Scanner(new File(filename));

        // Read number of nodes
        int numNodes = scanner.nextInt();
        System.out.println("\n*Total Nodes: " + numNodes);
        System.out.println("");
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

    /*
     * Computes maximum flow using Ford-Fulkerson algorithm with BFS (Edmonds-Karp)
     * Task 4 Implementation: Maximum flow computation
     * The Edmonds-Karp algorithm is a specific implementation of Ford-Fulkerson that:
     * - Always uses shortest augmenting paths (via BFS)
     * - Has O(VE²) time complexity instead of potentially infinite for basic
     * Ford-Fulkerson
     * - Is guaranteed to terminate with the correct max flow value
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
                System.out.println("\n*No more augmenting paths.");
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
                if (i < path.size() - 1)
                    System.out.print(" -> ");
            }
            System.out.println(" || Bottleneck = " + pathFlow);

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
        System.out.println("\n ------------------------------");
        System.out.println("| Final flow through each edge |");
        System.out.println(" ------------------------------");
        System.out.println("");
        updateOriginalNetwork(network, residual);
        for (int i = 0; i < network.getNumNodes(); i++) {
            for (Edge edge : network.getAdjacentEdges(i)) {
                System.out.println("Edge from " + edge.from + " to " + edge.to +
                        " || Capacity = " + edge.capacity +
                        " || Final Flow = " + edge.flow);
            }
        }

        // Print performance metrics
        long endTime = System.nanoTime();
        double timeInMs = (endTime - startTime) / 1_000_000.0;
        System.out.println("\n*Total Maximum Flow: " + maxFlow);
        System.out.printf("*Total Time Taken: %.2f ms%n", timeInMs);

        return maxFlow;
    }

    /*
     * Creates a residual graph for flow computations
     * The residual graph includes:
     * 1. Forward edges with capacity - flow remaining capacity
     * 2. Backward edges with capacity equal to the flow (for flow cancellation)
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

    /*
     * Updates the original network with final flows from residual graph
     * After the algorithm completes, this method transfers the computed
     * flows back to the original network for reporting and validation
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