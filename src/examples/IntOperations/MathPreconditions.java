/*
 * Copyright (C) 2011 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package IntOperations;

import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * A collection of preconditions for math functions.
 * 
 * @author Louis Wasserman
 */
class MathPreconditions {
  static int checkPositive(String role, int x) {
    if (x <= 0) {
      assert false: role + " (" + x + ") must be > 0";
    }
    return x;
  }

  static long checkPositive(String role, long x) {
    if (x <= 0) {
      assert false: role + " (" + x + ") must be > 0";
    }
    return x;
  }

  static BigInteger checkPositive(String role, BigInteger x) {
    if (x.signum() <= 0) {
      assert false: role + " (" + x + ") must be > 0";
    }
    return x;
  }

  static int checkNonNegative(String role, int x) {
    if (x < 0) {
      assert false: role + " (" + x + ") must be >= 0";
    }
    return x;
  }

  static long checkNonNegative(String role, long x) {
    if (x < 0) {
      assert false: role + " (" + x + ") must be >= 0";
    }
    return x;
  }
  
  static BigInteger checkNotNull(BigInteger x) {
  	if (x == null) {
  		assert false: x + "(" + x + ") must be non-null";
  	}
  	return x;
  }
  
  static RoundingMode checkNotNull(RoundingMode r) {
  	if (r == null) {
  		assert false: r + "(" + r + ") must be non-null";
  	}
  	return r;
  }

  public static void checkArgument(boolean expression) {
    if (!expression) {
      assert false: "Illegal argument exception.";
    }
  }

  static BigInteger checkNonNegative(String role, BigInteger x) {
    if (checkNotNull(x).signum() < 0) {
      assert false: role + " (" + x + ") must be >= 0";
    }
    return x;
  }

  static void checkRoundingUnnecessary(boolean condition) {
    if (!condition) {
      assert false: "mode was UNNECESSARY, but rounding was necessary";
    }
  }

  static void checkInRange(boolean condition) {
    if (!condition) {
      assert false: "not in range";
    }
  }

  static void checkNoOverflow(boolean condition) {
    if (!condition) {
      assert false: "overflow";
    }
  }

  private MathPreconditions() {}
}
