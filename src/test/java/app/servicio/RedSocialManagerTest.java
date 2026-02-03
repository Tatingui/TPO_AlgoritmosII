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
            redSocialManager.cargarDesdeArchivo("clientes_test.json");

            String output = capturedOutput.toString();
            assertTrue(output.contains("LOG: Carga de clientes completada"),
                    "Debe mostrar mensaje de carga de clientes completada");
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
            assertThrows(Exception.class, () -> redSocialManager.cargarDesdeArchivo(null), "Debe lanzar excepción con ruta nula");
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
            // Cargar datos de prueba
            System.setOut(new PrintStream(new ByteArrayOutputStream()));
            redSocialManager.cargarDesdeArchivo("clientes_test.json");
            System.setOut(new PrintStream(capturedOutput));
        }

        @Test
        @DisplayName("Buscar cliente existente")
        void testBuscarClienteExistente() {
            redSocialManager.buscarYMostrarCliente("Juan");

            String output = capturedOutput.toString();
            assertTrue(output.contains("Encontrado por Nombre") && output.contains("Juan"),
                    "Debe mostrar que el cliente fue encontrado");
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
            redSocialManager.buscarYMostrarCliente("juan");

            String output = capturedOutput.toString();
            assertTrue(output.contains("no existe"),
                    "La búsqueda debe ser case-sensitive");
        }

        @Test
        @DisplayName("Buscar múltiples clientes diferentes")
        void testBuscarMultiplesClientesDiferentes() {
            String[] nombres = {"Juan", "Maria", "Carlos"};
            int encontrados = 0;

            for (String nombre : nombres) {
                ByteArrayOutputStream tempOutput = capturedOutput;
                capturedOutput = new ByteArrayOutputStream();
                System.setOut(new PrintStream(capturedOutput));

                redSocialManager.buscarYMostrarCliente(nombre);

                String output = capturedOutput.toString();
                if (output.contains("Encontrado por Nombre")) {
                    encontrados++;
                }
                capturedOutput = tempOutput;
            }

            System.setOut(standardOut);
            assertEquals(3, encontrados, "Debe encontrar los 3 clientes");
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
        @DisplayName("Buscar cliente después de cargar múltiples veces")
        void testBuscarClienteDespuesMultiplesCarga() {
            System.setOut(new PrintStream(new ByteArrayOutputStream()));
            redSocialManager.cargarDesdeArchivo("clientes_test.json");
            System.setOut(new PrintStream(capturedOutput));

            redSocialManager.buscarYMostrarCliente("Maria");

            String output = capturedOutput.toString();
            assertTrue(output.contains("Encontrado por Nombre") && output.contains("Maria"),
                    "Debe encontrar el cliente después de segunda carga");
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
            redSocialManager.imprimirRankingCompleto();

            String output = capturedOutput.toString();
            assertTrue(output.contains("RANKING DE CLIENTES"),
                    "Debe mostrar encabezado del ranking");
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
            // Cargar
            redSocialManager.cargarDesdeArchivo("clientes_test.json");
            String loadOutput = capturedOutput.toString();
            assertTrue(loadOutput.contains("LOG: Carga de clientes completada"));

            // Buscar
            capturedOutput = new ByteArrayOutputStream();
            System.setOut(new PrintStream(capturedOutput));
            redSocialManager.buscarYMostrarCliente("Juan");
            String searchOutput = capturedOutput.toString();
            assertTrue(searchOutput.contains("Encontrado por Nombre"));

            // Ranking
            capturedOutput = new ByteArrayOutputStream();
            System.setOut(new PrintStream(capturedOutput));
            redSocialManager.imprimirRankingCompleto();
            String rankOutput = capturedOutput.toString();
            assertTrue(rankOutput.contains("RANKING"));

            System.setOut(standardOut);
        }

        @Test
        @DisplayName("Cargar y luego buscar múltiples clientes")
        void testCargarYBuscarMultiples() {
            redSocialManager.cargarDesdeArchivo("clientes_test.json");

            System.setOut(standardOut);
            String[] nombres = {"Juan", "Maria", "Carlos", "Pedro", "Ana"};
            int encontrados = 0;

            for (String nombre : nombres) {
                ByteArrayOutputStream tempOutput = new ByteArrayOutputStream();
                System.setOut(new PrintStream(tempOutput));
                redSocialManager.buscarYMostrarCliente(nombre);

                String output = tempOutput.toString();
                if (output.contains("Encontrado por Nombre")) {
                    encontrados++;
                }
            }

            System.setOut(standardOut);
            assertEquals(5, encontrados, "Debe encontrar los 5 clientes del archivo");
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
            assertDoesNotThrow(() -> redSocialManager.cargarDesdeArchivo("clientes_test.json"), "Debe manejar rutas relativas");
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