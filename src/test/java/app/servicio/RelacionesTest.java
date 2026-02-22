package app.servicio;

import app.modelo.Cliente;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class RelacionesTest {
    private RedSocialManager manager;

    @BeforeEach
    void setUp() {
        manager = new RedSocialManager();
    }

    @Test
    @DisplayName("Validar que las conexiones se cargan en el Grafo desde el JSON")
    void testCargaConexionesGrafo() {
        Cliente alice = manager.getRepositorio().buscarPorNombre("Alice");

        assertNotNull(alice);
        assertNotNull(alice.getGrafoConexiones(), "El grafo de conexiones debe estar inicializado");

        List<String> conexiones = alice.obtenerTodasConexiones();
        assertFalse(conexiones.isEmpty(), "Alice debería tener conexiones cargadas");
    }

    @Test
    @DisplayName("Validar integridad de la red al agregar nueva conexión")
    void testNuevaConexionPersistencia() {
        Cliente alice = manager.getRepositorio().buscarPorNombre("Alice");
        String nuevoAmigo = "Bob";

        alice.getGrafoConexiones().AgregarVertice(nuevoAmigo);
        alice.getGrafoConexiones().AgregarArista(alice.getNombre(), nuevoAmigo, 1);

        alice.prepararParaGuardar();
        assertTrue(alice.getConexiones().contains(nuevoAmigo),
                "La lista de persistencia debe tener al nuevo amigo antes de guardar");
    }

    @Test
    @DisplayName("Validar bidireccionalidad en el Grafo de Conexiones")
    void testConexionesBidireccionales() {
        Cliente alice = manager.getRepositorio().buscarPorNombre("Alice");
        String amigo = "Bob";

        alice.getGrafoConexiones().AgregarVertice(amigo);
        alice.getGrafoConexiones().AgregarArista(alice.getNombre(), amigo, 1);

        assertTrue(alice.getGrafoConexiones().ExisteArista(alice.getNombre(), amigo),
                "Debe existir la arista Alice -> Bob");
    }

    @Test
    @DisplayName("Validar restricción de máximo 2 seguidos")
    void testLimiteSeguidores() {
        Cliente alice = manager.getRepositorio().buscarPorNombre("Alice");
        if (alice == null) {
            alice = new Cliente("Alice", 100, new ArrayList<>(), new ArrayList<>());
            manager.getRepositorio().guardarCliente(alice);
        }

        manager.getRepositorio().guardarCliente(new Cliente("User1", 10, new ArrayList<>(), new ArrayList<>()));
        manager.getRepositorio().guardarCliente(new Cliente("User2", 10, new ArrayList<>(), new ArrayList<>()));
        manager.getRepositorio().guardarCliente(new Cliente("User3", 10, new ArrayList<>(), new ArrayList<>()));

        alice.getSiguiendo().clear();
        alice.getSiguiendo().add("User1");
        alice.getSiguiendo().add("User2");

        // 3. Intentamos seguir al tercero
        SolicitudesServicio.enviarSolicitud("Alice", "User3", manager.getRepositorio());

        // 4. Verificamos la pila con seguridad
        assertFalse(alice.getHistorial().PilaVacia(), "La pila no debería estar vacía, debería tener el log del error");
        String ultimoMovimiento = alice.getHistorial().Tope();
        assertTrue(ultimoMovimiento.contains("Límite 2"), "El historial debe mencionar el límite");
    }

    @Test
    @DisplayName("Validar que el borrado de conexiones persiste en el JSON")
    void testPersistenciaBorrado() {
        String rutaTest = "clientes_test_borrado.json";
        Cliente alice = manager.getRepositorio().buscarPorNombre("Alice");

        alice.getConexiones().clear();
        alice.getGrafoConexiones().InicializarGrafo(); // Esto vacía el TDA de raíz

        manager.guardarDatos(rutaTest);

        RedSocialManager nuevoManager = new RedSocialManager();
        nuevoManager.cargarDesdeArchivo(rutaTest);

        Cliente aliceCargada = nuevoManager.getRepositorio().buscarPorNombre("Alice");
        assertTrue(aliceCargada.getConexiones().isEmpty(),
                "La lista debería estar vacía porque el grafo se guardó vacío");
    }
}