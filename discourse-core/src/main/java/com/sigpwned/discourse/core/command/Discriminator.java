package com.sigpwned.discourse.core.command;

import static java.util.Objects.requireNonNull;
import java.util.Objects;
import java.util.regex.Pattern;

public final class Discriminator {
  public static Discriminator fromString(String text) {
    return of(text);
  }

  public static Discriminator of(String text) {
    return new Discriminator(text);
  }

  public static final Pattern PATTERN = Pattern.compile("[a-zA-Z0-9](?:[-._]?[a-zA-Z0-9])*");

  private final String text;

  public Discriminator(String text) {
    this.text = requireNonNull(text);
    if (!PATTERN.matcher(text).matches())
      throw new IllegalArgumentException("Invalid discriminator: " + text);
  }

  private String getText() {
    return text;
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
    Discriminator other = (Discriminator) obj;
    return Objects.equals(text, other.text);
  }

  @Override
  public String toString() {
    return text;
  }
}
