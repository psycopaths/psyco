package temp;

import gov.nasa.jpf.jdart.Symbolic;
import CEV.CEV;

public class MethodTestCase {

    @Symbolic("true")
    public static int P1;  
 

  public static void sequence() {
    // has to have default constructor ...
    CEV testObject = new CEV();
    testObject.reset(
 
       P1 
    );
  }

  public static void main(String[] args) {
    sequence();
  }
} 

