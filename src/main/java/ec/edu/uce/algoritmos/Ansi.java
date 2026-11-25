package ec.edu.uce.algoritmos;

/** Optional ANSI colors. They are switched off when the terminal does not support them. */
final class Ansi {

    private static final String RESET = "\u001B[0m";
    private static boolean enabled = detectSupport();

    private Ansi() {
    }

    /** Colors are used only if TERM is set (and is not "dumb") and the NO_COLOR variable is absent. */
    private static boolean detectSupport() {
        String term = System.getenv("TERM");
        return System.getenv("NO_COLOR") == null && term != null && !term.equals("dumb");
    }

    static void setEnabled(boolean value) {
        enabled = value;
    }

    static String green(String text) {
        return paint("\u001B[32m", text);
    }

    static String red(String text) {
        return paint("\u001B[31m", text);
    }

    static String cyan(String text) {
        return paint("\u001B[36m", text);
    }

    static String bold(String text) {
        return paint("\u001B[1m", text);
    }

    /** Removes color codes, so we can measure how wide a text really looks on screen. */
    static String strip(String text) {
        return text.replaceAll("\u001B\\[[0-9;]*m", "");
    }

    private static String paint(String code, String text) {
        return enabled ? code + text + RESET : text;
    }
}
