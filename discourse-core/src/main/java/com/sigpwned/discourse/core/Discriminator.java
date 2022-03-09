package com.sigpwned.discourse.core;

import static java.lang.String.format;
import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Pattern;
import com.sigpwned.discourse.core.util.Generated;

public class Discriminator implements Comparable<Discriminator> {
  public static final Pattern PATTERN=Pattern.compile("[a-zA-Z0-9][-a-zA-Z0-9_]*");
  
  public static Discriminator fromString(String s) {
    return new Discriminator(s);
  }
  
  private final String text;

  public Discriminator(String text) {
    if(!PATTERN.matcher(text).matches())
      throw new IllegalArgumentException(format("invalid discriminator '%s'", text));
    this.text = text;
  }

  /**
   * @return the text
   */
  public String getText() {
    return text;
  }

  @Override
  @Generated
  public int hashCode() {
    return Objects.hash(text);
  }

  @Override
  @Generated
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
    return getText();
  }
  
  public static final Comparator<Discriminator> COMPARATOR=Comparator.comparing(Discriminator::toString);

  @Override
  public int compareTo(Discriminator that) {
    return COMPARATOR.compare(this, that);
  }
}
