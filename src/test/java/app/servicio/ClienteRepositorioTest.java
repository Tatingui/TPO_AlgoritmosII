package app.servicio;

import java.util.ArrayList;
import app.modelo.Cliente;
import app.repositorio.ClienteRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

@DisplayName("Pruebas del Repositorio de Clientes (TADs)")
class ClienteRepositorioTest {

    private ClienteRepositorio repositorio;

    @BeforeEach
    void setUp() {
        repositorio = new ClienteRepositorio();
    }

    @Test
    @DisplayName("Debe guardar y recuperar un cliente por nombre")
    void testGuardarYRecuperar() {
        Cliente c = new Cliente("Messi", 99, new ArrayList<>(), new ArrayList<>());
        repositorio.guardarCliente(c);

        Cliente recuperado = repositorio.buscarPorNombre("Messi");

        assertNotNull(recuperado, "El cliente debería existir en el diccionario");
        assertEquals(99, recuperado.getScoring(), "El scoring debe coincidir");
    }

    @Test
    @DisplayName("Debe retornar null si el cliente no existe")
    void testBuscarInexistente() {
        Cliente recuperado = repositorio.buscarPorNombre("Inexistente");
        assertNull(recuperado, "Debe devolver null sin lanzar excepción");
    }

    @Test
    @DisplayName("La búsqueda por scoring debe filtrar correctamente")
    void testBusquedaPorScoring() {
        repositorio.guardarCliente(new Cliente("Alice", 88, new ArrayList<>(), new ArrayList<>()));
        repositorio.guardarCliente(new Cliente("Bob", 70, new ArrayList<>(), new ArrayList<>()));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        repositorio.buscarPorScoring(88);

        System.setOut(originalOut);
        String output = outContent.toString();

        assertTrue(output.contains("Alice"), "Debe encontrar a Alice");
        assertFalse(output.contains("Bob"), "No debe encontrar a Bob");
    }

    @Test
    @DisplayName("El ranking debe mostrar los clientes de mayor a menor scoring")
    void testRankingOrden() {
        repositorio.guardarCliente(new Cliente("Bajo", 10, new ArrayList<>(), new ArrayList<>()));
        repositorio.guardarCliente(new Cliente("Alto", 100, new ArrayList<>(), new ArrayList<>()));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        repositorio.mostrarRanking();

        System.setOut(originalOut);
        String output = outContent.toString();

        int indiceAlto = output.indexOf("Alto");
        int indiceBajo = output.indexOf("Bajo");

        assertTrue(indiceAlto != -1 && indiceBajo != -1, "Ambos clientes deben estar en la consola");
        assertTrue(indiceAlto < indiceBajo, "El cliente con 100 puntos debe aparecer antes que el de 10");
    }

    @Test
    @DisplayName("Actualizar scoring de un cliente existente")
    void testActualizarCliente() {
        repositorio.guardarCliente(new Cliente("Alice", 10, new ArrayList<>(), new ArrayList<>()));
        repositorio.guardarCliente(new Cliente("Alice", 90, new ArrayList<>(), new ArrayList<>()));

        Cliente recuperado = repositorio.buscarPorNombre("Alice");
        // CAMBIO: scoring() -> getScoring()
        assertEquals(90, recuperado.getScoring(), "El diccionario debe tener el valor más reciente");
    }

    // ... (El resto de los tests permanecen igual ya que no usan .scoring())

    @Test
    @DisplayName("Búsqueda por scoring sin resultados")
    void testBusquedaScoringSinResultados() {
        repositorio.guardarCliente(new Cliente("Solo", 10, new ArrayList<>(), new ArrayList<>()));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        repositorio.buscarPorScoring(999);

        System.setOut(originalOut);
        String output = outContent.toString();

        assertFalse(output.contains("Solo"), "No debería mostrar al cliente de 10 puntos");
    }

    @Test
    @DisplayName("Obtener todos los clientes en orden descendente por scoring")
    void testObtenerTodosOrdenado() {
        repositorio.guardarCliente(new Cliente("Messi", 99, new ArrayList<>(), new ArrayList<>()));
        repositorio.guardarCliente(new Cliente("Maradona", 95, new ArrayList<>(), new ArrayList<>()));
        repositorio.guardarCliente(new Cliente("Pele", 97, new ArrayList<>(), new ArrayList<>()));

        var clientes = repositorio.obtenerTodos();

        assertEquals(3, clientes.size(), "Debe tener 3 clientes");
        assertEquals(99, clientes.get(0).getScoring(), "Primer cliente debe tener scoring 99");
        assertEquals(97, clientes.get(1).getScoring(), "Segundo cliente debe tener scoring 97");
        assertEquals(95, clientes.get(2).getScoring(), "Tercer cliente debe tener scoring 95");
    }

    @Test
    @DisplayName("Búsqueda rápida por nombre O(log n)")
    void testBusquedaPorNombreEficiente() {
        // Agregar múltiples clientes para verificar que la búsqueda funciona con el árbol de nombres
        for (int i = 0; i < 100; i++) {
            repositorio.guardarCliente(new Cliente("Cliente" + i, i, new ArrayList<>(), new ArrayList<>()));
        }

        Cliente encontrado = repositorio.buscarPorNombre("Cliente50");
        assertNotNull(encontrado, "Debe encontrar Cliente50");
        assertEquals(50, encontrado.getScoring(), "Cliente50 debe tener scoring 50");
    }
}