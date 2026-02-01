package implementaciones;

import interfaces.ConjuntoTDA;
import interfaces.DiccionarioSimpleTDA;

public class DiccionarioSimpleLD<K, V> implements DiccionarioSimpleTDA<K, V> {
    NodoClave<K, V> origen;

    public void InicializarDiccionario() {
        origen = null;
    }

    public void Agregar(K clave, V valor) {
        NodoClave<K, V> nc = Clave2NodoClave(clave);
        if (nc == null) {
            nc = new NodoClave<K, V>();
            nc.clave = clave;
            nc.sigClave = origen;
            origen = nc;
        }
        nc.valor = valor;
    }

    private NodoClave<K, V> Clave2NodoClave(K clave) {
        NodoClave<K, V> aux = origen;
        while (aux != null && aux.clave != clave) {
            aux = aux.sigClave;
        }
        return aux;
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

    public V Recuperar(K clave) {
        NodoClave<K, V> n = Clave2NodoClave(clave);
        return n.valor;
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
        V valor;
        NodoClave<K, V> sigClave;
    }
}