package com.sigpwned.discourse.core.dialect.unix.format;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.command.planned.PlannedCommandProperty;
import com.sigpwned.discourse.core.dialect.ArgFormatter;
import com.sigpwned.discourse.core.dialect.unix.UnixDialectElement;
import com.sigpwned.discourse.core.module.parameter.flag.FlagCoordinate;

public class LongFlagUnixArgFormatter implements ArgFormatter, UnixDialectElement {
  @Override
  public Optional<List<List<String>>> formatArg(PlannedCommandProperty property,
      Coordinate coordinate) {
    if (!(coordinate instanceof FlagCoordinate flag))
      return Optional.empty();

    if (flag.getName().length() == 1)
      return Optional.empty();

    return Optional.of(List.of(List.of(LONG_NAME_PREFIX + flag.getName())));
  }
}
