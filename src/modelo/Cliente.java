/*
 * TAD de Dominio que representa a un usuario de la red social.
 * Se utiliza como el objeto principal que circulará por todo el sistema.
 */

package modelo;

public class Cliente {
    private String nombre;
    private int scoring;

    public Cliente(String nombre, int scoring) {
        this.nombre = nombre;
        this.scoring = scoring;
    }

    public String getNombre() { return nombre; }
    public int getScoring() { return scoring; }
    public void setScoring(int scoring) { this.scoring = scoring; }

    @Override
    public String toString() {
        return "Cliente{nombre='" + nombre + "', scoring=" + scoring + "}";
    }
}
