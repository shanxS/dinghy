import network.NodeAddress;

/**
 * User interface.
 * TODO: Vend out as separate pkg
 * @param <K> Key
 * @param <V> Value
 */
public interface Dinghy <K extends Comparable<K>, V extends Comparable<V>> {
    /**
     * Write API
     * @param key
     * @param value
     * @return null if request was sent to leader else Leader's address
     */
    NodeAddress commit(K key, V value);

    /**
     * Read API
     * @param key
     * @return gets value for given key in eventually consistent manner
     */
    V read(K key);
}
