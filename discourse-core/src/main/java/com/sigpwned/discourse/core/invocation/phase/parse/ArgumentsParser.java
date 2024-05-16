package com.sigpwned.discourse.core.invocation.phase.parse;

import java.util.List;
import java.util.Map;

public interface ArgumentsParser {

  /**
   * <p>
   * Parses the given arguments using the given vocabulary, returning the result as a list of
   * coordinate pairs.
   * </p>
   * 
   * <p>
   * The vocabulary is a map of switch names to their types. The parser will use this to determine
   * how to parse the arguments. The specific details of how the vocabulary is interpreted are
   * determined by the implementation.
   * </p>
   * 
   * <p>
   * The arguments are a list of strings. The parser will consume the arguments in order, and return
   * a list of key-value pairs representing the parsed arguments. The keys will be a description of
   * the location of the value within the arguments, and the values will be the parsed values as
   * they appeared in the user arguments.
   * </p>
   *
   * @param vocabulary the vocabulary to use
   * @param args the arguments to parse
   * @return the parsed arguments
   */
  public List<Map.Entry<String, String>> parse(Map<String, String> vocabulary, List<String> args);
}
