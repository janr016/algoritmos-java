package ec.edu.uce.algoritmos;

/**
 * Activity 2 - iterative versions (loops). Each method returns the result and the number of
 * dominant operations: multiplications (factorial), additions (sum) or comparisons (maximum).
 * Time O(n); auxiliary space O(1) because only a few local variables are used.
 */
public final class Iteration {

    private Iteration() {
    }

    /** n! for 0 <= n <= 20. Runs n multiplications (0! runs none and returns 1). */
    public static OperationResult factorial(int n) {
        Checks.requireFactorialRange(n);
        long result = 1;
        long operations = 0;
        for (int i = 1; i <= n; i++) {
            result *= i;
            operations++;
        }
        return new OperationResult(result, operations);
    }

    /** Sum of all elements. Runs one addition per element. Rejects null and empty arrays. */
    public static OperationResult sum(int[] array) {
        Checks.requireNotEmpty(array);
        long total = 0;
        long operations = 0;
        for (int i = 0; i < array.length; i++) {
            total += array[i];
            operations++;
        }
        return new OperationResult(total, operations);
    }

    /** Largest element. Starts with the first one and runs n - 1 comparisons. Rejects null and empty arrays. */
    public static OperationResult max(int[] array) {
        Checks.requireNotEmpty(array);
        int largest = array[0];
        long operations = 0;
        for (int i = 1; i < array.length; i++) {
            operations++;
            if (array[i] > largest) {
                largest = array[i];
            }
        }
        return new OperationResult(largest, operations);
    }
}
