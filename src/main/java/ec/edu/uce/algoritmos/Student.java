package ec.edu.uce.algoritmos;

/** A student record used only in the stability demo (option 5). Sorting is done by grade. */
public record Student(int code, String name, int grade) {

    @Override
    public String toString() {
        return name + " " + grade;
    }
}
