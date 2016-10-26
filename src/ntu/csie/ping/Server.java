package ntu.csie.ping;

import java.io.*;
import java.net.*;

public class Server {

  private ServerSocket serverSocket;

  public Server(int port) {
    try {
      serverSocket = new ServerSocket(port);
      serverSocket.setSoTimeout(10000);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

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

    @Override
    public void run() {
      try {
        Object obj = in.readObject();
        if (obj instanceof Packet) {
          Packet packet = (Packet)obj;
          System.out.println("recv from " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());

          out.writeObject(packet);
          out.flush();
        }

        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    int port = 5217;
    Server server = new Server(port);
    server.start();
  }
}
