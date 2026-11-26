package ec.edu.uce.algoritmos;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** The data sets must follow their definitions and be reproducible. */
class DataGeneratorTest {

    private static final long SEED = 123L;
    private static final int[] SIZES = {10, 100, 1_000, 10_000};

    private static int[] oneToN(int n) {
        int[] array = new int[n];
        for (int i = 0; i < n; i++) {
            array[i] = i + 1;
        }
        return array;
    }

    @Test
    void ascendingAndDescending() {
        for (int n : SIZES) {
            assertArrayEquals(oneToN(n), DataGenerator.generate(DataType.ASCENDING, n, SEED));
            int[] descending = DataGenerator.generate(DataType.DESCENDING, n, SEED);
            assertEquals(n, descending.length);
            assertEquals(n, descending[0]);
            assertEquals(1, descending[n - 1]);
            assertEquals((long) n * (n - 1) / 2, SortVerifier.countInversions(descending));
        }
    }

    @Test
    void randomHasTheRightLengthAndRange() {
        for (int n : SIZES) {
            int[] random = DataGenerator.generate(DataType.RANDOM, n, SEED);
            assertEquals(n, random.length);
            for (int value : random) {
                assertTrue(value >= 1 && value <= n);
            }
        }
        assertFalse(SortVerifier.isSorted(DataGenerator.generate(DataType.RANDOM, 100, SEED)));
    }

    @Test
    void repeatedHasExactlyFiveDistinctValues() {
        for (int n : SIZES) {
            int[] repeated = DataGenerator.generate(DataType.REPEATED, n, SEED);
            boolean[] seen = new boolean[6];
            for (int value : repeated) {
                assertTrue(value >= 1 && value <= 5, "value " + value);
                seen[value] = true;
            }
            for (int value = 1; value <= 5; value++) {
                assertTrue(seen[value], "missing value " + value + " for n=" + n);
            }
        }
    }

    @Test
    void nearlySortedChangesAboutFivePercentOfTheElements() {
        int[] ascending = oneToN(1_000);
        int[] nearly = DataGenerator.generate(DataType.NEARLY_SORTED, 1_000, SEED);
        assertTrue(SortVerifier.isPermutation(ascending, nearly));
        int displaced = 0;
        for (int i = 0; i < nearly.length; i++) {
            if (nearly[i] != ascending[i]) {
                displaced++;
            }
        }
        assertTrue(displaced >= 2 && displaced <= 50, "displaced elements: " + displaced); // 25 swaps move at most 50
        assertFalse(SortVerifier.isSorted(nearly));
    }

    @Test
    void sameSeedGivesTheSameData() {
        for (DataType type : DataType.values()) {
            assertArrayEquals(DataGenerator.generate(type, 500, SEED), DataGenerator.generate(type, 500, SEED));
        }
    }

    @Test
    void invalidSizeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> DataGenerator.generate(DataType.RANDOM, 0, SEED));
    }
}
