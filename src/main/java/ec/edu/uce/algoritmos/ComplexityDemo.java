package ec.edu.uce.algoritmos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/** Console report for Activity 1: analysis of the three algorithms and theory vs experiment. */
final class ComplexityDemo {

    private static final long[] SIZES = {10, 100, 1_000, 10_000};
    private static final Path CSV_FILE = Path.of("resultados", "tablas", "complexity.csv");

    private ComplexityDemo() {
    }

    static void run() {
        System.out.println(Ansi.bold("\n=== Activity 1: complexity analysis ==="));
        printAnalysis();

        ConsoleTable table = new ConsoleTable(
                "Algorithm", "n", "Theory T(n)", "Measured", "Match", "x previous n");
        List<String> csv = new ArrayList<>();
        csv.add("algorithm;n;theoretical;measured;match");

        addAlgorithm(table, csv, "A linear sum  O(n)", Complexity::theoreticalLinear,
                n -> Complexity.linearSum(sampleArray(n.intValue())).operations());
        addAlgorithm(table, csv, "B nested      O(n^2)", Complexity::theoreticalNested,
                n -> Complexity.nestedComparisons(sampleArray(n.intValue())).operations());
        addAlgorithm(table, csv, "C halving     O(log n)", Complexity::theoreticalHalving,
                n -> Complexity.halving(n).operations());

        System.out.println("\nTheory vs experiment (counter of the dominant operation):");
        table.print();
        System.out.println("Reading the last column: when n grows x10, A grows x10 (linear),");
        System.out.println("B grows about x100 (quadratic) and C only adds about 3 steps (logarithmic).");
        saveCsv(csv);
    }

    private static void printAnalysis() {
        ConsoleTable table = new ConsoleTable("Algorithm", "Input size n", "Dominant operation", "T(n)", "Big O");
        table.addRow("A linear sum", "array length", "addition sum += a[i]", "n", "O(n)");
        table.addRow("B nested", "array length", "comparison a[i] == a[j], i != j", "n(n-1) = n^2 - n", "O(n^2)");
        table.addRow("C halving", "the number itself", "division number / 2", "floor(log2 n)", "O(log n)");
        table.print();
    }

    private static void addAlgorithm(ConsoleTable table, List<String> csv, String name,
                                     Function<Long, Long> theory, Function<Long, Long> measure) {
        long previous = -1;
        for (long n : SIZES) {
            long expected = theory.apply(n);
            long measured = measure.apply(n);
            boolean match = expected == measured;
            String ratio = (previous > 0) ? String.format(Locale.ROOT, "%.2f", (double) measured / previous) : "-";
            table.addRow(name, format(n), format(expected), format(measured),
                    match ? Ansi.green("OK") : Ansi.red("FAIL"), ratio);
            csv.add(name.substring(0, 1) + ";" + n + ";" + expected + ";" + measured + ";" + match);
            previous = measured;
        }
    }

    /** Test data: the numbers 1..n. */
    private static int[] sampleArray(int n) {
        int[] array = new int[n];
        for (int i = 0; i < n; i++) {
            array[i] = i + 1;
        }
        return array;
    }

    private static String format(long value) {
        return String.format(Locale.ROOT, "%,d", value);
    }

    private static void saveCsv(List<String> lines) {
        try {
            Files.createDirectories(CSV_FILE.getParent());
            Files.write(CSV_FILE, lines, StandardCharsets.UTF_8);
            System.out.println("\nCSV saved to " + CSV_FILE);
        } catch (IOException e) {
            System.out.println("Could not save the CSV: " + e.getMessage());
        }
    }
}
