package ec.edu.uce.algoritmos;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.Test;

/** Stability of the four algorithms over Student records (menu option 5). */
class StabilityTest {

    private static Student[] students(int... grades) {
        Student[] list = new Student[grades.length];
        for (int i = 0; i < grades.length; i++) {
            list[i] = new Student(i + 1, "S" + (i + 1), grades[i]);
        }
        return list;
    }

    private static int[] codes(Student[] list) {
        int[] codes = new int[list.length];
        for (int i = 0; i < list.length; i++) {
            codes[i] = list[i].code();
        }
        return codes;
    }

    @Test
    void partOneExampleIsOrderedByGradeWithBetoBeforeDiego() {
        Student[] original = {new Student(1, "Ana", 8), new Student(2, "Beto", 6),
            new Student(3, "Carla", 9), new Student(4, "Diego", 6), new Student(5, "Elena", 7)};
        // expected order of codes: Beto, Diego, Elena, Ana, Carla
        assertArrayEquals(new int[] {2, 4, 5, 1, 3}, codes(StudentSorting.bubbleSort(original)));
        assertArrayEquals(new int[] {2, 4, 5, 1, 3}, codes(StudentSorting.insertionSort(original)));
    }

    @Test
    void bubbleAndInsertionAreStableOnManyRandomInputsWithTies() {
        Random random = new Random(7);
        for (int round = 0; round < 200; round++) {
            int[] grades = new int[30];
            for (int i = 0; i < grades.length; i++) {
                grades[i] = random.nextInt(5); // only 5 grades, so there are many ties
            }
            Student[] original = students(grades);
            assertTrue(StudentSorting.isStable(original, StudentSorting.bubbleSort(original)));
            assertTrue(StudentSorting.isStable(original, StudentSorting.insertionSort(original)));
        }
    }

    @Test
    void selectionIsNotStableWithA5B5C3() {
        Student[] original = students(5, 5, 3); // A5, B5, C3
        Student[] result = StudentSorting.selectionSort(original);
        assertArrayEquals(new int[] {3, 2, 1}, codes(result)); // C3, B5, A5
        assertFalse(StudentSorting.isStable(original, result));
    }

    @Test
    void shellIsNotStableWithA5B5C1D9() {
        Student[] original = students(5, 5, 1, 9); // A5, B5, C1, D9
        Student[] result = StudentSorting.shellSort(original);
        assertArrayEquals(new int[] {3, 2, 1, 4}, codes(result)); // C1, B5, A5, D9
        assertFalse(StudentSorting.isStable(original, result));
    }

    @Test
    void everyAlgorithmOrdersByGradeAndKeepsTheOriginalUntouched() {
        Student[] original = students(9, 4, 7, 4, 1, 9);
        int[] before = codes(original);
        Student[][] results = {StudentSorting.bubbleSort(original), StudentSorting.selectionSort(original),
            StudentSorting.insertionSort(original), StudentSorting.shellSort(original)};
        for (Student[] result : results) {
            for (int i = 0; i + 1 < result.length; i++) {
                assertTrue(result[i].grade() <= result[i + 1].grade());
            }
        }
        assertArrayEquals(before, codes(original));
    }
}
