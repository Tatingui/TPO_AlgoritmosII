package interfaces;

public interface DiccionarioSimpleTDA<K, V> {
    // Siempre que el diccionario esté inicializado
    void InicializarDiccionario();

    // Siempre que el diccionario esté inicializado
    void Agregar(K clave, V valor);

    // Siempre que el diccionario esté inicializado
    void Eliminar(K clave);

    // Siempre que el diccionario esté inicializado y la clave exista en el mismo
    V Recuperar(K clave);

    // Siempre que el diccionario esté inicializado
    ConjuntoTDA<K> Claves();
}
