package structures;


import interfaces.IHashTable;
/**
 * Implementation of a hash table using separate chaining for collision resolution.
 *
 * @param <K> the type of keys maintained by this hash table
 * @param <V> the type of mapped values
 */
public class HashTable<K, V> implements IHashTable<K, V> {

    private static final int capacity = 10;
    private final LinkedList<Input<K, V>>[] buckets;
    private int size;

    public HashTable() {
        buckets = new LinkedList[capacity];
        for (int i = 0; i < capacity; i++) {
            buckets[i] = new LinkedList<>();
        }
        size = 0;
    }
    /**
     * Associates the specified value with the specified key in this hash table.
     * If the key already exists, the value is updated.
     *
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     */
    public void insert(K key, V value) {
        int index= obtainIndex(key);
        LinkedList<Input<K, V>> bucket = buckets[index];
        Input<K, V> existing = findInBucket(bucket, key);
        if (existing != null) {
            existing.value = value;
        }else{
            bucket.add(new Input<>(key, value));
            size++;
        }

    }
    /**
     * Searches for a key in a specific bucket.
     *
     * @param bucket the linked list representing the bucket to search
     * @param key the key to search for
     * @return the Input entry if found, null otherwise
     */
    private Input<K, V> findInBucket(LinkedList<Input<K, V>> bucket, K key) {
        Node<Input<K, V>> current = bucket.getFirst();
        while(current != null){
            if(current.getData().key.equals(key)){
                return current.getData();

            }
            current = current.next;
        }
        return null;
    }
    /**
     * Returns the value to which the specified key is mapped, or null if not found.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if not found
     */
    public V obtain(K key) {
        int index = obtainIndex(key);
        LinkedList<Input<K, V>> bucket = buckets[index];
        Input<K, V> found = findInBucket(bucket, key);

        if (found != null) {
            return found.value;
        } else {
            return null;
        }
    }
    /**
     * Removes the mapping for the specified key from this hash table if present.
     *
     * @param key the key whose mapping is to be removed
     */
    public void delete(K key) {
        int index = obtainIndex(key);
        LinkedList<Input<K, V>> bucket = buckets[index];
        Node<Input<K, V>> current = bucket.getFirst();
        Node<Input<K, V>> prev = null;
        while(current != null){
            if(current.getData().key.equals(key)){
                if(prev == null){
                    bucket.setFirst(current.getNext());

                }else{
                    prev.setNext(current.getNext());
                }
                size--;
                return;
            }
            prev = current;
            current = current.next;
        }
    }
    /**
     * Computes the bucket index for a given key.
     *
     * @param key the key whose index is to be computed
     * @return the bucket index (0 to capacity-1)
     */
    private int obtainIndex(K key) {
        return Math.abs(key.hashCode() % capacity);
    }
    private static class Input<K, V> {
        K key;
        V value;

        Input(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
    /**
     * Returns true if this hash table contains no key-value mappings.
     *
     * @return true if this hash table is empty
     */
    public boolean isEmpty() {
        return size == 0;
    }
    /**
     * Returns the number of key-value mappings in this hash table.
     *
     * @return the number of key-value mappings
     */
    @Override
    public int getSize() {
        return size;
    }
}
