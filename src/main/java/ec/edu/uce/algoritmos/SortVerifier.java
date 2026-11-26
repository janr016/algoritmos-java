package ec.edu.uce.algoritmos;

import java.util.HashMap;
import java.util.Map;

/**
 * Independent checks for sorting results. None of them sorts anything:
 * they only look at the data, so they can be trusted to judge the algorithms.
 */
public final class SortVerifier {

    private SortVerifier() {
    }

    /** True if every element is less than or equal to the next one. */
    public static boolean isSorted(int[] array) {
        for (int i = 0; i + 1 < array.length; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }
        return true;
    }

    /** True if both arrays hold exactly the same elements (same values, same number of repeats). */
    public static boolean isPermutation(int[] original, int[] result) {
        if (original.length != result.length) {
            return false;
        }
        Map<Integer, Integer> occurrences = new HashMap<>();
        for (int value : original) {
            occurrences.merge(value, 1, Integer::sum);
        }
        for (int value : result) {
            Integer left = occurrences.get(value);
            if (left == null || left == 0) {
                return false;
            }
            occurrences.put(value, left - 1);
        }
        return true; // same length and no value ran out, so every count ended at 0
    }

    /** Inversions: pairs i < j with array[i] > array[j]. Simple O(n^2) count, used only to verify. */
    public static long countInversions(int[] array) {
        long inversions = 0;
        for (int i = 0; i < array.length; i++) {
            for (int j = i + 1; j < array.length; j++) {
                if (array[i] > array[j]) {
                    inversions++;
                }
            }
        }
        return inversions;
    }
}
