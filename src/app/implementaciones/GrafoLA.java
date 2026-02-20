package app.implementaciones;

import app.interfaces.ConjuntoTDA;
import app.interfaces.GrafoTDA;

public class GrafoLA<T> implements GrafoTDA<T> {
    NodoGrafo<T> origen;

    public void InicializarGrafo() {
        origen = null;
    }

    public void AgregarVertice(T v) {
//El vertice se inserta al inicio de la lista de nodos
        NodoGrafo<T> aux = new NodoGrafo<>();
        aux.nodo = v;
        aux.arista = null;
        aux.sigNodo = origen;
        origen = aux;
    }

    /*
     * Para agregar una nueva arista al grafo , primero se deben
     * buscar los nodos entre los cuales se va agregar la arista ,
     * y luego se inserta sobre la lista de adyacentes del nodo
     * origen (en este caso nombrado como v1)
     */
    public void AgregarArista(T v1, T v2, int peso) {
        NodoGrafo<T> n1 = Vert2Nodo(v1);
        NodoGrafo<T> n2 = Vert2Nodo(v2);
//La nueva arista se inserta al inicio de la lista
//de nodos adyacentes del nodo origen
        NodoArista<T> aux = new NodoArista<>();
        aux.etiqueta = peso;
        aux.nodoDestino = n2;
        aux.sigArista = n1.arista;
        n1.arista = aux;
    }

    private NodoGrafo<T> Vert2Nodo(T v) {
        NodoGrafo<T> aux = origen;
        while (aux != null && !aux.nodo.equals(v)) {
            aux = aux.sigNodo;
        }
        return aux;
    }

    public void EliminarVertice(T v) {
//Se recorre la lista de v´ertices para remover el nodo v
//y las aristas con este v´ertice.
// Distingue el caso que sea el primer nodo
        if (origen != null && origen.nodo.equals(v)) {
            origen = origen.sigNodo;
        }
        NodoGrafo<T> aux = origen;
        while (aux != null) {
// remueve de aux todas las aristas hacia v
            this.EliminarAristaNodo(aux, v);
            if (aux.sigNodo != null && aux.sigNodo.nodo.equals(v)) {
//Si el siguiente nodo de aux es v, lo elimina
                aux.sigNodo = aux.sigNodo.sigNodo;
            }
            aux = aux.sigNodo;
        }
    }

    /*
     * Si en las aristas del nodo existe
     * una arista hacia v, la elimina
     */
    private void EliminarAristaNodo(NodoGrafo<T> nodo, T v) {
        NodoArista<T> aux = nodo.arista;
        if (aux != null) {
//Si la arista a eliminar es la primera en
//la lista de nodos adyacentes
            if (aux.nodoDestino.nodo.equals(v)) {
                nodo.arista = aux.sigArista;
            } else {
                while (aux.sigArista != null && !aux.sigArista.nodoDestino.nodo.equals(v)) {
                    aux = aux.sigArista;
                }
                if (aux.sigArista != null) {
// Quita la referencia a la arista hacia v
                    aux.sigArista = aux.sigArista.sigArista;
                }
            }
        }
    }

    public ConjuntoTDA<T> Vertices() {
        ConjuntoTDA<T> c = new ConjuntoLD<>();
        c.InicializarConjunto();
        NodoGrafo<T> aux = origen;
        while (aux != null) {
            c.Agregar(aux.nodo);
            aux = aux.sigNodo;
        }
        return c;
    }

    /*
     * Se elimina la arista que tiene como origen al v´ertice v1
     * y destino al v´ertice v2
     */
    public void EliminarArista(T v1, T v2) {
        NodoGrafo<T> n1 = Vert2Nodo(v1);
        if (n1 != null) {
            EliminarAristaNodo(n1, v2);
        }
    }

    public boolean ExisteArista(T v1, T v2) {
        NodoGrafo<T> n1 = Vert2Nodo(v1);
        if (n1 == null) {
            return false;
        }
        NodoArista<T> aux = n1.arista;
        while (aux != null && !aux.nodoDestino.nodo.equals(v2)) {
            aux = aux.sigArista;
        }
//Solo si se encontro la arista buscada , aux no es null
        return aux != null;
    }

    public int PesoArista(T v1, T v2) {
        NodoGrafo<T> n1 = Vert2Nodo(v1);
        NodoArista<T> aux = n1.arista;
        while (!aux.nodoDestino.nodo.equals(v2)) {
            aux = aux.sigArista;
        }
//Se encontr´o la arista entre los dos nodos
        return aux.etiqueta;
    }
}
