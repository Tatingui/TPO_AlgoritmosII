package app.implementaciones;

import app.interfaces.ABBTDA;

public class ABB<T extends Comparable<T>> implements ABBTDA<T> {

    NodoABB<T> raiz;

    public T Raiz() {
        return raiz.info;
    }

    public boolean ArbolVacio() {
        return (raiz == null);
    }

    public void InicializarArbol() {
        raiz = null;
    }

    public ABBTDA<T> HijoDer() {
        return raiz.hijoDer;
    }

    public ABBTDA<T> HijoIzq() {
        return raiz.hijoIzq;
    }

    public void AgregarElem(T x) {
        if (raiz == null) {
            raiz = new NodoABB<>();
            raiz.info = x;
            raiz.hijoIzq = new ABB<>();
            raiz.hijoIzq.InicializarArbol();
            raiz.hijoDer = new ABB<>();
            raiz.hijoDer.InicializarArbol();
        } else if (raiz.info.compareTo(x) > 0)
            raiz.hijoIzq.AgregarElem(x);
        else if (raiz.info.compareTo(x) < 0)
            raiz.hijoDer.AgregarElem(x);
    }

    public void EliminarElem(T x) {
        if (raiz != null) {
            if (raiz.info.compareTo(x) == 0 && raiz.hijoIzq.ArbolVacio() &&
                    raiz.hijoDer.ArbolVacio()) {
                raiz = null;
            } else if (raiz.info.compareTo(x) == 0 && !raiz.hijoIzq.ArbolVacio()) {
                raiz.info = this.mayor(raiz.hijoIzq);
                raiz.hijoIzq.EliminarElem(raiz.info);
            } else if (raiz.info.compareTo(x) == 0 && raiz.hijoIzq.ArbolVacio()) {
                raiz.info = this.menor(raiz.hijoDer);
                raiz.hijoDer.EliminarElem(raiz.info);
            } else if (raiz.info.compareTo(x) < 0) {
                raiz.hijoDer.EliminarElem(x);
            } else {
                raiz.hijoIzq.EliminarElem(x);
            }
        }
    }

    private T mayor(ABBTDA<T> a) {
        if (a.HijoDer().ArbolVacio()) {
            return a.Raiz();
        } else {
            return mayor(a.HijoDer());
        }
    }

    private T menor(ABBTDA<T> a) {
        if (a.HijoIzq().ArbolVacio()) {
            return a.Raiz();
        } else {
            return menor(a.HijoIzq());
        }
    }

    public void recorridoPreOrder(ABBTDA<T> a) {
        if (!a.ArbolVacio()) {
            System.out.println(a.Raiz());
            recorridoPreOrder(a.HijoIzq());
            recorridoPreOrder(a.HijoDer());
        }
    }

    public void recorridoInOrder(ABBTDA<T> a) {
        if (!a.ArbolVacio()) {
            recorridoInOrder(a.HijoIzq());
            System.out.println(a.Raiz());
            recorridoInOrder(a.HijoDer());
        }
    }

    public void recorridoPostOrder(ABBTDA<T> a) {
        if (!a.ArbolVacio()) {
            recorridoPostOrder(a.HijoIzq());
            recorridoPostOrder(a.HijoDer());
            System.out.println(a.Raiz());
        }
    }
}
