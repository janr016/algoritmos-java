package ec.edu.uce.algoritmos;

import java.util.Arrays;

/**
 * Actividad 3 (Semana 5): Bubble, Selection, Insertion y Shell Sort hechos desde cero.
 * No se usa Arrays.sort, Collections.sort ni stream().sorted().
 *
 * CRITERIO DE CONTEO (igual para los cuatro algoritmos):
 *  - Comparacion: cada vez que se comparan dos valores del arreglo (o la clave con un valor del arreglo).
 *    Las condiciones de indices (j >= 0, i < n) NO cuentan.
 *  - Movimiento: cada vez que un elemento cambia de posicion, es decir, cada escritura de un
 *    elemento en el arreglo.
 *      * Un intercambio mueve 2 elementos = 2 movimientos (Bubble y Selection).
 *      * Un desplazamiento mueve 1 elemento = 1 movimiento (Insertion y Shell).
 *      * Colocar la clave en su hueco cuenta 1 movimiento, solo si cambia de posicion.
 *  - Todos ordenan de menor a mayor y reciben una copia del mismo arreglo.
 */
public class Ordenacion {

    /** Metricas de una ordenacion: comparaciones, movimientos y tiempo en nanosegundos. */
    public record Metricas(long comparaciones, long movimientos, long nanos) { }

    // ------------------------------------------------------------ Bubble Sort (optimizado)
    public static Metricas bubbleSort(int[] datos) {
        validar(datos);
        long inicio = System.nanoTime();
        long comparaciones = 0, movimientos = 0;
        int n = datos.length;

        for (int pasada = 0; pasada < n - 1; pasada++) {
            boolean huboIntercambio = false;
            for (int j = 0; j <= n - pasada - 2; j++) {
                comparaciones++;
                if (datos[j] > datos[j + 1]) {          // estricto: los iguales no se cruzan (estable)
                    int temporal = datos[j];
                    datos[j] = datos[j + 1];
                    datos[j + 1] = temporal;
                    movimientos += 2;                    // un intercambio = 2 movimientos
                    huboIntercambio = true;
                }
            }
            if (!huboIntercambio) break;                 // ya esta ordenado: termina antes
        }
        return new Metricas(comparaciones, movimientos, System.nanoTime() - inicio);
    }

    // ------------------------------------------------------------ Selection Sort
    public static Metricas selectionSort(int[] datos) {
        validar(datos);
        long inicio = System.nanoTime();
        long comparaciones = 0, movimientos = 0;
        int n = datos.length;

        for (int i = 0; i < n - 1; i++) {
            int minimo = i;
            for (int j = i + 1; j < n; j++) {
                comparaciones++;
                if (datos[j] < datos[minimo]) {          // estrictamente menor
                    minimo = j;
                }
            }
            if (minimo != i) {                           // solo intercambia si hace falta
                int temporal = datos[i];
                datos[i] = datos[minimo];
                datos[minimo] = temporal;
                movimientos += 2;
            }
        }
        return new Metricas(comparaciones, movimientos, System.nanoTime() - inicio);
    }

    // ------------------------------------------------------------ Insertion Sort
    public static Metricas insertionSort(int[] datos) {
        validar(datos);
        long inicio = System.nanoTime();
        long comparaciones = 0, movimientos = 0;
        int n = datos.length;

        for (int i = 1; i < n; i++) {
            int clave = datos[i];
            int j = i - 1;
            while (j >= 0) {
                comparaciones++;
                if (datos[j] > clave) {                  // estricto: se detiene ante un igual (estable)
                    datos[j + 1] = datos[j];             // desplazamiento
                    movimientos++;
                    j--;
                } else {
                    break;
                }
            }
            if (j + 1 != i) {                            // la clave cambio de posicion
                datos[j + 1] = clave;
                movimientos++;
            }
        }
        return new Metricas(comparaciones, movimientos, System.nanoTime() - inicio);
    }

    // ------------------------------------------------------------ Shell Sort (saltos de Shell: n/2, n/4, ..., 1)
    public static Metricas shellSort(int[] datos) {
        validar(datos);
        long inicio = System.nanoTime();
        long comparaciones = 0, movimientos = 0;
        int n = datos.length;

        for (int salto = n / 2; salto > 0; salto /= 2) {
            for (int i = salto; i < n; i++) {
                int clave = datos[i];
                int j = i;
                while (j >= salto) {
                    comparaciones++;
                    if (datos[j - salto] > clave) {
                        datos[j] = datos[j - salto];     // desplazamiento a distancia "salto"
                        movimientos++;
                        j -= salto;
                    } else {
                        break;
                    }
                }
                if (j != i) {
                    datos[j] = clave;
                    movimientos++;
                }
            }
        }
        return new Metricas(comparaciones, movimientos, System.nanoTime() - inicio);
    }

    // ------------------------------------------------------------ utilidades
    /** Comprueba que el arreglo este ordenado de menor a mayor. */
    public static boolean estaOrdenado(int[] datos) {
        for (int i = 1; i < datos.length; i++) {
            if (datos[i - 1] > datos[i]) return false;
        }
        return true;
    }

    private static void validar(int[] datos) {
        if (datos == null) {
            throw new IllegalArgumentException("El arreglo no puede ser null");
        }
    }

    // ------------------------------------------------------------ demostracion
    private interface Algoritmo { Metricas ordenar(int[] datos); }

    public static void main(String[] args) {
        String[] nombres = {"Bubble Sort", "Selection Sort", "Insertion Sort", "Shell Sort"};
        Algoritmo[] algoritmos = {Ordenacion::bubbleSort, Ordenacion::selectionSort,
                Ordenacion::insertionSort, Ordenacion::shellSort};

        // 1) Conjunto de la consigna
        int[] original = {45, 12, 90, 12, 3, 70, 45, 18};
        int[] esperado = {3, 12, 12, 18, 45, 45, 70, 90};
        System.out.println("=== Conjunto de la consigna ===");
        System.out.println("Entrada : " + Arrays.toString(original));
        System.out.println("Esperado: " + Arrays.toString(esperado));
        for (int k = 0; k < algoritmos.length; k++) {
            int[] copia = original.clone();
            Metricas m = algoritmos[k].ordenar(copia);
            System.out.printf("%-15s -> %s | comparaciones=%d movimientos=%d | %s%n",
                    nombres[k], Arrays.toString(copia), m.comparaciones(), m.movimientos(),
                    Arrays.equals(copia, esperado) ? "CORRECTO" : "ERROR");
        }
        System.out.println();

        // 2) Casos limite
        System.out.println("=== Casos limite (todos deben quedar ordenados) ===");
        int[][] casos = {
                {},
                {7},
                {5, 5, 5, 5, 5},
                {1, 2, 3, 4, 5, 6},
                {6, 5, 4, 3, 2, 1},
                {45, 12, 90, 12, 3, 70, 45, 18}
        };
        String[] etiquetas = {"vacio", "un elemento", "todos iguales", "ya ordenado", "orden inverso", "repetidos"};
        System.out.printf("%-15s", "caso");
        for (String nombre : nombres) System.out.printf("| %-30s", nombre);
        System.out.println();
        for (int c = 0; c < casos.length; c++) {
            System.out.printf("%-15s", etiquetas[c]);
            for (Algoritmo algoritmo : algoritmos) {
                int[] copia = casos[c].clone();
                Metricas m = algoritmo.ordenar(copia);
                String estado = estaOrdenado(copia) && copia.length == casos[c].length ? "ok" : "ERROR";
                System.out.printf("| %-4s comp=%-5d mov=%-5d    ", estado, m.comparaciones(), m.movimientos());
            }
            System.out.println();
        }
        System.out.println();

        // 3) Lista guia de la Parte I: Ana 18,5 | Luis 16,0 | Maria 19,2 | Pedro 15,7 | Sofia 18,5 (x10)
        int[] guia = {185, 160, 192, 157, 185};
        System.out.println("=== Lista guia de la Parte I (notas x10: 185, 160, 192, 157, 185) ===");
        for (int k = 0; k < algoritmos.length; k++) {
            int[] copia = guia.clone();
            Metricas m = algoritmos[k].ordenar(copia);
            System.out.printf("%-15s -> %s | comparaciones=%d movimientos=%d%n",
                    nombres[k], Arrays.toString(copia), m.comparaciones(), m.movimientos());
        }
    }
}