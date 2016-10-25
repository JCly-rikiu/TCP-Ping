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
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Request(Socket socket) {
      this.socket = socket;
      try {
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    @Override
    public void run() {
      try {
        out.writeObject(new Packet(System.currentTimeMillis()));
        out.flush();

        Object obj = in.readObject();
        if (obj instanceof Packet) {
          Packet packet = (Packet)obj;
          packet.show();
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
    Client client = new Client("127.0.0.1", 5217);
    client.start();
  }
}
