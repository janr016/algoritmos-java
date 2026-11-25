package ec.edu.uce.algoritmos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Activity 2: the three recursive methods, including the controlled stack overflow. */
class RecursionTest {

    private static final int[] VALUES = {8, 3, 10, 2, 7};

    @Test
    void requiredCases() {
        assertEquals(120, Recursion.factorial(5).value());
        assertEquals(30, Recursion.sum(VALUES).value());
        assertEquals(10, Recursion.max(VALUES).value());
    }

    @Test
    void factorialLimits() {
        assertEquals(1, Recursion.factorial(0).value()); // base case
        assertEquals(1, Recursion.factorial(1).value());
        assertEquals(2_432_902_008_176_640_000L, Recursion.factorial(20).value());
        assertThrows(IllegalArgumentException.class, () -> Recursion.factorial(-1));
        assertThrows(IllegalArgumentException.class, () -> Recursion.factorial(21));
    }

    @Test
    void arrayEdgeCases() {
        assertEquals(42, Recursion.sum(new int[] {42}).value());
        assertEquals(42, Recursion.max(new int[] {42}).value());
        assertEquals(-16, Recursion.sum(new int[] {-5, -2, -9}).value());
        assertEquals(-2, Recursion.max(new int[] {-5, -2, -9}).value());
        assertEquals(9, Recursion.max(new int[] {9, 1, 2}).value());
        assertEquals(9, Recursion.max(new int[] {1, 2, 9}).value());
        assertEquals(4, Recursion.max(new int[] {4, 4, 4}).value());
    }

    @Test
    void invalidArraysAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> Recursion.sum(null));
        assertThrows(IllegalArgumentException.class, () -> Recursion.sum(new int[0]));
        assertThrows(IllegalArgumentException.class, () -> Recursion.max(null));
        assertThrows(IllegalArgumentException.class, () -> Recursion.max(new int[0]));
    }

    @Test
    void operationCountsMatchTheIterativeVersions() {
        assertEquals(5, Recursion.factorial(5).operations());
        assertEquals(0, Recursion.factorial(0).operations());
        assertEquals(5, Recursion.sum(VALUES).operations());
        assertEquals(4, Recursion.max(VALUES).operations());
        assertEquals(0, Recursion.max(new int[] {42}).operations());
    }

    @Test
    void recursiveSumOfAHugeArrayOverflowsTheStack() {
        int[] huge = new int[1_000_000];
        assertThrows(StackOverflowError.class, () -> Recursion.sum(huge));
        assertEquals(0, Iteration.sum(huge).value()); // the loop handles the same input without trouble
    }

    @Test
    void stackDepthProbeReturnsAPositiveNumber() {
        assertTrue(Recursion.maxDepthBeforeOverflow() > 100);
    }
}
