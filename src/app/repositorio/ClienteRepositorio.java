package app.repositorio;

import app.modelo.Cliente;
import app.implementaciones.ABB;

import java.util.ArrayList;
import java.util.List;

public class ClienteRepositorio {
    // ABB de clientes ordenados por scoring (descendente) para ranking eficiente
    private final ABB<Cliente> arbolClientes;

    // ABB de nombres para búsqueda O(log n)
    private final ABB<ClienteNodo> arbolNombres;

    public ClienteRepositorio() {
        arbolClientes = new ABB<>();
        arbolClientes.InicializarArbol();

        arbolNombres = new ABB<>();
        arbolNombres.InicializarArbol();
    }

    public void guardarCliente(Cliente cliente) {
        // Verificar si el cliente ya existe
        Cliente existente = buscarPorNombre(cliente.getNombre());

        if (existente != null) {
            // Eliminar el cliente anterior
            arbolClientes.EliminarElem(existente);
            arbolNombres.EliminarElem(new ClienteNodo(existente.getNombre(), existente));
        }

        // Agregar el nuevo cliente
        arbolClientes.AgregarElem(cliente);
        arbolNombres.AgregarElem(new ClienteNodo(cliente.getNombre(), cliente));
    }

    public Cliente buscarPorNombre(String nombre) {
        // Búsqueda O(log n) en el árbol de nombres
        ClienteNodo nodo = arbolNombres.buscar(new ClienteNodo(nombre, null));
        return nodo != null ? nodo.cliente : null;
    }

    public void mostrarRanking() {
        System.out.println("--- RANKING DE CLIENTES (Usando ABB ordenado por Scoring) ---");
        List<Cliente> clientes = arbolClientes.recorridoInOrderLista();

        for (Cliente c : clientes) {
            System.out.println("Puntaje " + c.getScoring() + ": " + c.getNombre());
        }
    }

    public void buscarPorScoring(int scoringBuscado) {
        List<Cliente> clientes = arbolClientes.recorridoInOrderLista();
        boolean huboResultados = false;

        for (Cliente c : clientes) {
            if (c.getScoring() == scoringBuscado) {
                System.out.println("-> " + c.getNombre());
                huboResultados = true;
            }
        }

        if (!huboResultados) {
            System.out.println("No se encontraron clientes con ese puntaje.");
        }
    }

    public List<Cliente> obtenerTodos() {
        return arbolClientes.recorridoInOrderLista();
    }

    /**
     * Clase auxiliar para envolver Cliente con comparación por nombre
     * Permite búsqueda O(log n) en el árbol de nombres
     */
    private static class ClienteNodo implements Comparable<ClienteNodo> {
        String nombre;
        Cliente cliente;

        ClienteNodo(String nombre, Cliente cliente) {
            this.nombre = nombre;
            this.cliente = cliente;
        }

        @Override
        public int compareTo(ClienteNodo o) {
            return this.nombre.compareTo(o.nombre);
        }
    }

}

