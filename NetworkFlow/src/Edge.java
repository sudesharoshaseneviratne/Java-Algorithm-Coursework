/**
 * Represents an edge in the flow network
 * Task 2 Implementation: Basic building block for the flow network
 * Student: [Your Name]
 * ID: [Your Student ID]
 */
public class Edge {
    // Source node of the edge
    public final int from;
    // Target node of the edge
    public final int to;
    // Maximum capacity of the edge c(e)
    public final int capacity;
    // Current flow through the edge f(e)
    public int flow;

    /**
     * Creates a new edge in the flow network
     * @param from source node
     * @param to target node
     * @param capacity maximum flow capacity
     */
    public Edge(int from, int to, int capacity) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.flow = 0;  // Initially no flow
    }

    /**
     * String representation of the edge showing current flow and capacity
     * @return string in format "from->to (flow/capacity)"
     */
    @Override
    public String toString() {
        return String.format("%d->%d (%d/%d)", from, to, flow, capacity);
    }
}