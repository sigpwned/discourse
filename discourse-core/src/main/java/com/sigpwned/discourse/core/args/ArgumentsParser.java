package com.sigpwned.discourse.core.args;

import java.util.List;
import java.util.Map;

public interface ArgumentsParser {

  /**
   * <p>
   * Parses the arguments.
   * </p>
   *
   * <p>
   * The vocabulary is a map of argument names to their types. The parser should use this to
   * determine how to parse the arguments. The specific details of how the vocabulary is interpreted
   * are determined by the implementation.
   * </p>
   *
   * <p>
   * The arguments are a list of strings. The parser should consume the arguments in order, and
   * return a list of key-value pairs representing the parsed arguments. The keys should be a
   * description of the location of the value within the arguments, and the values should be the
   * parsed values as they appeared in the user arguments.
   * </p>
   *
   * @param vocabulary the vocabulary
   * @param args       the user arguments
   * @return the parsed arguments
   * @see com.sigpwned.discourse.core.args.impl.UnixStyleArgumentsParser
   */
  public List<Map.Entry<String, String>> parse(Map<String, String> vocabulary, List<String> args);
}
