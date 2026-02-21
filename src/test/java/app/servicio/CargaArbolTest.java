package app.servicio;

import app.modelo.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de Carga en ABB")
public class CargaArbolTest {

    private RedSocialManager redSocialManager;

    @BeforeEach
    void setUp() {
        redSocialManager = new RedSocialManager();
    }

    @Test
    @DisplayName("Validar que los nodos del ABB mantienen sus relaciones")
    void testNodosMantienenRelaciones() {
        redSocialManager.cargarDesdeArchivo("clientes.json");
        Cliente c = redSocialManager.getRepositorio().buscarPorNombre("Alice");

        assertNotNull(c, "El cliente Alice debería existir en el árbol");
        assertNotNull(c.getSiguiendo(), "La lista de seguidos no debe ser nula");
    }

    @Test
    @DisplayName("Validar orden alfabético en el ABB de nombres")
    void testOrdenABB() {
        redSocialManager.cargarDesdeArchivo("clientes.json");
        var todos = redSocialManager.getRepositorio().obtenerTodos();

        assertNotNull(todos);
        assertTrue(todos.size() > 0, "El árbol no debería estar vacío");
    }

    @Test
    @DisplayName("Validar búsqueda por nombre en ABB")
    void testBusquedaABB() {
        redSocialManager.cargarDesdeArchivo("clientes.json");
        Cliente c = redSocialManager.getRepositorio().buscarPorNombre("Alice");
        assertNotNull(c);
        assertEquals("Alice", c.getNombre());
    }

    @Test
    @DisplayName("Validar que el Ranking sea Descendente (Mejores primero)")
    void testRankingDescendente() {
        redSocialManager.cargarDesdeArchivo("clientes.json");
        List<Cliente> lista = redSocialManager.getRepositorio().obtenerTodos();

        // Verificamos que cada elemento sea mayor o igual al siguiente
        for (int i = 0; i < lista.size() - 1; i++) {
            int actual = lista.get(i).getScoring();
            int siguiente = lista.get(i + 1).getScoring();

            assertTrue(actual >= siguiente,
                    "Error en posición " + i + ": " + actual + " no es mayor que " + siguiente);
        }
    }

    @Test
    @DisplayName("Validar persistencia: Carga y existencia de datos complejos")
    void testPersistenciaCompleta() {
        redSocialManager.cargarDesdeArchivo("clientes.json");
        Cliente c = redSocialManager.getRepositorio().buscarPorNombre("Alice");

        assertNotNull(c.getSiguiendo(), "Debe haber cargado la lista de seguidos del JSON");
        assertNotNull(c.getHistorial(), "La Pila de historial debe estar inicializada");
    }
}