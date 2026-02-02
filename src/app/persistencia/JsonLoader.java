package app.persistencia;

import app.modelo.Clientes;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.json.JsonMapper;

import java.io.File;

public class JsonLoader {
    public Clientes cargarClientes(String ruta) {
        Clientes clientes = null;
        try (final JsonParser mapper = new JsonMapper().createParser(new File(ruta));) {
            clientes = mapper.readValueAs(Clientes.class);
        } catch (Exception e) {
            System.out.println("Error al leer el archivo JSON: " + e.getMessage());
        }
        return clientes;
    }
}