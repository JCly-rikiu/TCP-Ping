package ntu.csie.ping;

import java.io.*;
import java.net.*;

public class Client {

  private Socket socket;

  public Client(String host, int port) {
    try {
      socket = new Socket(host, port);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void start() {
    (new Request(socket)).start();
  }

  private class Request extends Thread {

    private Socket socket;

    public Request(Socket socket) {
      this.socket = socket;
    }

    @Override
    public void run() {

    }
  }

  public static void main(String[] args) {
    Client client = new Client("127.0.0.1", 5217);
    client.start();
  }
}
