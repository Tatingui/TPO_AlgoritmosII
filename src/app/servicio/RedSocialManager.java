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
        if (ruta == null) {
            throw new IllegalArgumentException("La ruta para cargar JSON no existe");
        }
        Clientes clientesNuevos = loader.cargarClientes(ruta);
        if (clientesNuevos != null) {
            for (Cliente c : clientesNuevos.clientes()) {
                repositorio.guardarCliente(c);
            }
            System.out.println("LOG: Carga de clientes completada.");
        }
    }

    // Cumple: Búsqueda por nombre
    public void buscarYMostrarCliente(String nombre) {
        if (nombre == null) {
            throw new IllegalArgumentException("El nombre tiene que ser un valor no nulo");
        }
        Cliente c = repositorio.buscarPorNombre(nombre);
        if (c != null) {
            System.out.println("Encontrado por Nombre: " + c);
        } else {
            System.out.println("El cliente '" + nombre + "' no existe.");
        }
    }

    // Cumple: Búsqueda por scoring (Usa la Cola de Prioridad del Repositorio)
    public void buscarYMostrarPorScoring(int scoringBuscado) {
        System.out.println("--- Buscando clientes con scoring: " + scoringBuscado + " ---");
        repositorio.buscarPorScoring(scoringBuscado);
    }

    public void imprimirRankingCompleto() {
        repositorio.mostrarRanking();
    }
}