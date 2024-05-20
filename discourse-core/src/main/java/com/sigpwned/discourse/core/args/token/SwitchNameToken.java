package com.sigpwned.discourse.core.args.token;

import static java.util.Objects.requireNonNull;
import java.util.Objects;
import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.args.Token;

public class SwitchNameToken extends Token {
  private final SwitchName name;

  public SwitchNameToken(SwitchName name) {
    this.name = requireNonNull(name);
  }

  public SwitchName getName() {
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
    SwitchNameToken other = (SwitchNameToken) obj;
    return Objects.equals(name, other.name);
  }

  @Override
  public String toString() {
    return "SwitchNameToken[" + name + "]";
  }
}
