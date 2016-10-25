package ntu.csie.ping;

import java.io.*;
import java.net.*;

public class Server {

  private ServerSocket serverSocket;

  public Server(int port) {
    try {
      serverSocket = new ServerSocket(port);
      System.out.println("listening on port: " + port);
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

    public Request(Socket socket) {
      this.socket = socket;
      System.out.println(socket.getRemoteSocketAddress());
    }

    @Override
    public void run() {

    }
  }

  public static void main(String[] args) {
    int port = 5217;
    Server server = new Server(port);
    server.start();
  }
}
