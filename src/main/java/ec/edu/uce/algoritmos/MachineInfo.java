package ec.edu.uce.algoritmos;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Basic characteristics of the computer and the benchmark settings, saved next to the results. */
public final class MachineInfo {

    private MachineInfo() {
    }

    public static List<String> describe(int[] sizes, int repetitions) {
        Runtime runtime = Runtime.getRuntime();
        List<String> lines = new ArrayList<>();
        lines.add("Date: " + LocalDateTime.now().withNano(0));
        lines.add("Operating system: " + System.getProperty("os.name") + " " + System.getProperty("os.version")
                + " (" + System.getProperty("os.arch") + ")");
        lines.add("Processor: " + processorName());
        lines.add("Logical processors: " + runtime.availableProcessors());
        lines.add("Physical memory: " + physicalMemory());
        lines.add("Java: " + System.getProperty("java.version") + " (" + System.getProperty("java.vm.name")
                + ", " + System.getProperty("java.vendor") + ")");
        lines.add(String.format(Locale.ROOT, "Maximum JVM heap: %.1f GB", runtime.maxMemory() / 1e9));
        lines.add("Sizes: " + java.util.Arrays.toString(sizes));
        lines.add("Repetitions per combination: " + repetitions);
        lines.add("Warm-up: yes, before measuring");
        lines.add("Random seed: " + Benchmark.SEED);
        return lines;
    }

    public static void write(Path file, int[] sizes, int repetitions) throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(file, describe(sizes, repetitions), StandardCharsets.UTF_8);
    }

    private static String physicalMemory() {
        try {
            if (ManagementFactory.getOperatingSystemMXBean() instanceof com.sun.management.OperatingSystemMXBean os) {
                return String.format(Locale.ROOT, "%.1f GB", os.getTotalMemorySize() / 1e9);
            }
        } catch (RuntimeException | LinkageError ignored) {
            // fall through
        }
        return "not available";
    }

    /** Tries the usual place of each system; "not available" if none works. */
    private static String processorName() {
        try {
            String os = System.getProperty("os.name", "").toLowerCase();
            if (os.contains("mac")) {
                Process process = new ProcessBuilder("sysctl", "-n", "machdep.cpu.brand_string")
                        .redirectErrorStream(true).start();
                String text = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
                process.waitFor();
                if (!text.isEmpty()) {
                    return text;
                }
            } else if (os.contains("linux")) {
                for (String line : Files.readAllLines(Path.of("/proc/cpuinfo"))) {
                    if (line.startsWith("model name")) {
                        return line.substring(line.indexOf(':') + 1).trim();
                    }
                }
            } else {
                String windows = System.getenv("PROCESSOR_IDENTIFIER");
                if (windows != null) {
                    return windows;
                }
            }
        } catch (IOException | InterruptedException | RuntimeException ignored) {
            // fall through
        }
        return "not available";
    }
}
