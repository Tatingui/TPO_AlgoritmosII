package app.servicio;

import app.modelo.Cliente;
import app.modelo.Clientes;
import app.persistencia.JsonLoader;
import app.repositorio.ClienteRepositorio;

public class RedSocialManager {
    private final ClienteRepositorio repositorio;
    private final JsonLoader loader;

    public RedSocialManager() {
        this.repositorio = new ClienteRepositorio();
        this.loader = new JsonLoader();
    }

    public void cargarDesdeArchivo(String ruta) {
        Clientes clientesNuevos = loader.cargarClientes(ruta);

        for (Cliente c : clientesNuevos.clientes()) {
            repositorio.guardarCliente(c);
        }
        System.out.println("LOG: Carga completada.");
    }

    public void buscarYMostrarCliente(String nombre) {
        Cliente c = repositorio.buscarPorNombre(nombre);
        if (c != null) {
            System.out.println("Cliente encontrado: " + c);
        } else {
            System.out.println("El cliente " + nombre + " no existe.");
        }
    }

    public void imprimirRanking() {
        repositorio.mostrarRanking();
    }
}