package app.servicio;

import app.implementaciones.PilaLD;
import app.interfaces.PilaTDA;

public class HistorialServicio {

    // Ya no necesitamos un atributo "historial" global aquí, 
    // porque el historial vive dentro de cada Cliente.

    /**
     * Muestra el historial de una pila específica (la de un cliente).
     */
    public static void mostrarHistorialPersonal(PilaTDA<String> pila) {
        if (pila == null || pila.PilaVacia()) {
            System.out.println("[!] El historial está vacío.");
            return;
        }

        // Usamos la interfaz PilaTDA para la auxiliar
        PilaTDA<String> tempPila = new PilaLD<>();
        tempPila.InicializarPila();

        System.out.println("\n=== HISTORIAL DE ACTIVIDAD ===");

        // Desapilamos para mostrar
        while (!pila.PilaVacia()) {
            String accion = pila.Tope();
            System.out.println(" • " + accion);
            tempPila.Apilar(accion);
            pila.Desapilar();
        }

        // Restauramos la pila original para no perder los datos
        while (!tempPila.PilaVacia()) {
            pila.Apilar(tempPila.Tope());
            tempPila.Desapilar();
        }
        System.out.println("==============================\n");
    }

    /**
     * Lógica para la opción 4 del menú (Deshacer)
     */
    public static void deshacerUltimaAccion(PilaTDA<String> pila) {
        if (pila != null && !pila.PilaVacia()) {
            String ultima = pila.Tope();
            System.out.println("[←] Deshaciendo última acción: " + ultima);
            pila.Desapilar();
        } else {
            System.out.println("[!] No hay acciones para deshacer.");
        }
    }
}