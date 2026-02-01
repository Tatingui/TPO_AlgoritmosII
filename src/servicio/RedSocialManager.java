package servicio;

import modelo.Cliente;
import persistencia.JsonLoader;
import repositorio.ClienteRepositorio;
import java.util.List;

public class RedSocialManager {
    private ClienteRepositorio repositorio;
    private JsonLoader loader;

    public RedSocialManager() {
        this.repositorio = new ClienteRepositorio();
        this.loader = new JsonLoader();
    }

    public void cargarDesdeArchivo(String ruta) {
        List<Cliente> clientesNuevos = loader.cargarClientes(ruta);

        for (Cliente c : clientesNuevos) {
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