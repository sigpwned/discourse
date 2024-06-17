package com.sigpwned.discourse.core.dialect.windows.format;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.coordinate.PositionalCoordinate;
import com.sigpwned.discourse.core.command.planned.PlannedCommandProperty;
import com.sigpwned.discourse.core.dialect.ArgFormatter;
import com.sigpwned.discourse.core.dialect.windows.WindowsDialectElement;

public class PositionalWindowsArgFormatter implements ArgFormatter, WindowsDialectElement {
  @Override
  public Optional<List<List<String>>> formatArg(PlannedCommandProperty property,
      Coordinate coordinate) {
    if (!(coordinate instanceof PositionalCoordinate positionalCoordinate))
      return Optional.empty();

    return Optional.of(List.of(List.of("<" + property.getName() + ">")));
  }
}
