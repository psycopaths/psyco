package <package>;

import gov.nasa.jpf.jdart.Symbolic;


public class Alphabet {

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

  <symbols:{s |
  public static void <s.wrapper>() {  
    if (<s.precondition>) {
      <s.call>;
    \} else {
       throw new TotallyPsyco("Odd Psyco"); 
    \}
  \}
  }> 

} 
  
