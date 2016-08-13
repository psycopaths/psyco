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
package gov.nasa.jpf.psyco.search.transitionSystem.helperVisitors;

public class TransitionEncoding {
  public static final char numericBooleanExpression = 'A';
  public static final char negation = 'N';
  public static final char constant = 'C';
  public static final char variable = 'V';
  public static final char numericComperator = 'F';
  public static final char numericOperator = 'G';
  public static final char numericCompund = 'D';
  public static final char propositionalCompound = 'P';
  public static final char logicalOpertaor = 'L';
  public static final char unaryMinus = 'U';
  public static final char bitVector = 'B';
  public static final char bitVectorOperator = 'O';
  public static final char bitVectorNegation = 'Q';
  public static final char effect ='E';
  public static final char guard = 'H';
  public static final char transitionBody = 'T';
  public static final char valuation = 'I';
  public static final char valuationEntry = 'W';
  public static final char error = 'K';
  public static final char okTransition = 'J';
  public static final char errorTransition = 'M';
}
