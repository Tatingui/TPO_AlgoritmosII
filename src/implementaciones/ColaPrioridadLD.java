package implementaciones;

import interfaces.ColaPrioridadTDA;

public class ColaPrioridadLD implements ColaPrioridadTDA {
    NodoPrioridad mayorPrioridad;

    public void InicializarCola() {
        mayorPrioridad = null;
    }

    public void AcolarPrioridad(int x, int prioridad) {
        // Creo el nuevo nodo que voy a acolar
        NodoPrioridad nuevo = new NodoPrioridad();
        nuevo.info = x;
        nuevo.prioridad = prioridad;
        // Si la cola est´a vac´ıa o bien es m´as prioritario que
        // el primero hay que agregarlo al principio
        if (mayorPrioridad == null ||
                prioridad > mayorPrioridad.prioridad) {
            nuevo.sig = mayorPrioridad;
            mayorPrioridad = nuevo;
        } else {
            // Sabemos que mayorPrioridad no es null
            NodoPrioridad aux = mayorPrioridad;
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

    public int Primero() {
        return mayorPrioridad.info;
    }

    public boolean ColaVacia() {
        return (mayorPrioridad == null);
    }

    public int Prioridad() {
        return mayorPrioridad.prioridad;
    }
}