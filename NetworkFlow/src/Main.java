// Student Name: Sudesh Arosha Senaratne
// IIT ID: 20232432
// UoW ID: 2054013
// Module: 5SENG003C.2 Algorithms: Theory, Design and Implementation
// Group: L5 SE Group-4

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

/*
 * Main class to run the network flow program
 * Task 1 Implementation: Project setup and main execution point
 * - Reads network from input file
 * - Computes maximum flow
 * - Displays results and performance metrics
*/
public class Main {
    public static void main(String[] args) {        //Entry point of the application
        boolean running = true;                     //Handles the main program flow, file selection, and error handling
                                                    //@param args Command line arguments (not used)
        while (running) {
            try {
                String selectedFile = showMenuAndGetSelection();                        // Show menu and get user's file selection                           
                if (selectedFile == null) {                                             // Check if user wants to exit
                    continue;                                                           // Invalid selection, show menu again
                } else if (selectedFile.equalsIgnoreCase("exit")) {
                    running = false;
                    System.out.println("\nExiting program. Goodbye!");
                } else {
                    FlowNetwork network = NetworkFlow.parseInputFile(selectedFile);     // Parse input file and create flow network
                    NetworkFlow.maxFlow(network);                                       // Compute and display maximum flow with detailed path information
                    System.out.println("\nPress Enter to continue...");
                    new Scanner(System.in).nextLine();                                  // Wait for user to press Enter
                }
            } catch (FileNotFoundException e) {
                System.err.println("Error: Input file not found");
                System.out.println("\nPress Enter to continue...");
                new Scanner(System.in).nextLine();                                      // Wait for user to press Enter
            }
        }
    }

    /*
     * Displays a menu of available benchmark files and gets user selection
     * @return Path to the selected file, "exit" if user wants to exit, or null if
     * invalid selection
     */
    private static String showMenuAndGetSelection() {

        File benchmarkDir = new File("benchmarks");                                 // Get benchmark text files from the benchmarks directory
        File[] files = benchmarkDir.listFiles((_, name) -> name.endsWith(".txt"));

        if (files == null || files.length == 0) {                                           // Check if benchmark files exist
            System.err.println("Error: No benchmark files found in the benchmarks directory");
            return null;
        }

        Arrays.sort(files, (f1, f2) -> {                                                    // Sort files by type (bridge/ladder) and number
            String name1 = f1.getName();
            String name2 = f2.getName();

            if (name1.startsWith("bridge") && name2.startsWith("ladder"))     // First compare by type (bridge vs ladder)
                return -1;
            if (name1.startsWith("ladder") && name2.startsWith("bridge"))
                return 1;

            int num1 = extractNumber(name1);                                                // Then compare by number
            int num2 = extractNumber(name2);
            return Integer.compare(num1, num2);
        });

        // Display menu
        System.out.println("\n ==============================");
        System.out.println("| Network Flow Benchmark Files |");
        System.out.println(" ==============================");
        System.out.println("\n*Please select a file by entering its number or type 'exit' to quit:-");
        System.out.println();

        // Set up grid layout parameters
        int filesPerColumn = 10;
        int numColumns = 4;
        int totalFiles = files.length;

        // Calculate how many rows we need for complete display
        int numRows = Math.min(filesPerColumn, (totalFiles + numColumns - 1) / numColumns);

        // Print header for each column
        for (int col = 0; col < numColumns; col++) {
            System.out.print(String.format("%-25s", "Column " + (col + 1)));
        }
        System.out.println();

        // Print files in a grid format
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numColumns; col++) {
                int index = col * filesPerColumn + row;
                if (index < totalFiles) {
                    String fileName = files[index].getName();
                    System.out.print(String.format("%2d. %-20s", (index + 1), fileName));
                }
            }
            System.out.println();
        }

        System.out.println("\n*Enter your choice (1-" + files.length + ") or type 'exit' to quit:- ");

        // Get user input
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();

        // Check if user wants to exit
        if (input.equalsIgnoreCase("exit")) {
            return "exit";
        }

        // Process numeric selection
        try {
            int choice = Integer.parseInt(input);
            if (choice >= 1 && choice <= files.length) {
                return files[choice - 1].getPath();
            } else {
                // Handle out-of-range selection
                System.err.println("\n*Invalid selection. Please choose a number between 1 and " + files.length
                        + " or type 'exit' to quit");
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine(); // Wait for user to press Enter
                return null;
            }
        } catch (NumberFormatException e) {
            // Handle non-numeric input
            System.err.println("Invalid input. Please enter a number or 'exit'.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for user to press Enter
            return null;
        }
        // Scanner is not closed to avoid closing System.in
    }

    /*
     * Extracts numeric portion from filename
     * Used for sorting files by number
     * @param fileName Name of file to process
     * @return Extracted number from the filename, or 0 if no number found
     */
    private static int extractNumber(String fileName) {
        try {
            return Integer.parseInt(fileName.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}