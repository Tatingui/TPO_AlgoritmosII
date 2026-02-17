package app.servicio;

import app.interfaces.ColaTDA;
import app.modelo.Solicitud;
import app.repositorio.ClienteRepositorio;
import java.util.HashSet;
import java.util.Set;
import app.modelo.Cliente;

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

    public static void enviarSolicitud(String emisor, String receptor, ClienteRepositorio repo) {
        if (emisor == null || receptor == null) return;

        Cliente clienteEmisor = repo.buscarPorNombre(emisor);
        Cliente clienteReceptor = repo.buscarPorNombre(receptor);

        if (clienteEmisor == null || clienteReceptor == null) {
            System.out.println("[!] Error: Usuario no encontrado.");
            return;
        }

        if (clienteEmisor.getSiguiendo().size() >= 2) {
            System.out.println("[!] Error: " + emisor + " ya sigue al máximo de 2 personas.");
            clienteEmisor.getHistorial().Apilar("Intento fallido de seguir a " + receptor + " (Límite 2)");
            return;
        }

        clienteReceptor.getSolicitudes().Acolar(emisor);

        clienteEmisor.getHistorial().Apilar("Envio solicitud a " + receptor);

        System.out.println("[+] Solicitud enviada de " + emisor + " a " + receptor);
    }

    public static void procesarSiguiente(ColaTDA<Solicitud> pendientes, boolean aceptar) {
        if (pendientes == null || pendientes.ColaVacia()) {
            throw new RuntimeException("No hay solicitudes pendientes.");
        }

        Solicitud s = pendientes.Primero();

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

        pendientes.Desacolar();
    }
}

