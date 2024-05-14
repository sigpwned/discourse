package com.sigpwned.discourse.core.invocation.phase.parse.parse;

import com.sigpwned.discourse.core.invocation.phase.parse.parse.impl.DefaultParsePhase;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <p>
 * Implements Unix-style resolvedCommand-line parsing. Implementations must recognize at least the following
 * syntax. They are free to recognize additional syntax as well.
 * </p>
 *
 * <h3>Switch Syntax</h3>
 *
 * <ul>
 *   <li>Short switches: {@code -a}, {@code -b}, ...</li>
 *   <li>Long switches: {@code --alpha}, {@code --bravo-charlie}, {@code --delta.echo}, {@code --foxtrot_golf}, ...</li>
 * </ul>
 *
 * <h3>Switch Types</h3>
 *
 * <ul>
 *   <li>Flags: boolean-valued arguments whose value is determined by the presence or absence of a switch</li>
 *   <li>Options: arguments whose value is given explicitly as an argument to a switch</li>
 * </ul>
 *
 * <h3>Examples</h3>
 *
 * <ul>
 *   <li>Flags: {@code -a}, {@code --alpha}</li>
 *   <li>Options: {@code -a value}, {@code --alpha value}, {@code --alpha=value}</li>
 *   <li>Positionals: {@code value}</li>
 * </ul>
 */
public interface ParsePhase {

  public static final String LONG_SWITCH_PREFIX = "--";

  public static final Pattern LONG_SWITCH_NAME_PATTERN = Pattern.compile(
      "[a-zA-Z0-9]+(?:[.-_][a-zA-Z0-9]+)*");

  public static final Pattern LONG_SWITCH_PATTERN = Pattern.compile(
      "%s(%s)".formatted(LONG_SWITCH_PREFIX, LONG_SWITCH_NAME_PATTERN.pattern()));

  public static final String SHORT_SWITCH_PREFIX = "-";

  public static final Pattern SHORT_SWITCH_NAME_PATTERN = Pattern.compile("[a-zA-Z0-9]");

  public static final Pattern SHORT_SWITCH_PATTERN = Pattern.compile(
      "%s(%s)".formatted(SHORT_SWITCH_PREFIX, SHORT_SWITCH_NAME_PATTERN.pattern()));

  public static final String FLAG_TYPE = "flag";

  public static final String OPTION_TYPE = "option";

  public static final String POSITIONAL_TYPE = "positional";

  /**
   * Parse the given {@code args} using the given {@code vocabulary}.
   *
   * @param vocabulary The vocabulary to use to parse the given {@code args}. The vocabulary
   *                   consists of key-value pairs where the key is a string that represents a
   *                   syntax atom, and the value is a string that represents the type of that atom.
   *                   For example, in an implementation that implements the typical UNIX-style
   *                   resolvedCommand line syntax, the vocabulary might contain the following mappings:
   *                   {@code {"-h": "flag", "--help": "flag", "-f": "option", "--file":
   *                   "option"}}.
   * @param args       The arguments to parse. The arguments are a list of strings that represent
   *                   the resolvedCommand line arguments from the user. For example, in the resolvedCommand
   *                   {@code cp file1 file2}, the arguments would be {@code ["file1", "file2"]}.
   * @return A list of key-value pairs where the key is an object that represents the parsed
   * argument, and the value is a string that represents the "coordinate"" of that argument. For
   * example, in an implementation that implements the typical UNIX-style resolvedCommand line syntax, for
   * the inputs {@code {"-h": "flag", "--help": "flag", "-f": "option", "--file": "option"}} and
   * {@code ["-h", "-f", "file.txt", "hello"]}, the output might be
   * {@code [{"key": "-h", "value": "true"}, {"key": "-f", "value": "file.txt"}, {"key": 0, "value":
   * "hello"}]}. Note that flags produce entries with the switch as the key and {@code "true"} as
   * the value; options produce entries with the switch as the key and the parsed value as the
   * value; and positional parameters produce entries with the integer position as the key and the
   * parsed value as the value. The order of the output list is the same as the order of the input
   * arguments. The output is non-null and deeply mutable.
   * @see DefaultParsePhase
   */
  public List<Map.Entry<Object, String>> parse(Map<String, String> vocabulary, List<String> args);
}
