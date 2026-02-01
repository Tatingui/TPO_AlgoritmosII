package implementaciones;

import interfaces.ConjuntoTDA;
import interfaces.DiccionarioMultipleTDA;

public class DiccionarioMultipleLD<K, V> implements DiccionarioMultipleTDA<K, V> {
    NodoClave<K, V> origen;

    public void InicializarDiccionario() {
        origen = null;
    }

    public void Agregar(K clave, V valor) {
        NodoClave<K, V> nc = Clave2NodoClave(clave);
        if (nc == null) {
            nc = new NodoClave<>();
            nc.clave = clave;
            nc.sigClave = origen;
            origen = nc;
        }
        NodoValor<V> aux = nc.valores;
        while (aux != null && aux.valor != valor) {
            aux = aux.sigValor;
        }
        if (aux == null) {
            NodoValor<V> nv = new NodoValor<V>();
            nv.valor = valor;
            nv.sigValor = nc.valores;
            nc.valores = nv;
        }
    }

    private NodoClave<K, V> Clave2NodoClave(K clave) {
        NodoClave<K, V> aux = origen;
        while (aux != null && aux.clave != clave) {
            aux = aux.sigClave;
        }
        return aux;
    }

    public void EliminarValor(K clave, V valor) {
        if (origen != null) {
            if (origen.clave == clave) {
                EliminarValorEnNodo(origen, valor);
                if (origen.valores == null) {
                    origen = origen.sigClave;
                }
            } else {
                NodoClave<K, V> aux = origen;
                while (aux.sigClave != null && aux.sigClave.clave
                        != clave) {
                    aux = aux.sigClave;
                }
                if (aux.sigClave != null) {
                    EliminarValorEnNodo(aux.sigClave, valor);
                    if (aux.sigClave.valores == null) {
                        aux.sigClave = aux.sigClave.sigClave;
                    }
                }
            }
        }
    }

    private void EliminarValorEnNodo(NodoClave<K, V> nodo, V valor) {
        if (nodo.valores != null) {
            if (nodo.valores.valor == valor) {
                nodo.valores = nodo.valores.sigValor;
            } else {
                NodoValor<V> aux = nodo.valores;
                while (aux.sigValor != null && aux.sigValor.valor
                        != valor) {
                    aux = aux.sigValor;
                }
                if (aux.sigValor != null) {
                    aux.sigValor = aux.sigValor.sigValor;
                }
            }
        }
    }

    public void Eliminar(K clave) {
        if (origen != null) {
            if (origen.clave == clave) {
                origen = origen.sigClave;
            } else {
                NodoClave<K, V> aux = origen;
                while (aux.sigClave != null && aux.sigClave.clave
                        != clave) {
                    aux = aux.sigClave;
                }
                if (aux.sigClave != null) {
                    aux.sigClave = aux.sigClave.sigClave;
                }
            }
        }
    }

    public ConjuntoTDA<V> Recuperar(K clave) {
        NodoClave<K, V> n = Clave2NodoClave(clave);
        ConjuntoTDA<V> c = new ConjuntoLD<V>();
        c.InicializarConjunto();
        if (n != null) {
            NodoValor<V> aux = n.valores;
            while (aux != null) {
                c.Agregar(aux.valor);
                aux = aux.sigValor;
            }
        }
        return c;
    }

    public ConjuntoTDA<K> Claves() {
        ConjuntoTDA<K> c = new ConjuntoLD<K>();
        c.InicializarConjunto();
        NodoClave<K, V> aux = origen;
        while (aux != null) {
            c.Agregar(aux.clave);
            aux = aux.sigClave;
        }
        return c;
    }

    static class NodoClave<K, V> {
        K clave;
        NodoValor<V> valores;
        NodoClave<K, V> sigClave;
    }

    static class NodoValor<V> {
        V valor;
        NodoValor<V> sigValor;
    }
}