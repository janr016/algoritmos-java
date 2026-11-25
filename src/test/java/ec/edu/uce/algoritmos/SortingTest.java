package ec.edu.uce.algoritmos;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * Activity 3. The expected numbers come from the control table of the assignment
 * (counting criterion documented in Sorting). No test uses any built-in sort.
 */
class SortingTest {

    private interface Algorithm {
        SortResult sort(int[] array);
    }

    private static final String[] NAMES = {"Bubble", "Selection", "Insertion", "Shell"};
    private static final Algorithm[] ALGORITHMS = {
        Sorting::bubbleSort, Sorting::selectionSort, Sorting::insertionSort, Sorting::shellSort
    };

    private static final int[] DATA = {45, 12, 90, 12, 3, 70, 45, 18};
    private static final int[] EXPECTED = {3, 12, 12, 18, 45, 45, 70, 90};

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

    /** Random values from 0 to 19, so there are many repeated values. Fixed seed. */
    private static int[] randomWithRepeats(int n, long seed) {
        Random random = new Random(seed);
        int[] array = new int[n];
        for (int i = 0; i < n; i++) {
            array[i] = random.nextInt(20);
        }
        return array;
    }

    /** expected[a] = {comparisons, moves} for algorithm a (same order as NAMES). */
    private static void assertCounts(int[] input, long[][] expected) {
        for (int a = 0; a < ALGORITHMS.length; a++) {
            SortResult result = ALGORITHMS[a].sort(input);
            assertEquals(expected[a][0], result.comparisons(), NAMES[a] + " comparisons");
            assertEquals(expected[a][1], result.moves(), NAMES[a] + " moves");
        }
    }

    @Test
    void requiredDataSetIsSorted() {
        for (int a = 0; a < ALGORITHMS.length; a++) {
            assertArrayEquals(EXPECTED, ALGORITHMS[a].sort(DATA).sorted(), NAMES[a]);
        }
    }

    @Test
    void controlTableMixedData() {
        assertCounts(DATA, new long[][] {{25, 14}, {28, 4}, {19, 14}, {22, 7}});
    }

    @Test
    void controlTableDescending() {
        assertCounts(descending(8), new long[][] {{28, 28}, {28, 4}, {28, 28}, {22, 12}});
    }

    @Test
    void controlTableAscending() {
        assertCounts(ascending(8), new long[][] {{7, 0}, {28, 0}, {7, 0}, {17, 0}});
    }

    @Test
    void selectionAlwaysMakesNTimesNMinusOneOverTwoComparisons() {
        int[][] inputs = {ascending(50), descending(50), randomWithRepeats(50, 1), randomWithRepeats(200, 2)};
        for (int[] input : inputs) {
            long n = input.length;
            assertEquals(n * (n - 1) / 2, Sorting.selectionSort(input).comparisons());
        }
    }

    @Test
    void bubbleOnAscendingAndDescendingData() {
        for (int n : new int[] {2, 10, 100}) {
            SortResult ascending = Sorting.bubbleSort(ascending(n));
            assertEquals(n - 1, ascending.comparisons()); // one pass, flag stays false
            assertEquals(0, ascending.moves());

            SortResult descending = Sorting.bubbleSort(descending(n));
            assertEquals((long) n * (n - 1) / 2, descending.comparisons());
            assertEquals((long) n * (n - 1) / 2, descending.moves());
        }
    }

    @Test
    void insertionOnDescendingData() {
        for (int n : new int[] {2, 10, 100}) {
            SortResult result = Sorting.insertionSort(descending(n));
            assertEquals((long) n * (n - 1) / 2, result.comparisons());
            assertEquals((long) n * (n - 1) / 2, result.moves());
        }
    }

    @Test
    void bubbleAndInsertionMovesEqualTheInversions() {
        int[][] inputs = {DATA, ascending(30), descending(30), randomWithRepeats(100, 3), randomWithRepeats(300, 4)};
        for (int[] input : inputs) {
            long inversions = SortVerifier.countInversions(input);
            assertEquals(inversions, Sorting.bubbleSort(input).moves(), "Bubble, n=" + input.length);
            assertEquals(inversions, Sorting.insertionSort(input).moves(), "Insertion, n=" + input.length);
        }
    }

    @Test
    void everyResultIsSortedAndAPermutationOfTheOriginal() {
        for (int n : new int[] {0, 1, 2, 3, 10, 100, 500}) {
            int[] input = randomWithRepeats(n, 10 + n);
            for (int a = 0; a < ALGORITHMS.length; a++) {
                int[] output = ALGORITHMS[a].sort(input).sorted();
                assertTrue(SortVerifier.isSorted(output), NAMES[a] + " sorted, n=" + n);
                assertTrue(SortVerifier.isPermutation(input, output), NAMES[a] + " permutation, n=" + n);
            }
        }
    }

    @Test
    void emptyAndSingleElementArrays() {
        for (int a = 0; a < ALGORITHMS.length; a++) {
            SortResult empty = ALGORITHMS[a].sort(new int[0]);
            assertEquals(0, empty.sorted().length, NAMES[a]);
            assertEquals(0, empty.comparisons(), NAMES[a]);
            assertEquals(0, empty.moves(), NAMES[a]);

            SortResult single = ALGORITHMS[a].sort(new int[] {42});
            assertArrayEquals(new int[] {42}, single.sorted(), NAMES[a]);
            assertEquals(0, single.comparisons(), NAMES[a]);
            assertEquals(0, single.moves(), NAMES[a]);
        }
    }

    @Test
    void repeatedAndAlreadySortedArrays() {
        for (int a = 0; a < ALGORITHMS.length; a++) {
            assertArrayEquals(new int[] {7, 7, 7, 7}, ALGORITHMS[a].sort(new int[] {7, 7, 7, 7}).sorted(), NAMES[a]);
            assertEquals(0, ALGORITHMS[a].sort(new int[] {7, 7, 7, 7}).moves(), NAMES[a]);
            assertArrayEquals(new int[] {1, 2, 3, 4, 5}, ALGORITHMS[a].sort(new int[] {1, 2, 3, 4, 5}).sorted(), NAMES[a]);
            assertEquals(0, ALGORITHMS[a].sort(new int[] {1, 2, 3, 4, 5}).moves(), NAMES[a]);
        }
    }

    @Test
    void nullIsRejected() {
        for (Algorithm algorithm : ALGORITHMS) {
            assertThrows(IllegalArgumentException.class, () -> algorithm.sort(null));
        }
    }

    @Test
    void theOriginalArrayIsNotModified() {
        for (int a = 0; a < ALGORITHMS.length; a++) {
            int[] input = DATA.clone();
            SortResult result = ALGORITHMS[a].sort(input);
            assertArrayEquals(DATA, input, NAMES[a] + " changed its input");
            assertNotSame(input, result.sorted(), NAMES[a] + " returned the same array");
        }
    }

    @Test
    void countsAreTheSameInEveryRepetition() {
        int[] input = randomWithRepeats(300, 99);
        for (int a = 0; a < ALGORITHMS.length; a++) {
            SortResult first = ALGORITHMS[a].sort(input);
            for (int repetition = 0; repetition < 5; repetition++) {
                SortResult again = ALGORITHMS[a].sort(input);
                assertEquals(first.comparisons(), again.comparisons(), NAMES[a]);
                assertEquals(first.moves(), again.moves(), NAMES[a]);
            }
        }
    }

    @Test
    void timeIsMeasured() {
        for (int a = 0; a < ALGORITHMS.length; a++) {
            SortResult result = ALGORITHMS[a].sort(descending(2_000));
            assertTrue(result.nanoseconds() > 0, NAMES[a] + " time");
            assertEquals(result.nanoseconds() / 1_000_000.0, result.milliseconds(), 0.0);
        }
    }

    @Test
    void verifierBehavesCorrectly() {
        assertTrue(SortVerifier.isSorted(new int[0]));
        assertTrue(SortVerifier.isSorted(new int[] {1, 1, 2}));
        assertFalse(SortVerifier.isSorted(new int[] {2, 1}));

        assertTrue(SortVerifier.isPermutation(new int[] {1, 2, 2, 3}, new int[] {2, 3, 1, 2}));
        assertFalse(SortVerifier.isPermutation(new int[] {1, 2, 2}, new int[] {1, 1, 2})); // different repeats
        assertFalse(SortVerifier.isPermutation(new int[] {1, 2}, new int[] {1, 2, 2}));    // different length

        assertEquals(5, SortVerifier.countInversions(new int[] {8, 6, 9, 6, 7})); // example of Part I
        assertEquals(0, SortVerifier.countInversions(ascending(8)));
        assertEquals(28, SortVerifier.countInversions(descending(8)));
    }
}
