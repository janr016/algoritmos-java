package ec.edu.uce.algoritmos;

/**
 * Activity 3: Bubble, Selection, Insertion and Shell Sort written from scratch
 * (no Arrays.sort, Collections.sort or streams).
 *
 * COUNTING CRITERION (the same for the four algorithms):
 * - Comparison = every comparison between two ELEMENTS of the array (a[x] > a[y]).
 *   Index conditions such as "j >= 0" or "j >= gap" are NOT counted.
 * - Move = in Bubble and Selection, every swap counts 1.
 *   In Insertion and Shell, every shift (one assignment a[j] = a[j - gap]) counts 1.
 *   Putting the saved element in its final place is not counted.
 *
 * Every method: rejects null, works on a COPY (the original is untouched),
 * and measures the time only around the sorting loops, not around the copy.
 * Empty and one-element arrays are valid and come back unchanged with 0 / 0.
 */
public final class Sorting {

    private Sorting() {
    }

    /**
     * Bubble Sort with a "no swaps" flag. Each pass compares neighbours and swaps them if the
     * left one is STRICTLY greater (strict ">" keeps it stable). After pass p the last p + 1
     * positions are final, so the next pass is one comparison shorter. If a whole pass swaps
     * nothing, the array is sorted and we stop.
     */
    public static SortResult bubbleSort(int[] array) {
        Checks.requireNotNull(array);
        int[] a = array.clone();
        int n = a.length;
        long comparisons = 0;
        long moves = 0;

        long start = System.nanoTime();
        boolean swapped = true;
        for (int pass = 0; pass < n - 1 && swapped; pass++) {
            swapped = false;
            for (int j = 0; j < n - 1 - pass; j++) {
                comparisons++;
                if (a[j] > a[j + 1]) {
                    int temp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = temp;
                    moves++;
                    swapped = true;
                }
            }
        }
        long elapsed = System.nanoTime() - start;

        return new SortResult(a, comparisons, moves, elapsed);
    }

    /**
     * Selection Sort. For each position i it looks for the smallest element in the unsorted part
     * and swaps it into position i (only if it is not already there).
     * Always n(n-1)/2 comparisons, whatever the order of the data. At most n - 1 swaps.
     */
    public static SortResult selectionSort(int[] array) {
        Checks.requireNotNull(array);
        int[] a = array.clone();
        int n = a.length;
        long comparisons = 0;
        long moves = 0;

        long start = System.nanoTime();
        for (int i = 0; i < n - 1; i++) {
            int smallest = i;
            for (int j = i + 1; j < n; j++) {
                comparisons++;
                if (a[j] < a[smallest]) {
                    smallest = j;
                }
            }
            if (smallest != i) {
                int temp = a[i];
                a[i] = a[smallest];
                a[smallest] = temp;
                moves++;
            }
        }
        long elapsed = System.nanoTime() - start;

        return new SortResult(a, comparisons, moves, elapsed);
    }

    /**
     * Insertion Sort. The element a[i] is saved, the elements of the sorted prefix that are
     * STRICTLY greater are shifted one position to the right, and the saved element is placed
     * in the gap. The comparison that stops the shifting is also counted.
     * Shifts = number of inversions of the input.
     */
    public static SortResult insertionSort(int[] array) {
        Checks.requireNotNull(array);
        int[] a = array.clone();
        int n = a.length;
        long comparisons = 0;
        long moves = 0;

        long start = System.nanoTime();
        for (int i = 1; i < n; i++) {
            int current = a[i];
            int j = i;
            while (j >= 1) {
                comparisons++;
                if (a[j - 1] > current) {
                    a[j] = a[j - 1]; // shift one position to the right
                    moves++;
                    j--;
                } else {
                    break;
                }
            }
            a[j] = current;
        }
        long elapsed = System.nanoTime() - start;

        return new SortResult(a, comparisons, moves, elapsed);
    }

    /**
     * Shell Sort with the original gaps n/2, n/4, ..., 1. For each gap it does an insertion sort
     * over the elements that are "gap" positions apart. The last gap (1) is a plain insertion sort.
     * Same counting as Insertion Sort: each shift is one move.
     */
    public static SortResult shellSort(int[] array) {
        Checks.requireNotNull(array);
        int[] a = array.clone();
        int n = a.length;
        long comparisons = 0;
        long moves = 0;

        long start = System.nanoTime();
        for (int gap = n / 2; gap >= 1; gap /= 2) {
            for (int i = gap; i < n; i++) {
                int current = a[i];
                int j = i;
                while (j >= gap) {
                    comparisons++;
                    if (a[j - gap] > current) {
                        a[j] = a[j - gap]; // shift "gap" positions to the right
                        moves++;
                        j -= gap;
                    } else {
                        break;
                    }
                }
                a[j] = current;
            }
        }
        long elapsed = System.nanoTime() - start;

        return new SortResult(a, comparisons, moves, elapsed);
    }
}
