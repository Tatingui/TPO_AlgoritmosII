package app.modelo;

import app.implementaciones.ColaLD;
import app.implementaciones.GrafoLA;
import app.implementaciones.PilaLD;
import app.interfaces.ColaTDA;
import app.interfaces.GrafoTDA;
import app.interfaces.PilaTDA;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

public class Cliente implements Comparable<Cliente> {
    private String nombre;
    private Integer scoring;
    private List<String> siguiendo = new ArrayList<>();
    private List<String> conexiones = new ArrayList<>();
    private List<String> solicitudesPendientes = new ArrayList<>();
    private List<String> historialPersonal = new ArrayList<>();

    @JsonIgnore
    private ColaTDA<String> solicitudes;
    @JsonIgnore
    private PilaTDA<String> historial;
    @JsonIgnore
    private GrafoTDA<String> grafoSiguiendo;
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

        this.grafoSiguiendo = new GrafoLA<>();
        this.grafoSiguiendo.InicializarGrafo();
        this.grafoConexiones = new GrafoLA<>();
        this.grafoConexiones.InicializarGrafo();
    }

    // ==================== MÉTODOS DE TRANSFORMACIÓN ====================

    /**
     * Convierte una lista de strings a grafo, agregando vértices y aristas con peso 1
     */
    private void listaAGrafo(List<String> lista, GrafoTDA<String> grafo) {
        if (lista == null || lista.isEmpty()) return;

        // Agregar vértices
        for (String nombre : lista) {
            grafo.AgregarVertice(nombre);
        }

        // Agregar aristas entre todos los vértices (peso 1)
        for (String origen : lista) {
            for (String destino : lista) {
                if (!origen.equals(destino) && !grafo.ExisteArista(origen, destino)) {
                    grafo.AgregarArista(origen, destino, 1);
                }
            }
        }
    }

    /**
     * Convierte un grafo a lista de vértices sin destruir el conjunto interno del grafo
     */
    private List<String> grafoALista(GrafoTDA<String> grafo) {
        List<String> lista = new ArrayList<>();
        if (grafo != null) {
            var vertices = grafo.Vertices();
            List<String> temp = new ArrayList<>();

            // Extraer todos los vértices a una lista temporal
            while (!vertices.ConjuntoVacio()) {
                String v = vertices.Elegir();
                temp.add(v);
                vertices.Sacar(v);
            }

            // Reconstruir el conjunto y agregar a la lista resultado
            for (String v : temp) {
                vertices.Agregar(v);
                lista.add(v);
            }
        }
        return lista;
    }

    // ==================== INICIALIZACIÓN Y PERSISTENCIA ====================

    public void inicializarEstructurasDesdeJson() {
        if (solicitudes == null) inicializarTDAs();

        if (this.nombre != null) {
            this.grafoConexiones.AgregarVertice(this.nombre);
        }

        if (conexiones != null) {
            for (String c : conexiones) {
                this.grafoConexiones.AgregarVertice(c);
                // Si es un grafo pesado, aquí pondrías el peso
                this.grafoConexiones.AgregarArista(this.nombre, c, 1);
            }
        }

        // Cargar solicitudes en la cola
        if (solicitudesPendientes != null) {
            for (String s : solicitudesPendientes) {
                this.solicitudes.Acolar(s);
            }
        }

        // Cargar historial en la pila
        if (historialPersonal != null) {
            for (int i = historialPersonal.size() - 1; i >= 0; i--) {
                this.historial.Apilar(historialPersonal.get(i));
            }
        }

        // Convertir listas a grafos
        listaAGrafo(this.siguiendo, this.grafoSiguiendo);
        listaAGrafo(this.conexiones, this.grafoConexiones);
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
            this.historialPersonal.add(0, h);
            tempPila.Apilar(h);
            this.historial.Desapilar();
        }
        this.historial = tempPila;

        this.siguiendo = grafoALista(this.grafoSiguiendo);
        this.conexiones = grafoALista(this.grafoConexiones);
    }

    // ==================== MÉTODOS DE MUTACIÓN ====================

    /**
     * Agrega un seguidor al grafo de siguiendo
     */
    public void agregarSeguidor(String nombre) {
        if (nombre == null || nombre.isBlank()) return;

        // Agregar solo el vértice del seguidor, sin auto-referencias
        if (!grafoSiguiendo.Vertices().Pertenece(nombre)) {
            grafoSiguiendo.AgregarVertice(nombre);
        }
    }


    /**
     * Agrega una conexión al grafo de conexiones (bidireccional)
     */
    public void agregarConexion(String nombre) {
        if (nombre == null || nombre.isBlank()) return;

        // Agregar vértices si no existen
        if (!grafoConexiones.Vertices().Pertenece(nombre)) {
            grafoConexiones.AgregarVertice(nombre);
        }
        if (!grafoConexiones.Vertices().Pertenece(this.nombre)) {
            grafoConexiones.AgregarVertice(this.nombre);
        }

        // Agregar aristas bidireccionales si no existen
        if (!grafoConexiones.ExisteArista(this.nombre, nombre)) {
            grafoConexiones.AgregarArista(this.nombre, nombre, 1);
        }
        if (!grafoConexiones.ExisteArista(nombre, this.nombre)) {
            grafoConexiones.AgregarArista(nombre, this.nombre, 1);
        }
    }

    // ==================== GETTERS ====================

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getScoring() {
        return scoring;
    }

    public void setScoring(Integer scoring) {
        this.scoring = scoring;
    }

    /**
     * Retorna lista de siguiendo (para compatibilidad con código existente y JSON)
     */
    public List<String> getSiguiendo() {
        return siguiendo;
    }

    /**
     * Retorna lista de conexiones (para compatibilidad con código existente y JSON)
     */
    public List<String> getConexiones() {
        return conexiones;
    }

    /**
     * Obtiene todos los seguidores del grafo (nuevo método)
     */
    @JsonIgnore
    public List<String> obtenerTodosSiguiendo() {
        return grafoALista(grafoSiguiendo);
    }

    /**
     * Obtiene todas las conexiones del grafo (nuevo método)
     */
    @JsonIgnore
    public List<String> obtenerTodasConexiones() {
        return grafoALista(grafoConexiones);
    }

    public List<String> getSolicitudesPendientes() {
        return solicitudesPendientes;
    }

    public List<String> getHistorialPersonal() {
        return historialPersonal;
    }

    @JsonIgnore
    public ColaTDA<String> getSolicitudes() {
        return solicitudes;
    }

    @JsonIgnore
    public PilaTDA<String> getHistorial() {
        return historial;
    }

    @JsonIgnore
    public GrafoTDA<String> getGrafoSiguiendo() {
        return grafoSiguiendo;
    }

    @JsonIgnore
    public GrafoTDA<String> getGrafoConexiones() {
        return grafoConexiones;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Cliente cliente)) return false;

        return new EqualsBuilder().append(getNombre(), cliente.getNombre()).append(getScoring(), cliente.getScoring()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getNombre()).append(getScoring()).toHashCode();
    }

    @Override
    public int compareTo(Cliente o) {
        int cmp = o.scoring.compareTo(this.scoring);
        if (cmp != 0) return cmp;
        return this.nombre.compareToIgnoreCase(o.nombre);
    }
}