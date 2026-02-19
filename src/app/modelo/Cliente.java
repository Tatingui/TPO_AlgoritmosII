package app.modelo;

import app.interfaces.ColaTDA;
import app.interfaces.ConjuntoTDA;
import app.interfaces.PilaTDA;
import app.interfaces.GrafoTDA;
import app.implementaciones.ColaLD;
import app.implementaciones.PilaLD;
import app.implementaciones.GrafoLA;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.ArrayList;

public class Cliente {
    private String nombre;
    private int scoring;
    private List<String> siguiendo = new ArrayList<>();
    private List<String> conexiones = new ArrayList<>();
    private List<String> solicitudesPendientes = new ArrayList<>();
    private List<String> historialPersonal = new ArrayList<>();

    @JsonIgnore
    private ColaTDA<String> solicitudes;
    @JsonIgnore
    private PilaTDA<String> historial;
    @JsonIgnore
    private GrafoTDA<String> grafoConexiones;

    public Cliente() {
        inicializarTDAs();
    }

    public Cliente(String nombre, int scoring, List<String> siguiendo, List<String> conexiones) {
        this.nombre = nombre;
        this.scoring = scoring;
        this.siguiendo = (siguiendo != null) ? siguiendo : new ArrayList<>();
        this.conexiones = (conexiones != null) ? conexiones : new ArrayList<>();
        inicializarTDAs();
    }

    private void inicializarTDAs() {
        this.solicitudes = new ColaLD();
        this.solicitudes.InicializarCola();
        this.historial = new PilaLD();
        this.historial.InicializarPila();
        this.grafoConexiones = new GrafoLA<>();
        this.grafoConexiones.InicializarGrafo();
        // Añadir el cliente como vértice en su propio grafo
        this.grafoConexiones.AgregarVertice(nombre);
    }

    public void inicializarEstructurasDesdeJson() {
        if (solicitudes == null) inicializarTDAs();

        if (solicitudesPendientes != null) {
            for (String s : solicitudesPendientes) {
                this.solicitudes.Acolar(s);
            }
        }
        if (historialPersonal != null) {
            // Se apila en orden inverso para que el Tope sea el último del JSON
            for (int i = historialPersonal.size() - 1; i >= 0; i--) {
                this.historial.Apilar(historialPersonal.get(i));
            }
        }
        // Sincronizar conexiones desde la lista al grafo (máximo 2 conexiones = 3 vértices totales)
        if (conexiones != null) {
            int conexionesAgregadas = 0;
            for (String conexion : conexiones) {
                if (conexionesAgregadas >= 2) break; // Máximo 2 conexiones
                if (!conexion.equals(nombre)) { // Evitar agregar arista a sí mismo
                    if (!grafoConexiones.ExisteArista(nombre, conexion)) {
                        grafoConexiones.AgregarVertice(conexion);
                        grafoConexiones.AgregarArista(nombre, conexion, 1); // peso default = 1
                        conexionesAgregadas++;
                    }
                }
            }
        }
    }

    public void prepararParaGuardar() {
        this.solicitudesPendientes.clear();
        this.historialPersonal.clear();

        // Volcar Cola de solicitudes
        ColaTDA<String> tempCola = new ColaLD();
        tempCola.InicializarCola();
        while (!this.solicitudes.ColaVacia()) {
            String s = this.solicitudes.Primero();
            this.solicitudesPendientes.add(s);
            tempCola.Acolar(s);
            this.solicitudes.Desacolar();
        }
        this.solicitudes = tempCola;

        // Volcar Pila de historial
        PilaTDA<String> tempPila = new PilaLD();
        tempPila.InicializarPila();
        while (!this.historial.PilaVacia()) {
            String h = this.historial.Tope();
            this.historialPersonal.add(0, h); // Insertar al inicio para mantener orden
            tempPila.Apilar(h);
            this.historial.Desapilar();
        }
        this.historial = tempPila;
    }

    // Getters y Setters limpios
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getScoring() { return scoring; }
    public void setScoring(int scoring) { this.scoring = scoring; }
    public List<String> getSiguiendo() { return siguiendo; }
    public List<String> getConexiones() { return conexiones; }
    public List<String> getSolicitudesPendientes() { return solicitudesPendientes; }
    public List<String> getHistorialPersonal() { return historialPersonal; }

    @JsonIgnore
    public ColaTDA<String> getSolicitudes() { return solicitudes; }
    @JsonIgnore
    public PilaTDA<String> getHistorial() { return historial; }

    // Métodos para trabajar con el grafo de conexiones
    @JsonIgnore
    public GrafoTDA<String> getGrafoConexiones() { return grafoConexiones; }

    /**
     * Agrega una nueva conexión entre este cliente y otro cliente
     * Restricción: Solo permite hasta 3 vértices en el grafo (este cliente + 2 otros)
     * @param nombreClienteConectado Nombre del cliente a conectar
     * @param peso Peso de la arista (puede representar intensidad de conexión)
     * @throws IllegalStateException Si ya hay 3 vértices y se intenta agregar otro
     */
    public void agregarConexion(String nombreClienteConectado, int peso) {
        if (nombreClienteConectado != null && !nombreClienteConectado.equals(nombre)) {
            if (!grafoConexiones.ExisteArista(nombre, nombreClienteConectado)) {
                // Contar vértices actuales
                ConjuntoTDA<String> verticesActuales = grafoConexiones.Vertices();
                int countVertices = contarVertices(verticesActuales);

                // Si ya hay 3 vértices, no permitir agregar más
                if (countVertices >= 3) {
                    throw new IllegalStateException(
                            "No se pueden agregar más conexiones. El grafo ya tiene el máximo de 3 vértices."
                    );
                }

                grafoConexiones.AgregarVertice(nombreClienteConectado);
                grafoConexiones.AgregarArista(nombre, nombreClienteConectado, peso);
            }
        }
    }

    /**
     * Agrega una conexión con peso por defecto de 1
     */
    public void agregarConexion(String nombreClienteConectado) {
        agregarConexion(nombreClienteConectado, 1);
    }

    /**
     * Método auxiliar para contar vértices en el grafo
     */
    private int contarVertices(ConjuntoTDA<String> conjunto) {
        int count = 0;
        while (!conjunto.ConjuntoVacio()) {
            String v = conjunto.Elegir();
            count++;
            conjunto.Sacar(v);
        }
        return count;
    }

    /**
     * Obtiene el conjunto de clientes conectados directamente
     * Nota: Máximo 2 clientes conectados (3 vértices totales: este + 2 otros)
     * @return ConjuntoTDA<String> con los nombres de los clientes conectados
     */
    @JsonIgnore
    public ConjuntoTDA<String> obtenerConexionesDirectas() {
        return grafoConexiones.Vertices();
    }
}
