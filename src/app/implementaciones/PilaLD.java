package app.implementaciones;

import app.interfaces.PilaTDA;

public class PilaLD<T> implements PilaTDA<T> {
    Nodo<T> primero;

    public void InicializarPila() {
        primero = null;
    }

    public void Apilar(T x) {
        Nodo<T> aux = new Nodo<>();
        aux.info = x;
        aux.sig = primero;
        primero = aux;
    }

    public void Desapilar() {
        primero = primero.sig;
    }

    public boolean PilaVacia() {
        return (primero == null);
    }

    public T Tope() {
        return primero.info;
    }
}
