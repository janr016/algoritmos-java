package ec.edu.uce.algoritmos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Activity 2: the three iterative methods. */
class IterationTest {

    private static final int[] VALUES = {8, 3, 10, 2, 7};

    @Test
    void requiredCases() {
        assertEquals(120, Iteration.factorial(5).value());
        assertEquals(30, Iteration.sum(VALUES).value());
        assertEquals(10, Iteration.max(VALUES).value());
    }

    @Test
    void factorialLimits() {
        assertEquals(1, Iteration.factorial(0).value());
        assertEquals(1, Iteration.factorial(1).value());
        assertEquals(2_432_902_008_176_640_000L, Iteration.factorial(20).value()); // largest that fits in a long
        assertThrows(IllegalArgumentException.class, () -> Iteration.factorial(-1));
        assertThrows(IllegalArgumentException.class, () -> Iteration.factorial(21));
    }

    @Test
    void arrayEdgeCases() {
        assertEquals(42, Iteration.sum(new int[] {42}).value());
        assertEquals(42, Iteration.max(new int[] {42}).value());
        assertEquals(-16, Iteration.sum(new int[] {-5, -2, -9}).value());
        assertEquals(-2, Iteration.max(new int[] {-5, -2, -9}).value());
        assertEquals(9, Iteration.max(new int[] {9, 1, 2}).value());  // maximum in first position
        assertEquals(9, Iteration.max(new int[] {1, 2, 9}).value());  // maximum in last position
        assertEquals(4, Iteration.max(new int[] {4, 4, 4}).value());  // all equal
    }

    @Test
    void invalidArraysAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> Iteration.sum(null));
        assertThrows(IllegalArgumentException.class, () -> Iteration.sum(new int[0]));
        assertThrows(IllegalArgumentException.class, () -> Iteration.max(null));
        assertThrows(IllegalArgumentException.class, () -> Iteration.max(new int[0]));
    }

    @Test
    void operationCounts() {
        assertEquals(5, Iteration.factorial(5).operations());  // 5 multiplications
        assertEquals(0, Iteration.factorial(0).operations());
        assertEquals(5, Iteration.sum(VALUES).operations());   // 5 additions
        assertEquals(4, Iteration.max(VALUES).operations());   // n - 1 comparisons
        assertEquals(0, Iteration.max(new int[] {42}).operations());
    }
}
