package networking;

import java.io.Serializable;
import java.util.Objects;

public class Address implements Serializable {
    private String host;
    private int port;

    public Address(String host, int port){
        this.host = host;
        this.port = port;
    }

    public Address(){

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return port == address.port && Objects.equals(host, address.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
