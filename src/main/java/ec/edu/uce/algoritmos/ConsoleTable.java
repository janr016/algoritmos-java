package ec.edu.uce.algoritmos;

import java.util.ArrayList;
import java.util.List;

/** Prints rows as an aligned text table. Cells that are plain numbers are aligned to the right. */
final class ConsoleTable {

    private final String[] headers;
    private final List<String[]> rows = new ArrayList<>();

    ConsoleTable(String... headers) {
        this.headers = headers;
    }

    void addRow(String... cells) {
        if (cells.length != headers.length) {
            throw new IllegalArgumentException("Expected " + headers.length + " cells, got " + cells.length);
        }
        rows.add(cells);
    }

    void print() {
        int[] widths = new int[headers.length];
        for (int c = 0; c < headers.length; c++) {
            widths[c] = Ansi.strip(headers[c]).length();
            for (String[] row : rows) {
                widths[c] = Math.max(widths[c], Ansi.strip(row[c]).length());
            }
        }
        printSeparator(widths);
        printRow(headers, widths, false);
        printSeparator(widths);
        for (String[] row : rows) {
            printRow(row, widths, true);
        }
        printSeparator(widths);
    }

    private void printSeparator(int[] widths) {
        StringBuilder line = new StringBuilder("+");
        for (int width : widths) {
            line.append("-".repeat(width + 2)).append('+');
        }
        System.out.println(line);
    }

    private void printRow(String[] cells, int[] widths, boolean alignNumbers) {
        StringBuilder line = new StringBuilder("|");
        for (int c = 0; c < cells.length; c++) {
            String visible = Ansi.strip(cells[c]);
            String padding = " ".repeat(widths[c] - visible.length());
            boolean right = alignNumbers && visible.matches("-?[0-9][0-9.,]*");
            line.append(' ').append(right ? padding + cells[c] : cells[c] + padding).append(" |");
        }
        System.out.println(line);
    }
}
