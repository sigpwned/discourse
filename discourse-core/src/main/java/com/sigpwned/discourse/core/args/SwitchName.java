package com.sigpwned.discourse.core.args;

import static java.util.Objects.requireNonNull;
import java.util.Objects;

public class SwitchName {
  public static SwitchName fromString(String s) {
    return new SwitchName(s);
  }

  private final String text;

  public SwitchName(String text) {
    this.text = requireNonNull(text);
    if (text.isEmpty())
      throw new IllegalArgumentException("switch name must not be empty");
  }

  public String getText() {
    return text;
  }

  public int length() {
    return text.length();
  }

  @Override
  public int hashCode() {
    return Objects.hash(text);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SwitchName other = (SwitchName) obj;
    return Objects.equals(text, other.text);
  }

  @Override
  public String toString() {
    return text;
  }
}
