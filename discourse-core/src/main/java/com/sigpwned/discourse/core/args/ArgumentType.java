package com.sigpwned.discourse.core.args;

import static java.util.Objects.requireNonNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ArgumentType {
  private static final Map<String, ArgumentType> VALUES = new HashMap<>();

  public static synchronized ArgumentType valueOf(String name) {
    return VALUES.computeIfAbsent(name, ArgumentType::new);
  }

  /**
   * A flag argument type. A boolean-valued parameter whose value is given by the flag's presence or
   * absence on the command line. For example, {@code --help} or {@code -h}.
   */
  public static final ArgumentType FLAG = ArgumentType.valueOf("flag");

  /**
   * A value argument type. A parameter that takes a value. For example, {@code --output file.txt}.
   */
  public static final ArgumentType OPTION = ArgumentType.valueOf("value");

  /**
   * A positional argument type. A parameter whose value is given by its position on the command
   * line. For example, {@code command arg1 arg2}.
   */
  public static final ArgumentType POSITIONAL = ArgumentType.valueOf("positional");

  private final String name;

  private ArgumentType(String name) {
    this.name = requireNonNull(name);
  }

  public String name() {
    return name;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ArgumentType other = (ArgumentType) obj;
    return Objects.equals(name, other.name);
  }
}
