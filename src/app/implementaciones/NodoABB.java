package app.implementaciones;

public class NodoABB<T extends Comparable<T>> {
    T info;
    ABB<T> hijoIzq;
    ABB<T> hijoDer;
}
