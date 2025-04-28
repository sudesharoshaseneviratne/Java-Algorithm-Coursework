import java.io.FileNotFoundException;

/**
 * Main class to run the network flow program
 * Task 1 Implementation: Project setup and main execution point
 * - Reads network from input file
 * - Computes maximum flow
 * - Displays results and performance metrics
 * Student: [Your Name]
 * ID: [Your Student ID]
 */
public class Main {
    public static void main(String[] args) {
        try {
            // Parse input file and create flow network
            FlowNetwork network = NetworkFlow.parseInputFile("test_input.txt");
            
            // Compute and display maximum flow with detailed path information
            NetworkFlow.maxFlow(network);
        } catch (FileNotFoundException e) {
            System.err.println("Error: Input file not found");
        }
    }
}