package ec.edu.uce.algoritmos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Benchmark with a small configuration (sizes 10 and 100, 3 repetitions) so the test is fast. */
class BenchmarkTest {

    private static final int[] SIZES = {10, 100};
    private static final int REPETITIONS = 3;

    private static List<BenchmarkRow> smallRun() {
        return Benchmark.run(SIZES, REPETITIONS, message -> { });
    }

    private static BenchmarkRow find(List<BenchmarkRow> rows, String algorithm, int n, DataType type) {
        for (BenchmarkRow row : rows) {
            if (row.algorithm().equals(algorithm) && row.n() == n && row.type() == type) {
                return row;
            }
        }
        throw new AssertionError("row not found: " + algorithm + " " + n + " " + type);
    }

    @Test
    void thereIsOneRowPerCombination() {
        // 2 sizes x 5 data types x 4 algorithms
        assertEquals(40, smallRun().size());
    }

    @Test
    void theoryHoldsInEveryRow() {
        for (BenchmarkRow row : smallRun()) {
            long n = row.n();
            assertTrue(row.averageNanos() > 0, "time of " + row.algorithm());
            if (row.algorithm().equals("Selection Sort")) {
                assertEquals(n * (n - 1) / 2, row.comparisons()); // always, whatever the order
            }
            if (row.type() == DataType.ASCENDING) {
                assertEquals(0, row.moves(), row.algorithm() + " on ascending data");
            }
            if (row.type() == DataType.DESCENDING && !row.algorithm().equals("Shell Sort")
                    && !row.algorithm().equals("Selection Sort")) {
                assertEquals(n * (n - 1) / 2, row.comparisons());
                assertEquals(n * (n - 1) / 2, row.moves());
            }
        }
    }

    @Test
    void movesOfBubbleAndInsertionEqualTheInversions() {
        List<BenchmarkRow> rows = smallRun();
        for (int n : SIZES) {
            for (DataType type : DataType.values()) {
                long inversions = SortVerifier.countInversions(DataGenerator.generate(type, n, Benchmark.SEED));
                assertEquals(inversions, find(rows, "Bubble Sort", n, type).moves());
                assertEquals(inversions, find(rows, "Insertion Sort", n, type).moves());
            }
        }
    }

    @Test
    void csvHasHeaderAndOneLinePerRow() throws IOException {
        List<BenchmarkRow> rows = smallRun();
        Path file = Files.createTempFile("benchmark", ".csv");
        try {
            Benchmark.writeCsv(file, rows, REPETITIONS);
            List<String> lines = Files.readAllLines(file);
            assertEquals(rows.size() + 1, lines.size());
            assertEquals("algorithm,n,data_type,comparisons,moves,avg_time_ms,avg_time_ns,repetitions", lines.get(0));
            for (String line : lines) {
                assertEquals(8, line.split(",").length, line);
            }
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    void chartsAreWrittenAsPngFiles() throws IOException {
        Path folder = Files.createTempDirectory("charts");
        List<Path> files = ChartWriter.writeAll(folder, smallRun());
        assertEquals(10, files.size()); // time + comparisons for each of the 5 data types
        for (Path file : files) {
            byte[] bytes = Files.readAllBytes(file);
            assertTrue(bytes.length > 1_000, file + " is too small");
            assertEquals(0x89, bytes[0] & 0xFF); // PNG signature: 0x89 'P' 'N' 'G'
            assertEquals('P', bytes[1]);
            assertEquals('N', bytes[2]);
            assertEquals('G', bytes[3]);
            Files.deleteIfExists(file);
        }
        Files.deleteIfExists(folder);
    }
}
