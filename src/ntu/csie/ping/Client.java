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
        for (int i = numberOfPackets; i > 0 || numberOfPackets == 0; i--) {
          ExecutorService executor = Executors.newSingleThreadExecutor();
          Future<String> future = executor.submit(new Request(host, port));

          try {
            System.out.println(future.get(timeout, TimeUnit.MILLISECONDS));
          } catch (TimeoutException e) {
            future.cancel(true);
            System.out.println("timeout");
          } catch (Exception e) {
            future.cancel(true);
            System.out.println("timeout");
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
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Request(String host, int port) {
      this.host = host;
      this.port = port;
    }

    @Override
    public String call() throws Exception {
      socket = new Socket(host, port);
      out = new ObjectOutputStream(socket.getOutputStream());
      in = new ObjectInputStream(socket.getInputStream());

      out.writeObject(new Packet(System.currentTimeMillis()));
      out.flush();

      Object obj = in.readObject();
      String returnMessage = null;
      if (obj instanceof Packet) {
        Packet packet = (Packet)obj;
        returnMessage = "recv from " + socket.getInetAddress().getHostAddress() + ", RTT = " + packet.getRTT(System.currentTimeMillis()) + " msec";
        System.out.flush();
      }

      socket.close();

      return returnMessage;
    }
  }

  public static void main(String[] args) {
    Client client = new Client("oasis2.csie.ntu.edu.twd", 5217, 10, 1);
    client.start();
    client = new Client("oasis2.csie.ntu.edu.tw", 5217, 48, 100);
    client.start();
  }
}
