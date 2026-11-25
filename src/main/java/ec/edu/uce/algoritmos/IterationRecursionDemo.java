package ec.edu.uce.algoritmos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Console report for Activity 2: required test cases, iterative vs recursive comparison
 * (result, operations, time), input validation and the controlled StackOverflowError.
 */
final class IterationRecursionDemo {

    private static final long SEED = 42;
    private static final int[] FACTORIAL_INPUTS = {5, 10, 15, 20};
    private static final int[] ARRAY_SIZES = {10, 100, 1_000, 5_000};
    private static final int OVERFLOW_ARRAY_SIZE = 1_000_000;

    /** Calls made before timing, so the JVM has already compiled the method (warm-up). */
    private static final int WARMUP_CALLS = 2_000;
    /** Number of timed repetitions; the reported time is their average. */
    private static final int REPETITIONS = 5;
    /** In each repetition we run about this many basic steps and divide, because one call is shorter than the clock tick. */
    private static final int STEPS_PER_REPETITION = 1_000_000;

    private static final Path CSV_FILE = Path.of("resultados", "tablas", "iteration_vs_recursion.csv");

    /** Every result is added here so the JVM cannot skip the calls we are timing. */
    private static long sink;

    private IterationRecursionDemo() {
    }

    static void run() {
        System.out.println(Ansi.bold("\n=== Activity 2: iterative vs recursive ==="));
        printRequiredCases();
        printComparison();
        printValidation();
        printStackOverflow();
        printComplexitySummary();
    }

    // ---- Required test cases -------------------------------------------------------------

    private static void printRequiredCases() {
        int[] values = {8, 3, 10, 2, 7};
        int number = 5;
        System.out.println("\nRequired test cases: values = {8, 3, 10, 2, 7}, number = 5");
        ConsoleTable table = new ConsoleTable("Problem", "Expected", "Iterative", "Recursive", "Result");
        addCase(table, "factorial(5)", 120, Iteration.factorial(number), Recursion.factorial(number));
        addCase(table, "sum", 30, Iteration.sum(values), Recursion.sum(values));
        addCase(table, "maximum", 10, Iteration.max(values), Recursion.max(values));
        table.print();
    }

    private static void addCase(ConsoleTable table, String problem, long expected,
                                OperationResult iterative, OperationResult recursive) {
        boolean ok = iterative.value() == expected && recursive.value() == expected;
        table.addRow(problem, String.valueOf(expected), String.valueOf(iterative.value()),
                String.valueOf(recursive.value()), ok ? Ansi.green("OK") : Ansi.red("FAIL"));
    }

    // ---- Comparison: results, operations, time -------------------------------------------

    private static void printComparison() {
        ConsoleTable table = new ConsoleTable("Problem", "Input", "Iter. result", "Rec. result", "Same?",
                "Ops iter", "Ops rec", "Iter ns/call", "Rec ns/call");
        List<String> csv = new ArrayList<>();
        csv.add("problem;input;iterative_result;recursive_result;same_result;iterative_ops;recursive_ops;"
                + "iterative_ns_per_call;recursive_ns_per_call");

        for (int n : FACTORIAL_INPUTS) {
            compare(table, csv, "factorial", "n=" + n, n,
                    () -> Iteration.factorial(n), () -> Recursion.factorial(n));
        }
        Random random = new Random(SEED);
        for (int size : ARRAY_SIZES) {
            int[] array = randomArray(random, size);
            compare(table, csv, "sum", "length=" + size, size,
                    () -> Iteration.sum(array), () -> Recursion.sum(array));
        }
        for (int size : ARRAY_SIZES) {
            int[] array = randomArray(random, size);
            compare(table, csv, "maximum", "length=" + size, size,
                    () -> Iteration.max(array), () -> Recursion.max(array));
        }
        System.out.println("\nComparison (times are the average of " + REPETITIONS + " repetitions, after warm-up):");
        table.print();
        saveCsv(csv);
    }

    private static void compare(ConsoleTable table, List<String> csv, String problem, String input, int size,
                                Supplier<OperationResult> iterative, Supplier<OperationResult> recursive) {
        OperationResult it = iterative.get();
        OperationResult rec = recursive.get();
        boolean same = it.value() == rec.value();
        double itNs = nanosPerCall(iterative, size);
        double recNs = nanosPerCall(recursive, size);
        table.addRow(problem, input, String.valueOf(it.value()), String.valueOf(rec.value()),
                same ? Ansi.green("yes") : Ansi.red("NO"),
                String.valueOf(it.operations()), String.valueOf(rec.operations()),
                formatNs(itNs), formatNs(recNs));
        csv.add(problem + ";" + input + ";" + it.value() + ";" + rec.value() + ";" + same + ";"
                + it.operations() + ";" + rec.operations() + ";" + formatNs(itNs) + ";" + formatNs(recNs));
    }

    /**
     * Average time of one call in nanoseconds: warm-up first, then REPETITIONS timed batches.
     * Only the calls are inside the System.nanoTime() window; printing and data creation are outside.
     */
    private static double nanosPerCall(Supplier<OperationResult> call, int size) {
        int batch = Math.max(1, STEPS_PER_REPETITION / Math.max(1, size));
        for (int i = 0; i < WARMUP_CALLS; i++) {
            sink += call.get().value();
        }
        long totalNanos = 0;
        for (int r = 0; r < REPETITIONS; r++) {
            long start = System.nanoTime();
            for (int i = 0; i < batch; i++) {
                sink += call.get().value();
            }
            totalNanos += System.nanoTime() - start;
        }
        return totalNanos / ((double) REPETITIONS * batch);
    }

    private static int[] randomArray(Random random, int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(10_000);
        }
        return array;
    }

    private static String formatNs(double ns) {
        return String.format(Locale.ROOT, "%.2f", ns);
    }

    // ---- Input validation ----------------------------------------------------------------

    private static void printValidation() {
        System.out.println("\nInput validation (every invalid input must be rejected, not crash silently):");
        ConsoleTable table = new ConsoleTable("Call", "Iterative", "Recursive");
        int[] empty = {};
        table.addRow("factorial(-1)", rejection(() -> Iteration.factorial(-1)), rejection(() -> Recursion.factorial(-1)));
        table.addRow("factorial(21)", rejection(() -> Iteration.factorial(21)), rejection(() -> Recursion.factorial(21)));
        table.addRow("sum(null)", rejection(() -> Iteration.sum(null)), rejection(() -> Recursion.sum(null)));
        table.addRow("sum(empty)", rejection(() -> Iteration.sum(empty)), rejection(() -> Recursion.sum(empty)));
        table.addRow("max(null)", rejection(() -> Iteration.max(null)), rejection(() -> Recursion.max(null)));
        table.addRow("max(empty)", rejection(() -> Iteration.max(empty)), rejection(() -> Recursion.max(empty)));
        table.print();
    }

    private static String rejection(Supplier<OperationResult> call) {
        try {
            call.get();
            return Ansi.red("NOT REJECTED");
        } catch (IllegalArgumentException e) {
            return Ansi.green("rejected");
        }
    }

    // ---- Stack overflow ------------------------------------------------------------------

    private static void printStackOverflow() {
        System.out.println("\nStack overflow risk of the recursive versions (controlled demonstration):");
        System.out.println("- A method that calls itself with no base case fit about "
                + String.format(Locale.ROOT, "%,d", Recursion.maxDepthBeforeOverflow())
                + " nested calls before failing (depends on JVM and stack size).");

        int[] huge = new int[OVERFLOW_ARRAY_SIZE];
        java.util.Arrays.fill(huge, 1);
        try {
            Recursion.sum(huge);
            System.out.println("- Recursive sum of 1,000,000 elements finished (unexpected on a default stack).");
        } catch (StackOverflowError e) {
            System.out.println("- Recursive sum of 1,000,000 elements: " + Ansi.red("StackOverflowError")
                    + " caught (one stack frame per element).");
        }
        System.out.println("- Iterative sum of the same array: " + Iteration.sum(huge).value()
                + " (a loop uses constant memory).");
        System.out.println("- Recursive factorial cannot overflow the stack: n is limited to 20, so the long overflows first.");
    }

    // ---- Complexity summary --------------------------------------------------------------

    private static void printComplexitySummary() {
        System.out.println("\nComplexity of the six methods:");
        ConsoleTable table = new ConsoleTable("Problem", "Version", "Time", "Auxiliary space", "Why");
        table.addRow("factorial", "iterative", "O(n)", "O(1)", "n multiplications, a few variables");
        table.addRow("factorial", "recursive", "O(n)", "O(n)", "n+1 pending calls on the stack");
        table.addRow("sum", "iterative", "O(n)", "O(1)", "n additions, one accumulator");
        table.addRow("sum", "recursive", "O(n)", "O(n)", "n+1 pending calls on the stack");
        table.addRow("maximum", "iterative", "O(n)", "O(1)", "n-1 comparisons, one variable");
        table.addRow("maximum", "recursive", "O(n)", "O(n)", "n pending calls on the stack");
        table.print();
    }

    private static void saveCsv(List<String> lines) {
        try {
            Files.createDirectories(CSV_FILE.getParent());
            Files.write(CSV_FILE, lines, StandardCharsets.UTF_8);
            System.out.println("CSV saved to " + CSV_FILE);
        } catch (IOException e) {
            System.out.println("Could not save the CSV: " + e.getMessage());
        }
    }
}
