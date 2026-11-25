package ec.edu.uce.algoritmos;

import java.util.Random;

/**
 * Actividad 1 (Semana 3): tres algoritmos con distinto crecimiento.
 *
 * Criterio de conteo (el mismo del informe de la Parte I):
 *  - "dominante": cuantas veces se ejecuta la operacion que mas se repite.
 *  - "total": T(n) = inicializaciones + comparaciones de la condicion + actualizaciones + operacion dominante.
 *  - Una comparacion o suma dentro del cuerpo cuenta como 1 operacion.
 *
 * A: suma lineal          -> dominante = n,          T(n) = 3n + 3,        O(n)
 * B: ciclos anidados      -> dominante = n * n,      T(n) = 3n^2 + 4n + 2, O(n^2)
 * C: reduccion a la mitad -> dominante = floor(log2 n), T(n) = 2k + 1,     O(log n)
 */
public class Complejidad {

    /** Resultado de una ejecucion: valor calculado, operacion dominante, total de operaciones y tiempo en ns. */
    public record Resultado(long valor, long dominante, long total, long nanos) { }

    // ---------------------------------------------------------------- A
    /** Algoritmo A: recorre el arreglo y suma sus elementos. */
    public static Resultado sumaLineal(int[] datos) {
        validar(datos);
        long inicio = System.nanoTime();
        long dominante = 0;
        long total = 0;

        long suma = 0;  total++;                 // inicializar acumulador
        int i = 0;      total++;                 // inicializar indice
        while (i < datos.length) {
            total++;                             // comprobacion de la condicion (verdadera)
            suma += datos[i];                    // OPERACION DOMINANTE
            dominante++; total++;
            i++;         total++;                // actualizacion del indice
        }
        total++;                                 // ultima comprobacion (falsa)

        return new Resultado(suma, dominante, total, System.nanoTime() - inicio);
    }

    // ---------------------------------------------------------------- B
    /**
     * Algoritmo B: compara cada elemento con todos los demas (y consigo mismo) con dos ciclos anidados.
     * El valor devuelto es el numero de pares de posiciones distintas con el mismo valor.
     */
    public static Resultado compararTodosConTodos(int[] datos) {
        validar(datos);
        int n = datos.length;
        long inicio = System.nanoTime();
        long dominante = 0;
        long total = 0;

        long coincidencias = 0;
        int i = 0;               total++;        // inicializar i
        while (i < n) {
            total++;                             // condicion externa (verdadera)
            int j = 0;           total++;        // inicializar j
            while (j < n) {
                total++;                         // condicion interna (verdadera)
                if (i != j && datos[i] == datos[j]) {   // OPERACION DOMINANTE: comparar(i, j)
                    coincidencias++;
                }
                dominante++; total++;
                j++;             total++;        // actualizacion de j
            }
            total++;                             // condicion interna (falsa)
            i++;                 total++;        // actualizacion de i
        }
        total++;                                 // condicion externa (falsa)

        return new Resultado(coincidencias, dominante, total, System.nanoTime() - inicio);
    }

    // ---------------------------------------------------------------- C
    /** Algoritmo C: reduce un numero positivo a la mitad hasta llegar a 1. El valor devuelto es el numero de pasos. */
    public static Resultado reducirALaMitad(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("n debe ser positivo (>= 1), se recibio: " + n);
        }
        long inicio = System.nanoTime();
        long dominante = 0;
        long total = 0;

        int m = n;
        while (m > 1) {
            total++;                             // condicion (verdadera)
            m = m / 2;                           // OPERACION DOMINANTE: la division
            dominante++; total++;
        }
        total++;                                 // ultima comprobacion (falsa)

        return new Resultado(dominante, dominante, total, System.nanoTime() - inicio);
    }

    // ------------------------------------------------- formulas teoricas
    public static long teoricoA(int n) { return 3L * n + 3; }

    public static long teoricoB(int n) { return 3L * n * n + 4L * n + 2; }

    public static long teoricoC(int n) { return 2L * pisoLog2(n) + 1; }

    /** floor(log2 n) para n >= 1. */
    public static int pisoLog2(int n) { return 31 - Integer.numberOfLeadingZeros(n); }

    private static void validar(int[] datos) {
        if (datos == null) {
            throw new IllegalArgumentException("El arreglo no puede ser null");
        }
    }

    // ------------------------------------------------- demostracion
    public static void main(String[] args) {
        int[] tamanos = {10, 100, 1000, 10000};
        Random azar = new Random(42);            // semilla fija: los resultados se pueden repetir

        System.out.println("=== Caso del informe: n = 16 ===");
        Resultado c16 = reducirALaMitad(16);
        System.out.println("16 -> 8 -> 4 -> 2 -> 1 : " + c16.dominante() + " divisiones (log2 16 = 4)");
        System.out.println();

        System.out.println("=== Operaciones contadas frente a la teoria ===");
        System.out.printf("%-7s | %-12s %-12s %-6s | %-14s %-14s %-6s | %-6s %-6s %-6s%n",
                "n", "A dominante", "A T(n)=3n+3", "ok?",
                "B dominante", "B T(n)", "ok?",
                "C pasos", "C T(n)", "ok?");
        for (int n : tamanos) {
            int[] datos = new int[n];
            for (int k = 0; k < n; k++) datos[k] = azar.nextInt(n);

            Resultado a = sumaLineal(datos);
            Resultado b = compararTodosConTodos(datos);
            Resultado c = reducirALaMitad(n);

            System.out.printf("%-7d | %-12d %-12d %-6s | %-14d %-14d %-6s | %-6d %-6d %-6s%n",
                    n,
                    a.dominante(), a.total(), a.total() == teoricoA(n) ? "si" : "NO",
                    b.dominante(), b.total(), b.total() == teoricoB(n) ? "si" : "NO",
                    c.dominante(), c.total(), c.total() == teoricoC(n) ? "si" : "NO");
        }
        System.out.println();

        System.out.println("=== Tiempo promedio de 3 ejecuciones (ms), con una ejecucion de calentamiento ===");
        System.out.printf("%-7s | %-12s %-12s %-12s%n", "n", "A (ms)", "B (ms)", "C (ms)");
        for (int n : tamanos) {
            int[] datos = new int[n];
            for (int k = 0; k < n; k++) datos[k] = azar.nextInt(n);

            sumaLineal(datos); compararTodosConTodos(datos); reducirALaMitad(n);   // calentamiento
            long ta = 0, tb = 0, tc = 0;
            for (int r = 0; r < 3; r++) {
                ta += sumaLineal(datos).nanos();
                tb += compararTodosConTodos(datos).nanos();
                tc += reducirALaMitad(n).nanos();
            }
            System.out.printf("%-7d | %-12.4f %-12.4f %-12.4f%n", n, ta / 3e6, tb / 3e6, tc / 3e6);
        }
        System.out.println();
        System.out.println("Lectura: al multiplicar n por 10, A se multiplica por 10, B por 100 y C solo suma unos 3 pasos.");
        System.out.println("Los tiempos incluyen el costo de los contadores y dependen del equipo; lo que se compara son las operaciones.");
    }
}