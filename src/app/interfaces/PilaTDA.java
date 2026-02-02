package app.interfaces;

public interface PilaTDA<T> {
    void InicializarPila();

    void Apilar(T x);

    void Desapilar();

    boolean PilaVacia();

    T Tope();
}
