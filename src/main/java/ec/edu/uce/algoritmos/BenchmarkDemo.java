package ec.edu.uce.algoritmos;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/** Console side of Activity 4 (menu option 4): runs the benchmark, prints the table and saves the files. */
final class BenchmarkDemo {

    private static final Path TABLES_FOLDER = Path.of("resultados", "tablas");
    private static final Path CHARTS_FOLDER = Path.of("resultados", "graficos");

    private BenchmarkDemo() {
    }

    static void run() {
        System.out.println();
        System.out.println(Ansi.bold("=== Activity 4: benchmark ==="));
        System.out.println("Sizes " + java.util.Arrays.toString(Benchmark.SIZES) + ", 5 data types, 4 algorithms, "
                + Benchmark.REPETITIONS + " repetitions each, after a JVM warm-up.");
        System.out.println("Please wait, this can take up to a minute...");
        System.out.println();

        List<BenchmarkRow> rows = Benchmark.run(Benchmark.SIZES, Benchmark.REPETITIONS,
                message -> System.out.println("  " + message));

        System.out.println();
        printTable(rows);
        saveFiles(rows);
    }

    private static void printTable(List<BenchmarkRow> rows) {
        ConsoleTable table = new ConsoleTable(
                "Algorithm", "n", "Data type", "Comparisons", "Moves", "Average time (ms)");
        for (BenchmarkRow row : rows) {
            table.addRow(row.algorithm(), String.format(Locale.ROOT, "%,d", row.n()), row.type().label(),
                    String.format(Locale.ROOT, "%,d", row.comparisons()),
                    String.format(Locale.ROOT, "%,d", row.moves()),
                    SortingDemo.formatMillis(row.averageMillis()));
        }
        table.print();
    }

    private static void saveFiles(List<BenchmarkRow> rows) {
        try {
            Path csv = TABLES_FOLDER.resolve("benchmark.csv");
            Benchmark.writeCsv(csv, rows, Benchmark.REPETITIONS);
            Path machine = TABLES_FOLDER.resolve("machine.txt");
            MachineInfo.write(machine, Benchmark.SIZES, Benchmark.REPETITIONS);
            List<Path> charts = ChartWriter.writeAll(CHARTS_FOLDER, rows);
            System.out.println();
            System.out.println("Results table saved to " + csv);
            System.out.println("Computer and settings saved to " + machine);
            System.out.println(charts.size() + " charts (PNG) saved to " + CHARTS_FOLDER + "/");
        } catch (IOException e) {
            System.out.println(Ansi.red("Could not save the files: " + e.getMessage()));
        }
    }
}
