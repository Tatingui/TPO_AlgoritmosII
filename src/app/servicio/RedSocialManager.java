package app.servicio;

import app.modelo.Cliente;
import app.modelo.Clientes;
import app.persistencia.JsonLoader;
import app.repositorio.ClienteRepositorio;
import tools.jackson.databind.json.JsonMapper;

import java.io.File;

public class RedSocialManager {
    private final ClienteRepositorio repositorio;
    private final JsonLoader loader; // Si lo usabas abajo, no lo borres

    private final HistorialServicio historialServicio;
    private final SolicitudesServicio solicitudesServicio;

    public RedSocialManager() {
        this.repositorio = new ClienteRepositorio();
        this.loader = new JsonLoader();
        this.historialServicio = new HistorialServicio();
        this.solicitudesServicio = new SolicitudesServicio();

        System.out.println("[SISTEMA] Iniciando Red Social...");
        inicializarDatos();
    }

    private void inicializarDatos() {
        try {
            this.cargarDesdeArchivo("clientes.json");
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
            System.out.println("Encontrado por Nombre: " + c);
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
}