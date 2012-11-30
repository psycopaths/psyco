package <package>;

import gov.nasa.jpf.jdart.Symbolic;


public class RefinementQuery {

  public static class TotallyPsyco extends java.lang.AssertionError {
    private static final long serialVersionUID = 1L;
    TotallyPsyco(String msg) {
      super(msg);
    }
  }

  <params:{p | 
  @Symbolic("true")
  public static <p.type> <p.name>;  
  }> 

          
  public static void sequence() {
  
    <steps:{s |
    if (<s.precondition>) {
      <s.call>;
    \} else {
       throw new TotallyPsyco("Odd Psyco"); 
    \}
    }> 

  }

  public static void main(String[] args) {
    sequence();
  }
} 
  
