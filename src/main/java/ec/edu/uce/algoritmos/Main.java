package ec.edu.uce.algoritmos;

import java.util.Scanner;

/** Console application entry point. Options 3-5 are added in later phases. */
public class Main {

    public static void main(String[] args) {
        for (String arg : args) {
            if (arg.equals("--no-color")) {
                Ansi.setEnabled(false);
            }
        }
        Scanner input = new Scanner(System.in);
        boolean running = true;
        while (running) {
            printMenu();
            if (!input.hasNextLine()) {
                break; // no more input (for example the input was piped)
            }
            String option = input.nextLine().trim();
            switch (option) {
                case "1" -> ComplexityDemo.run();
                case "2" -> IterationRecursionDemo.run();
                case "3", "4", "5" -> System.out.println("Not available yet (coming in a later phase).");
                case "0" -> running = false;
                default -> System.out.println("Invalid option: " + option);
            }
        }
        System.out.println("Bye!");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println(Ansi.bold("=== Algorithms - Main menu ==="));
        System.out.println("1. Complexity analysis");
        System.out.println("2. Iterative vs recursive");
        System.out.println("3. Sorting algorithms     (coming soon)");
        System.out.println("4. Benchmark              (coming soon)");
        System.out.println("5. Stability demo         (coming soon)");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }
}
