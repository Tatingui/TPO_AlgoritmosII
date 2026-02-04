package app;

import app.servicio.HistorialServicio;
import app.servicio.RedSocialManager;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        RedSocialManager manager = new RedSocialManager(); // :contentReference[oaicite:2]{index=2}
        HistorialServicio historial = new HistorialServicio(); // :contentReference[oaicite:3]{index=3}

        boolean salir = false;

        while (!salir) {
            System.out.println("\n=== RED SOCIAL - MENÚ ===");
            System.out.println("1) Cargar clientes desde JSON");
            System.out.println("2) Buscar cliente por nombre");
            System.out.println("3) Buscar clientes por scoring");
            System.out.println("4) Mostrar ranking completo");
            System.out.println("5) Deshacer última acción");
            System.out.println("6) Mostrar historial");
            System.out.println("0) Salir");
            System.out.print("Opción: ");

            String opcion = sc.nextLine().trim();

            try {
                switch (opcion) {
                    case "1" -> {
                        System.out.print("Cargando archivo Clientes.json... ");
                        String ruta = "Clientes.json";
                        manager.cargarDesdeArchivo(ruta);
                        historial.registrarAccion("CargarJSON", "Se cargó el archivo: " + ruta);
                    }
                    case "2" -> {
                        System.out.print("Nombre a buscar: ");
                        String nombre = sc.nextLine().trim();
                        manager.buscarYMostrarCliente(nombre);
                        historial.registrarAccion("BuscarPorNombre", "Búsqueda de: " + nombre);
                    }
                    case "3" -> {
                        System.out.print("Scoring a buscar (entero): ");
                        int scoring = Integer.parseInt(sc.nextLine().trim());
                        manager.buscarYMostrarPorScoring(scoring);
                        historial.registrarAccion("BuscarPorScoring", "Búsqueda de scoring: " + scoring);
                    }
                    case "4" -> {
                        manager.imprimirRankingCompleto();
                        historial.registrarAccion("MostrarRanking", "Se mostró el ranking completo");
                    }
                    case "5" -> historial.deshacerUltimaAccion();
                    case "6" -> historial.mostrarHistorial();
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
