package com.sigpwned.discourse.core.format.help.synopsis.entry.format;

import java.util.Optional;
import com.sigpwned.discourse.core.command.PlannedCommand;
import com.sigpwned.discourse.core.command.PlannedCommandProperty;
import com.sigpwned.discourse.core.format.help.synopsis.entry.PositionalArgumentSynopsisEntry;
import com.sigpwned.discourse.core.format.help.synopsis.entry.SynopsisEntry;
import com.sigpwned.discourse.core.format.help.synopsis.entry.SynopsisEntryFormatter;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class PositionalArgumentSynopsisEntryFormatter implements SynopsisEntryFormatter {
  @Override
  public Optional<String> formatSynopsisEntry(PlannedCommand<?> command, SynopsisEntry entry,
      InvocationContext context) {
    if (!(entry instanceof PositionalArgumentSynopsisEntry positionalEntry))
      return Optional.empty();

    PlannedCommandProperty property = command.getProperties().stream()
        .filter(p -> p.getCoordinates().contains(positionalEntry.getCoordinate())).findFirst()
        .orElseThrow();

    String open, close;
    if (property.isRequired()) {
      open = "<";
      close = ">";
    } else {
      open = "[";
      close = "]";
    }

    String result = open + property.getName() + close;

    if (property.getSink().isCollection())
      result = open + result + " ..." + close;

    return Optional.of(result);
  }
}
