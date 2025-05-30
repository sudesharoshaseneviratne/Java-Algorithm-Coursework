// Student Name: Sudesh Arosha Senaratne
// IIT ID: 20232432
// UoW ID: 2054013
// Module: 5SENG003C.2 Algorithms: Theory, Design and Implementation
// Group: L5 SE Group-4

import java.util.ArrayList;
import java.util.List;

/*
 * Represents a flow network with nodes and edges
 * Task 2 Implementation: Main data structure for the flow network
 * - Uses adjacency list representation for efficient edge access
 * - Supports directed edges with capacities
 */
public class FlowNetwork {
    // Total number of nodes in the network
    private final int numNodes;
    // Adjacency list representation: adjacencyList[v] contains all edges with v as
    // source
    private final List<List<Edge>> adjacencyList;

    /*
     * Creates a new flow network with specified number of nodes
     * @param numNodes number of nodes in the network
     */
    public FlowNetwork(int numNodes) {
        this.numNodes = numNodes;
        this.adjacencyList = new ArrayList<>();
        // Initialize adjacency lists for all nodes
        for (int i = 0; i < numNodes; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    /**
     * Adds a directed edge to the network
     * @param from     source node
     * @param to       target node
     * @param capacity maximum flow capacity
     */
    public void addEdge(int from, int to, int capacity) {
        // Create new edge and add it to the adjacency list of the source node
        Edge edge = new Edge(from, to, capacity);
        adjacencyList.get(from).add(edge);
    }

    /*
     * Gets all edges originating from a node
     * @param node source node
     * @return list of edges where node is the source
     */
    public List<Edge> getAdjacentEdges(int node) {
        // Return the adjacency list for the specified node
        return adjacencyList.get(node);
    }

    /*
     * Gets the total number of nodes in the network
     * @return number of nodes
     */
    public int getNumNodes() {
        return numNodes;
    }
}