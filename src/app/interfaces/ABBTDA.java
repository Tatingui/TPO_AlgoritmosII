package app.interfaces;

public interface ABBTDA<T extends Comparable<T>> {

    // siempre que el árbol esté inicializado y no esté vacío
    T Raiz();

    // siempre que el árbol esté inicializado y no esté vacío
    ABBTDA<T> HijoIzq();

    // siempre que el árbol esté inicializado y no esté vacío
    ABBTDA<T> HijoDer();

    // siempre que el árbol esté inicializado
    boolean ArbolVacio();

    void InicializarArbol();

    // siempre que el árbol esté inicializado
    void AgregarElem(T x);

    // siempre que el árbol esté inicializado
    void EliminarElem(T x);
}
