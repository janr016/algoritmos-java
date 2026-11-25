package ec.edu.uce.algoritmos;

/** Input validation shared by all algorithms, so every method rejects bad input the same way. */
final class Checks {

    /** 20! = 2,432,902,008,176,640,000 is the largest factorial that fits in a long. */
    static final int MAX_FACTORIAL_INPUT = 20;

    private Checks() {
    }

    static void requireNotNull(int[] array) {
        if (array == null) {
            throw new IllegalArgumentException("The array must not be null");
        }
    }

    static void requireNotEmpty(int[] array) {
        requireNotNull(array);
        if (array.length == 0) {
            throw new IllegalArgumentException("The array must not be empty");
        }
    }

    static void requireFactorialRange(int n) {
        if (n < 0 || n > MAX_FACTORIAL_INPUT) {
            throw new IllegalArgumentException(
                    "n must be between 0 and " + MAX_FACTORIAL_INPUT + " (long overflows after 20!), got: " + n);
        }
    }
}
