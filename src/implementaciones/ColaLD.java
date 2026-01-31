package implementaciones;

import interfaces.ColaTDA;

public class ColaLD<T> implements ColaTDA<T> {
    // Primer elemento en la cola
    Nodo<T> primero;
    // Último elemento en la cola, el último agregado
    Nodo<T> ultimo;

    public void InicializarCola() {
        primero = null;
        ultimo = null;
    }

    public void Acolar(T x) {
        Nodo<T> aux = new Nodo<T>();
        aux.info = x;
        aux.sig = null;
        // Si la cola no está vacía
        if (ultimo != null) {
            ultimo.sig = aux;
        }
        ultimo = aux;
        // Si la cola estaba vacía
        if (primero == null) {
            primero = ultimo;
        }
    }

    public void Desacolar() {
        primero = primero.sig;
        // Si la cola queda vacía
        if (primero == null) {
            ultimo = null;
        }
    }

    public boolean ColaVacia() {
        return (ultimo == null);
    }

    public T Primero() {
        return primero.info;
    }
}
