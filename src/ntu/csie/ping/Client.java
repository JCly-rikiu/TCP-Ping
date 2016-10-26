package ntu.csie.ping;

import java.io.*;
import java.net.*;
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

  public void start() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        String ip = null;
        try {
          InetAddress address = InetAddress.getByName(host);
          ip = address.getHostAddress();
        } catch (UnknownHostException e) {
          ip = "Unknown Host " + host;
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

    @Override
    public String call() throws Exception {
      socket = new Socket(host, port);
      out = new ObjectOutputStream(socket.getOutputStream());
      in = new ObjectInputStream(socket.getInputStream());

      out.writeObject(new Packet(System.currentTimeMillis(), seq));
      out.flush();

      Object obj = in.readObject();
      StringBuilder returnMessage = new StringBuilder("recv from ");
      if (obj instanceof Packet) {
        Packet packet = (Packet)obj;
        returnMessage.append(socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
        returnMessage.append(", seq = " + seq);
        returnMessage.append(", RTT = " + packet.getRTT(System.currentTimeMillis()) + " msec");
      }

      socket.close();

      return returnMessage.toString();
    }
  }

  public static void main(String[] args) {
    Client client = new Client("oasis2.csie.ntu.edu.twd", 5217, 10, 1);
    client.start();
    client = new Client("oasis2.csie.ntu.edu.tw", 5217, 48, 100);
    client.start();
    client = new Client("140.112.30.52", 5217, 48, 100);
    client.start();
  }
}
