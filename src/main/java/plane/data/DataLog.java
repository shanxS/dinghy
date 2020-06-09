package plane.data;

import java.util.List;

/**
 * Keep tracks of data after consensus was done
 */
public class DataLog <K extends Comparable<K>, V extends Comparable<V>> {
    List<Datum<K, V>> dataSeq;

    public void add(Datum<K,V> datum) {
        dataSeq.add(datum);
    }

}
