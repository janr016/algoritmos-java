package ec.edu.uce.algoritmos;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.imageio.ImageIO;

/**
 * Draws the benchmark charts as PNG files using only the JDK (BufferedImage + Graphics2D).
 * It works without a screen. Both axes use a logarithmic scale, because sizes go from 10 to
 * 10,000 and times / comparisons span many orders of magnitude.
 * For every data type it writes two charts: time vs n, and comparisons vs n.
 */
public final class ChartWriter {

    private static final int WIDTH = 900;
    private static final int HEIGHT = 600;
    private static final int LEFT = 100;
    private static final int RIGHT = 40;
    private static final int TOP = 70;
    private static final int BOTTOM = 80;
    private static final Color[] COLORS = {
        new Color(0xD62728), new Color(0x1F77B4), new Color(0x2CA02C), new Color(0xFF7F0E)
    };

    static {
        System.setProperty("java.awt.headless", "true"); // no window is needed
    }

    private ChartWriter() {
    }

    /** Writes 2 PNG files per data type into the folder and returns their paths. */
    public static List<Path> writeAll(Path folder, List<BenchmarkRow> rows) throws IOException {
        Files.createDirectories(folder);
        List<Path> files = new ArrayList<>();
        double[] sizes = distinctSizes(rows);
        String[] names = Benchmark.algorithmNames();
        for (DataType type : DataType.values()) {
            double[][] times = new double[names.length][sizes.length];
            double[][] comparisons = new double[names.length][sizes.length];
            for (BenchmarkRow row : rows) {
                if (row.type() != type) {
                    continue;
                }
                int a = indexOf(names, row.algorithm());
                int s = indexOf(sizes, row.n());
                times[a][s] = row.averageMillis();
                comparisons[a][s] = row.comparisons();
            }
            Path timeFile = folder.resolve("time_" + type.fileName() + ".png");
            draw(timeFile, "Average sorting time - " + type.label() + " data", "Average time (ms)",
                    sizes, names, times);
            files.add(timeFile);
            Path comparisonsFile = folder.resolve("comparisons_" + type.fileName() + ".png");
            draw(comparisonsFile, "Number of comparisons - " + type.label() + " data", "Comparisons",
                    sizes, names, comparisons);
            files.add(comparisonsFile);
        }
        return files;
    }

    private static void draw(Path file, String title, String yLabel, double[] xs, String[] names, double[][] ys)
            throws IOException {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, WIDTH, HEIGHT);

            int plotW = WIDTH - LEFT - RIGHT;
            int plotH = HEIGHT - TOP - BOTTOM;

            double minY = Double.MAX_VALUE;
            double maxY = 0;
            for (double[] series : ys) {
                for (double y : series) {
                    if (y > 0) {
                        minY = Math.min(minY, y);
                        maxY = Math.max(maxY, y);
                    }
                }
            }
            if (maxY == 0) { // nothing to draw: keep a valid scale
                minY = 1;
                maxY = 10;
            }
            int lowPower = (int) Math.floor(Math.log10(minY));
            int highPower = (int) Math.ceil(Math.log10(maxY));
            if (highPower == lowPower) {
                highPower++;
            }
            double lx0 = Math.log10(xs[0]);
            double lx1 = Math.log10(xs[xs.length - 1]);
            if (lx1 == lx0) {
                lx0 -= 0.5;
                lx1 += 0.5;
            }

            // grid and axis labels
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
            FontMetrics fm = g.getFontMetrics();
            for (int k = lowPower; k <= highPower; k++) {
                int y = yPixel(Math.pow(10, k), lowPower, highPower, plotH);
                g.setColor(new Color(0xDDDDDD));
                g.drawLine(LEFT, y, LEFT + plotW, y);
                g.setColor(Color.DARK_GRAY);
                String label = powerLabel(k);
                g.drawString(label, LEFT - 8 - fm.stringWidth(label), y + fm.getAscent() / 2 - 2);
            }
            for (double x : xs) {
                int px = xPixel(x, lx0, lx1, plotW);
                g.setColor(new Color(0xDDDDDD));
                g.drawLine(px, TOP, px, TOP + plotH);
                g.setColor(Color.DARK_GRAY);
                String label = String.format(Locale.ROOT, "%,d", (long) x);
                g.drawString(label, px - fm.stringWidth(label) / 2, TOP + plotH + 20);
            }
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(1.5f));
            g.drawRect(LEFT, TOP, plotW, plotH);

            // titles
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
            String shownTitle = title;
            g.drawString(shownTitle, (WIDTH - g.getFontMetrics().stringWidth(shownTitle)) / 2, 35);
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            String xLabel = "Input size n (log scale)";
            g.drawString(xLabel, LEFT + (plotW - g.getFontMetrics().stringWidth(xLabel)) / 2, HEIGHT - 20);
            String shownYLabel = yLabel + " (log scale)";
            g.rotate(-Math.PI / 2);
            g.drawString(shownYLabel, -(TOP + (plotH + g.getFontMetrics().stringWidth(shownYLabel)) / 2), 25);
            g.rotate(Math.PI / 2);

            // one line per algorithm, with a different marker shape each
            for (int a = 0; a < names.length; a++) {
                g.setColor(COLORS[a % COLORS.length]);
                g.setStroke(new BasicStroke(2.5f));
                int previousX = -1;
                int previousY = -1;
                for (int s = 0; s < xs.length; s++) {
                    if (ys[a][s] <= 0) {
                        continue;
                    }
                    int px = xPixel(xs[s], lx0, lx1, plotW);
                    int py = yPixel(ys[a][s], lowPower, highPower, plotH);
                    if (previousX >= 0) {
                        g.drawLine(previousX, previousY, px, py);
                    }
                    marker(g, a, px, py);
                    previousX = px;
                    previousY = py;
                }
            }

            // legend (top-left corner of the plot, where the lines are rarely present)
            int legendX = LEFT + 15;
            int legendY = TOP + 15;
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            g.setColor(new Color(255, 255, 255, 230));
            g.fillRect(legendX - 8, legendY - 8, 170, names.length * 24 + 8);
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(legendX - 8, legendY - 8, 170, names.length * 24 + 8);
            for (int a = 0; a < names.length; a++) {
                int rowY = legendY + a * 24 + 8;
                g.setColor(COLORS[a % COLORS.length]);
                g.setStroke(new BasicStroke(2.5f));
                g.drawLine(legendX, rowY, legendX + 28, rowY);
                marker(g, a, legendX + 14, rowY);
                g.setColor(Color.BLACK);
                g.drawString(names[a], legendX + 38, rowY + 5);
            }
        } finally {
            g.dispose();
        }
        if (!ImageIO.write(image, "png", file.toFile())) {
            throw new IOException("No PNG writer available");
        }
    }

    private static void marker(Graphics2D g, int shape, int x, int y) {
        int r = 5;
        switch (shape % 4) {
            case 0 -> g.fillOval(x - r, y - r, 2 * r, 2 * r);
            case 1 -> g.fillRect(x - r, y - r, 2 * r, 2 * r);
            case 2 -> g.fillPolygon(new int[] {x - r - 1, x + r + 1, x}, new int[] {y + r, y + r, y - r - 1}, 3);
            default -> g.fillPolygon(new int[] {x, x + r + 1, x, x - r - 1}, new int[] {y - r - 1, y, y + r + 1, y}, 4);
        }
    }

    private static int xPixel(double x, double lx0, double lx1, int plotW) {
        return LEFT + (int) Math.round((Math.log10(x) - lx0) / (lx1 - lx0) * plotW);
    }

    private static int yPixel(double y, int lowPower, int highPower, int plotH) {
        double fraction = (Math.log10(y) - lowPower) / (highPower - lowPower);
        return TOP + plotH - (int) Math.round(fraction * plotH);
    }

    /** 10^k written as a plain number: 0.001, 1, 1,000, 1,000,000... */
    private static String powerLabel(int k) {
        if (k >= 0) {
            return String.format(Locale.ROOT, "%,d", (long) Math.pow(10, k));
        }
        return String.format(Locale.ROOT, "%." + (-k) + "f", Math.pow(10, k));
    }

    private static double[] distinctSizes(List<BenchmarkRow> rows) {
        List<Integer> sizes = new ArrayList<>();
        for (BenchmarkRow row : rows) {
            if (!sizes.contains(row.n())) {
                sizes.add(row.n());
            }
        }
        double[] result = new double[sizes.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = sizes.get(i);
        }
        return result; // rows are produced in increasing size, so this is already in order
    }

    private static int indexOf(String[] names, String name) {
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(name)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Unknown algorithm: " + name);
    }

    private static int indexOf(double[] values, int value) {
        for (int i = 0; i < values.length; i++) {
            if (values[i] == value) {
                return i;
            }
        }
        throw new IllegalArgumentException("Unknown size: " + value);
    }
}
