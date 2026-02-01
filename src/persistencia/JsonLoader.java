package persistencia;

import modelo.Cliente;
import interfaces.ColaTDA;
import implementaciones.ColaLD;
import java.io.File;
import java.util.Scanner;

public class JsonLoader {

    public ColaTDA<Cliente> cargarClientes(String ruta) {
        ColaTDA<Cliente> cola = new ColaLD<Cliente>();
        cola.InicializarCola();

        try (Scanner scanner = new Scanner(new File(ruta))) {
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine().trim();

                if (linea.contains("\"nombre\"")) {
                    String nombre = extraerValor(linea);

                    if (scanner.hasNextLine()) {
                        String lineaScoring = scanner.nextLine().trim();
                        int scoring = Integer.parseInt(extraerValor(lineaScoring));

                        cola.Acolar(new Cliente(nombre, scoring));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error al leer el archivo JSON: " + e.getMessage());
        }
        return cola;
    }

    private String extraerValor(String linea) {
        return linea.split(":")[1].replace("\"", "").replace(",", "").trim();
    }
}