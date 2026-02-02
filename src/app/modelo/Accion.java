/*
 * TAD de Dominio que representa un evento en el sistema.
 * Es la unidad mínima de información que guardaremos en la Pila (Stack).
 */

package app.modelo;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDateTime;

import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public record Accion(String tipoAccion, String descripcion, LocalDateTime timestamp) {
    @Override
    public String toString() {
        return new ToStringBuilder(this, JSON_STYLE)
                .append("tipoAccion", tipoAccion())
                .append("descripcion", descripcion())
                .append("timestamp", timestamp())
                .toString();
    }
}
