package app.servicio;

import app.implementaciones.PilaLD;
import app.modelo.Accion;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de HistorialServicio")
class HistorialServicioTest {

    private final PrintStream standardOut = System.out;
    private HistorialServicio historialServicio;
    private ByteArrayOutputStream capturedOutput;

    @BeforeEach
    void setUp() {
        historialServicio = new HistorialServicio();
        capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput));
    }

    @AfterEach
    void tearDown() {
        System.setOut(standardOut);
    }

    /**
     * Extrae el historial interno de manera segura para verificación.
     * Utiliza reflexión para acceder al campo privado.
     */
    private PilaLD<Accion> extraerHistorial() {
        try {
            var field = HistorialServicio.class.getDeclaredField("historial");
            field.setAccessible(true);
            return (PilaLD<Accion>) field.get(historialServicio);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("No se pudo acceder al historial: " + e.getMessage());
            return null;
        }
    }

    /**
     * Cuenta el número de acciones en la pila sin modificarla.
     */
    private int contarAccionesEnPila() {
        try {
            PilaLD<Accion> pila = extraerHistorial();
            int contador = 0;
            PilaLD<Accion> temp = new PilaLD<>();
            temp.InicializarPila();

            while (!pila.PilaVacia()) {
                contador++;
                temp.Apilar(pila.Tope());
                pila.Desapilar();
            }

            // Restaurar el estado original
            while (!temp.PilaVacia()) {
                pila.Apilar(temp.Tope());
                temp.Desapilar();
            }

            return contador;
        } catch (Exception e) {
            fail("Error al contar acciones: " + e.getMessage());
            return -1;
        }
    }

    @Nested
    @DisplayName("Pruebas de registrarAccion")
    class RegistrarAccionTests {

        @Test
        @DisplayName("Registrar una acción simple")
        void testRegistrarUnaSola() {
            historialServicio.registrarAccion("AgregarAmigo", "Se agregó a Juan");
            System.setOut(standardOut);

            assertDoesNotThrow(() -> {
                PilaLD<Accion> tempPila = extraerHistorial();
                assertFalse(tempPila.PilaVacia(), "La pila debe contener la acción registrada");
            });
        }

        @Test
        @DisplayName("Registrar múltiples acciones")
        void testRegistrarMultiples() {
            historialServicio.registrarAccion("AgregarAmigo", "Se agregó a Juan");
            historialServicio.registrarAccion("PublicarEstado", "Nuevo estado publicado");
            historialServicio.registrarAccion("EnviarMensaje", "Mensaje enviado a Pedro");

            System.setOut(standardOut);
            assertEquals(3, contarAccionesEnPila(), "Debe haber 3 acciones en el historial");
        }

        @Test
        @DisplayName("Registrar acciones con diferentes tipos y descripciones")
        void testRegistrarAccionesVariadas() {
            String[] tipos = {"AgregarAmigo", "PublicarEstado", "EnviarMensaje", "EliminarContacto"};
            String[] descripciones = {"Amigo agregado", "Publicación hecha", "Mensaje enviado", "Contacto eliminado"};

            for (int i = 0; i < tipos.length; i++) {
                historialServicio.registrarAccion(tipos[i], descripciones[i]);
            }

            System.setOut(standardOut);
            assertEquals(4, contarAccionesEnPila(), "Debe haber 4 acciones registradas");
        }

        @Test
        @DisplayName("Registrar acción con valores null")
        void testRegistrarAccionConValoresNull() {
            assertDoesNotThrow(() -> {
                historialServicio.registrarAccion(null, "descripción");
                historialServicio.registrarAccion("tipo", null);
            });
        }

        @Test
        @DisplayName("Registrar acción con strings vacíos")
        void testRegistrarAccionConStringsVacios() {
            historialServicio.registrarAccion("", "");
            System.setOut(standardOut);

            assertEquals(1, contarAccionesEnPila(), "Debe registrarse aunque sean strings vacíos");
        }

        @Test
        @DisplayName("Timestamp se registra correctamente")
        void testTimestampRegistrado() {
            LocalDateTime antesDeRegistro = LocalDateTime.now();
            historialServicio.registrarAccion("Test", "Test descripción");
            LocalDateTime despuesDeRegistro = LocalDateTime.now();

            System.setOut(standardOut);
            PilaLD<Accion> tempPila = extraerHistorial();

            if (!tempPila.PilaVacia()) {
                Accion accion = tempPila.Tope();
                assertTrue(accion.timestamp().isAfter(antesDeRegistro.minusSeconds(1)),
                        "Timestamp debe estar después del antes");
                assertTrue(accion.timestamp().isBefore(despuesDeRegistro.plusSeconds(1)),
                        "Timestamp debe estar antes del después");
            }
        }
    }

    @Nested
    @DisplayName("Pruebas de deshacerUltimaAccion")
    class DeshacerUltimaAccionTests {

        @Test
        @DisplayName("Deshacer cuando el historial está vacío")
        void testDeshacerEnHistorialVacio() {
            historialServicio.deshacerUltimaAccion();

            String output = capturedOutput.toString();
            assertTrue(output.contains("No hay acciones para deshacer"),
                    "Debe mostrar mensaje cuando no hay acciones");
        }

        @Test
        @DisplayName("Deshacer una acción")
        void testDeshacerUnaAccion() {
            historialServicio.registrarAccion("AgregarAmigo", "Se agregó a Juan");
            historialServicio.deshacerUltimaAccion();

            String output = capturedOutput.toString();
            assertTrue(output.contains("Deshaciendo acción"),
                    "Debe mostrar mensaje de deshacimiento");
            assertEquals(0, contarAccionesEnPila(), "La pila debe estar vacía después de deshacer");
        }

        @Test
        @DisplayName("Deshacer múltiples acciones en orden LIFO")
        void testDeshacerMultiplesAcciones() {
            historialServicio.registrarAccion("Acción 1", "Primera");
            historialServicio.registrarAccion("Acción 2", "Segunda");
            historialServicio.registrarAccion("Acción 3", "Tercera");

            // Deshacer 3 veces
            historialServicio.deshacerUltimaAccion();
            historialServicio.deshacerUltimaAccion();
            historialServicio.deshacerUltimaAccion();

            System.setOut(standardOut);
            assertEquals(0, contarAccionesEnPila(), "La pila debe estar vacía después de deshacer todas");
        }

        @Test
        @DisplayName("Deshacer actúa sobre la última acción (LIFO)")
        void testDeshacerUltimaEnOrdenLIFO() {
            historialServicio.registrarAccion("Primera", "Desc1");
            historialServicio.registrarAccion("Segunda", "Desc2");
            historialServicio.registrarAccion("Tercera", "Desc3");

            // Capturar el mensaje de deshacimiento
            historialServicio.deshacerUltimaAccion();

            String output = capturedOutput.toString();
            assertTrue(output.contains("Tercera"),
                    "Debe deshacer la última acción (Tercera)");
        }

        @Test
        @DisplayName("Deshacer más acciones de las que existen")
        void testDeshacerMasAccionesDelimite() {
            historialServicio.registrarAccion("Única", "Única acción");

            historialServicio.deshacerUltimaAccion();
            historialServicio.deshacerUltimaAccion(); // Segunda vez en vacío

            String output = capturedOutput.toString();
            assertTrue(output.contains("No hay acciones para deshacer"),
                    "Debe indicar que no hay acciones al intentar deshacer de nuevo");
        }

        @Test
        @DisplayName("Deshacimiento imprime información correcta")
        void testMensajeDeshacimiento() {
            historialServicio.registrarAccion("TestTipo", "TestDescripción");
            historialServicio.deshacerUltimaAccion();

            String output = capturedOutput.toString();
            assertTrue(output.contains("Deshaciendo acción") && output.contains("TestTipo"),
                    "Debe imprimir el tipo de acción deshecha");
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================

    @Nested
    @DisplayName("Pruebas de mostrarHistorial")
    class MostrarHistorialTests {

        @Test
        @DisplayName("Mostrar historial vacío")
        void testMostrarHistorialVacio() {
            historialServicio.mostrarHistorial();

            String output = capturedOutput.toString();
            assertTrue(output.contains("Historial de acciones"),
                    "Debe mostrar encabezado del historial");
        }

        @Test
        @DisplayName("Mostrar una acción en historial")
        void testMostrarUnaAccion() {
            historialServicio.registrarAccion("TestTipo", "TestDescripcion");
            historialServicio.mostrarHistorial();

            String output = capturedOutput.toString();
            assertTrue(output.contains("TestTipo") && output.contains("TestDescripcion"),
                    "Debe mostrar la acción registrada");
        }

        @Test
        @DisplayName("Mostrar múltiples acciones en historial")
        void testMostrarMultiplesAcciones() {
            historialServicio.registrarAccion("Accion 1", "Descripcion 1");
            historialServicio.registrarAccion("Accion 2", "Descripcion 2");
            historialServicio.registrarAccion("Accion 3", "Descripcion 3");

            historialServicio.mostrarHistorial();

            String output = capturedOutput.toString();
            assertTrue(output.contains("Accion 1") && output.contains("Accion 2") && output.contains("Accion 3"),
                    "Debe mostrar todas las acciones");
        }

        @Test
        @DisplayName("Mostrar historial no modifica el historial original")
        void testMostrarHistorialNoModifica() {
            historialServicio.registrarAccion("Primera", "Desc1");
            historialServicio.registrarAccion("Segunda", "Desc2");
            historialServicio.registrarAccion("Tercera", "Desc3");

            int accionesAntes = contarAccionesEnPila();
            historialServicio.mostrarHistorial();
            int accionesDespues = contarAccionesEnPila();

            System.setOut(standardOut);
            assertEquals(accionesAntes, accionesDespues,
                    "El historial debe tener el mismo número de acciones después de mostrar");
        }

        @Test
        @DisplayName("Mostrar historial preserva el orden LIFO")
        void testMostrarHistorialPreservaOrdenLIFO() {
            historialServicio.registrarAccion("Primero", "Desc1");
            historialServicio.registrarAccion("Segundo", "Desc2");
            historialServicio.registrarAccion("Tercero", "Desc3");

            historialServicio.mostrarHistorial();

            String output = capturedOutput.toString();
            int posSegundo = output.indexOf("Segundo");
            int posPrimero = output.indexOf("Primero");

            assertTrue(posSegundo < posPrimero,
                    "Debe mostrar en orden inverso (LIFO): último primero");
        }

        @Test
        @DisplayName("Mostrar historial múltiples veces")
        void testMostrarHistorialMultipleVeces() {
            historialServicio.registrarAccion("Test", "Descripción");

            historialServicio.mostrarHistorial();
            int primeraLlamada = contarAccionesEnPila();

            historialServicio.mostrarHistorial();
            int segundaLlamada = contarAccionesEnPila();

            System.setOut(standardOut);
            assertEquals(primeraLlamada, segundaLlamada,
                    "El historial debe ser idéntico en múltiples llamadas");
            assertEquals(1, primeraLlamada, "Debe mantener la acción después de dos mostrar");
        }

        @Test
        @DisplayName("Mostrar historial imprime encabezado")
        void testEncabezadoHistorial() {
            historialServicio.mostrarHistorial();

            String output = capturedOutput.toString();
            assertTrue(output.contains("Historial de acciones:"),
                    "Debe imprimir el encabezado del historial");
        }
    }

    @Nested
    @DisplayName("Pruebas de integración")
    class IntegracionTests {

        @Test
        @DisplayName("Flujo completo: registrar, mostrar, deshacer")
        void testFlujoCompleto() {
            // Registrar acciones
            historialServicio.registrarAccion("AgregarAmigo", "Juan agregado");
            historialServicio.registrarAccion("PublicarEstado", "Estado nuevo");

            // Mostrar historial
            historialServicio.mostrarHistorial();

            // Verificar que hay 2 acciones
            System.setOut(standardOut);
            assertEquals(2, contarAccionesEnPila(), "Debe haber 2 acciones después de registrar y mostrar");

            // Deshacer una
            System.setOut(new PrintStream(capturedOutput));
            historialServicio.deshacerUltimaAccion();
            System.setOut(standardOut);

            assertEquals(1, contarAccionesEnPila(), "Debe haber 1 acción después de deshacer");
        }

        @Test
        @DisplayName("Alternar entre registro y deshacimiento")
        void testAlternacionRegistroDeshacimiento() {
            historialServicio.registrarAccion("Acción 1", "Desc 1");
            assertEquals(1, contarAccionesEnPila());

            System.setOut(new PrintStream(capturedOutput));
            historialServicio.deshacerUltimaAccion();
            System.setOut(standardOut);
            assertEquals(0, contarAccionesEnPila());

            historialServicio.registrarAccion("Acción 2", "Desc 2");
            historialServicio.registrarAccion("Acción 3", "Desc 3");
            assertEquals(2, contarAccionesEnPila());
        }

        @Test
        @DisplayName("Registrar después de deshacer todo")
        void testRegistrarDespuesDeshacerTodo() {
            historialServicio.registrarAccion("Primera", "Desc");

            System.setOut(new PrintStream(capturedOutput));
            historialServicio.deshacerUltimaAccion();
            System.setOut(standardOut);

            assertEquals(0, contarAccionesEnPila());

            historialServicio.registrarAccion("Segunda", "Desc");
            assertEquals(1, contarAccionesEnPila());
        }

        @Test
        @DisplayName("Mostrar después de deshacer")
        void testMostrarDespuesDeshacer() {
            historialServicio.registrarAccion("A", "D1");
            historialServicio.registrarAccion("B", "D2");

            System.setOut(new PrintStream(capturedOutput));
            historialServicio.deshacerUltimaAccion();

            ByteArrayOutputStream outputAntes = capturedOutput;
            capturedOutput = new ByteArrayOutputStream();
            System.setOut(new PrintStream(capturedOutput));

            historialServicio.mostrarHistorial();
            System.setOut(standardOut);

            String output = capturedOutput.toString();
            assertTrue(output.contains("A") && !output.contains("B"),
                    "Debe mostrar solo la acción restante");
        }
    }
}