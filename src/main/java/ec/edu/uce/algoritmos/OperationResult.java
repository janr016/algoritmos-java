package ec.edu.uce.algoritmos;

/**
 * What an algorithm returns: the value it computed and how many times
 * its dominant operation ran (the "counter" asked for in the assignment).
 */
public record OperationResult(long value, long operations) {
}
