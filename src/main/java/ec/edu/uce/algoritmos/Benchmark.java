package ec.edu.uce.algoritmos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Activity 4: runs the four sorting algorithms on every size and data type.
 *
 * Rules that make the measurement fair:
 * - The data is generated once per (size, type) and OUTSIDE the timing; the four algorithms
 *   receive the very same array (each one sorts its own copy, made before the clock starts).
 * - The clock (System.nanoTime) runs only around the sorting loops, inside Sorting.
 * - A warm-up phase runs first so the JVM has already compiled the code when we measure.
 * - Each combination is run several times and the times are averaged.
 * - Comparisons and moves must be identical in every repetition (they are deterministic);
 *   if not, the benchmark stops with an error.
 * - The first result of each combination is checked: sorted and a permutation of the input.
 */
public final class Benchmark {

    public static final int[] SIZES = {10, 100, 1_000, 10_000};
    public static final int REPETITIONS = 5;
    public static final long SEED = 20261125L;

    private static final int WARM_UP_ROUNDS = 30;
    private static final String[] NAMES = {"Bubble Sort", "Selection Sort", "Insertion Sort", "Shell Sort"};

    private interface Algorithm {
        SortResult sort(int[] array);
    }

    private static final Algorithm[] ALGORITHMS = {
        Sorting::bubbleSort, Sorting::selectionSort, Sorting::insertionSort, Sorting::shellSort
    };

    private Benchmark() {
    }

    public static String[] algorithmNames() {
        return NAMES.clone();
    }

    /** Runs everything. "progress" receives one short message per (size, data type) finished. */
    public static List<BenchmarkRow> run(int[] sizes, int repetitions, Consumer<String> progress) {
        if (repetitions < 1) {
            throw new IllegalArgumentException("repetitions must be at least 1");
        }
        warmUp();
        List<BenchmarkRow> rows = new ArrayList<>();
        for (int n : sizes) {
            for (DataType type : DataType.values()) {
                int[] data = DataGenerator.generate(type, n, SEED);
                int[] untouched = data.clone();
                for (int a = 0; a < ALGORITHMS.length; a++) {
                    rows.add(measure(a, n, type, data, repetitions));
                }
                if (!java.util.Arrays.equals(data, untouched)) {
                    throw new IllegalStateException("An algorithm modified the shared input data");
                }
                progress.accept("n = " + n + ", " + type.label() + " done");
            }
        }
        return rows;
    }

    /** Runs all algorithms many times on throw-away data so the JVM compiles them before measuring. */
    private static void warmUp() {
        int[] medium = DataGenerator.generate(DataType.RANDOM, 1_000, SEED + 1);
        int[] small = DataGenerator.generate(DataType.RANDOM, 100, SEED + 2);
        for (int round = 0; round < WARM_UP_ROUNDS; round++) {
            for (Algorithm algorithm : ALGORITHMS) {
                algorithm.sort(medium);
                algorithm.sort(small);
            }
        }
    }

    private static BenchmarkRow measure(int a, int n, DataType type, int[] data, int repetitions) {
        SortResult first = ALGORITHMS[a].sort(data);
        if (!SortVerifier.isSorted(first.sorted()) || !SortVerifier.isPermutation(data, first.sorted())) {
            throw new IllegalStateException(NAMES[a] + " gave a wrong result for n=" + n + ", " + type.label());
        }
        long totalNanos = first.nanoseconds();
        for (int repetition = 1; repetition < repetitions; repetition++) {
            SortResult again = ALGORITHMS[a].sort(data);
            if (again.comparisons() != first.comparisons() || again.moves() != first.moves()) {
                throw new IllegalStateException(NAMES[a] + " counts changed between repetitions (n=" + n + ", "
                        + type.label() + "): they must be deterministic");
            }
            totalNanos += again.nanoseconds();
        }
        return new BenchmarkRow(NAMES[a], n, type, first.comparisons(), first.moves(),
                (double) totalNanos / repetitions);
    }

    /** CSV with one line per row; decimals always with a dot. */
    public static void writeCsv(Path file, List<BenchmarkRow> rows, int repetitions) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("algorithm,n,data_type,comparisons,moves,avg_time_ms,avg_time_ns,repetitions");
        for (BenchmarkRow row : rows) {
            lines.add(String.format(Locale.ROOT, "%s,%d,%s,%d,%d,%.6f,%.1f,%d",
                    row.algorithm(), row.n(), row.type().label(), row.comparisons(), row.moves(),
                    row.averageMillis(), row.averageNanos(), repetitions));
        }
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(file, lines, StandardCharsets.UTF_8);
    }
}
