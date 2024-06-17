package com.sigpwned.discourse.core.dialect;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.command.planned.PlannedCommandProperty;

public class ArgFormatterChain extends Chain<ArgFormatter> implements ArgFormatter {
  @Override
  public Optional<List<List<String>>> formatArg(PlannedCommandProperty property,
      Coordinate coordinate) {
    List<List<String>> result = new ArrayList<>();
    for (ArgFormatter formatter : this) {
      Optional<List<List<String>>> formatted = formatter.formatArg(property, coordinate);
      if (formatted.isPresent()) {
        result.addAll(formatted.orElseThrow());
      }
    }
    return result.isEmpty() ? Optional.empty() : Optional.of(result);
  }
}
