package repositorio; // Única vez y en la primera línea

import modelo.Cliente;
import java.util.*;
/*
 * Clase encargada de la persistencia en memoria RAM.
 * Implementa las estructuras necesarias para cumplir con la eficiencia exigida.
 */

public class ClienteRepositorio {
    // Búsqueda por nombre: Eficiencia O(1) - HashMap
    private Map<String, Cliente> mapaNombres = new HashMap<>();

    // Búsqueda por scoring: Eficiencia O(log n) - (TreeMap)
    private TreeMap<Integer, List<Cliente>> mapaScoring = new TreeMap<>(Collections.reverseOrder());

    public void guardarCliente(Cliente cliente) {
        // Guardar en el mapa de nombres O(1)
        mapaNombres.put(cliente.getNombre(), cliente);

        // Guardar en el árbol de scoring O(log n)
        mapaScoring.computeIfAbsent(cliente.getScoring(), k -> new ArrayList<>()).add(cliente);
    }

    public Cliente buscarPorNombre(String nombre) {
        return mapaNombres.get(nombre); // O(1)
    }

    public List<Cliente> buscarPorScoringExacto(int scoring) {
        return mapaScoring.get(scoring); // O(log n)
    }

    public void mostrarRanking() {
        System.out.println("--- RANKING DE CLIENTES (Ordenado por Árbol) ---");
        mapaScoring.forEach((puntos, clientes) -> {
            System.out.println("Puntaje " + puntos + ": " + clientes);
        });
    }
}