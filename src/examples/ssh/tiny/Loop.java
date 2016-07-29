package ssh.tiny;

import gov.nasa.jpf.jdart.Symbolic;

/**
 *
 * @author falk
 */
public class Loop {

  @Symbolic("true")
  private int e = 0;

  @Symbolic("true")
  private int s = 2;

  public Loop() {

  }

  public void doLoop() {

    if (s == 2) {
      if (e == 0) {
        e = 1;
      }
      s = 3;
    } else if (s == 3) {
      if (e == 1) {
        e = 2;
      }
      s = 4;
    } else if (s == 4) {
      assert (e != 3);
      s = 5;
    }
  }

}
