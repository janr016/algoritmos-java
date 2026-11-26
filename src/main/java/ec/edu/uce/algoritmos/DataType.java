package ec.edu.uce.algoritmos;

/** The five kinds of data sets used by the benchmark (Activity 4). */
public enum DataType {
    ASCENDING("Ascending"),
    DESCENDING("Descending"),
    RANDOM("Random"),
    REPEATED("Repeated"),
    NEARLY_SORTED("Nearly sorted");

    private final String label;

    DataType(String label) {
        this.label = label;
    }

    /** Name shown in tables. */
    public String label() {
        return label;
    }

    /** Name used in file names, e.g. "nearly_sorted". */
    public String fileName() {
        return name().toLowerCase();
    }
}
