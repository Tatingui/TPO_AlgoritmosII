package implementaciones;

import interfaces.ColaTDA;

public class ColaLD implements ColaTDA {
    // Primer elemento en la cola
    Nodo primero;
    //´U ltimo elemento en la cola , es decir , el ´ultimo agregado
    Nodo ultimo;

    public void InicializarCola() {
        primero = null;
        ultimo = null;
    }

    public void Acolar(int x) {
        Nodo aux = new Nodo();
        aux.info = x;
        aux.sig = null;
        //Si la cola no est´a vac´ıa
        if (ultimo != null)
            ultimo.sig = aux;
        ultimo = aux;
        // Si la cola estaba vac´ıa
        if (primero == null)
            primero = ultimo;
    }

    public void Desacolar() {
        primero = primero.sig;
        // Si la cola queda vac´ıa
        if (primero == null)
            ultimo = null;
    }

    public boolean ColaVacia() {
        return (ultimo == null);
    }

    public int Primero() {
        return primero.info;
    }
}
