package app.implementaciones;

import app.interfaces.ABBTDA;

public class ABB implements ABBTDA {

    NodoABB raiz;

    public int Raiz() {
        return raiz.info;
    }

    public boolean ArbolVacio() {
        return (raiz == null);
    }

    public void InicializarArbol() {
        raiz = null;
    }

    public ABBTDA HijoDer() {
        return raiz.hijoDer;
    }

    public ABBTDA HijoIzq() {
        return raiz.hijoIzq;
    }

    public void AgregarElem(int x) {
        if (raiz == null) {
            raiz = new NodoABB();
            raiz.info = x;
            raiz.hijoIzq = new ABB();
            raiz.hijoIzq.InicializarArbol();
            raiz.hijoDer = new ABB();
            raiz.hijoDer.InicializarArbol();
        } else if (raiz.info > x)
            raiz.hijoIzq.AgregarElem(x);
        else if (raiz.info < x)
            raiz.hijoDer.AgregarElem(x);
    }

    public void EliminarElem(int x) {
        if (raiz != null) {
            if (raiz.info == x && raiz.hijoIzq.ArbolVacio() &&
                    raiz.hijoDer.ArbolVacio()) {
                raiz = null;
            } else if (raiz.info == x && !raiz.hijoIzq.ArbolVacio()
            ) {
                raiz.info = this.mayor(raiz.hijoIzq);
                raiz.hijoIzq.EliminarElem(raiz.info);
            } else if (raiz.info == x && raiz.hijoIzq.ArbolVacio()) {
                raiz.info = this.menor(raiz.hijoDer);
                raiz.hijoDer.EliminarElem(raiz.info);
            } else if (raiz.info < x) {
                raiz.hijoDer.EliminarElem(x);
            } else {
                raiz.hijoIzq.EliminarElem(x);
            }
        }
    }

    private int mayor(ABBTDA a) {
        if (a.HijoDer().ArbolVacio()) {
            return a.Raiz();
        } else {
            return mayor(a.HijoDer());
        }
    }

    private int menor(ABBTDA a) {
        if (a.HijoIzq().ArbolVacio()) {
            return a.Raiz();
        } else {
            return menor(a.HijoIzq());
        }
    }

    public void recorridoPreOrder(ABBTDA a) {
        if (!a.ArbolVacio()) {
            System.out.println(a.Raiz());
            recorridoPreOrder(a.HijoIzq());
            recorridoPreOrder(a.HijoDer());
        }
    }

    public void recorridoInOrder(ABBTDA a) {
        if (!a.ArbolVacio()) {
            recorridoInOrder(a.HijoIzq());
            System.out.println(a.Raiz());
            recorridoInOrder(a.HijoDer());
        }
    }

    public void recorridoPostOrder(ABBTDA a) {
        if (!a.ArbolVacio()) {
            recorridoPostOrder(a.HijoIzq());
            recorridoPostOrder(a.HijoDer());
            System.out.println(a.Raiz());
        }
    }
}
