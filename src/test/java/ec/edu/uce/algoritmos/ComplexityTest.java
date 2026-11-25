package ec.edu.uce.algoritmos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Activity 1: the counters must match the formulas T(n) worked out by hand. */
class ComplexityTest {

    private static final int[] SIZES = {10, 100, 1_000, 10_000};

    private static int[] oneToN(int n) {
        int[] array = new int[n];
        for (int i = 0; i < n; i++) {
            array[i] = i + 1;
        }
        return array;
    }

    @Test
    void linearSumCountsNAdditions() {
        for (int n : SIZES) {
            OperationResult result = Complexity.linearSum(oneToN(n));
            assertEquals(n, result.operations());
            assertEquals(Complexity.theoreticalLinear(n), result.operations());
            assertEquals((long) n * (n + 1) / 2, result.value()); // 1 + 2 + ... + n
        }
    }

    @Test
    void nestedComparisonsCountNTimesNMinusOne() {
        for (int n : SIZES) {
            OperationResult result = Complexity.nestedComparisons(oneToN(n));
            assertEquals((long) n * (n - 1), result.operations());
            assertEquals(Complexity.theoreticalNested(n), result.operations());
            assertEquals(0, result.value()); // all values are different, so no equal pair
        }
    }

    @Test
    void nestedComparisonsFindEqualPairs() {
        // {5, 5, 7}: ordered pairs with equal values are (0,1) and (1,0)
        OperationResult result = Complexity.nestedComparisons(new int[] {5, 5, 7});
        assertEquals(2, result.value());
        assertEquals(6, result.operations()); // 3 * 2
    }

    @Test
    void halvingCountsFloorOfLog2() {
        assertEquals(3, Complexity.halving(10).operations());
        assertEquals(6, Complexity.halving(100).operations());
        assertEquals(9, Complexity.halving(1_000).operations());
        assertEquals(13, Complexity.halving(10_000).operations());
        for (int n : SIZES) {
            assertEquals(Complexity.theoreticalHalving(n), Complexity.halving(n).operations());
            assertEquals(1, Complexity.halving(n).value()); // always ends at 1
        }
    }

    @Test
    void halvingBoundaryCases() {
        assertEquals(0, Complexity.halving(1).operations());  // already 1: no division needed
        assertEquals(1, Complexity.halving(2).operations());
        assertEquals(1, Complexity.halving(3).operations());  // 3 -> 1
        assertEquals(10, Complexity.halving(1_024).operations()); // exact power of two
    }

    @Test
    void invalidInputsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> Complexity.halving(0));
        assertThrows(IllegalArgumentException.class, () -> Complexity.halving(-8));
        assertThrows(IllegalArgumentException.class, () -> Complexity.linearSum(null));
        assertThrows(IllegalArgumentException.class, () -> Complexity.nestedComparisons(null));
    }

    @Test
    void emptyArrayMeansZeroOperations() {
        assertEquals(0, Complexity.linearSum(new int[0]).operations());
        assertEquals(0, Complexity.nestedComparisons(new int[0]).operations());
    }
}
