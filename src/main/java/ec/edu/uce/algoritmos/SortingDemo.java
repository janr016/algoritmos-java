package ec.edu.uce.algoritmos;

import java.util.Arrays;
import java.util.Locale;

/** Console demo of Activity 3 (menu option 3): required data set, results table and edge cases. */
final class SortingDemo {

    private static final int[] DATA = {45, 12, 90, 12, 3, 70, 45, 18};
    private static final int[] EXPECTED = {3, 12, 12, 18, 45, 45, 70, 90};
    private static final String[] NAMES = {"Bubble Sort", "Selection Sort", "Insertion Sort", "Shell Sort"};

    private interface Algorithm {
        SortResult sort(int[] array);
    }

    private static final Algorithm[] ALGORITHMS = {
        Sorting::bubbleSort, Sorting::selectionSort, Sorting::insertionSort, Sorting::shellSort
    };

    private SortingDemo() {
    }

    static void run() {
        System.out.println();
        System.out.println(Ansi.bold("=== Activity 3: sorting algorithms ==="));
        System.out.println("Counting criterion: comparison = every comparison between two array elements");
        System.out.println("(index conditions are not counted); move = each swap (Bubble, Selection)");
        System.out.println("or each shift (Insertion, Shell).");
        System.out.println();
        System.out.println("Input:    " + Arrays.toString(DATA));
        System.out.println("Expected: " + Arrays.toString(EXPECTED));
        System.out.println();
        printMainTable();
        System.out.println();
        printEdgeCases();
    }

    private static void printMainTable() {
        ConsoleTable table = new ConsoleTable(
                "Algorithm", "Sorted array", "Comparisons", "Moves", "Time (ms)", "Check");
        for (int i = 0; i < ALGORITHMS.length; i++) {
            SortResult result = ALGORITHMS[i].sort(DATA);
            boolean ok = Arrays.equals(result.sorted(), EXPECTED)
                    && SortVerifier.isSorted(result.sorted())
                    && SortVerifier.isPermutation(DATA, result.sorted());
            table.addRow(NAMES[i], Arrays.toString(result.sorted()),
                    String.valueOf(result.comparisons()), String.valueOf(result.moves()),
                    formatMillis(result.milliseconds()), status(ok));
        }
        table.print();
        System.out.println("(a single run of 8 elements takes microseconds; the benchmark averages many runs)");
    }

    private static void printEdgeCases() {
        System.out.println("Edge cases (each one is run with the four algorithms):");
        ConsoleTable table = new ConsoleTable("Case", "Input", "Output", "Check");
        addEdgeCase(table, "empty array", new int[0]);
        addEdgeCase(table, "one element", new int[] {5});
        addEdgeCase(table, "repeated values", new int[] {7, 3, 7, 3, 7});
        addEdgeCase(table, "already sorted", new int[] {1, 2, 3, 4, 5});

        boolean allRejected = true;
        for (Algorithm algorithm : ALGORITHMS) {
            try {
                algorithm.sort(null);
                allRejected = false; // it should have thrown
            } catch (IllegalArgumentException expected) {
                // this is the correct behaviour
            }
        }
        table.addRow("null", "null", "IllegalArgumentException", status(allRejected));
        table.print();
    }

    private static void addEdgeCase(ConsoleTable table, String label, int[] input) {
        boolean ok = true;
        int[] output = null;
        for (Algorithm algorithm : ALGORITHMS) {
            output = algorithm.sort(input).sorted();
            ok = ok && SortVerifier.isSorted(output) && SortVerifier.isPermutation(input, output);
        }
        table.addRow(label, Arrays.toString(input), Arrays.toString(output), status(ok));
    }

    /** Milliseconds with 6 decimals, always with a dot (e.g. 0.012345). */
    static String formatMillis(double milliseconds) {
        return String.format(Locale.ROOT, "%.6f", milliseconds);
    }

    private static String status(boolean ok) {
        return ok ? Ansi.green("OK") : Ansi.red("FAIL");
    }
}
