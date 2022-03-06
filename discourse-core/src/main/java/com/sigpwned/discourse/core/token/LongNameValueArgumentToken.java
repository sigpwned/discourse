package com.sigpwned.discourse.core.token;

import java.util.Objects;
import com.sigpwned.discourse.core.ArgumentToken;
import com.sigpwned.discourse.core.coordinate.name.switches.LongSwitchNameCoordinate;

public class LongNameValueArgumentToken extends ArgumentToken {
  private final String longName;
  private final String value;

  public LongNameValueArgumentToken(String text, String longName, String value) {
    super(Type.LONG_NAME_VALUE, text);
    if (longName == null)
      throw new NullPointerException();
    if (!LongSwitchNameCoordinate.PATTERN.matcher(longName).matches())
      throw new IllegalArgumentException("invalid long name: " + longName);
    if (value == null)
      throw new NullPointerException();
    this.longName = longName;
    this.value = value;
  }

  /**
   * @return the shortName
   */
  public String getLongName() {
    return longName;
  }

  /**
   * @return the value
   */
  public String getValue() {
    return value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(longName, value);
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
    LongNameValueArgumentToken other = (LongNameValueArgumentToken) obj;
    return Objects.equals(longName, other.longName) && Objects.equals(value, other.value);
  }
}
