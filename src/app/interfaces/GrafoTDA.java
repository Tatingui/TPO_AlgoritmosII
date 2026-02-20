package app.interfaces;

public interface GrafoTDA<T> {
    void InicializarGrafo();

    // siempre que el grafo esté inicializado y no exista el nodo
    void AgregarVertice(T v);

    // siempre que el grafo esté inicializado y exista el nodo
    void EliminarVertice(T v);

    // siempre que el grafo esté inicializado
    ConjuntoTDA<T> Vertices();

    // siempre que el grafo esté inicializado ,no exista la arista ,pero existan ambos nodos
    void AgregarArista(T v1, T v2, int peso);

    // siempre que el grafo esté inicializado y exista la arista
    void EliminarArista(T v1, T v2);

    // siempre que el grafo esté inicializado y existan los nodos
    boolean ExisteArista(T v1, T v2);

    // siempre que el grafo esté inicializado y exista la arista
    int PesoArista(T v1, T v2);
}
