package com.sigpwned.discourse.core.dialect.unix.format;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.coordinate.PositionalCoordinate;
import com.sigpwned.discourse.core.command.planned.PlannedCommandProperty;
import com.sigpwned.discourse.core.dialect.ArgFormatter;
import com.sigpwned.discourse.core.dialect.unix.UnixDialectElement;

public class PositionalUnixArgFormatter implements ArgFormatter, UnixDialectElement {
  @Override
  public Optional<List<List<String>>> formatArg(PlannedCommandProperty property,
      Coordinate coordinate) {
    if (!(coordinate instanceof PositionalCoordinate positional))
      return Optional.empty();

    return Optional.of(List.of(List.of("<" + property.getName() + ">")));
  }
}
