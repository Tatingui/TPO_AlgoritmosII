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
        // Usamos 4 parámetros para coincidir con tu Record
        Cliente c = new Cliente("Messi", 99, new ArrayList<>(), new ArrayList<>());
        repositorio.guardarCliente(c);

        Cliente recuperado = repositorio.buscarPorNombre("Messi");

        assertNotNull(recuperado, "El cliente debería existir en el diccionario");
        assertEquals(99, recuperado.scoring(), "El scoring debe coincidir");
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

        // Buscamos los nombres, que son únicos y no se superponen como los números
        int indiceAlto = output.indexOf("Alto");
        int indiceBajo = output.indexOf("Bajo");

        assertTrue(indiceAlto != -1 && indiceBajo != -1, "Ambos clientes deben estar en la consola");

        // Si es de mayor a menor, "Alto" (100) debe estar antes (índice menor) que "Bajo" (10)
        assertTrue(indiceAlto < indiceBajo, "El cliente con 100 puntos debe aparecer antes que el de 10");
    }

    @Test
    @DisplayName("Debe manejar múltiples clientes con el mismo scoring")
    void testMismoScoring() {
        repositorio.guardarCliente(new Cliente("Messi", 100, new ArrayList<>(), new ArrayList<>()));
        repositorio.guardarCliente(new Cliente("Ronaldo", 100, new ArrayList<>(), new ArrayList<>()));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        repositorio.buscarPorScoring(100);

        System.setOut(originalOut);
        String output = outContent.toString();

        assertTrue(output.contains("Messi") && output.contains("Ronaldo"),
                "Debe encontrar a ambos jugadores aunque tengan el mismo puntaje");
    }

    @Test
    @DisplayName("El ranking no debe destruir los datos al ser consultado")
    void testRankingNoDestructivo() {
        repositorio.guardarCliente(new Cliente("Permanente", 50, new ArrayList<>(), new ArrayList<>()));

        // Consultamos el ranking una vez
        repositorio.mostrarRanking();

        // Verificamos si el cliente sigue existiendo en el repositorio
        assertNotNull(repositorio.buscarPorNombre("Permanente"),
                "El cliente no debe desaparecer después de mostrar el ranking");
    }

    @Test
    @DisplayName("Actualizar scoring de un cliente existente")
    void testActualizarCliente() {
        repositorio.guardarCliente(new Cliente("Alice", 10, new ArrayList<>(), new ArrayList<>()));
        // Volvemos a guardar con el mismo nombre pero distinto score
        repositorio.guardarCliente(new Cliente("Alice", 90, new ArrayList<>(), new ArrayList<>()));

        Cliente recuperado = repositorio.buscarPorNombre("Alice");
        assertEquals(90, recuperado.scoring(), "El diccionario debe tener el valor más reciente");
    }
    @Test
    @DisplayName("El ranking no debe fallar si el repositorio está vacío")
    void testRankingVacio() {
        // No guardamos nada
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // Esto no debería lanzar NullPointerException
        assertDoesNotThrow(() -> repositorio.mostrarRanking());

        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Búsqueda por scoring sin resultados")
    void testBusquedaScoringSinResultados() {
        repositorio.guardarCliente(new Cliente("Solo", 10, new ArrayList<>(), new ArrayList<>()));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // Buscamos un valor que no existe
        repositorio.buscarPorScoring(999);

        System.setOut(originalOut);
        String output = outContent.toString();

        assertFalse(output.contains("Solo"), "No debería mostrar al cliente de 10 puntos");
    }
}