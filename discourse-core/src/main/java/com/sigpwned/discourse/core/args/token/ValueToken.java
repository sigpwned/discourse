package com.sigpwned.discourse.core.args.token;

import static java.util.Objects.requireNonNull;
import java.util.Objects;
import com.sigpwned.discourse.core.args.Token;

public class ValueToken extends Token {
  private final String value;

  /**
   * Whether the value is syntactically attached to the option or not, e.g., in {@code --foo=bar},
   * the value {@code bar} is attached to the option {@code --foo}.
   */
  private final boolean attached;

  public ValueToken(String value, boolean attached) {
    this.value = requireNonNull(value);
    this.attached = attached;
  }

  public String getValue() {
    return value;
  }

  public boolean isAttached() {
    return attached;
  }

  @Override
  public int hashCode() {
    return Objects.hash(attached, value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ValueToken other = (ValueToken) obj;
    return attached == other.attached && Objects.equals(value, other.value);
  }

  @Override
  public String toString() {
    return "ValueToken[" + value + ", " + attached + "]";
  }
}
