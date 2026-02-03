package app.servicio;

import app.interfaces.ColaTDA;
import app.modelo.Solicitud;
import org.junit.jupiter.api.*;
import org.mockito.InOrder;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Pruebas de SolicitudesServicio")
class SolicitudesServicioTest {

    private final PrintStream standardOut = System.out;
    private ByteArrayOutputStream capturedOutput;

    @BeforeEach
    void setUp() {
        capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput));
    }

    @AfterEach
    void tearDown() {
        System.setOut(standardOut);
    }

    @Nested
    @DisplayName("Pruebas de procesarSiguiente - Aceptar solicitud")
    class AceptarSolicitudTests {

        @Test
        @DisplayName("Aceptar una solicitud válida")
        void testAceptarSolicitudValida() {
            // Arrange
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);
            Solicitud solicitud = new Solicitud("Juan", "Maria");

            when(colaMock.ColaVacia()).thenReturn(false);
            when(colaMock.Primero()).thenReturn(solicitud);

            // Act
            SolicitudesServicio.procesarSiguiente(colaMock, true);

            // Assert
            verify(colaMock).ColaVacia();
            verify(colaMock).Primero();
            verify(colaMock).Desacolar();

            String output = capturedOutput.toString();
            assertTrue(output.contains("ACEPTADA"),
                    "Debe mostrar que la solicitud fue aceptada");
            assertTrue(output.contains("Juan") && output.contains("Maria"),
                    "Debe mostrar los nombres de seguidor y seguido");
        }

        @Test
        @DisplayName("Aceptar solicitud imprime mensaje correcto")
        void testMensajeAceptacion() {
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);
            Solicitud solicitud = new Solicitud("Carlos", "Ana");

            when(colaMock.ColaVacia()).thenReturn(false);
            when(colaMock.Primero()).thenReturn(solicitud);

            SolicitudesServicio.procesarSiguiente(colaMock, true);

            String output = capturedOutput.toString();
            assertTrue(output.contains("ACEPTADA: Carlos -> Ana"),
                    "Debe mostrar formato correcto de aceptación");
        }

        @Test
        @DisplayName("Aceptar múltiples solicitudes consecutivas")
        void testAceptarMultiplesSolicitudes() {
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);
            Solicitud solicitud1 = new Solicitud("Juan", "Maria");
            Solicitud solicitud2 = new Solicitud("Carlos", "Ana");

            when(colaMock.ColaVacia()).thenReturn(false);
            when(colaMock.Primero()).thenReturn(solicitud1).thenReturn(solicitud2);

            SolicitudesServicio.procesarSiguiente(colaMock, true);

            ByteArrayOutputStream firstOutput = capturedOutput;
            capturedOutput = new ByteArrayOutputStream();
            System.setOut(new PrintStream(capturedOutput));

            SolicitudesServicio.procesarSiguiente(colaMock, true);

            System.setOut(standardOut);
            String allOutput = firstOutput.toString() + capturedOutput.toString();

            assertTrue(allOutput.contains("ACEPTADA"),
                    "Ambas solicitudes deben ser aceptadas");
            verify(colaMock, times(2)).Desacolar();
        }

        @Test
        @DisplayName("Aceptar solicitud con nombres especiales")
        void testAceptarSolicitudNombresEspeciales() {
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);
            Solicitud solicitud = new Solicitud("José María", "Ángel");

            when(colaMock.ColaVacia()).thenReturn(false);
            when(colaMock.Primero()).thenReturn(solicitud);

            assertDoesNotThrow(() -> SolicitudesServicio.procesarSiguiente(colaMock, true));

            String output = capturedOutput.toString();
            assertTrue(output.contains("ACEPTADA"),
                    "Debe manejar nombres especiales");
        }

        @Test
        @DisplayName("Aceptar verifica desapilamiento")
        void testAceptarVerificaDesapilamiento() {
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);
            Solicitud solicitud = new Solicitud("A", "B");

            when(colaMock.ColaVacia()).thenReturn(false);
            when(colaMock.Primero()).thenReturn(solicitud);

            SolicitudesServicio.procesarSiguiente(colaMock, true);

            verify(colaMock).Desacolar();
            verify(colaMock, times(1)).Desacolar();
        }
    }

    @Nested
    @DisplayName("Pruebas de procesarSiguiente - Rechazar solicitud")
    class RechazarSolicitudTests {

        @Test
        @DisplayName("Rechazar una solicitud válida")
        void testRechazarSolicitudValida() {
            // Arrange
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);
            Solicitud solicitud = new Solicitud("Pedro", "Luis");

            when(colaMock.ColaVacia()).thenReturn(false);
            when(colaMock.Primero()).thenReturn(solicitud);

            // Act
            SolicitudesServicio.procesarSiguiente(colaMock, false);

            // Assert
            verify(colaMock).ColaVacia();
            verify(colaMock).Primero();
            verify(colaMock).Desacolar();

            String output = capturedOutput.toString();
            assertTrue(output.contains("RECHAZADA"),
                    "Debe mostrar que la solicitud fue rechazada");
            assertTrue(output.contains("Pedro") && output.contains("Luis"),
                    "Debe mostrar los nombres");
        }

        @Test
        @DisplayName("Rechazar solicitud imprime mensaje correcto")
        void testMensajeRechazo() {
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);
            Solicitud solicitud = new Solicitud("Eva", "David");

            when(colaMock.ColaVacia()).thenReturn(false);
            when(colaMock.Primero()).thenReturn(solicitud);

            SolicitudesServicio.procesarSiguiente(colaMock, false);

            String output = capturedOutput.toString();
            assertTrue(output.contains("RECHAZADA: Eva -> David"),
                    "Debe mostrar formato correcto de rechazo");
        }

        @Test
        @DisplayName("Rechazar múltiples solicitudes")
        void testRechazarMultiplesSolicitudes() {
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);
            Solicitud solicitud1 = new Solicitud("A", "B");
            Solicitud solicitud2 = new Solicitud("C", "D");

            when(colaMock.ColaVacia()).thenReturn(false);
            when(colaMock.Primero()).thenReturn(solicitud1).thenReturn(solicitud2);

            SolicitudesServicio.procesarSiguiente(colaMock, false);

            ByteArrayOutputStream firstOutput = capturedOutput;
            capturedOutput = new ByteArrayOutputStream();
            System.setOut(new PrintStream(capturedOutput));

            SolicitudesServicio.procesarSiguiente(colaMock, false);

            System.setOut(standardOut);
            String allOutput = firstOutput.toString() + capturedOutput.toString();

            assertTrue(allOutput.contains("RECHAZADA"),
                    "Ambas solicitudes deben ser rechazadas");
        }

        @Test
        @DisplayName("Rechazar solicitud llama Desacolar")
        void testRechazarVerificaDesapilamiento() {
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);
            Solicitud solicitud = new Solicitud("X", "Y");

            when(colaMock.ColaVacia()).thenReturn(false);
            when(colaMock.Primero()).thenReturn(solicitud);

            SolicitudesServicio.procesarSiguiente(colaMock, false);

            verify(colaMock).Desacolar();
        }
    }

    @Nested
    @DisplayName("Pruebas de procesarSiguiente - Cola vacía")
    class ColaVaciaTests {

        @Test
        @DisplayName("Procesar cuando cola está vacía lanza excepción")
        void testProcesarColaVacia() {
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);
            when(colaMock.ColaVacia()).thenReturn(true);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> SolicitudesServicio.procesarSiguiente(colaMock, true), "Debe lanzar RuntimeException cuando la cola está vacía");

            assertTrue(exception.getMessage().contains("No hay solicitudes pendientes"),
                    "El mensaje debe indicar que no hay solicitudes");
        }

        @Test
        @DisplayName("Excepción al procesar cola vacía aceptando")
        void testExcepcionColaVaciaAceptar() {
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);
            when(colaMock.ColaVacia()).thenReturn(true);

            assertThrows(RuntimeException.class, () -> SolicitudesServicio.procesarSiguiente(colaMock, true));

            verify(colaMock, never()).Primero();
            verify(colaMock, never()).Desacolar();
        }

        @Test
        @DisplayName("Excepción al procesar cola vacía rechazando")
        void testExcepcionColaVaciaRechazar() {
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);
            when(colaMock.ColaVacia()).thenReturn(true);

            assertThrows(RuntimeException.class, () -> SolicitudesServicio.procesarSiguiente(colaMock, false));

            verify(colaMock, never()).Primero();
            verify(colaMock, never()).Desacolar();
        }

        @Test
        @DisplayName("Mensaje de excepción específico")
        void testMensajeExcepcionEspecifico() {
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);
            when(colaMock.ColaVacia()).thenReturn(true);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> SolicitudesServicio.procesarSiguiente(colaMock, true));

            assertEquals("No hay solicitudes pendientes.", exception.getMessage(),
                    "El mensaje debe ser exacto");
        }
    }

    @Nested
    @DisplayName("Pruebas de comportamiento FIFO")
    class ComportamientoFIFOTests {

        @Test
        @DisplayName("Procesar en orden FIFO (aceptar primera, rechazar segunda)")
        void testOrdenFIFO() {
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);
            Solicitud primera = new Solicitud("Primer", "Seguido");
            Solicitud segunda = new Solicitud("Segundo", "Seguido");

            when(colaMock.ColaVacia()).thenReturn(false);
            when(colaMock.Primero()).thenReturn(primera).thenReturn(segunda);

            // Procesar primera aceptando
            SolicitudesServicio.procesarSiguiente(colaMock, true);
            String primerOutput = capturedOutput.toString();

            // Procesar segunda rechazando
            capturedOutput = new ByteArrayOutputStream();
            System.setOut(new PrintStream(capturedOutput));
            SolicitudesServicio.procesarSiguiente(colaMock, false);
            String segundoOutput = capturedOutput.toString();

            System.setOut(standardOut);
            assertTrue(primerOutput.contains("ACEPTADA: Primer -> Seguido"));
            assertTrue(segundoOutput.contains("RECHAZADA: Segundo -> Seguido"));
        }

        @Test
        @DisplayName("Verificar que siempre procesa Primero()")
        void testSiempreProcesaPrimero() {
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);
            Solicitud solicitud = new Solicitud("Test", "Test");

            when(colaMock.ColaVacia()).thenReturn(false);
            when(colaMock.Primero()).thenReturn(solicitud);

            SolicitudesServicio.procesarSiguiente(colaMock, true);

            verify(colaMock).Primero();
        }

        @Test
        @DisplayName("Orden de operaciones: verificar vacío, obtener primero, desacolar")
        void testOrdenOperaciones() {
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);
            Solicitud solicitud = new Solicitud("A", "B");

            when(colaMock.ColaVacia()).thenReturn(false);
            when(colaMock.Primero()).thenReturn(solicitud);

            SolicitudesServicio.procesarSiguiente(colaMock, true);

            // Verificar orden de llamadas
            InOrder inOrder = inOrder(colaMock);
            inOrder.verify(colaMock).ColaVacia();
            inOrder.verify(colaMock).Primero();
            inOrder.verify(colaMock).Desacolar();
        }
    }

    @Nested
    @DisplayName("Pruebas de edge cases")
    class EdgeCasesTests {

        @Test
        @DisplayName("Procesar solicitud con nombres vacíos")
        void testSolicitudNombresVacios() {
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);
            Solicitud solicitud = new Solicitud("", "");

            when(colaMock.ColaVacia()).thenReturn(false);
            when(colaMock.Primero()).thenReturn(solicitud);

            assertDoesNotThrow(() -> SolicitudesServicio.procesarSiguiente(colaMock, true));

            String output = capturedOutput.toString();
            assertTrue(output.contains("ACEPTADA"),
                    "Debe procesar solicitud incluso con nombres vacíos");
        }

        @Test
        @DisplayName("Procesar solicitud con nombres null")
        void testSolicitudNombresNull() {
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);
            Solicitud solicitud = new Solicitud(null, null);

            when(colaMock.ColaVacia()).thenReturn(false);
            when(colaMock.Primero()).thenReturn(solicitud);

            assertDoesNotThrow(() -> SolicitudesServicio.procesarSiguiente(colaMock, true));

            verify(colaMock).Desacolar();
        }

        @Test
        @DisplayName("Procesar solicitud con mismo seguidor y seguido")
        void testSolicitudMismoSeguidor() {
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);
            Solicitud solicitud = new Solicitud("Juan", "Juan");

            when(colaMock.ColaVacia()).thenReturn(false);
            when(colaMock.Primero()).thenReturn(solicitud);

            assertDoesNotThrow(() -> SolicitudesServicio.procesarSiguiente(colaMock, true));

            String output = capturedOutput.toString();
            assertTrue(output.contains("ACEPTADA: Juan -> Juan"),
                    "Debe procesarse incluso si son la misma persona");
        }

        @Test
        @DisplayName("Procesar solicitud con nombres muy largos")
        void testSolicitudNombresLargos() {
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);
            String nombreLargo = "a".repeat(1000);
            Solicitud solicitud = new Solicitud(nombreLargo, "b");

            when(colaMock.ColaVacia()).thenReturn(false);
            when(colaMock.Primero()).thenReturn(solicitud);

            assertDoesNotThrow(() -> SolicitudesServicio.procesarSiguiente(colaMock, true));

            verify(colaMock).Desacolar();
        }

        @Test
        @DisplayName("Procesar solicitud null (si es posible)")
        void testSolicitudNull() {
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);

            when(colaMock.ColaVacia()).thenReturn(false);
            when(colaMock.Primero()).thenReturn(null);

            // Debería lanzar NPE o manejarse gracefully
            try {
                SolicitudesServicio.procesarSiguiente(colaMock, true);
            } catch (NullPointerException e) {
                // Comportamiento aceptable
                verify(colaMock).Desacolar();
            }
        }
    }

    @Nested
    @DisplayName("Pruebas de integración")
    class IntegracionTests {

        @Test
        @DisplayName("Procesar secuencia: Aceptar, Rechazar, Aceptar")
        void testSecuenciaMixta() {
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);
            Solicitud s1 = new Solicitud("A", "B");
            Solicitud s2 = new Solicitud("C", "D");
            Solicitud s3 = new Solicitud("E", "F");

            when(colaMock.ColaVacia()).thenReturn(false);
            when(colaMock.Primero()).thenReturn(s1).thenReturn(s2).thenReturn(s3);

            // Aceptar primera
            SolicitudesServicio.procesarSiguiente(colaMock, true);
            String output1 = capturedOutput.toString();

            // Rechazar segunda
            capturedOutput = new ByteArrayOutputStream();
            System.setOut(new PrintStream(capturedOutput));
            SolicitudesServicio.procesarSiguiente(colaMock, false);
            String output2 = capturedOutput.toString();

            // Aceptar tercera
            capturedOutput = new ByteArrayOutputStream();
            System.setOut(new PrintStream(capturedOutput));
            SolicitudesServicio.procesarSiguiente(colaMock, true);
            String output3 = capturedOutput.toString();

            System.setOut(standardOut);
            assertTrue(output1.contains("ACEPTADA: A -> B"));
            assertTrue(output2.contains("RECHAZADA: C -> D"));
            assertTrue(output3.contains("ACEPTADA: E -> F"));
        }

        @Test
        @DisplayName("Múltiples procesamientos verifican desapilamiento")
        void testMultiplesDesapilamiento() {
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);
            Solicitud s1 = new Solicitud("A", "B");
            Solicitud s2 = new Solicitud("C", "D");

            when(colaMock.ColaVacia()).thenReturn(false);
            when(colaMock.Primero()).thenReturn(s1).thenReturn(s2);

            SolicitudesServicio.procesarSiguiente(colaMock, true);
            System.setOut(new PrintStream(new ByteArrayOutputStream()));
            SolicitudesServicio.procesarSiguiente(colaMock, false);

            System.setOut(standardOut);
            verify(colaMock, times(2)).Desacolar();
        }

        @Test
        @DisplayName("Procesar y luego intentar en cola vacía")
        void testProcesarLuegoVacio() {
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);
            Solicitud solicitud = new Solicitud("A", "B");

            when(colaMock.ColaVacia()).thenReturn(false).thenReturn(true);
            when(colaMock.Primero()).thenReturn(solicitud);

            // Primera llamada ok
            SolicitudesServicio.procesarSiguiente(colaMock, true);

            // Segunda llamada debe fallar
            assertThrows(RuntimeException.class, () -> SolicitudesServicio.procesarSiguiente(colaMock, true));
        }
    }

    @Nested
    @DisplayName("Pruebas de mock behavior")
    class MockBehaviorTests {

        @Test
        @DisplayName("Verificar que no se modifica la solicitud")
        void testNoModificaSolicitud() {
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);
            Solicitud original = new Solicitud("Juan", "Maria");

            when(colaMock.ColaVacia()).thenReturn(false);
            when(colaMock.Primero()).thenReturn(original);

            SolicitudesServicio.procesarSiguiente(colaMock, true);

            // La solicitud debe ser la misma
            assertEquals("Juan", original.seguidor());
            assertEquals("Maria", original.seguido());
        }

        @Test
        @DisplayName("Verificar que se llama a ColaVacia antes de Primero")
        void testVerificaVaciaAntesDePrimero() {
            ColaTDA<Solicitud> colaMock = mock(ColaTDA.class);
            Solicitud solicitud = new Solicitud("A", "B");

            when(colaMock.ColaVacia()).thenReturn(false);
            when(colaMock.Primero()).thenReturn(solicitud);

            SolicitudesServicio.procesarSiguiente(colaMock, true);

            InOrder inOrder = inOrder(colaMock);
            inOrder.verify(colaMock).ColaVacia();
            inOrder.verify(colaMock).Primero();
        }
    }
}