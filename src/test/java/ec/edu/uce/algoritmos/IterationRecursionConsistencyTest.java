package ec.edu.uce.algoritmos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;
import org.junit.jupiter.api.Test;

/** Both strategies must give the same value and the same operation count for every input. */
class IterationRecursionConsistencyTest {

    @Test
    void factorialAgreesForEveryAllowedInput() {
        for (int n = 0; n <= 20; n++) {
            assertEquals(Iteration.factorial(n), Recursion.factorial(n), "factorial(" + n + ")");
        }
    }

    @Test
    void sumAndMaxAgreeOnRandomArrays() {
        Random random = new Random(7); // fixed seed: the test always uses the same data
        for (int size : new int[] {1, 2, 3, 10, 100, 1_000}) {
            int[] array = new int[size];
            for (int i = 0; i < size; i++) {
                array[i] = random.nextInt(2_001) - 1_000; // includes negative numbers
            }
            assertEquals(Iteration.sum(array), Recursion.sum(array), "sum, size " + size);
            assertEquals(Iteration.max(array), Recursion.max(array), "max, size " + size);
        }
    }
}
