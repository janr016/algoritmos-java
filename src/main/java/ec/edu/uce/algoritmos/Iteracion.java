package ec.edu.uce.algoritmos;

import java.util.Arrays;

/**
 * Actividad 2 (Semana 4) - Parte iterativa:
 *  1. Factorial de un entero no negativo (validando desbordamiento de long).
 *  2. Suma de los elementos de un arreglo.
 *  3. Valor maximo de un arreglo (rechazando arreglos vacios).
 *
 * Criterio de conteo:
 *  - operaciones: cada iteracion del ciclo cuenta las operaciones del cuerpo/control relevantes
 *    (multiplicacion y acumulacion en factorial; suma y acumulacion en suma; comparacion de maximo).
 */
public class Iteracion {

    /** Resultado de una ejecucion: valor calculado, operaciones y tiempo en nanosegundos. */
    public record Resultado(long valor, long operaciones, long nanos) { }

    /** Maximo valor de n cuyo factorial cabe en un long con signo (20! = 2432902008176640000). */
    public static final int MAX_FACTORIAL_LONG = 20;

    // ------------------------------------------------------------ 1. Factorial iterativo
    public static Resultado factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n no puede ser negativo: " + n);
        }
        if (n > MAX_FACTORIAL_LONG) {
            throw new IllegalArgumentException("n supera el limite de long (" + MAX_FACTORIAL_LONG + "!): " + n);
        }

        long inicio = System.nanoTime();
        long acumulado = 1L;
        long operaciones = 0;

        for (int i = 2; i <= n; i++) {
            acumulado *= i;
            operaciones++;
        }

        return new Resultado(acumulado, operaciones, System.nanoTime() - inicio);
    }

    // ------------------------------------------------------------ 2. Suma de arreglo iterativa
    public static Resultado sumaArreglo(int[] datos) {
        validarArregloNoNulo(datos);

        long inicio = System.nanoTime();
        long suma = 0L;
        long operaciones = 0;

        for (int valor : datos) {
            suma += valor;
            operaciones++;
        }

        return new Resultado(suma, operaciones, System.nanoTime() - inicio);
    }

    // ------------------------------------------------------------ 3. Maximo de arreglo iterativo
    public static Resultado maximoArreglo(int[] datos) {
        validarArregloNoVacio(datos);

        long inicio = System.nanoTime();
        long maximo = datos[0];
        long operaciones = 0;

        for (int i = 1; i < datos.length; i++) {
            operaciones++;
            if (datos[i] > maximo) {
                maximo = datos[i];
            }
        }

        return new Resultado(maximo, operaciones, System.nanoTime() - inicio);
    }

    // ------------------------------------------------------------ validaciones
    private static void validarArregloNoNulo(int[] datos) {
        if (datos == null) {
            throw new IllegalArgumentException("El arreglo no puede ser null");
        }
    }

    private static void validarArregloNoVacio(int[] datos) {
        validarArregloNoNulo(datos);
        if (datos.length == 0) {
            throw new IllegalArgumentException("El arreglo no puede estar vacio");
        }
    }

    // ------------------------------------------------------------ demostracion
    public static void main(String[] args) {
        int numero = 5;
        int[] valores = {8, 3, 10, 2, 7};

        Resultado f5 = factorial(numero);
        Resultado suma = sumaArreglo(valores);
        Resultado max = maximoArreglo(valores);

        System.out.println("=== Casos de prueba de la consigna ===");
        System.out.println("Factorial de " + numero + " = " + f5.valor() + " (ops=" + f5.operaciones() + ")");
        System.out.println("Suma de " + Arrays.toString(valores) + " = " + suma.valor() + " (ops=" + suma.operaciones() + ")");
        System.out.println("Maximo de " + Arrays.toString(valores) + " = " + max.valor() + " (ops=" + max.operaciones() + ")");

        System.out.println("\n=== Casos limite ===");
        Resultado f0 = factorial(0);
        Resultado f1 = factorial(1);
        Resultado sumaVacio = sumaArreglo(new int[]{});
        Resultado maxUno = maximoArreglo(new int[]{-42});

        System.out.println("0! = " + f0.valor() + " (ops=" + f0.operaciones() + ")");
        System.out.println("1! = " + f1.valor() + " (ops=" + f1.operaciones() + ")");
        System.out.println("Suma arreglo vacio = " + sumaVacio.valor() + " (ops=" + sumaVacio.operaciones() + ")");
        System.out.println("Maximo de [-42] = " + maxUno.valor() + " (ops=" + maxUno.operaciones() + ")");

        System.out.println("\n=== Validaciones de error esperadas ===");
        probarError("n = -1", () -> factorial(-1));
        probarError("n = 21 (desborda long)", () -> factorial(21));
        probarError("maximo en arreglo vacio", () -> maximoArreglo(new int[]{}));
    }

    private static void probarError(String descripcion, Runnable accion) {
        try {
            accion.run();
            System.out.println("FALLO (no lanzo error): " + descripcion);
        } catch (IllegalArgumentException ex) {
            System.out.println("OK (" + descripcion + ") -> " + ex.getMessage());
        }
    }
}