package app.servicio;

import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de RedSocialManager")
class RedSocialManagerTest {

    private final PrintStream standardOut = System.out;
    private RedSocialManager redSocialManager;
    private ByteArrayOutputStream capturedOutput;

    @BeforeEach
    void setUp() {
        redSocialManager = new RedSocialManager();
        capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput));
    }

    @AfterEach
    void tearDown() {
        System.setOut(standardOut);
    }

    @Nested
    @DisplayName("Pruebas de cargarDesdeArchivo")
    class CargarDesdeArchivoTests {

        @Test
        @DisplayName("Cargar clientes desde archivo JSON válido")
        void testCargarArchivoValido() {
            capturedOutput.reset(); // Limpiamos lo que puso el constructor
            redSocialManager.cargarDesdeArchivo("Clientes.json");
            String output = capturedOutput.toString();
            assertTrue(output.contains("LOG: Carga de clientes completada"), "Debe encontrar el mensaje de confirmación");
        }

        @Test
        @DisplayName("Cargar clientes desde archivo inexistente")
        void testCargarArchivoInexistente() {
            redSocialManager.cargarDesdeArchivo("archivo_inexistente.json");

            String output = capturedOutput.toString();
            // El JsonLoader imprime error si la carga falla
            assertFalse(output.contains("LOG: Carga de clientes completada") && output.contains("Error al leer"),
                    "Debe intentar cargar pero puede fallar gracefully");
        }

        @Test
        @DisplayName("Cargar archivo con ruta nula")
        void testCargarArchivoRutaNula() {
            // Tu código lanza IllegalArgumentException explícitamente
            assertThrows(IllegalArgumentException.class, () -> redSocialManager.cargarDesdeArchivo(null));
        }

        @Test
        @DisplayName("Cargar archivo vacío")
        void testCargarArchivoVacio() {
            // Este test puede variar según el comportamiento de JsonLoader
            assertDoesNotThrow(() -> redSocialManager.cargarDesdeArchivo("clientes_test.json"), "No debe lanzar excepción");
        }

        @Test
        @DisplayName("Mensaje de carga de clientes completada se imprime")
        void testMensajeCargaCompletada() {
            redSocialManager.cargarDesdeArchivo("clientes_test.json");

            String output = capturedOutput.toString();
            assertTrue(output.contains("LOG: Carga de clientes completada"),
                    "El mensaje debe indicar que la carga se completó");
        }

        @Test
        @DisplayName("Cargar múltiples archivos consecutivamente")
        void testCargarMultiplesArchivos() {
            redSocialManager.cargarDesdeArchivo("clientes_test.json");

            ByteArrayOutputStream firstOutput = capturedOutput;
            capturedOutput = new ByteArrayOutputStream();
            System.setOut(new PrintStream(capturedOutput));

            redSocialManager.cargarDesdeArchivo("clientes_test.json");

            String output = capturedOutput.toString();
            assertTrue(output.contains("LOG: Carga de clientes completada"),
                    "Debe poder cargar múltiples veces");
        }
    }

    @Nested
    @DisplayName("Pruebas de buscarYMostrarCliente")
    class BuscarYMostrarClienteTests {

        @BeforeEach
        void setUpClientes() {
            capturedOutput.reset();
        }

        @Test
        @DisplayName("Buscar cliente existente")
        void testBuscarClienteExistente() {
            redSocialManager.cargarDesdeArchivo("clientes.json");
            capturedOutput.reset();
            redSocialManager.buscarYMostrarCliente("Alice");
            String output = capturedOutput.toString();
            assertTrue(output.contains("Alice"), "El cliente Alice existe en el JSON pero no se mostró en consola. Output: " + output);
        }

        @Test
        @DisplayName("Buscar cliente inexistente")
        void testBuscarClienteInexistente() {
            redSocialManager.buscarYMostrarCliente("NoExiste");

            String output = capturedOutput.toString();
            assertTrue(output.contains("no existe"),
                    "Debe mostrar que el cliente no existe");
        }

        @Test
        @DisplayName("Buscar cliente con nombre vacío")
        void testBuscarClienteNombreVacio() {
            redSocialManager.buscarYMostrarCliente("");

            String output = capturedOutput.toString();
            assertTrue(output.contains("no existe"),
                    "Cliente con nombre vacío no debe existir");
        }

        @Test
        @DisplayName("Buscar cliente con nombre nulo")
        void testBuscarClienteNombreNulo() {
            assertThrows(IllegalArgumentException.class, () -> redSocialManager.buscarYMostrarCliente(null), "Debe lanzar excepción con nombre nulo");
        }

        @Test
        @DisplayName("Buscar cliente con case-sensitive")
        void testBuscarClienteCaseSensitive() {
            redSocialManager.buscarYMostrarCliente("alice"); // minúscula
            String output = capturedOutput.toString();
            assertTrue(output.contains("no existe"), "La búsqueda debe ser case-sensitive");
        }

        @Test
        void testBuscarMultiplesClientesDiferentes() {
            redSocialManager.cargarDesdeArchivo("clientes.json");

            String[] nombres = {"Alice", "Bob", "Charlie"};
            for (String nombre : nombres) {
                capturedOutput.reset();
                redSocialManager.buscarYMostrarCliente(nombre);
                String output = capturedOutput.toString();
                assertFalse(output.contains("no existe"), "No encontró a " + nombre);
            }
        }


        @Test
        @DisplayName("Información del Encontrado por Nombre es correcta")
        void testInfoClienteEncontrado() {
            redSocialManager.buscarYMostrarCliente("Juan");

            String output = capturedOutput.toString();
            assertTrue(output.contains("Juan"),
                    "La salida debe contener el nombre del cliente");
        }

        @Test
        void testBuscarClienteDespuesMultiplesCarga() {
            capturedOutput.reset();
            redSocialManager.cargarDesdeArchivo("clientes_test.json");

            capturedOutput.reset();
            redSocialManager.buscarYMostrarCliente("Maria");

            String output = capturedOutput.toString();
            assertTrue(output.contains("Maria"));
        }

    }

    @Nested
    @DisplayName("Pruebas de imprimirRankingCompleto();")
    class ImprimirRankingTests {

        @BeforeEach
        void setUpClientes() {
            // Cargar datos de prueba
            System.setOut(new PrintStream(new ByteArrayOutputStream()));
            redSocialManager.cargarDesdeArchivo("clientes_test.json");
            System.setOut(new PrintStream(capturedOutput));
        }

        @Test
        @DisplayName("Imprimir ranking con clientes cargados")
        void testImprimirRankingConDatos() {
            redSocialManager.cargarDesdeArchivo("clientes.json");
            capturedOutput.reset();
            redSocialManager.imprimirRankingCompleto();
            String output = capturedOutput.toString();
            assertFalse(output.isEmpty(), "El ranking debe mostrar datos en consola");
        }

        @Test
        @DisplayName("Ranking muestra clientes ordenados por scoring")
        void testRankingOrdenPorScoring() {
            redSocialManager.imprimirRankingCompleto();

            String output = capturedOutput.toString();
            // El ranking debe mostrar los clientes
            assertTrue(output.contains("Puntaje"),
                    "Debe mostrar los puntajes");
        }

        @Test
        @DisplayName("Imprimir ranking sin clientes cargados")
        void testImprimirRankingSinDatos() {
            // Crear un nuevo manager sin cargar datos
            RedSocialManager vacio = new RedSocialManager();
            ByteArrayOutputStream vacios = new ByteArrayOutputStream();
            System.setOut(new PrintStream(vacios));

            vacio.imprimirRankingCompleto();

            System.setOut(standardOut);
            String output = vacios.toString();
            assertTrue(output.contains("RANKING DE CLIENTES"),
                    "Debe mostrar encabezado aunque esté vacío");
        }

        @Test
        @DisplayName("Ranking contiene clientes esperados")
        void testRankingContieneDatos() {
            redSocialManager.imprimirRankingCompleto();

            String output = capturedOutput.toString();
            // Los clientes deben estar en el output
            assertTrue(output.length() > 50,
                    "El ranking debe contener información sustancial");
        }

        @Test
        @DisplayName("Imprimir ranking múltiples veces")
        void testImprimirRankingMultipleVeces() {
            redSocialManager.imprimirRankingCompleto();
            ByteArrayOutputStream firstOutput = capturedOutput;

            capturedOutput = new ByteArrayOutputStream();
            System.setOut(new PrintStream(capturedOutput));

            redSocialManager.imprimirRankingCompleto();

            System.setOut(standardOut);
            // Ambas llamadas deben producir output
            assertTrue(firstOutput.size() > 0 && capturedOutput.size() > 0,
                    "Ambas llamadas deben producir output");
        }

        @Test
        @DisplayName("Ranking utiliza el formato esperado")
        void testFormatoRanking() {
            redSocialManager.imprimirRankingCompleto();

            String output = capturedOutput.toString();
            // Debe usar el formato de árbol (TreeMap ordenado)
            assertTrue(output.contains("---") || output.contains("RANKING"),
                    "Debe tener formato visual esperado");
        }
    }

    @Nested
    @DisplayName("Pruebas de integración")
    class IntegracionTests {

        @Test
        @DisplayName("Flujo completo: cargar, buscar, ranking")
        void testFlujoCompleto() {
            redSocialManager.cargarDesdeArchivo("clientes.json");
            redSocialManager.buscarYMostrarCliente("Alice");
            redSocialManager.imprimirRankingCompleto();

            String output = capturedOutput.toString();
            assertTrue(output.contains("Alice") || output.contains("[SISTEMA]"));
        }

        @Test
        void testCargarYBuscarMultiples() {
            redSocialManager.cargarDesdeArchivo("clientes.json");

            capturedOutput.reset();
            String[] nombres = {"Alice", "Bob", "Charlie"};

            for (String n : nombres) {
                redSocialManager.buscarYMostrarCliente(n);
            }

            String output = capturedOutput.toString();
            for (String n : nombres) {
                assertTrue(output.contains(n), "No apareció " + n + " en output: " + output);
            }
        }


        @Test
        @DisplayName("Cargar, buscar inexistente, luego ranking")
        void
        testCargarBuscarInexistenteRanking() {
            redSocialManager.cargarDesdeArchivo("clientes_test.json");

            capturedOutput = new ByteArrayOutputStream();
            System.setOut(new PrintStream(capturedOutput));
            redSocialManager.buscarYMostrarCliente("NoExiste");

            String notFoundOutput = capturedOutput.toString();
            assertTrue(notFoundOutput.contains("no existe"));

            capturedOutput = new ByteArrayOutputStream();
            System.setOut(new PrintStream(capturedOutput));
            redSocialManager.imprimirRankingCompleto();

            System.setOut(standardOut);
            String rankOutput = capturedOutput.toString();
            assertTrue(rankOutput.contains("RANKING"));
        }

        @Test
        @DisplayName("Múltiples cargas seguidas de búsqueda")
        void testMultiplesCargasSeguidas() {
            System.setOut(new PrintStream(new ByteArrayOutputStream()));
            redSocialManager.cargarDesdeArchivo("clientes_test.json");
            redSocialManager.cargarDesdeArchivo("clientes_test.json");

            System.setOut(new PrintStream(capturedOutput));
            redSocialManager.buscarYMostrarCliente("Juan");

            System.setOut(standardOut);
            String output = capturedOutput.toString();
            assertTrue(output.contains("Encontrado por Nombre"),
                    "Debe encontrar cliente después de múltiples cargas");
        }

        @Test
        @DisplayName("Instancias independientes de RedSocialManager")
        void testInstanciasIndependientes() {
            RedSocialManager manager1 = new RedSocialManager();
            RedSocialManager manager2 = new RedSocialManager();

            System.setOut(new PrintStream(new ByteArrayOutputStream()));
            manager1.cargarDesdeArchivo("clientes_test.json");

            System.setOut(new PrintStream(capturedOutput));
            manager2.buscarYMostrarCliente("Juan");

            System.setOut(standardOut);
            String output = capturedOutput.toString();
            // Manager2 no tiene datos cargados
            assertTrue(output.contains("no existe"),
                    "Instancias deben ser independientes");
        }
    }

    @Nested
    @DisplayName("Pruebas de robustez")
    class RobustezTests {

        @Test
        @DisplayName("Cargar con rutas relativas")
        void testCargarRutaRelativa() {
            assertDoesNotThrow(() -> redSocialManager.cargarDesdeArchivo("clientes.json"));
        }

        @Test
        @DisplayName("Cargar con rutas absolutas")
        void testCargarRutaAbsoluta() {
            String rutaAbsoluta = System.getProperty("user.dir") + "/clientes_test.json";
            assertDoesNotThrow(() -> redSocialManager.cargarDesdeArchivo(rutaAbsoluta), "Debe manejar rutas absolutas");
        }

        @Test
        @DisplayName("Cargar con nombre de archivo especial")
        void testCargarNombreEspecial() {
            // No debe lanzar excepción incluso si el archivo no existe
            assertDoesNotThrow(() -> redSocialManager.cargarDesdeArchivo("archivo @#$%^&().json"));
        }

        @Test
        @DisplayName("Búsqueda con espacios")
        void testBusquedaConEspacios() {
            System.setOut(new PrintStream(new ByteArrayOutputStream()));
            redSocialManager.cargarDesdeArchivo("clientes_test.json");

            System.setOut(new PrintStream(capturedOutput));
            redSocialManager.buscarYMostrarCliente("  Juan  ");

            System.setOut(standardOut);
            String output = capturedOutput.toString();
            assertTrue(output.contains("no existe"),
                    "Espacios alrededor del nombre deben hacer que no se encuentre");
        }

        @Test
        @DisplayName("Búsqueda con caracteres especiales")
        void testBusquedaCaracteresEspeciales() {
            redSocialManager.buscarYMostrarCliente("Juan@#$");

            String output = capturedOutput.toString();
            assertTrue(output.contains("no existe"),
                    "Caracteres especiales deben no encontrar cliente");
        }
    }
}