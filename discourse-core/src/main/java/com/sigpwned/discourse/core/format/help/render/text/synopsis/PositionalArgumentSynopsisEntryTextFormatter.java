package com.sigpwned.discourse.core.format.help.render.text.synopsis;

import java.util.Optional;
import com.sigpwned.discourse.core.format.help.model.synopsis.PositionalArgumentCommandSynopsisEntry;
import com.sigpwned.discourse.core.command.planned.PlannedCommand;
import com.sigpwned.discourse.core.command.planned.PlannedCommandProperty;
import com.sigpwned.discourse.core.format.help.SynopsisEntryFormatter;
import com.sigpwned.discourse.core.format.help.model.synopsis.CommandSynopsisEntry;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class PositionalArgumentSynopsisEntryTextFormatter implements SynopsisEntryFormatter {
  @Override
  public Optional<String> formatSynopsisEntry(PlannedCommand<?> command, CommandSynopsisEntry entry,
      InvocationContext context) {
    if (!(entry instanceof PositionalArgumentCommandSynopsisEntry positionalEntry))
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
