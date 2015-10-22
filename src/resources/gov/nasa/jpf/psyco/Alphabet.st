package <package>;

import gov.nasa.jpf.jdart.Symbolic;
import gov.nasa.jpf.psyco.oracles.JDartOracle.PreconditionError;


public class <class> {

  private static <obj>;

  <params:{p | 
  @Symbolic("true")
  public static <p.type> <p.name>;  
  }> 

  <symbols:{s |
  public static void <s.name>() {  
    if (<s.precondition>) {
      <s.call>;
    \} else {
       throw new PreconditionError("Odd Psyco"); 
    \}
  \}
  }> 

} 
  
