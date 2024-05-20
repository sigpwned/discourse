package com.sigpwned.discourse.core.module.parameter.flag;

import static java.util.Objects.requireNonNull;
import java.util.Objects;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.SwitchName;

public class FlagCoordinate extends Coordinate {
  private final SwitchName name;

  public FlagCoordinate(SwitchName name) {
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
    FlagCoordinate other = (FlagCoordinate) obj;
    return Objects.equals(name, other.name);
  }

  @Override
  public String toString() {
    return "FlagCoordinate[" + name + "]";
  }
}
