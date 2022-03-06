package com.sigpwned.discourse.core.token;

import java.util.Objects;
import com.sigpwned.discourse.core.ArgumentToken;
import com.sigpwned.discourse.core.coordinate.name.switches.ShortSwitchNameCoordinate;

public class ShortNameArgumentToken extends ArgumentToken {
  private final String shortName;

  public ShortNameArgumentToken(String text, String shortName) {
    super(Type.SHORT_NAME, text);
    if (shortName == null)
      throw new NullPointerException();
    if (!ShortSwitchNameCoordinate.PATTERN.matcher(shortName).matches())
      throw new IllegalArgumentException("invalid short name: " + shortName);
    this.shortName = shortName;
  }

  /**
   * @return the shortName
   */
  public String getShortName() {
    return shortName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(shortName);
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
    ShortNameArgumentToken other = (ShortNameArgumentToken) obj;
    return Objects.equals(shortName, other.shortName);
  }
}
