package app.servicio;

import app.implementaciones.GrafoLA;
import app.interfaces.ConjuntoTDA;
import app.interfaces.GrafoTDA;
import app.modelo.Cliente;
import app.modelo.Clientes;
import app.persistencia.JsonLoader;
import app.repositorio.ClienteRepositorio;
import tools.jackson.databind.json.JsonMapper;
import app.implementaciones.ABB;
import app.interfaces.ABBTDA;
import app.modelo.Cliente;

import java.io.File;
import java.util.*;

public class RedSocialManager {
    private final ClienteRepositorio repositorio;
    private final JsonLoader loader; // Si lo usabas abajo, no lo borres

    private final HistorialServicio historialServicio;
    private final SolicitudesServicio solicitudesServicio;

    private final GrafoTDA<Cliente> grafoSeguimientos;

    public RedSocialManager() {
        this.repositorio = new ClienteRepositorio();
        this.loader = new JsonLoader();
        this.historialServicio = new HistorialServicio();
        this.solicitudesServicio = new SolicitudesServicio();

        this.grafoSeguimientos = new GrafoLA<>();
        this.grafoSeguimientos.InicializarGrafo();

        System.out.println("[SISTEMA] Iniciando Red Social...");
        inicializarDatos();
    }

    private void inicializarDatos() {
        try {
            this.cargarDesdeArchivo("Clientes.json");
            System.out.println("[SISTEMA] Datos cargados automáticamente.");
        } catch (Exception e) {
            System.err.println("[ERROR] No se pudo cargar automáticamente.");
        }
    }

    public void cargarDesdeArchivo(String ruta) {
        if (ruta == null) {
            throw new IllegalArgumentException("La ruta para cargar JSON no existe");
        }
        Clientes clientesNuevos = loader.cargarClientes(ruta);
        if (clientesNuevos != null) {
            for (Cliente c : clientesNuevos.getClientes()) {
                repositorio.guardarCliente(c);
            }
            System.out.println("LOG: Carga de clientes completada.");
        }
        if (clientesNuevos != null) {
            for (Cliente c : clientesNuevos.getClientes()) {
                // ¡ESTA LÍNEA ES VITAL!
                c.inicializarEstructurasDesdeJson();
                repositorio.guardarCliente(c);
            }
        }
    }

    public void buscarYMostrarCliente(String nombre) {
        if (nombre == null) {
            throw new IllegalArgumentException("El nombre tiene que ser un valor no nulo");
        }
        Cliente c = repositorio.buscarPorNombre(nombre);
        if (c != null) {
            System.out.println("Encontrado por Nombre: " + c.getNombre());
        } else {
            System.out.println("El cliente '" + nombre + "' no existe.");
        }
    }

    public void buscarYMostrarPorScoring(int scoringBuscado) {
        System.out.println("--- Buscando clientes con scoring: " + scoringBuscado + " ---");
        repositorio.buscarPorScoring(scoringBuscado);
    }

    public void imprimirRankingCompleto() {
        repositorio.mostrarRanking();
    }

    public ClienteRepositorio getRepositorio() {
        return this.repositorio; // O clienteRepositorio, según el nombre que le dejaste
    }

    public void guardarDatos(String ruta) {
        try {
            // 1. Sincronizamos: Pasamos datos de los TDAs (Pila/Cola) a las Listas de Java
            for (Cliente c : repositorio.obtenerTodos()) {
                c.prepararParaGuardar(); // Usamos este que es el que tenés en Cliente.java
            }

            // 2. Preparamos el contenedor que Jackson entiende
            Clientes wrapper = new Clientes();
            wrapper.setClientes(repositorio.obtenerTodos());

            // 3. Escribimos en el archivo físico
            JsonMapper mapper = new JsonMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(ruta), wrapper);

            System.out.println("[SISTEMA] Cambios guardados en: " + ruta);
        } catch (Exception e) {
            System.err.println("[ERROR] No se pudo guardar el archivo: " + e.getMessage());
        }
    }

    public void consultarConexionesDeCliente(String nombreCliente) {
        if (nombreCliente == null || nombreCliente.isBlank()) {
            System.out.println("Nombre inválido.");
            return;
        }

        Cliente cliente = repositorio.buscarPorNombre(nombreCliente);

        if (cliente == null) {
            System.out.println("No existe el cliente: " + nombreCliente);
            return;
        }

        if (cliente.getSiguiendo() == null || cliente.getSiguiendo().isEmpty()) {
            System.out.println(cliente.getNombre() + " no sigue a nadie.");
            return;
        }

        System.out.println("Conexiones de " + cliente.getNombre() + " (a quiénes sigue):");

        for (String nombreSeguido : cliente.getSiguiendo()) {
            Cliente seguido = repositorio.buscarPorNombre(nombreSeguido);

            if (seguido != null) {
                System.out.println("- " + seguido.getNombre() + " (scoring: " + seguido.getScoring() + ")");
            } else {
                // por si hay nombres en JSON que ya no existen en el repo
                System.out.println("- " + nombreSeguido + " (no encontrado en repositorio)");
            }
        }
    }
    public void mostrarCuartoNivelABBDeConexiones(String nombreCliente) {
        if (nombreCliente == null || nombreCliente.isBlank()) {
            System.out.println("Nombre inválido.");
            return;
        }

        Cliente cliente = repositorio.buscarPorNombre(nombreCliente);
        if (cliente == null) {
            System.out.println("No existe el cliente: " + nombreCliente);
            return;
        }

        if (cliente.getSiguiendo() == null || cliente.getSiguiendo().isEmpty()) {
            System.out.println(cliente.getNombre() + " no sigue a nadie.");
            return;
        }

        ABB<Cliente> arbol = new ABB<>();
        arbol.InicializarArbol();

        // Cargar en ABB los clientes que sigue
        for (String nombreSeguido : cliente.getSiguiendo()) {
            Cliente seguido = repositorio.buscarPorNombre(nombreSeguido);
            if (seguido != null) {
                arbol.AgregarElem(seguido);
            }
        }

        System.out.println("Clientes seguidos por " + cliente.getNombre() + " cargados en ABB.");
        System.out.println("Clientes en el CUARTO nivel del ABB (nivel 3):");

        boolean hayNodos = imprimirNivelABB(arbol, 3);

        if (!hayNodos) {
            System.out.println("(No hay nodos en el cuarto nivel)");
        }
    }
    private boolean imprimirNivelABB(ABBTDA<Cliente> arbol, int nivelObjetivo) {
        return imprimirNivelABBRec(arbol, 0, nivelObjetivo);
    }

    private boolean imprimirNivelABBRec(ABBTDA<Cliente> nodo, int nivelActual, int nivelObjetivo) {
        if (nodo == null || nodo.ArbolVacio()) return false;

        if (nivelActual == nivelObjetivo) {
            Cliente c = nodo.Raiz();
            System.out.println("- " + c.getNombre() + " (scoring: " + c.getScoring() + ")");
            return true;
        }

        boolean izq = imprimirNivelABBRec(nodo.HijoIzq(), nivelActual + 1, nivelObjetivo);
        boolean der = imprimirNivelABBRec(nodo.HijoDer(), nivelActual + 1, nivelObjetivo);

        return izq || der;
    }

    public void calcularDistanciaEntreClientes(String nombreOrigen, String nombreDestino) {
        if (nombreOrigen == null || nombreOrigen.isBlank() ||
                nombreDestino == null || nombreDestino.isBlank()) {
            System.out.println("Nombres inválidos.");
            return;
        }
        reconstruirGrafoSeguimientos();
        Cliente origen = repositorio.buscarPorNombre(nombreOrigen);
        Cliente destino = repositorio.buscarPorNombre(nombreDestino);

        if (origen == null || destino == null) {
            System.out.println("Uno o ambos clientes no existen.");
            return;
        }

        int distancia = distanciaEnSaltos(origen, destino);

        if (distancia == -1) {
            System.out.println("No hay conexión entre " + origen.getNombre() + " y " + destino.getNombre() + ".");
        } else {
            System.out.println("Distancia entre " + origen.getNombre() + " y " + destino.getNombre() +
                    ": " + distancia + " salto(s).");
        }
    }

    private int distanciaEnSaltos(Cliente origen, Cliente destino) {
        if (origen.equals(destino)) return 0;

        // Obtener todos los vértices una vez
        List<Cliente> vertices = obtenerVerticesComoLista();

        Queue<Cliente> cola = new LinkedList<>();
        Map<Cliente, Integer> distancia = new HashMap<>();
        Set<Cliente> visitados = new HashSet<>();

        cola.add(origen);
        visitados.add(origen);
        distancia.put(origen, 0);

        while (!cola.isEmpty()) {
            Cliente actual = cola.poll();
            int distActual = distancia.get(actual);

            // "Vecinos" = todos los vértices a los que actual tiene arista
            for (Cliente candidato : vertices) {
                if (!visitados.contains(candidato) && grafoSeguimientos.ExisteArista(actual, candidato)) {
                    visitados.add(candidato);
                    distancia.put(candidato, distActual + 1);

                    if (candidato.equals(destino)) {
                        return distActual + 1;
                    }

                    cola.add(candidato);
                }
            }
        }

        return -1; // no hay camino
    }
    private List<Cliente> obtenerVerticesComoLista() {
        List<Cliente> lista = new ArrayList<>();

        // OJO: Vertices() devuelve ConjuntoTDA<Cliente>.
        // Lo vaciamos para copiar a una lista temporal y luego usar BFS con Java collections.
        ConjuntoTDA<Cliente> vertices = grafoSeguimientos.Vertices();

        while (!vertices.ConjuntoVacio()) {
            Cliente c = vertices.Elegir(); // misma referencia que está en el grafo
            lista.add(c);
            vertices.Sacar(c); // funciona porque Sacar usa referencia (==)
        }

        return lista;
    }

    private void reconstruirGrafoSeguimientos() {
        grafoSeguimientos.InicializarGrafo();

        // 1) Vértices
        for (Cliente c : repositorio.obtenerTodos()) {
            grafoSeguimientos.AgregarVertice(c);
        }

        // 2) Aristas: seguidor -> seguido
        for (Cliente seguidor : repositorio.obtenerTodos()) {
            if (seguidor.getSiguiendo() == null) continue;

            for (String nombreSeguido : seguidor.getSiguiendo()) {
                Cliente seguido = repositorio.buscarPorNombre(nombreSeguido);
                if (seguido != null && !grafoSeguimientos.ExisteArista(seguidor, seguido)) {
                    grafoSeguimientos.AgregarArista(seguidor, seguido, 1);
                }
            }
        }
    }

}