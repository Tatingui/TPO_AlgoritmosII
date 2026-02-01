package persistencia;

import modelo.Cliente;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JsonLoader {

    public List<Cliente> cargarClientes(String ruta) {
        List<Cliente> lista = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(ruta))) {
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine().trim();

                // Busca la línea que tiene el nombre
                if (linea.contains("\"nombre\"")) {
                    String nombre = extraerValor(linea);

                    // Lee la siguiente línea para el scoring
                    if (scanner.hasNextLine()) {
                        String lineaScoring = scanner.nextLine().trim();
                        int scoring = Integer.parseInt(extraerValor(lineaScoring));
                        lista.add(new Cliente(nombre, scoring));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error al leer el archivo JSON: " + e.getMessage());
        }
        return lista;
    }

    private String extraerValor(String linea) {
        // Limpia las comillas, comas y espacios después de los dos puntos
        return linea.split(":")[1]
                .replace("\"", "")
                .replace(",", "")
                .trim();
    }
}