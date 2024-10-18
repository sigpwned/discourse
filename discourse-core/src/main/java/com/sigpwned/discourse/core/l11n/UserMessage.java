package com.sigpwned.discourse.core.l11n;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import java.util.List;
import java.util.Objects;

/**
 * A user-facing message. This is the basis for all message translation in the framework.
 */
public class UserMessage {
  public static UserMessage of(String message) {
    return new UserMessage(message);
  }

  public static UserMessage of(String message, List<Object> arguments) {
    return new UserMessage(message, arguments);
  }

  private final String message;
  private final List<Object> arguments;

  public UserMessage(String message) {
    this(message, List.of());
  }

  public UserMessage(String message, List<Object> arguments) {
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
    UserMessage other = (UserMessage) obj;
    return Objects.equals(message, other.message) && Objects.equals(arguments, other.arguments);
  }

  @Override
  public String toString() {
    return "UserMessage [message=" + message + ", arguments=" + arguments + "]";
  }
}
