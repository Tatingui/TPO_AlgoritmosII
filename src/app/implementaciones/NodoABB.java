package app.implementaciones;

import app.interfaces.ABBTDA;

public class NodoABB<T extends Comparable<T>> {
    T info;
    ABBTDA<T> hijoIzq;
    ABBTDA<T> hijoDer;
}
