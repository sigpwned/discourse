/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
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
package com.sigpwned.discourse.core.util;

import static java.util.Arrays.asList;
import java.util.List;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public final class Text {
  private Text() {}

  private static final Pattern WHITESPACE = Pattern.compile("\\s+");

  /**
   * Apply word wrap to the given line width
   */
  public static String wrap(String s, int width) {
    if (s == null)
      throw new NullPointerException();
    if (width <= 0)
      throw new IllegalArgumentException("width must be positive");
    return wrap(s, lineNumber -> width);
  }

  /**
   * Apply word wrap to the given length, computed from each line number
   */
  public static String wrap(String s, IntUnaryOperator widthFunction) {
    return wrap(s, widthFunction, line -> line);
  }

  /**
   * Apply word wrap to the given length, computed from each line number
   */
  public static String wrap(String s, IntUnaryOperator widthFunction,
      UnaryOperator<String> indentFunction) {
    if (s == null)
      throw new NullPointerException();
    if (widthFunction == null)
      throw new NullPointerException();
    if (indentFunction == null)
      throw new NullPointerException();

    if (s.isBlank())
      return "";

    List<String> tokens = asList(WHITESPACE.split(s.strip()));

    int lineNumber = 0;
    Integer width = null;
    StringBuilder line = new StringBuilder();
    StringBuilder result = new StringBuilder();
    for (String token : tokens) {
      if (width == null) {
        width = widthFunction.applyAsInt(lineNumber);
        if (width <= 0)
          throw new IllegalArgumentException("width must be positive");
      }

      if (line.length() == 0) {
        line.append(token);
      } else {
        if (line.length() + token.length() + 1 <= width) {
          line.append(" ").append(token);
        } else {
          result.append(indentFunction.apply(line.toString())).append("\n");
          line.setLength(0);
          line.append(token);
          width = null;
          lineNumber = lineNumber + 1;
        }
      }
    }

    if (line.length() != 0)
      result.append(indentFunction.apply(line.toString()));

    return result.toString();
  }

  /**
   * Return the given string, repeated times times
   */
  public static String times(String s, int times) {
    if (s == null)
      throw new NullPointerException();
    if (times < 0)
      throw new IllegalArgumentException("times must be non-negative");
    if (s.isEmpty())
      return "";
    if (times == 0)
      return "";

    // This is not the most efficient algorithm. It's good enough.
    StringBuilder result = new StringBuilder();
    for (int i = 1; i <= times; i++)
      result.append(s);

    return result.toString();
  }
}
