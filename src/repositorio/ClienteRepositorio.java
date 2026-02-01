package repositorio;

import modelo.Cliente;
import implementaciones.DiccionarioSimpleLD;
import implementaciones.ColaPrioridadLD;
import interfaces.DiccionarioSimpleTDA;
import interfaces.ColaPrioridadTDA;

public class ClienteRepositorio {
    // Definimos los TADs usando tus interfaces
    // K = String (nombre), V = Cliente
    private DiccionarioSimpleTDA<String, Cliente> diccionarioNombres;
    // T = Cliente
    private ColaPrioridadTDA<Cliente> rankingScoring;

    public ClienteRepositorio() {
        // Instanciamos tus implementaciones LD
        diccionarioNombres = new DiccionarioSimpleLD<String, Cliente>();
        diccionarioNombres.InicializarDiccionario();

        rankingScoring = new ColaPrioridadLD<Cliente>();
        rankingScoring.InicializarCola();
    }

    public void guardarCliente(Cliente cliente) {
        // Agregar(K clave, V valor)
        diccionarioNombres.Agregar(cliente.getNombre(), cliente);

        // AcolarPrioridad(T x, int prioridad)
        rankingScoring.AcolarPrioridad(cliente, cliente.getScoring());
    }

    public Cliente buscarPorNombre(String nombre) {
        // Recuperar(K clave)
        try {
            return diccionarioNombres.Recuperar(nombre);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public void mostrarRanking() {
        System.out.println("--- RANKING DE CLIENTES (ColaPrioridadLD) ---");

        ColaPrioridadTDA<Cliente> aux = new ColaPrioridadLD<Cliente>();
        aux.InicializarCola();

        while (!rankingScoring.ColaVacia()) {
            Cliente c = rankingScoring.Primero();
            int p = rankingScoring.Prioridad();

            System.out.println("Scoring: " + p + " | " + c.getNombre());

            aux.AcolarPrioridad(c, p);
            rankingScoring.Desacolar();
        }

        while (!aux.ColaVacia()) {
            rankingScoring.AcolarPrioridad(aux.Primero(), aux.Prioridad());
            aux.Desacolar();
        }
    }
}