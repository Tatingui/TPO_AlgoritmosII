package app.servicio;

import app.implementaciones.PilaLD;
import app.interfaces.PilaTDA;
import app.modelo.Cliente;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Completas de HistorialServicio")
class HistorialServicioTest {

    private final PrintStream standardOut = System.out;
    private ByteArrayOutputStream capturedOutput;
    private Cliente clientePrueba;

    @BeforeEach
    void setUp() {
        // Inicializamos el cliente con listas vacías
        clientePrueba = new Cliente("TestUser", 100, new ArrayList<>(), new ArrayList<>());
        // Importante: Forzamos la inicialización de los TDAs internos
        clientePrueba.inicializarEstructurasDesdeJson();

        capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput));
    }

    @AfterEach
    void tearDown() {
        System.setOut(standardOut);
    }

    /**
     * Auxiliar para contar elementos en la pila sin destruirla.
     */
    private int contarAccionesEnPila() {
        PilaTDA<String> pila = clientePrueba.getHistorial();
        int contador = 0;
        PilaTDA<String> temp = new PilaLD<>();
        temp.InicializarPila();

        while (!pila.PilaVacia()) {
            contador++;
            temp.Apilar(pila.Tope());
            pila.Desapilar();
        }
        while (!temp.PilaVacia()) {
            pila.Apilar(temp.Tope());
            temp.Desapilar();
        }
        return contador;
    }

    @Nested
    @DisplayName("Pruebas de Registro de Acciones")
    class RegistroTests {

        @Test
        @DisplayName("Registrar múltiples acciones preservando el orden")
        void testRegistrarMultiples() {
            clientePrueba.getHistorial().Apilar("Accion 1");
            clientePrueba.getHistorial().Apilar("Accion 2");
            clientePrueba.getHistorial().Apilar("Accion 3");

            assertEquals(3, contarAccionesEnPila(), "Debe haber exactamente 3 acciones");
            assertEquals("Accion 3", clientePrueba.getHistorial().Tope(), "El tope debe ser la última ingresada (LIFO)");
        }

        @Test
        @DisplayName("Registrar con Strings vacíos")
        void testRegistrarAccionVacia() {
            clientePrueba.getHistorial().Apilar("");
            assertEquals(1, contarAccionesEnPila(), "Debe registrarse aunque sea un string vacío");
        }

        @Test
        @DisplayName("Registrar valor null")
        void testRegistrarNull() {
            // Verificamos que la implementación de PilaLD no explote con null
            assertDoesNotThrow(() -> clientePrueba.getHistorial().Apilar(null));
        }
    }

    @Nested
    @DisplayName("Pruebas de Deshacer (Undo)")
    class DeshacerTests {

        @Test
        @DisplayName("Deshacer en historial vacío")
        void testDeshacerVacio() {
            HistorialServicio.deshacerUltimaAccion(clientePrueba.getHistorial());
            String output = capturedOutput.toString();
            // Verificamos que el mensaje contenga la advertencia de vacío
            assertTrue(output.contains("vacío") || output.contains("No hay acciones"),
                    "Debe informar que no hay nada para deshacer");
        }

        @Test
        @DisplayName("Deshacer quita el elemento correcto (LIFO)")
        void testDeshacerOrdenLIFO() {
            clientePrueba.getHistorial().Apilar("Primera");
            clientePrueba.getHistorial().Apilar("Segunda");

            HistorialServicio.deshacerUltimaAccion(clientePrueba.getHistorial());

            assertEquals(1, contarAccionesEnPila());
            assertEquals("Primera", clientePrueba.getHistorial().Tope(), "Debe quedar la primera acción");
        }

        @Test
        @DisplayName("Deshacer más veces de las posibles")
        void testDeshacerExcesivo() {
            clientePrueba.getHistorial().Apilar("Única");

            HistorialServicio.deshacerUltimaAccion(clientePrueba.getHistorial()); // Queda vacía
            HistorialServicio.deshacerUltimaAccion(clientePrueba.getHistorial()); // Intenta en vacío

            String output = capturedOutput.toString();
            assertTrue(output.contains("vacío") || output.contains("No hay acciones"),
                    "Debe manejar el vaciado total");
            assertEquals(0, contarAccionesEnPila());
        }
    }

    @Nested
    @DisplayName("Pruebas de Visualización")
    class MostrarTests {

        @Test
        @DisplayName("Mostrar historial no debe destruir los datos")
        void testMostrarNoDestructivo() {
            clientePrueba.getHistorial().Apilar("Accion A");
            clientePrueba.getHistorial().Apilar("Accion B");

            int antes = contarAccionesEnPila();
            HistorialServicio.mostrarHistorialPersonal(clientePrueba.getHistorial());
            int despues = contarAccionesEnPila();

            assertEquals(antes, despues, "Mostrar historial no debe vaciar la pila");
        }

        @Test
        @DisplayName("Mostrar historial vacío")
        void testMostrarVacio() {
            HistorialServicio.mostrarHistorialPersonal(clientePrueba.getHistorial());
            String output = capturedOutput.toString();
            assertTrue(output.contains("vacío") || output.contains("Historial"),
                    "Debe mostrar que el historial no tiene datos o el encabezado");
        }

        @Test
        @DisplayName("Verificar orden LIFO al mostrar")
        void testOrdenAlMostrar() {
            clientePrueba.getHistorial().Apilar("Vieja");
            clientePrueba.getHistorial().Apilar("Nueva");

            HistorialServicio.mostrarHistorialPersonal(clientePrueba.getHistorial());
            String output = capturedOutput.toString();

            int posNueva = output.indexOf("Nueva");
            int posVieja = output.indexOf("Vieja");

            assertTrue(posNueva != -1 && posVieja != -1, "Ambas acciones deben figurar en consola");
            assertTrue(posNueva < posVieja, "Al mostrar, la acción más reciente debe aparecer arriba");
        }
    }

    @Nested
    @DisplayName("Pruebas de Integración")
    class IntegracionTests {

        @Test
        @DisplayName("Flujo: Registrar -> Mostrar -> Deshacer -> Registrar")
        void testFlujoCompleto() {
            clientePrueba.getHistorial().Apilar("Login");
            clientePrueba.getHistorial().Apilar("Busqueda");

            HistorialServicio.deshacerUltimaAccion(clientePrueba.getHistorial()); // Borra Busqueda
            clientePrueba.getHistorial().Apilar("Logout");

            assertEquals(2, contarAccionesEnPila());
            assertEquals("Logout", clientePrueba.getHistorial().Tope());
        }
    }
}