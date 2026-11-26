package ec.edu.uce.algoritmos;

/**
 * The same four algorithms as in Sorting, but over Student records and ordered by grade.
 * It exists ONLY for the stability demo: the official algorithms (Sorting) stay clean and
 * work on int[]. The loops are identical; only a[x] > a[y] became a[x].grade() > a[y].grade().
 * Each method works on a copy and returns the sorted copy.
 
 */
public final class StudentSorting {

    private StudentSorting() {
    }

    /** Strictly greater: equal grades are never swapped, so Bubble is stable. */
    public static Student[] bubbleSort(Student[] input) {
        Student[] a = input.clone();
        boolean swapped = true;
        for (int pass = 0; pass < a.length - 1 && swapped; pass++) {
            swapped = false;
            for (int j = 0; j < a.length - 1 - pass; j++) {
                if (a[j].grade() > a[j + 1].grade()) {
                    Student temp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = temp;
                    swapped = true;
                }
            }
        }
        return a;
    }

    /** The long-distance swap can jump over an equal element, so Selection is NOT stable.    */
    public static Student[] selectionSort(Student[] input) {
        Student[] a = input.clone();
        for (int i = 0; i < a.length - 1; i++) {
            int smallest = i;
            for (int j = i + 1; j < a.length; j++) {
                if (a[j].grade() < a[smallest].grade()) {
                    smallest = j;
                }
            }
            if (smallest != i) {
                Student temp = a[i];
                a[i] = a[smallest];
                a[smallest] = temp;
            }
        }
        return a;
    }

    /** Shifts only strictly greater grades, so equal ones keep their order: Insertion is stable. */
    public static Student[] insertionSort(Student[] input) {
        Student[] a = input.clone();
        for (int i = 1; i < a.length; i++) {
            Student current = a[i];
            int j = i;
            while (j >= 1 && a[j - 1].grade() > current.grade()) {
                a[j] = a[j - 1];
                j--;
            }
            a[j] = current;
        }
        return a;
    }

    /** Gaps n/2, n/4, ..., 1. Elements far apart can cross equal ones, so Shell is NOT stable. */
    public static Student[] shellSort(Student[] input) {
        Student[] a = input.clone();
        for (int gap = a.length / 2; gap >= 1; gap /= 2) {
            for (int i = gap; i < a.length; i++) {
                Student current = a[i];
                int j = i;
                while (j >= gap && a[j - gap].grade() > current.grade()) {
                    a[j] = a[j - gap];
                    j -= gap;
                }
                a[j] = current;
            }
        }
        return a;
    }

    /**
     * True if students with the same grade appear in the same relative order as in the original.
     * Codes are unique, so we look up each code's position in the original list.
     */
    public static boolean isStable(Student[] original, Student[] result) {
        for (int i = 0; i + 1 < result.length; i++) {
            if (result[i].grade() == result[i + 1].grade()
                    && positionOf(original, result[i]) > positionOf(original, result[i + 1])) {
                return false;
            }
        }
        return true;
    }

    private static int positionOf(Student[] list, Student student) {
        for (int i = 0; i < list.length; i++) {
            if (list[i].code() == student.code()) {
                return i;
            }
        }
        return -1;
    }
}
