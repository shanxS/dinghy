package plane.control.personality;

import plane.control.Daemon;

public interface Personality <V extends Comparable<V>, K extends Comparable<K>> {

    void initShutdown();

    void setMetamorphosisCallback(Daemon<K,V> daemon);
//    appendEntry();
}
