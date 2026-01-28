package interfaces;

public interface IHashTable <K,V>{
    void insert(K key, V value);
    V obtain(K key);
    void delete(K key);
    boolean isEmpty();
    int getSize();
}
