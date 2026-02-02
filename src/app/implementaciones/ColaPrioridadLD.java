package app.implementaciones;

import app.interfaces.ColaPrioridadTDA;

public class ColaPrioridadLD<T> implements ColaPrioridadTDA<T> {
    NodoPrioridad<T> mayorPrioridad;

    public void InicializarCola() {
        mayorPrioridad = null;
    }

    public void AcolarPrioridad(T x, int prioridad) {
        // Creo el nuevo nodo que voy a acolar
        NodoPrioridad<T> nuevo = new NodoPrioridad<T>();
        nuevo.info = x;
        nuevo.prioridad = prioridad;
        // Si la cola está vacía o es más prioritario que el primero,
        // el primero hay que agregarlo al principio
        if (mayorPrioridad == null ||
                prioridad > mayorPrioridad.prioridad) {
            nuevo.sig = mayorPrioridad;
            mayorPrioridad = nuevo;
        } else {
            // Sabemos que mayorPrioridad no es null
            NodoPrioridad<T> aux = mayorPrioridad;
            while (aux.sig != null && aux.sig.prioridad >= prioridad) {
                aux = aux.sig;
            }
            nuevo.sig = aux.sig;
            aux.sig = nuevo;
        }
    }

    public void Desacolar() {
        mayorPrioridad = mayorPrioridad.sig;
    }

    public T Primero() {
        return mayorPrioridad.info;
    }

    public boolean ColaVacia() {
        return (mayorPrioridad == null);
    }

    public int Prioridad() {
        return mayorPrioridad.prioridad;
    }
}