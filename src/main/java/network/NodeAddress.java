package network;

import lombok.Data;

import java.net.InetAddress;

/*
Pojo which determines node address
 */
@Data
public class NodeAddress {
    private InetAddress inetAddress;
    private int port;
    private String id = port + "_" + inetAddress.getCanonicalHostName();
}
