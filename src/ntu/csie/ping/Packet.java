package ntu.csie.ping;

import java.io.Serializable;

/*
 * Object to be transfered as packet, contain sequence number and sendtime.
 */
public class Packet implements Serializable {

  private long sendTime;
  private int seq;

  public Packet(long sendTime, int seq) {
    this.sendTime = sendTime;
    this.seq = seq;
  }

  public long getRTT(long receiveTime) {
    return receiveTime - sendTime;
  }

  public int getSeq() {
    return seq;
  }
}
