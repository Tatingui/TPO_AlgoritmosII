package app;

// Busqueda por nombres: HashMap <String, Cliente> = O(1)
// Busqueda por scoring: TreeMap <Integer, Integer> = O(log n) (HashMap desordenaria esto)
// Historial de acciones: Stack <Accion> o Deque(Accion) = O(1)
// Solicitudes de Seguimiento: Queue <Solicitud> Para procesar en orden FIFO O(1)

/*
 * Punto de entrada del programa.
 * Orquesta la inicialización de los servicios y lanza la interfaz de usuario.
 */

import app.servicio.RedSocialManager;

public class Main {
    static void main(String[] args) {
        RedSocialManager sistema = new RedSocialManager();

        // 1. Se cargan los datos
        String rutaJson = "Clientes.json";
        sistema.cargarDesdeArchivo(rutaJson);

        // 2. Busqueda de personas en O(1) | HashMap
        System.out.println("\n--- Prueba de Búsqueda ---");
        sistema.buscarYMostrarCliente("Messi"); // No existe
        sistema.buscarYMostrarCliente("Alice"); // Si existe en el json

        // 3. Busqueda de ranking O(log n) | TreeMap
        System.out.println("\n--- Prueba de Ranking ---");
        sistema.imprimirRankingCompleto();
    }
}