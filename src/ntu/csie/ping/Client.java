package ntu.csie.ping;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Client {

  private String host;
  private int port;
  private int timeout;
  private int numberOfPackets;

  public Client(String host, int port, int timeout, int numberOfPackets) {
    this.host = host;
    this.port = port;
    this.timeout = timeout;
    this.numberOfPackets = numberOfPackets;
  }

  /*
   * Thread to prevent blocking the main thread.
   * Resolve URL to the ip address.
   * Use ExecutorService and Future to timeout the connection.
   */
  public void start() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        String ip = null;
        try {
          InetAddress address = InetAddress.getByName(host);
          ip = address.getHostAddress();
        } catch (UnknownHostException e) {
          ip = "unknown Host(" + host + ")";
        }
        for (int i = 0; i != numberOfPackets || numberOfPackets == 0; i++) {
          ExecutorService executor = Executors.newSingleThreadExecutor();
          Future<String> future = executor.submit(new Request(host, port, i));

          try {
            System.out.println(future.get(timeout, TimeUnit.MILLISECONDS));
          } catch (Exception e) {
            future.cancel(true);
            System.out.println("timeout when connect to " + ip + ":" + port + ", seq = " + i);
          }
          System.out.flush();
          executor.shutdownNow();
        }
      }
    }).start();
  }

  private class Request implements Callable<String> {

    private Socket socket;
    private String host;
    private int port;
    private int seq;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Request(String host, int port, int seq) {
      this.host = host;
      this.port = port;
      this.seq = seq;
    }

    /*
     * Build socket connection with server.
     * Send packet with the sequence number and the sendtime.
     * Receive packet and check the sequence number.
     * If the sequence number is right, show recv message.
     * Else, throw an exception.
     */
    @Override
    public String call() throws Exception {
      try {
        socket = new Socket(host, port);
      } catch (IOException e) {
        Thread.sleep(10000000);
      }
      out = new ObjectOutputStream(socket.getOutputStream());
      in = new ObjectInputStream(socket.getInputStream());

      out.writeObject(new Packet(System.currentTimeMillis(), seq));
      out.flush();

      Object obj = in.readObject();
      StringBuilder returnMessage = new StringBuilder("recv from ");
      if (obj instanceof Packet) {
        Packet packet = (Packet)obj;
        if (seq == packet.getSeq()) {
          returnMessage.append(socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
          returnMessage.append(", seq = " + seq);
          returnMessage.append(", RTT = " + packet.getRTT(System.currentTimeMillis()) + " msec");
        } else {
          throw new Exception();
        }
      }

      socket.close();

      return returnMessage.toString();
    }
  }

  public static void main(String[] args) {
    try {
      List<RequestInfo> list = parseArgs(args);
      for (int i = 0; i != list.size(); i++) {
        RequestInfo now = list.get(i);
        Client client = new Client(now.getHost(), now.getPort(), RequestInfo.getTimeout(), RequestInfo.getNumberOfPackets());
        client.start();
      }
    } catch (Exception e) {
      System.out.println("Unvalid arguments");
    }
  }

  private static List<RequestInfo> parseArgs(String[] args) throws Exception {
    List<RequestInfo> list = new ArrayList<RequestInfo>();
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-t")) {
        i++;
        int t = Integer.parseInt(args[i]);
        RequestInfo.setTimeout(t);
      }
      else if (args[i].equals("-n")) {
        i++;
        int n = Integer.parseInt(args[i]);
        RequestInfo.setNumberOfPackets(n);
      }
      else {
        String[] address = args[i].split(":");
        if (address.length != 2)
          throw new Exception();
        list.add(new RequestInfo(address[0], Integer.parseInt(address[1])));
      }
    }

    return list;
  }
}
