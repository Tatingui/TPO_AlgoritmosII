package app.modelo;

public class Solicitud {
    public final String seguidor;
    public final String seguido;

    public Solicitud(String seguidor, String seguido) {
        this.seguidor = seguidor;
        this.seguido = seguido;
    }

    @Override
    public String toString() {
        return seguidor + " -> " + seguido;
    }
}