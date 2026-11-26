# algoritmos-java

**Integrative Group Assignment – Weeks 3, 4, and 5 · Part II: Implementation and Practical Application in Java**
Central University of Ecuador · Faculty of Engineering and Applied Sciences · Course: Algorithms (4th semester, 2026–2027 academic period)

**Members:** 
- Caiza Hidalgo Derlis Mauricio
- Dueñas Flores Luis David
- Naranjo Vilaña Juan Andres
- Tapa Tamayo Joel Matias

Console application in Java 21 (Maven + JUnit 5) that implements and evaluates complexity, iteration and recursion, and sorting algorithms, featuring a benchmark module that generates tables and charts.

## Content by Activity

| Activity | Description | Main Classes |
|---|---|---|
| 1. Complexity | Three algorithms with a dominant operation counter: linear sum $O(n)$, nested comparisons $O(n^2)$, and halving reduction $O(\log n)$. Compares theoretical $T(n)$ against measured values. | `Complexity`, `ComplexityDemo` |
| 2. Iteration and Recursion | Six independent methods (factorial, sum, and maximum, each implemented iteratively and recursively) with input validation, execution times, operation counts, and a controlled `StackOverflowError` demonstration. | `Iteration`, `Recursion`, `IterationRecursionDemo` |
| 3. Sorting | Bubble, Selection, Insertion, and Shell Sort written from scratch (without using `Arrays.sort`, `Collections.sort`, or `stream().sorted()`), tracking comparisons, movements, and execution time. | `Sorting`, `SortResult`, `SortVerifier`, `SortingDemo` |
| 4. Benchmark | 4 algorithms × 4 sizes (10, 100, 1,000, 10,000) × 5 data types = 80 combinations, 5 runs each, with JVM warm-up and `System.nanoTime()`. Exports CSV files and PNG charts. | `Benchmark`, `BenchmarkRow`, `DataGenerator`, `DataType`, `ChartWriter`, `MachineInfo`, `BenchmarkDemo` |
| 5. Technical Analysis | Answers to the seven questions, supported by benchmark figures. Included in the PDF report (Section 9). | — |

Extra (not requested): stability demonstration using student records (`Student`, `StudentSorting`, `StabilityDemo`, menu option 5).

## Equivalence with the Suggested Structure in the Prompt

Class names are written in English. All classes belong to the `ec.edu.uce.algoritmos` package.

| Suggested in the Prompt | In This Project |
|---|---|
| `Complejidad.java` | `Complexity.java` |
| `Iteracion.java` | `Iteration.java` |
| `Recursion.java` | `Recursion.java` |
| `Ordenacion.java` | `Sorting.java` |
| `Benchmark.java` | `Benchmark.java` |
| `Main.java` | `Main.java` |

## Repository Structure

```
algoritmos-java/
├── README.md
├── pom.xml
├── src/
│   ├── main/java/ec/edu/uce/algoritmos/   source code
│   └── test/java/ec/edu/uce/algoritmos/   JUnit 5 tests (54 tests)
├── resultados/
│   ├── tablas/      benchmark.csv, complexity.csv, iteration_vs_recursion.csv, machine.txt
│   ├── graficos/    10 PNG charts (time and comparisons by data type)
│   └── capturas/    screenshots of menu executions and test runs
└── informe/
    └── informe-practico.pdf
```

## Requirements

- Java 21 or higher (the project compiles with `maven.compiler.release = 21`)
- Maven 3.9+
- Git

## Build, Test, and Run

```bash
mvn clean compile                                   # compile
mvn clean test                                      # run JUnit 5 tests
java -cp target/classes ec.edu.uce.algoritmos.Main  # run the application
```

Application menu:

| Option | Action |
|---|---|
| 1 | Algorithmic complexity (Activity 1) |
| 2 | Iterative vs. recursive (Activity 2) |
| 3 | Sorting algorithms (Activity 3) |
| 4 | Full benchmark (Activity 4) |
| 5 | Stability demonstration |
| 0 | Exit |

> **Important:** The menu options write output to the `resultados/` folder relative to the directory from which the application is executed. If run from inside the repository root, the CSV files and charts used in the report will be **overwritten** (execution times vary on every run). To test without altering them, run the program from a temporary directory, for example:
> `mkdir /tmp/test && cd /tmp/test && java -cp /path/to/repo/target/classes ec.edu.uce.algoritmos.Main`

## Counting Criteria (Activity 3)

- **Comparison:** Every comparison between two array elements (for example, `a[j] > a[j+1]`). Conditions involving array indices are not counted.
- **Movement:** In Bubble and Selection Sort, each swap counts as 1. In Insertion and Shell Sort, each shift (`a[j] = a[j - gap]`) counts as 1. Placing the stored key element into its final position is not counted.
- **Time:** `System.nanoTime()` measured strictly around the sorting loop, excluding array copying, data generation, and console output.

## Benchmark Methodology

- Data generated with a fixed seed (`20261125`) outside the timer; all four algorithms receive identical array copies.
- Five data types: ascending, descending, random, repeated (5 distinct values), and nearly sorted (~5% of elements swapped).
- JVM warm-up prior to measurement and 5 runs per combination (the requested minimum is 3); the average is reported.
- Every output is verified using `SortVerifier` (confirming it is sorted and a valid permutation of the original array).
- Comparisons and movements are deterministic and reproducible; execution times depend on the hardware. The specifications of the machine used are recorded in `resultados/tablas/machine.txt`.

## Report

The practical report (implementation descriptions, test cases, tables, charts, complexity analysis, answers to the seven questions, and conclusions) is located at [`informe/informe-practico.pdf`](informe/informe-practico.pdf).
