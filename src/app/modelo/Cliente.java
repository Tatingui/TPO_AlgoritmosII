package app.modelo;

import app.interfaces.ColaTDA;
import app.interfaces.PilaTDA;
import app.implementaciones.ColaLD;
import app.implementaciones.PilaLD;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.ArrayList;

public class Cliente {
    private String nombre;
    private int scoring;
    private List<String> siguiendo = new ArrayList<>();
    private List<String> conexiones = new ArrayList<>();
    private List<String> solicitudes_pendientes = new ArrayList<>();
    private List<String> historial_personal = new ArrayList<>();

    @JsonIgnore
    private ColaTDA<String> solicitudes;
    @JsonIgnore
    private PilaTDA<String> historial;

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
    }

    public void inicializarEstructurasDesdeJson() {
        if (solicitudes == null) inicializarTDAs();

        if (solicitudes_pendientes != null) {
            for (String s : solicitudes_pendientes) {
                this.solicitudes.Acolar(s);
            }
        }
        if (historial_personal != null) {
            // Se apila en orden inverso para que el Tope sea el último del JSON
            for (int i = historial_personal.size() - 1; i >= 0; i--) {
                this.historial.Apilar(historial_personal.get(i));
            }
        }
    }

    public void prepararParaGuardar() {
        this.solicitudes_pendientes.clear();
        this.historial_personal.clear();

        // Volcar Cola de solicitudes
        ColaTDA<String> tempCola = new ColaLD();
        tempCola.InicializarCola();
        while (!this.solicitudes.ColaVacia()) {
            String s = this.solicitudes.Primero();
            this.solicitudes_pendientes.add(s);
            tempCola.Acolar(s);
            this.solicitudes.Desacolar();
        }
        this.solicitudes = tempCola;

        // Volcar Pila de historial
        PilaTDA<String> tempPila = new PilaLD();
        tempPila.InicializarPila();
        while (!this.historial.PilaVacia()) {
            String h = this.historial.Tope();
            this.historial_personal.add(0, h); // Insertar al inicio para mantener orden
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
    public List<String> getSolicitudes_pendientes() { return solicitudes_pendientes; }
    public List<String> getHistorial_personal() { return historial_personal; }

    @JsonIgnore
    public ColaTDA<String> getSolicitudes() { return solicitudes; }
    @JsonIgnore
    public PilaTDA<String> getHistorial() { return historial; }
}