package ntu.csie.ping;

import java.io.*;
import java.net.*;

public class Server {

  private ServerSocket serverSocket;

  /*
   * Listen on the specific port, throw exception when fail.
   */
  public Server(int port) {
    try {
      serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /*
   * Start a loop to accept socket connection from client.
   * Create new thread for every socket connection.
   */
  public void start() {
    while (true) {
      try {
        Socket socket = serverSocket.accept();
        (new Request(socket)).start();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /*
   * Thread to run socket connection from client.
   */
  private class Request extends Thread {

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Request(Socket socket) {
      this.socket = socket;
      try {
        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    /*
     * Read the object from client, check the format.
     * If the format is right, show recv message and send the object back.
     * Else, ignore it.
     */
    @Override
    public void run() {
      try {
        Object obj = in.readObject();
        if (obj instanceof Packet) {
          Packet packet = (Packet)obj;
          System.out.println("recv from " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + ", seq = " + packet.getSeq());

          out.writeObject(packet);
          out.flush();
        }

        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
      System.out.flush();
    }
  }

  public static void main(String[] args) {
    try {
      int port = getPort(args);
      Server server = new Server(port);
      server.start();
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  private static int getPort(String[] args) throws Exception {
    if (args.length != 1)
      throw new Exception("Wrong Argument");
    return Integer.parseInt(args[0]);
  }
}
