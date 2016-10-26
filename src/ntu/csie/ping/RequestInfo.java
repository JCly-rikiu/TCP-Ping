package ntu.csie.ping;

public class RequestInfo {

  private String host;
  private int port;
  private int timeout;
  private int numberOfPackets;

  public RequestInfo(String host, int port, int timeout, int numberOfPackets) {
    this.host = host;
    this.port = port;
    this.timeout = timeout;
    this.numberOfPackets = numberOfPackets;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public int getTimeout() {
    return timeout;
  }

  public int getNumberOfPackets() {
    return numberOfPackets;
  }
}
