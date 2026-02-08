package app.servicio;

import app.interfaces.ColaTDA;
import app.modelo.Solicitud;
import app.repositorio.ClienteRepositorio;
import java.util.HashSet;
import java.util.Set;

public class SolicitudesServicio {

    private static final Set<String> pendientesSet = new HashSet<>();
    private static final Set<String> aceptadasSet = new HashSet<>();
    private static final Set<String> rechazadasSet = new HashSet<>();

    // Agregamos verificación de nulos para que el test no explote
    private static String generarKey(String emisor, String receptor) {
        if (emisor == null || receptor == null) {
            return "null"; // Retornamos un string seguro
        }
        return emisor.trim().toLowerCase() + "->" + receptor.trim().toLowerCase();
    }

    public static void enviarSolicitud(ColaTDA<Solicitud> cola, String emisor, String receptor, ClienteRepositorio repo) {
        // Validación de nulidad para evitar NullPointerException
        if (emisor == null || receptor == null) {
            System.out.println("[!] Error: Los nombres no pueden ser nulos.");
            return;
        }

        String key = generarKey(emisor, receptor);

        if (repo != null) {
            var clienteEmisor = repo.buscarPorNombre(emisor);
            if (clienteEmisor != null && clienteEmisor.siguiendo() != null && clienteEmisor.siguiendo().contains(receptor)) {
                System.out.println("[!] Error: Ya sigues a esta persona.");
                return;
            }
        }

        if (pendientesSet.contains(key)) return;
        if (aceptadasSet.contains(key)) return;

        rechazadasSet.remove(key);
        Solicitud nueva = new Solicitud(emisor, receptor);
        cola.Acolar(nueva);
        pendientesSet.add(key);
    }

    public static void procesarSiguiente(ColaTDA<Solicitud> pendientes, boolean aceptar) {
        if (pendientes == null || pendientes.ColaVacia()) {
            throw new RuntimeException("No hay solicitudes pendientes.");
        }

        Solicitud s = pendientes.Primero();

        // Verificación de nulidad de la solicitud dentro de la cola
        if (s != null) {
            String key = generarKey(s.seguidor(), s.seguido());
            if (aceptar) {
                aceptadasSet.add(key);
                System.out.println("ACEPTADA: " + s);
            } else {
                rechazadasSet.add(key);
                System.out.println("RECHAZADA: " + s);
            }
            pendientesSet.remove(key);
        }

        // MUY IMPORTANTE: Desacolar DEBE llamarse siempre para que el test pase
        pendientes.Desacolar();
    }
}