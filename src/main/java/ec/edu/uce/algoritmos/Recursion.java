package ec.edu.uce.algoritmos;

/**
 * Activity 2 - recursive versions. Same results and same operation counts as {@link Iteration},
 * but every pending call stays on the call stack: time O(n), auxiliary space O(n).
 * The public methods validate once and then call a private helper that does the recursion.
 * The helper receives "operations", a one-element array used as a counter that all calls share
 * (a plain long parameter would be copied on every call and lose the count).
 */
public final class Recursion {

    /** Written by dive() so we can report how deep the stack got before it overflowed. */
    private static int depthReached;

    private Recursion() {
    }

    /** n! for 0 <= n <= 20. Base case: n == 0. Recursive case: n * (n - 1)!. */
    public static OperationResult factorial(int n) {
        Checks.requireFactorialRange(n);
        long[] operations = new long[1];
        long result = factorialRec(n, operations);
        return new OperationResult(result, operations[0]);
    }

    private static long factorialRec(int n, long[] operations) {
        if (n == 0) {
            return 1; // base case
        }
        operations[0]++; // one multiplication
        return n * factorialRec(n - 1, operations);
    }

    /** Sum of all elements. Base case: no elements left (index == length). Rejects null and empty arrays. */
    public static OperationResult sum(int[] array) {
        Checks.requireNotEmpty(array);
        long[] operations = new long[1];
        long total = sumRec(array, 0, operations);
        return new OperationResult(total, operations[0]);
    }

    private static long sumRec(int[] array, int index, long[] operations) {
        if (index == array.length) {
            return 0; // base case
        }
        operations[0]++; // one addition
        return array[index] + sumRec(array, index + 1, operations);
    }

    /** Largest element. Base case: the last element. Rejects null and empty arrays. */
    public static OperationResult max(int[] array) {
        Checks.requireNotEmpty(array);
        long[] operations = new long[1];
        int largest = maxRec(array, 0, operations);
        return new OperationResult(largest, operations[0]);
    }

    private static int maxRec(int[] array, int index, long[] operations) {
        if (index == array.length - 1) {
            return array[index]; // base case: a single element is its own maximum
        }
        int largestOfRest = maxRec(array, index + 1, operations);
        operations[0]++; // one comparison
        return (array[index] > largestOfRest) ? array[index] : largestOfRest;
    }

    /**
     * Controlled stack overflow demo: a method with NO base case keeps calling itself until the JVM
     * throws StackOverflowError, which we catch. Returns how many nested calls fit in the stack.
     * The number depends on the JVM and its stack size (-Xss); it is a measurement, not a constant.
     */
    public static int maxDepthBeforeOverflow() {
        depthReached = 0;
        try {
            dive(1);
        } catch (StackOverflowError expected) {
            // this is exactly what we wanted to provoke
        }
        return depthReached;
    }

    private static void dive(int depth) {
        depthReached = depth;
        dive(depth + 1); // deliberately no base case
    }
}
