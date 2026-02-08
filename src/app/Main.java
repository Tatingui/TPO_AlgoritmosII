package app;

import app.implementaciones.ColaLD;
import app.interfaces.ColaTDA;
import app.modelo.Solicitud;
import app.servicio.HistorialServicio;
import app.servicio.RedSocialManager;
import app.servicio.SolicitudesServicio;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {

    private static String key(String seguidor, String seguido) {
        return seguidor.trim().toLowerCase() + "->" + seguido.trim().toLowerCase();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Al instanciarse, el manager ya carga el JSON automáticamente (Punto 4)
        RedSocialManager manager = new RedSocialManager();
        HistorialServicio historial = new HistorialServicio();

        // Estructura para solicitudes pendientes (Punto 3)
        ColaTDA<Solicitud> pendientes = new ColaLD<>();
        pendientes.InicializarCola();

        // Control de estados para evitar duplicados
        Set<String> pendientesSet = new HashSet<>();
        Set<String> aceptadasSet = new HashSet<>();
        Set<String> rechazadasSet = new HashSet<>();

        boolean salir = false;

        System.out.println("======================================");
        System.out.println("   SISTEMA DE RED SOCIAL INICIADO     ");
        System.out.println("======================================");

        while (!salir) {
            System.out.println("\n=== MENÚ PRINCIPAL ===");
            System.out.println("1) Buscar cliente por nombre");
            System.out.println("2) Buscar clientes por scoring");
            System.out.println("3) Mostrar ranking completo (Prioridad)");
            System.out.println("4) Deshacer última acción (Pila)");
            System.out.println("5) Mostrar historial completo");
            System.out.println("--- Gestión de Solicitudes ---");
            System.out.println("6) Enviar solicitud de seguimiento");
            System.out.println("7) Ver próxima solicitud en cola");
            System.out.println("8) Aceptar próxima solicitud");
            System.out.println("9) Rechazar próxima solicitud");
            System.out.println("0) Salir");
            System.out.print("Seleccione una opción: ");

            String opcion = sc.nextLine().trim();

            try {
                switch (opcion) {
                    case "1" -> {
                        System.out.print("Nombre a buscar: ");
                        String nombre = sc.nextLine().trim();
                        manager.buscarYMostrarCliente(nombre);
                        historial.registrarAccion("BuscarNombre", "Búsqueda: " + nombre);
                    }

                    case "2" -> {
                        System.out.print("Scoring exacto a buscar: ");
                        int scoring = Integer.parseInt(sc.nextLine().trim());
                        manager.buscarYMostrarPorScoring(scoring);
                        historial.registrarAccion("BuscarScoring", "Scoring: " + scoring);
                    }

                    case "3" -> {
                        manager.imprimirRankingCompleto();
                        historial.registrarAccion("VerRanking", "Consulta de ranking");
                    }

                    case "4" -> historial.deshacerUltimaAccion();

                    case "5" -> historial.mostrarHistorial();

                    case "6" -> {
                        System.out.print("Tu nombre (Emisor): ");
                        String seguidor = sc.nextLine().trim();
                        System.out.print("Nombre a seguir (Receptor): ");
                        String seguido = sc.nextLine().trim();

                        if (seguidor.equalsIgnoreCase(seguido)) {
                            System.out.println("Error: No puedes seguirte a ti mismo.");
                            break;
                        }

                        SolicitudesServicio.enviarSolicitud(pendientes, seguidor, seguido, manager.getRepositorio());
                        historial.registrarAccion("EnviarSolicitud", "De " + seguidor + " a " + seguido);
                    }

                    case "7" -> {
                        if (pendientes.ColaVacia()) {
                            System.out.println("No hay solicitudes en la cola.");
                        } else {
                            System.out.println("Siguiente en espera: " + pendientes.Primero());
                        }
                    }

                    case "8" -> {
                        if (pendientes.ColaVacia()) {
                            System.out.println("Cola vacía.");
                            break;
                        }
                        Solicitud s = pendientes.Primero();

                        SolicitudesServicio.procesarSiguiente(pendientes, true);

                        historial.registrarAccion("AceptarSolicitud", "Aceptada: " + s);
                    }

                    case "9" -> {
                        if (pendientes.ColaVacia()) {
                            System.out.println("Cola vacía.");
                            break;
                        }
                        Solicitud s = pendientes.Primero();

                        SolicitudesServicio.procesarSiguiente(pendientes, false);

                        historial.registrarAccion("RechazarSolicitud", "Rechazada: " + s);
                    }

                    case "0" -> salir = true;
                    default -> System.out.println("Opción no válida.");
                }
            } catch (Exception e) {
                System.out.println("Ocurrió un error: " + e.getMessage());
            }
        }

        System.out.println("Programa finalizado. ¡Hasta luego!");
        sc.close();
    }
}