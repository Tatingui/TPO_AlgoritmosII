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

        RedSocialManager manager = new RedSocialManager();
        HistorialServicio historial = new HistorialServicio();

        ColaTDA<Solicitud> pendientes = new ColaLD<>();
        pendientes.InicializarCola();

        // Para evitar duplicados y permitir reintento solo si fue rechazada
        Set<String> pendientesSet = new HashSet<>();
        Set<String> aceptadasSet = new HashSet<>();
        Set<String> rechazadasSet = new HashSet<>();

        boolean salir = false;
        boolean clientesCargados = false;

        while (!salir) {
            System.out.println("\n=== RED SOCIAL - MENÚ ===");
            System.out.println("1) Cargar clientes desde JSON (Clientes.json)");
            System.out.println("2) Buscar cliente por nombre");
            System.out.println("3) Buscar clientes por scoring");
            System.out.println("4) Mostrar ranking completo");
            System.out.println("5) Deshacer última acción");
            System.out.println("6) Mostrar historial");
            System.out.println("--- Solicitudes de seguimiento ---");
            System.out.println("7) Enviar solicitud (encolar)");
            System.out.println("8) Ver próxima solicitud");
            System.out.println("9) Aceptar próxima solicitud");
            System.out.println("10) Rechazar próxima solicitud");
            System.out.println("0) Salir");
            System.out.print("Opción: ");

            String opcion = sc.nextLine().trim();

            try {
                switch (opcion) {
                    case "1" -> {
                        System.out.println("Cargando archivo Clientes.json...");
                        String ruta = "Clientes.json";
                        manager.cargarDesdeArchivo(ruta);
                        clientesCargados = true;
                        historial.registrarAccion("CargarJSON", "Se cargó el archivo: " + ruta);
                    }

                    case "2" -> {
                        if (!clientesCargados) {
                            System.out.println("Primero cargá clientes (opción 1).");
                            break;
                        }
                        System.out.print("Nombre a buscar: ");
                        String nombre = sc.nextLine().trim();
                        manager.buscarYMostrarCliente(nombre);
                        historial.registrarAccion("BuscarPorNombre", "Búsqueda de: " + nombre);
                    }

                    case "3" -> {
                        if (!clientesCargados) {
                            System.out.println("Primero cargá clientes (opción 1).");
                            break;
                        }
                        System.out.print("Scoring a buscar (entero): ");
                        int scoring = Integer.parseInt(sc.nextLine().trim());
                        manager.buscarYMostrarPorScoring(scoring);
                        historial.registrarAccion("BuscarPorScoring", "Búsqueda de scoring: " + scoring);
                    }

                    case "4" -> {
                        if (!clientesCargados) {
                            System.out.println("Primero cargá clientes (opción 1).");
                            break;
                        }
                        manager.imprimirRankingCompleto();
                        historial.registrarAccion("MostrarRanking", "Se mostró el ranking completo");
                    }

                    case "5" -> historial.deshacerUltimaAccion();
                    case "6" -> historial.mostrarHistorial();

                    // ====== SOLICITUDES ======
                    case "7" -> {
                        System.out.print("Seguidor (quién quiere seguir): ");
                        String seguidor = sc.nextLine().trim();
                        System.out.print("Seguido (a quién quiere seguir): ");
                        String seguido = sc.nextLine().trim();

                        if (seguidor.equalsIgnoreCase(seguido)) {
                            System.out.println("No podés enviarte una solicitud a vos mismo.");
                            break;
                        }

                        String k = key(seguidor, seguido);

                        // Regla: no permitir duplicar si ya está pendiente o fue aceptada.
                        // Solo permitir repetir si estaba en rechazadas (y ahí la sacamos de rechazadas y la volvemos a encolar).
                        if (pendientesSet.contains(k)) {
                            System.out.println("Ya existe una solicitud PENDIENTE entre esas personas.");
                            break;
                        }
                        if (aceptadasSet.contains(k)) {
                            System.out.println("Esa solicitud ya fue ACEPTADA anteriormente. No se puede volver a generar.");
                            break;
                        }

                        // Si estaba rechazada, permitimos reintento
                        if (rechazadasSet.contains(k)) {
                            rechazadasSet.remove(k);
                        }

                        Solicitud s = new Solicitud(seguidor, seguido); // :contentReference[oaicite:1]{index=1}
                        pendientes.Acolar(s);
                        pendientesSet.add(k);

                        System.out.println("Solicitud enviada: " + s);
                        historial.registrarAccion("EncolarSolicitud", "Se encoló: " + s);
                    }

                    case "8" -> {
                        if (pendientes.ColaVacia()) {
                            System.out.println("No hay solicitudes pendientes.");
                        } else {
                            System.out.println("Próxima solicitud: " + pendientes.Primero());
                        }
                    }

                    case "9" -> {
                        if (pendientes.ColaVacia()) {
                            System.out.println("No hay solicitudes pendientes.");
                            break;
                        }

                        // Tomo la solicitud antes de procesarla para actualizar los sets
                        Solicitud s = pendientes.Primero();
                        String k = key(s.seguidor(), s.seguido());

                        // Esto IMPRIME y DESACOLA siempre :contentReference[oaicite:2]{index=2}
                        SolicitudesServicio.procesarSiguiente(pendientes, true);

                        // Actualizo control de duplicados
                        pendientesSet.remove(k);
                        aceptadasSet.add(k);

                        historial.registrarAccion("AceptarSolicitud", "Se aceptó: " + s);
                    }

                    case "10" -> {
                        if (pendientes.ColaVacia()) {
                            System.out.println("No hay solicitudes pendientes.");
                            break;
                        }

                        Solicitud s = pendientes.Primero();
                        String k = key(s.seguidor(), s.seguido());

                        // Esto IMPRIME y DESACOLA siempre :contentReference[oaicite:3]{index=3}
                        SolicitudesServicio.procesarSiguiente(pendientes, false);

                        pendientesSet.remove(k);
                        rechazadasSet.add(k);

                        historial.registrarAccion("RechazarSolicitud", "Se rechazó: " + s);
                    }

                    case "0" -> salir = true;
                    default -> System.out.println("Opción inválida.");
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }

        System.out.println("Fin del programa.");
        sc.close();
    }
}

