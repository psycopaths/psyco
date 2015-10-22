/*
 * Copyright (C) 2015, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * The PSYCO: A Predicate-based Symbolic Compositional Reasoning environment 
 * platform is licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may obtain a 
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0. 
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations under the License.
 */
package issta2013.math;

import static java.lang.Math.abs;
import static java.math.RoundingMode.HALF_EVEN;
import static java.math.RoundingMode.HALF_UP;

import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * A class for arithmetic on values of type {@code int}. Where possible, methods are defined and
 * named analogously to their {@code BigInteger} counterparts.
 *
 * <p>The implementations of many methods in this class are based on material from Henry S. Warren,
 * Jr.'s <i>Hacker's Delight</i>, (Addison Wesley, 2002).
 *
 * <p>Similar functionality for {@code long} and for {@link BigInteger} can be found in
 * {@link LongMath} and {@link BigIntegerMath} respectively.  For other common operations on
 * {@code int} values, see {@link com.google.common.primitives.Ints}.
 *
 * @since 11.0
 */

public class IntMath {
  
  public IntMath() {}
  
	public int UNNECESSARY = 0;
	public int DOWN = 1;
	public int FLOOR = 2;
	public int UP = 3;
	public int CEILING = 4;
	public int HALF_DOWN = 5;
	public int HALF_UP = 6;
	public int HALF_EVEN = 7;
	
  // NOTE: Whenever both tests are cheap and functional, it's faster to use &, | instead of &&, ||

  // only used for concrete execution
  public  void internalReset() {}

  /**
   * Returns {@code true} if {@code x} represents a power of two.
   *
   * <p>This differs from {@code Integer.bitCount(x) == 1}, because
   * {@code Integer.bitCount(Integer.MIN_VALUE) == 1}, but {@link Integer#MIN_VALUE} is not a power
   * of two.
   */
  public void isPowerOfTwo(int x) {
    boolean ret =  x > 0 & (x & (x - 1)) == 0;
  }

  private boolean _isPowerOfTwo(int x) {
    return  x > 0 & (x & (x - 1)) == 0;
  }  
  /**
   * Returns the base-2 logarithm of {@code x}, rounded according to the specified rounding mode.
   *
   * @throws IllegalArgumentException if {@code x <= 0}
   * @throws ArithmeticException if {@code mode} is {@link RoundingMode#UNNECESSARY} and {@code x}
   *         is not a power of two
   */
  @SuppressWarnings("fallthrough")
  public  void log2(int x, int mode) {
    MathPreconditions.checkPositive("x", x);
    if (mode == UNNECESSARY)
    	MathPreconditions.checkRoundingUnnecessary(_isPowerOfTwo(x));
        // fall through
    if (mode == DOWN || mode == FLOOR) {
    	MathPreconditions.checkRoundingUnnecessary(_isPowerOfTwo(x));
    	int ret =  (Integer.SIZE - 1) - Integer.numberOfLeadingZeros(x);
    }

    if (mode == UP || mode == CEILING) {
    	int ret =  Integer.SIZE - Integer.numberOfLeadingZeros(x - 1);
    }

    if (mode == HALF_DOWN || mode == HALF_UP || mode == HALF_EVEN) {
    	// Since sqrt(2) is irrational, log2(x) - logFloor cannot be exactly 0.5
    	int leadingZeros = Integer.numberOfLeadingZeros(x);
    	int cmp = MAX_POWER_OF_SQRT2_UNSIGNED >>> leadingZeros;
    	// floor(2^(logFloor + 0.5))
    	int logFloor = (Integer.SIZE - 1) - leadingZeros;
    	int ret =  (x <= cmp) ? logFloor : logFloor + 1;
    }

    throw new AssertionError();
  }

  /** The biggest half power of two that can fit in an unsigned int. */
   final int MAX_POWER_OF_SQRT2_UNSIGNED = 0xB504F333;

  /**
   * Returns the base-10 logarithm of {@code x}, rounded according to the specified rounding mode.
   *
   * @throws IllegalArgumentException if {@code x <= 0}
   * @throws ArithmeticException if {@code mode} is {@link RoundingMode#UNNECESSARY} and {@code x}
   *         is not a power of ten
   */
  @SuppressWarnings("fallthrough")
  public  void log10(int x, int mode) {
  	MathPreconditions.checkPositive("x", x);
    int logFloor = log10Floor(x);
    int floorPow = POWERS_OF_10[logFloor];

    if (mode == UNNECESSARY)
      	MathPreconditions.checkRoundingUnnecessary(x == floorPow);

  	if (mode == FLOOR || mode == DOWN)
      ; //ret =  logFloor;
  	
  	if (mode == CEILING || mode == UP)
  		; // int ret =  (x == floorPow) ? logFloor : logFloor + 1;
  	
  	if (mode == HALF_DOWN || mode == HALF_UP || mode == HALF_EVEN)
        // sqrt(10) is irrational, so log10(x) - logFloor is never exactly 0.5
  		; // int ret =  (x <= HALF_POWERS_OF_10[logFloor]) ? logFloor : logFloor + 1;
  	
  	assert false: "Illegal mode.";
    int ret =  0;
  }

  private int log10Floor(int x) {
    for (int i = 1; i < POWERS_OF_10.length; i++) {
      if (x < POWERS_OF_10[i]) {
        int ret =  i - 1;
      }
    }
    return  POWERS_OF_10.length - 1;
  }

   final int[] POWERS_OF_10 =
      {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000};

  // HALF_POWERS_OF_10[i] = largest int less than 10^(i + 0.5)
   final int[] HALF_POWERS_OF_10 =
      {3, 31, 316, 3162, 31622, 316227, 3162277, 31622776, 316227766, Integer.MAX_VALUE};

  /**
   * Returns {@code b} to the {@code k}th power. Even if the result overflows, it will be equal to
   * {@code BigInteger.valueOf(b).pow(k).intValue()}. This implementation runs in {@code O(log k)}
   * time.
   *
   * <p>Compare {@link #checkedPow}, which throws an {@link ArithmeticException} upon overflow.
   *
   * @throws IllegalArgumentException if {@code k < 0}
   */
  public  void pow(int b, int k) {
  	if (k < 0)
  		assert false;
    int __x = (b) ;
      if (__x ==  0) {
        int ret =  (k == 0) ? 1 : 0;
      } else if (__x ==  1) {
        int ret =  1;
      } else if (__x ==  (-1)) {
         int ret =  ((k & 1) == 0) ? 1 : -1;
      } else if (__x ==  2) {
         int ret =  (k < Integer.SIZE) ? (1 << k) : 0;
      } else if (__x ==  (-2)) {
        if (k < Integer.SIZE) {
          int ret =  ((k & 1) == 0) ? (1 << k) : -(1 << k);
        } else {
          int ret =  0;
        }
    }
    for (int accum = 1;; k >>= 1) {
      __x = (k) ;
        if (__x ==  0) {
          int ret =  accum;
          return;
        } else if (__x ==  1) {
          int ret =  b * accum;
          return;
        } else {
          accum *= ((k & 1) == 0) ? 1 : b;
          b *= b;
      }
    }
  }

  /**
   * Returns the square root of {@code x}, rounded with the specified rounding mode.
   *
   * @throws IllegalArgumentException if {@code x < 0}
   * @throws ArithmeticException if {@code mode} is {@link RoundingMode#UNNECESSARY} and
   *         {@code sqrt(x)} is not an integer
   */
  @SuppressWarnings("fallthrough")
  public  void sqrt(int x, int mode) {
  	MathPreconditions.checkNonNegative("x", x);
    int sqrtFloor = sqrtFloor(x);
    if (mode == UNNECESSARY)
      	MathPreconditions.checkRoundingUnnecessary(sqrtFloor * sqrtFloor == x); // fall through
    
    if (mode == FLOOR || mode == DOWN) {
        int ret =  sqrtFloor;
    }
    if (mode == CEILING || mode == UP) {
        int ret =  (sqrtFloor * sqrtFloor == x) ? sqrtFloor : sqrtFloor + 1;
    }
    if (mode == HALF_DOWN || mode == HALF_UP || mode == HALF_EVEN) {
    	int halfSquare = sqrtFloor * sqrtFloor + sqrtFloor;
        /*
         * We wish to test whether or not x <= (sqrtFloor + 0.5)^2 = halfSquare + 0.25.
         * Since both x and halfSquare are integers, this is equivalent to testing whether or not
         * x <= halfSquare.  (We have to deal with overflow, though.)
         */
        int ret =  (x <= halfSquare | halfSquare < 0) ? sqrtFloor : sqrtFloor + 1;
    }
    assert false;
    int ret =  0;
  }

  private  int sqrtFloor(int x) {
    // There is no loss of precision in converting an int to a double, according to
    // http://java.sun.com/docs/books/jls/third_edition/html/conversions.html#5.1.2
    return (int) Math.sqrt(x);
  }

  /**
   * Returns the result of dividing {@code p} by {@code q}, rounding using the specified
   * {@code RoundingMode}.
   *
   * @throws ArithmeticException if {@code q == 0}, or if {@code mode == UNNECESSARY} and {@code a}
   *         is not an integer multiple of {@code b}
   */
  public  void divide(int p, int q, int mode) {
    if (q == 0) {
      assert false; // for GWT
    }

    int div = p / q;
    int rem = p - q * div; // equal to p % q

    if (rem == 0) {
      int ret =  div;
    }

    /*
     * Normal Java division rounds towards 0, consistently with RoundingMode.DOWN. We just have to
     * deal with the cases where rounding towards 0 is wrong, which typically depends on the sign of
     * p / q.
     *
     * signum is 1 if p and q are both nonnegative or both negative, and -1 otherwise.
     */
    int signum = 1 | ((p ^ q) >> (Integer.SIZE - 1));
    boolean increment = false;
    
    if (mode == UNNECESSARY) {
    	MathPreconditions.checkRoundingUnnecessary((rem == 0));
    }
    // fall through
    
    if (mode == DOWN) {
    	increment = false;
    } else if (mode == UP) {
    	increment = true;
    } else if (mode == CEILING) {
    	increment = signum > 0;
    } else if (mode == FLOOR) {
    	increment = signum < 0;
    } else if (mode == HALF_EVEN || mode == HALF_DOWN || mode == HALF_UP) {
    	int absRem = abs(rem);
    	int cmpRemToHalfDivisor = absRem - (abs(q) - absRem);
    	// subtracting two nonnegative ints can't overflow
    	// cmpRemToHalfDivisor has the same sign as compare(abs(rem), abs(q) / 2).
    	if (cmpRemToHalfDivisor == 0) { // exactly on the half mark
    		increment = (mode == HALF_UP || (mode == HALF_EVEN & (div & 1) != 0));
    	} else {
    		increment = cmpRemToHalfDivisor > 0; // closer to the UP value
    	}
    } else {
    	assert false;
    }
    int ret =  increment ? div + signum : div;
  }

  /**
   * Returns {@code x mod m}. This differs from {@code x % m} in that it always returns a
   * non-negative result.
   *
   * <p>For example:<pre> {@code
   *
   * mod(7, 4) == 3
   * mod(-7, 4) == 1
   * mod(-1, 4) == 3
   * mod(-8, 4) == 0
   * mod(8, 4) == 0}</pre>
   *
   * @throws ArithmeticException if {@code m <= 0}
   */
  public  void mod(int x, int m) {
    if (m <= 0) {
      assert false: "Modulus " + m + " must be > 0";
    }
    if (x < 0) {
    	assert false: "Modulus " + x + " must be >= 0";
    }
    int result = x % m;
    int ret =  result;
//    int ret =  (result >= 0) ? result : result + m;
  }

  /**
   * Returns the greatest common divisor of {@code a, b}. Returns {@code 0} if
   * {@code a == 0 && b == 0}.
   *
   * @throws IllegalArgumentException if {@code a < 0} or {@code b < 0}
   */
  public  void gcd(int a, int b) {
    /*
     * The reason we require both arguments to be >= 0 is because otherwise, what do you return on
     * gcd(0, Integer.MIN_VALUE)? BigInteger.gcd would return positive 2^31, but positive 2^31
     * isn't an int.
     */
  	MathPreconditions.checkNonNegative("a", a);
  	MathPreconditions.checkNonNegative("b", b);
    // The simple Euclidean algorithm is the fastest for ints, and is easily the most readable.
    while (b != 0) {
      int t = b;
      b = a % b;
      a = t;
    }
    int ret =  a;
  }

  /**
   * Returns the sum of {@code a} and {@code b}, provided it does not overflow.
   *
   * @throws ArithmeticException if {@code a + b} overflows in signed {@code int} arithmetic
   */
  public  void checkedAdd(int a, int b) {
  	int la = a;
  	int lb = b;
  	int result = la + lb;
    MathPreconditions.checkNoOverflow(result == (int) result);
    int ret =  (int) result;
  }

  /**
   * Returns the difference of {@code a} and {@code b}, provided it does not overflow.
   *
   * @throws ArithmeticException if {@code a - b} overflows in signed {@code int} arithmetic
   */
  public  void checkedSubtract(int a, int b) {
    int result = (int) a - b;
    MathPreconditions.checkNoOverflow(result == (int) result);
    int ret =  (int) result;
  }

  /**
   * Returns the product of {@code a} and {@code b}, provided it does not overflow.
   *
   * @throws ArithmeticException if {@code a * b} overflows in signed {@code int} arithmetic
   */
  public  void checkedMultiply(int a, int b) {
    int result = (int) a * b;
    MathPreconditions.checkNoOverflow(result == (int) result);
    int ret =  (int) result;
  }

  /**
   * Returns the {@code b} to the {@code k}th power, provided it does not overflow.
   *
   * <p>{@link #pow} may be faster, but does not check for overflow.
   *
   * @throws ArithmeticException if {@code b} to the {@code k}th power overflows in signed
   *         {@code int} arithmetic
   */
  public  void checkedPow(int b, int k) {
  	MathPreconditions.checkNonNegative("exponent", k);
    int __x = (b) ;
      if (__x ==  0) {
        int ret =  (k == 0) ? 1 : 0;
      } else if (__x ==  1) {
         int ret =  1;
      } else if (__x ==  (-1)) {
         int ret =  ((k & 1) == 0) ? 1 : -1;
      } else if (__x ==  2) {
      	MathPreconditions.checkNoOverflow(k < Integer.SIZE - 1);
         int ret =  1 << k;
      } else if (__x ==  (-2)) {
      	MathPreconditions.checkNoOverflow(k < Integer.SIZE);
         int ret =  ((k & 1) == 0) ? 1 << k : -1 << k;
    }
    int accum = 1;
    while (true) {
       __x = (k) ;
        if (__x ==  0) {
          int ret =  accum;
        } else if (__x ==  1) {
          checkedMultiply(accum, b);
        } else {
          if ((k & 1) != 0) {
            checkedMultiply(accum, b);
          }
          k >>= 1;
          if (k > 0) {
          	MathPreconditions.checkNoOverflow(-FLOOR_SQRT_MAX_INT <= b & b <= FLOOR_SQRT_MAX_INT);
            b *= b;
          }
      }
    }
  }

   final int FLOOR_SQRT_MAX_INT = 46340;

  /**
   * Returns {@code n!}, that is, the product of the first {@code n} positive
   * integers, {@code 1} if {@code n == 0}, or {@link Integer#MAX_VALUE} if the
   * result does not fit in a {@code int}.
   *
   * @throws IllegalArgumentException if {@code n < 0}
   */
  public  void factorial(int n) {
  	MathPreconditions.checkNonNegative("n", n);
    int ret =  (n < FACTORIALS.length) ? FACTORIALS[n] : Integer.MAX_VALUE;
  }

   final int[] FACTORIALS = {
      1,
      1,
      1 * 2,
      1 * 2 * 3,
      1 * 2 * 3 * 4,
      1 * 2 * 3 * 4 * 5,
      1 * 2 * 3 * 4 * 5 * 6,
      1 * 2 * 3 * 4 * 5 * 6 * 7,
      1 * 2 * 3 * 4 * 5 * 6 * 7 * 8,
      1 * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9,
      1 * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10,
      1 * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11,
      1 * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11 * 12};

  /**
   * Returns {@code n} choose {@code k}, also known as the binomial coefficient of {@code n} and
   * {@code k}, or {@link Integer#MAX_VALUE} if the result does not fit in an {@code int}.
   *
   * @throws IllegalArgumentException if {@code n < 0}, {@code k < 0} or {@code k > n}
   */
  public  void binomial(int n, int k) {
  	MathPreconditions.checkNonNegative("n", n);
  	MathPreconditions.checkNonNegative("k", k);
  	MathPreconditions.checkArgument(k <= n);
    if (k > (n >> 1)) {
      k = n - k;
    }
    if (k >= BIGGEST_BINOMIALS.length || n > BIGGEST_BINOMIALS[k]) {
      int ret =  Integer.MAX_VALUE;
    }
    int __x = (k) ;
      if (__x ==  0) {
        int ret =  1;
      } else if (__x ==  1) {
         int ret =  n;
      } else {
        long result = 1;
        for (int i = 0; i < k; i++) {
          result *= n - i;
          result /= i + 1;
        }
        int ret =  (int) result;
    }
  }

  // binomial(BIGGEST_BINOMIALS[k], k) fits in an int, but not binomial(BIGGEST_BINOMIALS[k]+1,k).
   int[] BIGGEST_BINOMIALS = {
    Integer.MAX_VALUE,
    Integer.MAX_VALUE,
    65536,
    2345,
    477,
    193,
    110,
    75,
    58,
    49,
    43,
    39,
    37,
    35,
    34,
    34,
    33
  };

}
