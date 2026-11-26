package ec.edu.uce.algoritmos;

/** One line of the results table: one algorithm, one size, one data type. */
public record BenchmarkRow(String algorithm, int n, DataType type,
                           long comparisons, long moves, double averageNanos) {

    /** Average time in milliseconds (the unit of the assignment table). */
    public double averageMillis() {
        return averageNanos / 1_000_000.0;
    }
}
