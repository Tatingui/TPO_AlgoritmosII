/*
 * TAD de Dominio que representa a un usuario de la red social.
 * Se utiliza como el objeto principal que circulará por todo el sistema.
 */

package app.modelo;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public record Cliente(String nombre, int scoring, List<String> siguiendo, List<String> conexiones) {
    @Override
    public String toString() {
        return new ToStringBuilder(this, JSON_STYLE)
                .append("nombre", nombre())
                .append("scoring", scoring())
                .append("siguiendo", siguiendo())
                .append("conexiones", conexiones())
                .toString();
    }
}
