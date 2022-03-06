package com.sigpwned.discourse.core.token;

import java.util.Objects;
import com.sigpwned.discourse.core.ArgumentToken;
import com.sigpwned.discourse.core.coordinate.name.switches.LongSwitchNameCoordinate;

public class LongNameArgumentToken extends ArgumentToken {
  private final String longName;

  public LongNameArgumentToken(String text, String longName) {
    super(Type.LONG_NAME, text);
    if (longName == null)
      throw new NullPointerException();
    if (!LongSwitchNameCoordinate.PATTERN.matcher(longName).matches())
      throw new IllegalArgumentException("invalid long name: " + longName);
    this.longName = longName;
  }

  /**
   * @return the shortName
   */
  public String getLongName() {
    return longName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(longName);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    LongNameArgumentToken other = (LongNameArgumentToken) obj;
    return Objects.equals(longName, other.longName);
  }
}
