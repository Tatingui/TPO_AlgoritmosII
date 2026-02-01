package servicio;

import modelo.Cliente;
import persistencia.JsonLoader;
import repositorio.ClienteRepositorio;
import interfaces.ColaTDA;
import modelo.Accion; // Importamos la clase Accion de tu compañero

public class RedSocialManager {
    private ClienteRepositorio repositorio;
    private JsonLoader loader;

    // CAMBIO: En lugar de PilaTDA, usamos el servicio de tu compañero
    private HistorialServicio historial;

    public RedSocialManager() {
        this.repositorio = new ClienteRepositorio();
        this.loader = new JsonLoader();

        // CAMBIO: Inicializamos el servicio de historial
        this.historial = new HistorialServicio();
    }

    public void cargarDesdeArchivo(String ruta) {
        ColaTDA<Cliente> clientesNuevos = loader.cargarClientes(ruta);

        while (!clientesNuevos.ColaVacia()) {
            Cliente c = clientesNuevos.Primero();
            repositorio.guardarCliente(c);
            clientesNuevos.Desacolar();
        }

        // Ahora registrarAccion FUNCIONA porque historial es de tipo HistorialServicio
        historial.registrarAccion("CARGA", "Carga masiva desde " + ruta);
        System.out.println("LOG: Carga completada.");
    }

    public void buscarYMostrarCliente(String nombre) {
        Cliente c = repositorio.buscarPorNombre(nombre);
        if (c != null) {
            System.out.println("Cliente encontrado: " + c);
            historial.registrarAccion("BUSQUEDA", "Se encontró a " + nombre);
        } else {
            System.out.println("El cliente " + nombre + " no existe.");
            historial.registrarAccion("BUSQUEDA_FALLIDA", "No se encontró a " + nombre);
        }
    }

    // Usamos el método de tu compañero para ver todo
    public void verHistorialCompleto() {
        historial.mostrarHistorial();
    }

    public void imprimirRanking() {
        repositorio.mostrarRanking();
    }
}