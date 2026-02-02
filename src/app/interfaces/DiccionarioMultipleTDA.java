package app.interfaces;

public interface DiccionarioMultipleTDA<K, V> {
    void InicializarDiccionario();

    // Siempre que el diccionario esté inicializado
    void Agregar(K clave, V valor);

    // Siempre que el diccionario esté inicializado
    void Eliminar(K clave);

    // Siempre que el diccionario esté inicializado
    void EliminarValor(K clave, V valor);

    // Siempre que el diccionario esté inicializado
    ConjuntoTDA<V> Recuperar(K clave);

    // Siempre que el diccionario esté inicializado
    ConjuntoTDA<K> Claves();
}
