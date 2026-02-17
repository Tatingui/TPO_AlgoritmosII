package app.persistencia;

import app.modelo.Cliente;
import app.modelo.Clientes;
import tools.jackson.databind.json.JsonMapper;
import java.io.File;

public class JsonLoader {
    public Clientes cargarClientes(String ruta) {
        try {
            JsonMapper mapper = new JsonMapper();
            // 1. Jackson lee el JSON y crea los objetos Cliente
            Clientes wrapper = mapper.readValue(new File(ruta), Clientes.class);

            if (wrapper != null && wrapper.getClientes() != null) {
                // 2. "Sembramos" los TDAs con los datos que Jackson leyó en las listas
                for (Cliente c : wrapper.getClientes()) {
                    c.inicializarEstructurasDesdeJson();
                }
            }
            return wrapper;
        } catch (Exception e) {
            System.out.println("Error al leer el archivo JSON: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}