package ec.edu.uce.algoritmos;

import java.util.Arrays;

/**
 * What every sorting method returns (Activity 3).
 *
 * @param sorted      a NEW array with the sorted data (the original array is never modified)
 * @param comparisons how many times two array elements were compared (see counting criterion in Sorting)
 * @param moves       swaps (Bubble, Selection) or shifts (Insertion, Shell)
 * @param nanoseconds time spent only inside the sorting loops, measured with System.nanoTime()
 */
public record SortResult(int[] sorted, long comparisons, long moves, long nanoseconds) {

    /** The same time in milliseconds, e.g. 0.012345 (the assignment table asks for ms). */
    public double milliseconds() {
        return nanoseconds / 1_000_000.0;
    }

    @Override
    public String toString() {
        return Arrays.toString(sorted) + " comparisons=" + comparisons + " moves=" + moves
                + " time=" + nanoseconds + " ns";
    }
}
