/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 - 2024 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.discourse.core.invocation.phase.parse;

import java.util.List;
import java.util.Map;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.Token;

public interface ParsePhaseListener {
  // PREPROCESS COORDINATES STEP //////////////////////////////////////////////////////////////////
  default void beforeParsePhasePreprocessCoordinatesStep(Map<Coordinate, String> coordinates) {}

  default void afterParsePhasePreprocessCoordinatesStep(Map<Coordinate, String> coordinates,
      Map<Coordinate, String> preprocessedCoordinates) {}

  default void catchParsePhasePreprocessCoordinatesStep(Throwable problem) {}

  default void finallyParsePhasePreprocessCoordinatesStep() {}

  // PREPROCESS ARGS STEP /////////////////////////////////////////////////////////////////////////
  default void beforeParsePhasePreprocessArgsStep(List<String> args) {}

  default void afterParsePhasePreprocessArgsStep(List<String> args,
      List<String> preprocessedArgs) {}

  default void catchParsePhasePreprocessArgsStep(Throwable problem) {}

  default void finallyParsePhasePreprocessArgsStep() {}

  // TOKENIZE STEP ////////////////////////////////////////////////////////////////////////////////
  default void beforeParsePhaseTokenizeStep(List<String> preprocessedArgs) {}

  default void afterParsePhaseTokenizeStep(List<String> preprocessedArgs, List<Token> tokens) {}

  default void catchParsePhaseTokenizeStep(Throwable problem) {}

  default void finallyParsePhaseTokenizeStep() {}

  // PREPROCESS TOKENS STEP ///////////////////////////////////////////////////////////////////////
  default void beforeParsePhasePreprocessTokensStep(List<Token> tokens) {}

  default void afterParsePhasePreprocessTokensStep(List<Token> tokens,
      List<Token> preprocessedTokens) {}

  default void catchParsePhasePreprocessTokensStep(Throwable problem) {}

  default void finallyParsePhasePreprocessTokensStep() {}

  // PARSE STEP ///////////////////////////////////////////////////////////////////////////////////
  default void beforeParsePhaseParseStep(List<Token> preprocessedTokens) {}

  default void afterParsePhaseParseStep(List<Token> preprocessedTokens,
      List<Map.Entry<Coordinate, String>> parsedArgs) {}

  default void catchParsePhaseParseStep(Throwable problem) {}

  default void finallyParsePhaseParseStep() {}

  // ATTRIBUTE STEP ///////////////////////////////////////////////////////////////////////////////
  default void beforeParsePhaseAttributeStep(Map<Coordinate, String> naming,
      List<Map.Entry<Coordinate, String>> parsedArgs) {}

  default void afterParsePhaseAttributeStep(Map<Coordinate, String> naming,
      List<Map.Entry<Coordinate, String>> parsedArgs,
      List<Map.Entry<String, String>> attributedArgs) {}

  default void catchParsePhaseAttributeStep(Throwable problem) {}

  default void finallyParsePhaseAttributeStep() {}
}