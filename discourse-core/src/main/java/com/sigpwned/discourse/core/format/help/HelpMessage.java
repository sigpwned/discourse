package com.sigpwned.discourse.core.format.help;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import java.util.List;
import java.util.Objects;

public class HelpMessage {
  public static HelpMessage of(String message) {
    return new HelpMessage(message);
  }

  public static HelpMessage of(String message, List<Object> arguments) {
    return new HelpMessage(message, arguments);
  }

  private final String message;
  private final List<Object> arguments;

  public HelpMessage(String message) {
    this(message, List.of());
  }

  public HelpMessage(String message, List<Object> arguments) {
    this.message = requireNonNull(message);
    this.arguments = unmodifiableList(arguments);
  }

  public String getMessage() {
    return message;
  }

  public List<Object> getArguments() {
    return arguments;
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, arguments);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    HelpMessage other = (HelpMessage) obj;
    return Objects.equals(message, other.message) && Objects.equals(arguments, other.arguments);
  }

  @Override
  public String toString() {
    return "HelpMessage [message=" + message + ", arguments=" + arguments + "]";
  }
}
