package ec.edu.uce.algoritmos;

import java.util.Arrays;

/** Console demo of menu option 5: which algorithms keep the order of students with equal grades. */
final class StabilityDemo {

    private interface StudentAlgorithm {
        Student[] sort(Student[] students);
    }

    private static final String[] NAMES = {"Bubble Sort", "Selection Sort", "Insertion Sort", "Shell Sort"};
    private static final StudentAlgorithm[] ALGORITHMS = {
        StudentSorting::bubbleSort, StudentSorting::selectionSort,
        StudentSorting::insertionSort, StudentSorting::shellSort
    };

    private StabilityDemo() {
    }

    static void run() {
        System.out.println();
        System.out.println(Ansi.bold("=== Stability demo (students sorted by grade) ==="));
        System.out.println("An algorithm is STABLE if students with the same grade keep their original order.");

        showCase("Case 1: the example from Part I (Beto and Diego both have 6)", new Student[] {
            new Student(1, "Ana", 8), new Student(2, "Beto", 6), new Student(3, "Carla", 9),
            new Student(4, "Diego", 6), new Student(5, "Elena", 7)});

        showCase("Case 2: A5, B5, C3 (Selection swaps C with A and breaks the order of A and B)",
                new Student[] {new Student(1, "A", 5), new Student(2, "B", 5), new Student(3, "C", 3)});

        showCase("Case 3: A5, B5, C1, D9 (Shell, gap 2, moves A past B)", new Student[] {
            new Student(1, "A", 5), new Student(2, "B", 5), new Student(3, "C", 1), new Student(4, "D", 9)});

        System.out.println();
        System.out.println("Conclusion: Bubble and Insertion are stable because they use a STRICT comparison (>).");
        System.out.println("Selection and Shell are not stable in general: a long-distance swap or gap");
        System.out.println("can jump over an equal element. A stable result in one case (like case 1 for these two");
        System.out.println("algorithms) is a coincidence, not a guarantee.");
    }

    private static void showCase(String title, Student[] original) {
        System.out.println();
        System.out.println(Ansi.cyan(title));
        System.out.println("Original: " + Arrays.toString(original));
        ConsoleTable table = new ConsoleTable("Algorithm", "Result", "Stable?");
        for (int i = 0; i < ALGORITHMS.length; i++) {
            Student[] result = ALGORITHMS[i].sort(original);
            boolean stable = StudentSorting.isStable(original, result);
            table.addRow(NAMES[i], Arrays.toString(result), stable ? Ansi.green("YES") : Ansi.red("NO"));
        }
        table.print();
    }
}
