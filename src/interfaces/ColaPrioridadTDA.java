package interfaces;

public interface ColaPrioridadTDA<T> {
    void InicializarCola();

    void AcolarPrioridad(T x, int prioridad);

    void Desacolar();

    T Primero();

    boolean ColaVacia();

    int Prioridad();
}
