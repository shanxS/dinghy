package plane.data;

import lombok.Data;
import network.NodeAddress;

@Data
public class Term {
    private long term, index;
    private NodeAddress leader;
}
