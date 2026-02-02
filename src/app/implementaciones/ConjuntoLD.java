package app.implementaciones;

import app.interfaces.ConjuntoTDA;

public class ConjuntoLD<T> implements ConjuntoTDA<T> {
    Nodo<T> c;

    public void InicializarConjunto() {
        c = null;
    }

    public boolean ConjuntoVacio() {
        return (c == null);
    }

    public void Agregar(T x) {
        // Verifica que x no este en el conjunto
        if (!this.Pertenece(x)) {
            Nodo<T> aux = new Nodo<T>();
            aux.info = x;
            aux.sig = c;
            c = aux;
        }
    }

    public T Elegir() {
        return c.info;
    }

    public void Sacar(T x) {
        if (c != null) {
            // Si es el primer elemento de la lista
            if (c.info == x) {
                c = c.sig;
            } else {
                Nodo<T> aux = c;
                while (aux.sig != null && aux.sig.info != x)
                    aux = aux.sig;
                if (aux.sig != null)
                    aux.sig = aux.sig.sig;
            }
        }
    }

    public boolean Pertenece(T x) {
        Nodo<T> aux = c;
        while ((aux != null) && (aux.info != x)) {
            aux = aux.sig;
        }
        return (aux != null);
    }
}

