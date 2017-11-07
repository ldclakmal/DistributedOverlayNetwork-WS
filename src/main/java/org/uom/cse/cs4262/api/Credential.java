package org.uom.cse.cs4262.api;

/**
 * @author Chamin Wickramarathna
 * @date 22/10/2017
 * @since 1.0
 */
public class Credential {
    private String ip;
    private int port;
    private String username;

    public Credential(String ip, int port, String username) {
        this.ip = ip;
        this.port = port;
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Credential)) {
            return false;
        }
        Credential that = (Credential) other;
        // Custom equality check here.
        return (this.ip.equals(that.getIp())
                && this.port == that.getPort());
//                && this.username.equals(that.getUsername()));
    }

}
