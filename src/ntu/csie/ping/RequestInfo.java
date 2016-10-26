package ntu.csie.ping;

public class RequestInfo {

  private String host;
  private int port;
  private static int timeout = 1000;
  private static int numberOfPackets = 0;

  public RequestInfo(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public static void setTimeout(int t) {
    timeout = t;
  }

  public static int getTimeout() {
    return timeout;
  }

  public static void setNumberOfPackets(int n) {
    numberOfPackets = n;
  }

  public static int getNumberOfPackets() {
    return numberOfPackets;
  }
}
