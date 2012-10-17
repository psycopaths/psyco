package temp;

import gov.nasa.jpf.jdart.Symbolic;
import CEV.CEV;

public class MethodTestCase {

 

  public static void sequence() {
    // has to have default constructor ...
    CEV testObject = new CEV();
    testObject.lsamRendezvous(
 
    );
  }

  public static void main(String[] args) {
    sequence();
  }
} 

