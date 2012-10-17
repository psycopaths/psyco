package temp;

import gov.nasa.jpf.jdart.Symbolic;


public class Query {

  public static class TotallyPsyco extends java.lang.AssertionError {
    private static final long serialVersionUID = 1L;
    TotallyPsyco(String msg) {
      super(msg);
    }
  }

    @Symbolic("true")
    public static int P_1;  
    @Symbolic("true")
    public static int P_2;  
 

  public static void sequence() {

        if ((true && true)) {
          exampleProtocolSteffen2011.Protocol.msg(P_1,P_2);
        } else {
           throw new TotallyPsyco("Odd Psyco"); 
        }
 

  }

  public static void main(String[] args) {
    sequence();
  }
} 

