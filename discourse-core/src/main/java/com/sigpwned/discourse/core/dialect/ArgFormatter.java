package com.sigpwned.discourse.core.dialect;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.command.planned.PlannedCommandProperty;

public interface ArgFormatter {
  // TODO Should this return List<List<UserMessage>>?
  public Optional<List<List<String>>> formatArg(PlannedCommandProperty property,
      Coordinate coordinate);
}
