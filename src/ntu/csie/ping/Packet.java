package ntu.csie.ping;

import java.io.Serializable;

public class Packet implements Serializable {

  private long sendTime;

  public Packet(long sendTime) {
    this.sendTime = sendTime;
  }

  public void show() {
    System.out.println(sendTime);
  }
}
