package ec.edu.uce.algoritmos;

/**
 * Activity 1: three algorithms with different growth, each one counting its dominant operation.
 * The theoretical*() methods hold the formula T(n) obtained by hand, so tests and tables can
 * compare theory against what the counter really measured.
 */
public final class Complexity {

    private Complexity() {
    }

    /**
     * Algorithm A - linear traversal.
     * n = array length. Dominant operation: the addition "sum += array[i]".
     * It runs once per element, so T(n) = n, which is O(n).
     */
    public static OperationResult linearSum(int[] array) {
        Checks.requireNotNull(array);
        long sum = 0;
        long operations = 0;
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
            operations++;
        }
        return new OperationResult(sum, operations);
    }

    /**
     * Algorithm B - every element compared with all the others (nested loops).
     * n = array length. Dominant operation: the comparison "array[i] == array[j]" with i != j.
     * The outer loop runs n times and the inner one compares with the other n - 1 elements,
     * so T(n) = n * (n - 1) = n^2 - n, which is O(n^2).
     * The returned value is how many ordered pairs (i, j) hold equal numbers.
     */
    public static OperationResult nestedComparisons(int[] array) {
        Checks.requireNotNull(array);
        long equalPairs = 0;
        long operations = 0;
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length; j++) {
                if (i == j) {
                    continue; // an element is not compared with itself
                }
                operations++;
                if (array[i] == array[j]) {
                    equalPairs++;
                }
            }
        }
        return new OperationResult(equalPairs, operations);
    }

    /**
     * Algorithm C - halve a positive number until it reaches 1.
     * n = the number itself (not an array). Dominant operation: the division "number = number / 2".
     * It runs while number > 1, which is floor(log2 n) times, so T(n) = floor(log2 n), O(log n).
     * The returned value is the final number (always 1).
     */
    public static OperationResult halving(long n) {
        if (n < 1) {
            throw new IllegalArgumentException("n must be a positive number, got: " + n);
        }
        long number = n;
        long operations = 0;
        while (number > 1) {
            number = number / 2;
            operations++;
        }
        return new OperationResult(number, operations);
    }

    /** T(n) of algorithm A, worked out by hand. */
    public static long theoreticalLinear(long n) {
        return n;
    }

    /** T(n) of algorithm B, worked out by hand. */
    public static long theoreticalNested(long n) {
        return n * (n - 1);
    }

    /** T(n) of algorithm C: floor(log2 n) = position of the highest bit of n. */
    public static long theoreticalHalving(long n) {
        return 63 - Long.numberOfLeadingZeros(n);
    }
}
