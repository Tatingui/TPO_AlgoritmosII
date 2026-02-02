package app.modelo;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public record Clientes(List<Cliente> clientes) {
    @Override
    public String toString() {
        return new ToStringBuilder(this, JSON_STYLE)
                .append("clientes", clientes())
                .toString();
    }
}
