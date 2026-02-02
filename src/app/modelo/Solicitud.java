package app.modelo;

public record Solicitud(String seguidor, String seguido) {

    @Override
    public String toString() {
        return seguidor + " -> " + seguido;
    }
}