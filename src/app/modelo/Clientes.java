package app.modelo;

import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public class Clientes {
    private List<Cliente> clientes;

    // Constructor vacío para Jackson
    public Clientes() {}

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, JSON_STYLE)
                .append("clientes", clientes)
                .toString();
    }
}