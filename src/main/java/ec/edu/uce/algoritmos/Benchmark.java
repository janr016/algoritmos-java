package ec.edu.uce.algoritmos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Function;

/**
 * Actividad 4 (Semana 5): benchmark de Bubble, Selection, Insertion y Shell Sort.
 *
 * Diseno de la medicion:
 *  - 4 tamanos (10, 100, 1000, 10000) x 5 tipos de datos x 4 algoritmos.
 *  - Cada combinacion se ejecuta EJECUCIONES veces y se promedia el tiempo.
 *  - Antes de medir hay un calentamiento de la JVM (CALENTAMIENTO ejecuciones descartadas).
 *  - Los datos se generan UNA vez por (tamano, tipo) con semilla fija y cada ejecucion usa una COPIA identica.
 *  - El tiempo mide solo el metodo de ordenacion: no incluye generar datos, copiarlos ni imprimir.
 *  - Comparaciones y movimientos no dependen del equipo (son deterministas); el tiempo si.
 *  - Resultados: tabla en consola y archivo resultados/tablas/benchmark.csv
 */
public class Benchmark {

    static final int[] TAMANOS = {10, 100, 1000, 10000};
    static final int EJECUCIONES = 5;
    static final int CALENTAMIENTO = 3;

    enum TipoDatos {
        ASCENDENTES("Ascendentes"),
        DESCENDENTES("Descendentes"),
        ALEATORIOS("Aleatorios"),
        REPETIDOS("Repetidos"),
        CASI_ORDENADOS("Casi ordenados");

        final String etiqueta;
        TipoDatos(String etiqueta) { this.etiqueta = etiqueta; }
    }

    /** Una fila de la tabla de resultados. */
    record Fila(String algoritmo, int n, String tipo, long comparaciones, long movimientos, double tiempoMs) { }

    // ------------------------------------------------------------ generacion de datos
    static int[] generar(TipoDatos tipo, int n) {
        Random azar = new Random(1000L + n + tipo.ordinal());      // semilla fija: resultados repetibles
        int[] datos = new int[n];
        switch (tipo) {
            case ASCENDENTES -> {
                for (int i = 0; i < n; i++) datos[i] = i;
            }
            case DESCENDENTES -> {
                for (int i = 0; i < n; i++) datos[i] = n - i;
            }
            case ALEATORIOS -> {
                for (int i = 0; i < n; i++) datos[i] = azar.nextInt(n * 10);
            }
            case REPETIDOS -> {                                     // pocos valores distintos: n/10 (minimo 2)
                int distintos = Math.max(2, n / 10);
                for (int i = 0; i < n; i++) datos[i] = azar.nextInt(distintos);
            }
            case CASI_ORDENADOS -> {                                // ordenados con ~5% de pares intercambiados
                for (int i = 0; i < n; i++) datos[i] = i;
                int cambios = Math.max(1, n / 20);
                for (int k = 0; k < cambios; k++) {
                    int a = azar.nextInt(n), b = azar.nextInt(n);
                    int temporal = datos[a]; datos[a] = datos[b]; datos[b] = temporal;
                }
            }
        }
        return datos;
    }

    // ------------------------------------------------------------ medicion
    static Fila medir(String nombre, Function<int[], Ordenacion.Metricas> algoritmo, int[] original, TipoDatos tipo) {
        for (int k = 0; k < CALENTAMIENTO; k++) {                   // calentamiento: no se registra
            algoritmo.apply(original.clone());
        }
        long sumaNanos = 0;
        Ordenacion.Metricas ultima = null;
        for (int k = 0; k < EJECUCIONES; k++) {
            int[] copia = original.clone();                         // copia fuera del tiempo medido
            ultima = algoritmo.apply(copia);
            if (!Ordenacion.estaOrdenado(copia)) {
                throw new IllegalStateException(nombre + " no ordeno correctamente (n=" + original.length + ")");
            }
            sumaNanos += ultima.nanos();
        }
        double promedioMs = sumaNanos / (double) EJECUCIONES / 1_000_000.0;
        return new Fila(nombre, original.length, tipo.etiqueta, ultima.comparaciones(), ultima.movimientos(), promedioMs);
    }

    // ------------------------------------------------------------ salida
    static String describirEquipo() {
        Runtime rt = Runtime.getRuntime();
        return "Sistema operativo : " + System.getProperty("os.name") + " " + System.getProperty("os.version")
                + " (" + System.getProperty("os.arch") + ")\n"
                + "Procesadores logicos: " + rt.availableProcessors() + "\n"
                + "Memoria maxima JVM : " + rt.maxMemory() / (1024 * 1024) + " MB\n"
                + "Java              : " + Runtime.version() + " (" + System.getProperty("java.vendor") + ")\n"
                + "Ejecuciones por combinacion: " + EJECUCIONES + " (mas " + CALENTAMIENTO + " de calentamiento)\n";
    }

    static void guardarCsv(List<Fila> filas, Path ruta) throws IOException {
        StringBuilder csv = new StringBuilder("Algoritmo,n,TipoDatos,Comparaciones,Movimientos,TiempoPromedioMs\n");
        for (Fila f : filas) {
            csv.append(String.format(Locale.ROOT, "%s,%d,%s,%d,%d,%.4f%n",
                    f.algoritmo(), f.n(), f.tipo(), f.comparaciones(), f.movimientos(), f.tiempoMs()));
        }
        Files.createDirectories(ruta.getParent());
        Files.writeString(ruta, csv.toString(), StandardCharsets.UTF_8);
    }

    // ------------------------------------------------------------ programa principal
    public static void main(String[] args) throws IOException {
        String[] nombres = {"Bubble Sort", "Selection Sort", "Insertion Sort", "Shell Sort"};
        List<Function<int[], Ordenacion.Metricas>> algoritmos = List.of(
                Ordenacion::bubbleSort, Ordenacion::selectionSort,
                Ordenacion::insertionSort, Ordenacion::shellSort);

        System.out.println("=== Equipo de prueba ===");
        System.out.print(describirEquipo());
        System.out.println("(Anota tambien el modelo de procesador y la RAM de tu computador en el informe)\n");

        System.out.println("Calentando la JVM...");
        int[] base = generar(TipoDatos.ALEATORIOS, 1000);
        for (int k = 0; k < 20; k++) {
            for (Function<int[], Ordenacion.Metricas> a : algoritmos) a.apply(base.clone());
        }

        System.out.println("Midiendo (puede tardar unos segundos)...\n");
        List<Fila> filas = new ArrayList<>();
        for (int n : TAMANOS) {
            for (TipoDatos tipo : TipoDatos.values()) {
                int[] datos = generar(tipo, n);
                for (int k = 0; k < algoritmos.size(); k++) {
                    filas.add(medir(nombres[k], algoritmos.get(k), datos, tipo));
                }
            }
        }

        System.out.printf(Locale.ROOT, "%-15s %7s %-15s %14s %12s %18s%n",
                "Algoritmo", "n", "Tipo de datos", "Comparaciones", "Movimientos", "Tiempo promedio (ms)");
        for (Fila f : filas) {
            System.out.printf(Locale.ROOT, "%-15s %7d %-15s %14d %12d %18.4f%n",
                    f.algoritmo(), f.n(), f.tipo(), f.comparaciones(), f.movimientos(), f.tiempoMs());
        }

        Path csv = Path.of("resultados", "tablas", "benchmark.csv");
        guardarCsv(filas, csv);
        Files.writeString(Path.of("resultados", "tablas", "equipo.txt"), describirEquipo(), StandardCharsets.UTF_8);
        System.out.println("\nArchivos guardados: " + csv.toAbsolutePath());
        System.out.println("Tambien: resultados/tablas/equipo.txt");
    }
}