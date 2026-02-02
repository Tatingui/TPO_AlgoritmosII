package app.interfaces;

public interface ColaTDA<T> {
    void InicializarCola();

    void Acolar(T x);

    void Desacolar();

    boolean ColaVacia();

    T Primero();
}
