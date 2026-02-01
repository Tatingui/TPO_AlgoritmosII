// Maneja la Pila Deque

/*
 * Gestiona el historial de cambios permitiendo la función "Deshacer".
 * Utiliza el concepto de Pila (LIFO).
 */

package servicio;

import implementaciones.PilaLD;
import modelo.Accion;

public class HistorialServicio {
    private final PilaLD<Accion> historial;

    public HistorialServicio() {
        historial = new PilaLD<>();
        historial.InicializarPila();
    }

    public void registrarAccion(String tipo, String descripcion) {
        historial.Apilar(new Accion(tipo, descripcion));
    }

    public void deshacerUltimaAccion() {
        if (!historial.PilaVacia()) {
            Accion ultimaAccion = historial.Tope();
            // Lógica para revertir la acción según su tipo
            // Por ejemplo, si es "AgregarAmigo", eliminar ese amigo
            // Si es "PublicarEstado", eliminar esa publicación, etc.
            System.out.println("Deshaciendo acción: " + ultimaAccion);
            historial.Desapilar();
        } else {
            System.out.println("No hay acciones para deshacer.");
        }
    }

    public void mostrarHistorial() {
        PilaLD<Accion> tempPila = new PilaLD<>();
        tempPila.InicializarPila();

        System.out.println("Historial de acciones:");
        while (!historial.PilaVacia()) {
            Accion accion = historial.Tope();
            System.out.println(accion);
            tempPila.Apilar(accion);
            historial.Desapilar();
        }

        // Restaurar el historial original
        while (!tempPila.PilaVacia()) {
            historial.Apilar(tempPila.Tope());
            tempPila.Desapilar();
        }
    }
}
