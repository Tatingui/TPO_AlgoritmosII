/*
 * TAD de Dominio que representa un evento en el sistema.
 * Es la unidad mínima de información que guardaremos en la Pila (Stack).
 */

package modelo;

import java.time.LocalDateTime;
import java.util.StringJoiner;

public class Accion {
    private String tipoAccion;
    private String descripcion;
    private LocalDateTime timestamp;

    public Accion(String tipoAccion, String descripcion) {
        this.tipoAccion = tipoAccion;
        this.descripcion = descripcion;
        this.timestamp = LocalDateTime.now();
    }

    public String getTipoAccion() {
        return tipoAccion;
    }

    public void setTipoAccion(String tipoAccion) {
        this.tipoAccion = tipoAccion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Accion.class.getSimpleName() + "[", "]")
                .add("tipoAccion='" + getTipoAccion() + "'")
                .add("descripcion='" + getDescripcion() + "'")
                .add("timestamp=" + getTimestamp().toString())
                .toString();
    }
}
