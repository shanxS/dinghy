package plane.data;

import lombok.Data;

@Data
public class Datum <K extends Comparable<K>, V extends Comparable<V>>  {
    K key;
    V value;
    long commitIndex, termIndex;
}
