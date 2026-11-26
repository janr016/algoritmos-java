package ec.edu.uce.algoritmos;

import java.util.Random;

/**
 * Builds the benchmark data sets. The same (type, n, seed) always gives the same array,
 * so every algorithm and every repetition sorts exactly the same data.
 *
 * Definitions:
 * - ASCENDING: 1, 2, ..., n.
 * - DESCENDING: n, n-1, ..., 1.
 * - RANDOM: n values between 1 and n, drawn with a fixed seed.
 * - REPEATED: only 5 distinct values (1 to 5), each one repeated n/5 times, in shuffled order.
 * - NEARLY_SORTED: ascending, then about 5% of the elements are moved by random swaps.
 *   Every swap moves 2 elements, so we do n * 5% / 2 swaps (at least 1).
 */
public final class DataGenerator {

    private DataGenerator() {
    }

    public static int[] generate(DataType type, int n, long seed) {
        if (n < 1) {
            throw new IllegalArgumentException("n must be at least 1, got: " + n);
        }
        Random random = new Random(seed + n * 31L + type.ordinal()); // different but reproducible per set
        return switch (type) {
            case ASCENDING -> ascending(n);
            case DESCENDING -> descending(n);
            case RANDOM -> randomValues(n, random);
            case REPEATED -> repeatedValues(n, random);
            case NEARLY_SORTED -> nearlySorted(n, random);
        };
    }

    private static int[] ascending(int n) {
        int[] array = new int[n];
        for (int i = 0; i < n; i++) {
            array[i] = i + 1;
        }
        return array;
    }

    private static int[] descending(int n) {
        int[] array = new int[n];
        for (int i = 0; i < n; i++) {
            array[i] = n - i;
        }
        return array;
    }

    private static int[] randomValues(int n, Random random) {
        int[] array = new int[n];
        for (int i = 0; i < n; i++) {
            array[i] = random.nextInt(n) + 1;
        }
        return array;
    }

    /** Values 1..5 in turn (1,2,3,4,5,1,2,...) and then shuffled, so exactly 5 distinct values appear. */
    private static int[] repeatedValues(int n, Random random) {
        int[] array = new int[n];
        for (int i = 0; i < n; i++) {
            array[i] = (i % 5) + 1;
        }
        for (int i = n - 1; i > 0; i--) { // Fisher-Yates shuffle
            swap(array, i, random.nextInt(i + 1));
        }
        return array;
    }

    private static int[] nearlySorted(int n, Random random) {
        int[] array = ascending(n);
        if (n < 2) {
            return array;
        }
        int swaps = Math.max(1, (int) Math.round(n * 0.05 / 2));
        for (int s = 0; s < swaps; s++) {
            int i = random.nextInt(n);
            int j = random.nextInt(n);
            while (j == i) {
                j = random.nextInt(n);
            }
            swap(array, i, j);
        }
        return array;
    }

    private static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
