package app;

import app.modelo.Cliente;
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

        // Control de estados para evitar duplicados
        Set<String> aceptadasSet = new HashSet<>();
        Set<String> rechazadasSet = new HashSet<>();

        boolean salir = false;

        System.out.println("======================================");
        System.out.println("   SISTEMA DE RED SOCIAL INICIADO     ");
        System.out.println("======================================");

        while (!salir) {
            System.out.println("\n=== ITERACION 1 ===");
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
            System.out.println("\n=== ITERACION 2 ===");
            System.out.println("10) Consultar conexiones de un cliente");
            System.out.println("11) Mostrar cuarto nivel ABB de conexiones de un cliente");
            System.out.println("\n=== ITERACION 3 ===");
            System.out.println("12) Calcular distancia entre clientes (saltos)");

            System.out.println("0) Guardar cambios y salir");
            System.out.print("Seleccione una opción: ");

            String opcion = sc.nextLine().trim();

            try {
                switch (opcion) {
                    // En Main.java

                    case "1" -> {
                        System.out.print("Nombre a buscar: ");
                        String nombre = sc.nextLine().trim();
                        manager.buscarYMostrarCliente(nombre);
                    }

                    case "2" -> {
                        System.out.print("Scoring exacto a buscar: ");
                        int scoring = Integer.parseInt(sc.nextLine().trim());
                        manager.buscarYMostrarPorScoring(scoring);
                    }
                    case "3" -> {
                        manager.imprimirRankingCompleto();

                    }

                    case "4" -> { // Deshacer
                        System.out.print("Nombre del usuario: ");
                        String nombre = sc.nextLine().trim();
                        Cliente c = manager.getRepositorio().buscarPorNombre(nombre);
                        if (c != null) {
                            HistorialServicio.deshacerUltimaAccion(c.getHistorial());
                        }
                    }

                    case "5" -> { // Mostrar Historial
                        System.out.print("Nombre del usuario: ");
                        String nombre = sc.nextLine().trim();
                        Cliente c = manager.getRepositorio().buscarPorNombre(nombre);
                        if (c != null) {
                            HistorialServicio.mostrarHistorialPersonal(c.getHistorial());
                        }
                    }

                    case "6" -> {
                        System.out.print("Tu nombre (Emisor): ");
                        String seguidor = sc.nextLine().trim();
                        System.out.print("Nombre a seguir (Receptor): ");
                        String seguido = sc.nextLine().trim();

                        if (seguidor.equalsIgnoreCase(seguido)) {
                            System.out.println("Error: No puedes seguirte a ti mismo.");
                            break;
                        }

                        // Invocación directa
                        SolicitudesServicio.enviarSolicitud(seguidor, seguido, manager.getRepositorio());
                    }

                    case "7" -> {
                        System.out.print("Tu nombre (para ver tus solicitudes): ");
                        String nombre = sc.nextLine().trim();
                        Cliente c = manager.getRepositorio().buscarPorNombre(nombre);
                        if (c != null) {
                            if (c.getSolicitudes().ColaVacia()) {
                                System.out.println("No tienes solicitudes en espera.");
                            } else {
                                System.out.println("Siguiente en espera para ti: " + c.getSolicitudes().Primero());
                            }
                        }
                    }

                    case "8" -> {
                        System.out.print("Tu nombre (quien acepta la solicitud): ");
                        String nombreReceptor = sc.nextLine().trim();
                        Cliente receptor = manager.getRepositorio().buscarPorNombre(nombreReceptor);

                        if (receptor == null || receptor.getSolicitudes().ColaVacia()) {
                            System.out.println("No hay solicitudes para " + nombreReceptor);
                            break;
                        }

                        String nombreEmisor = receptor.getSolicitudes().Primero();
                        receptor.getSolicitudes().Desacolar();

                        Cliente emisor = manager.getRepositorio().buscarPorNombre(nombreEmisor);

                        if (emisor != null) {
                            emisor.agregarSeguidor(receptor.getNombre());
                            emisor.getHistorial().Apilar("Ahora sigues a: " + receptor.getNombre());
                            receptor.getHistorial().Apilar("Aceptaste la solicitud de: " + emisor.getNombre());

                            System.out.println("[+] " + emisor.getNombre() + " ahora sigue a " + receptor.getNombre());
                        }
                    }

                    case "9" -> {
                        System.out.print("Tu nombre (quien rechaza): ");
                        String nombre = sc.nextLine().trim();
                        Cliente receptor = manager.getRepositorio().buscarPorNombre(nombre);

                        if (receptor != null && !receptor.getSolicitudes().ColaVacia()) {
                            String emisor = receptor.getSolicitudes().Primero();
                            receptor.getSolicitudes().Desacolar();
                            receptor.getHistorial().Apilar("Rechazaste solicitud de: " + emisor);
                            System.out.println("Solicitud de " + emisor + " rechazada.");
                        } else {
                            System.out.println("No hay solicitudes para rechazar.");
                        }
                    }
                    case "10" -> {
                        System.out.print("Ingrese nombre del cliente: ");
                        String nombre = sc.nextLine().trim();
                        manager.consultarConexionesDeCliente(nombre);
                    }

                    case "11" -> {
                        System.out.print("Ingrese nombre del cliente: ");
                        String nombre = sc.nextLine().trim();
                        manager.mostrarCuartoNivelABBDeConexiones(nombre);

                    }case "12" -> {
                        System.out.print("Cliente origen: ");
                        String origen = sc.nextLine().trim();
                        System.out.print("Cliente destino: ");
                        String destino = sc.nextLine().trim();

                        manager.calcularDistanciaEntreClientes(origen, destino);
                    }


                    case "0" -> {
                        System.out.println("Guardando datos antes de salir...");
                        manager.guardarDatos("clientes.json");
                        salir = true;
                    }

                }
            } catch (Exception e) {
                System.out.println("Ocurrió un error: " + e.getMessage());
            }
        }

        System.out.println("Programa finalizado. ¡Hasta luego!");
        sc.close();
    }
}