package modelo;
import interfaces.ColaTDA;


public class procesadorSolicitudes {

    public static void procesarSiguiente(ColaTDA<Solicitud> pendientes, boolean aceptar) {
        if (pendientes.ColaVacia()) {
            throw new RuntimeException("No hay solicitudes pendientes.");
        }

        // 1) Tomo la más antigua (la primera en la cola)
        Solicitud s = pendientes.Primero();

        // 2) Decido qué hacer con esa solicitud
        if (aceptar) {
            // crear la relación "seguidor sigue a seguido"
            System.out.println("ACEPTADA: " + s);
        } else {
            System.out.println("RECHAZADA: " + s);
        }

        // 3) La remuevo: recién ahora sale de pendientes
        pendientes.Desacolar();
    }
}
