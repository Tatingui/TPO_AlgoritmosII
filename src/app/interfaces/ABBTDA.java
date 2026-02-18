package app.interfaces;

public interface ABBTDA {

    // siempre que el árbol esté inicializado y no esté vacío
    int Raiz();

    // siempre que el árbol esté inicializado y no esté vacío
    ABBTDA HijoIzq();

    // siempre que el árbol esté inicializado y no esté vacío
    ABBTDA HijoDer();

    // siempre que el árbol esté inicializado
    boolean ArbolVacio();

    void InicializarArbol();

    // siempre que el árbol esté inicializado
    void AgregarElem(int x);

    // siempre que el árbol esté inicializado
    void EliminarElem(int x);
}
