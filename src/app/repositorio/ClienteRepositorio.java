package app.repositorio;

import app.interfaces.ConjuntoTDA;
import app.modelo.Cliente;
import app.interfaces.DiccionarioSimpleTDA;
import app.interfaces.ColaPrioridadTDA;
import app.implementaciones.DiccionarioSimpleLD;
import app.implementaciones.ColaPrioridadLD;

import java.util.ArrayList;
import java.util.List;

public class ClienteRepositorio {
    // Reemplazamos HashMap por DiccionarioSimpleTDA
    private final DiccionarioSimpleTDA<String, Cliente> mapaNombres;

    // Reemplazamos TreeMap por ColaPrioridadTDA
    private final ColaPrioridadTDA<Cliente> rankingScoring;

    public ClienteRepositorio() {
        mapaNombres = new DiccionarioSimpleLD<>();
        mapaNombres.InicializarDiccionario();

        rankingScoring = new ColaPrioridadLD<>();
        rankingScoring.InicializarCola();
    }

    public void guardarCliente(Cliente cliente) {
        // O(n) porque es LD, pero cumple con el TDA
        mapaNombres.Agregar(cliente.getNombre(), cliente);

        // La cola de prioridad mantiene el orden de scoring automáticamente
        rankingScoring.AcolarPrioridad(cliente, cliente.getScoring());
    }

    public Cliente buscarPorNombre(String nombre) {
        return mapaNombres.Recuperar(nombre);
    }

    public void mostrarRanking() {
        System.out.println("--- RANKING DE CLIENTES (Usando Cola de Prioridad) ---");
        // Clonamos para no destruir la original al mostrar
        ColaPrioridadTDA<Cliente> aux = new ColaPrioridadLD<>();
        aux.InicializarCola();

        while (!rankingScoring.ColaVacia()) {
            Cliente c = rankingScoring.Primero();
            System.out.println("Puntaje " + rankingScoring.Prioridad() + ": " + c.getNombre());

            aux.AcolarPrioridad(c, rankingScoring.Prioridad());
            rankingScoring.Desacolar();
        }
        // Restaurar
        reponerCola(aux);
    }

    private void reponerCola(ColaPrioridadTDA<Cliente> aux) {
        while (!aux.ColaVacia()) {
            rankingScoring.AcolarPrioridad(aux.Primero(), aux.Prioridad());
            aux.Desacolar();
        }
    }

    public void buscarPorScoring(int scoringBuscado) {
        ColaPrioridadTDA<Cliente> aux = new ColaPrioridadLD<>();
        aux.InicializarCola();
        boolean huboResultados = false;

        while (!rankingScoring.ColaVacia()) {
            Cliente c = rankingScoring.Primero();
            int prioridadActual = rankingScoring.Prioridad();

            if (prioridadActual == scoringBuscado) {
                System.out.println("-> " + c.getNombre());
                huboResultados = true;
            }

            aux.AcolarPrioridad(c, prioridadActual);
            rankingScoring.Desacolar();
        }
        // Restaurar la cola original
        reponerCola(aux);

        if (!huboResultados) System.out.println("No se encontraron clientes con ese puntaje.");
    }

    public List<Cliente> obtenerTodos() {
        List<Cliente> lista = new ArrayList<>();

        // AGREGAMOS EL <String> AQUÍ:
        ConjuntoTDA<String> llaves = mapaNombres.Claves();

        while (!llaves.ConjuntoVacio()) {
            // Ahora Java sabe que Elegir() devuelve un String
            String nombre = llaves.Elegir();

            Cliente c = mapaNombres.Recuperar(nombre);
            if (c != null) {
                lista.add(c);
            }

            llaves.Sacar(nombre);
        }
        return lista;
    }

}

