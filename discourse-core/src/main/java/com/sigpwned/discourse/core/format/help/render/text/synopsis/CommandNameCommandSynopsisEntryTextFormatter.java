package com.sigpwned.discourse.core.format.help.render.text.synopsis;

import java.util.Optional;
import com.sigpwned.discourse.core.command.planned.PlannedCommand;
import com.sigpwned.discourse.core.format.help.SynopsisEntryFormatter;
import com.sigpwned.discourse.core.format.help.model.synopsis.CommandNameCommandSynopsisEntry;
import com.sigpwned.discourse.core.format.help.model.synopsis.CommandSynopsisEntry;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class CommandNameCommandSynopsisEntryTextFormatter implements SynopsisEntryFormatter {
  @Override
  public Optional<String> formatSynopsisEntry(PlannedCommand<?> command, CommandSynopsisEntry entry,
      InvocationContext context) {
    if (!(entry instanceof CommandNameCommandSynopsisEntry))
      return Optional.empty();
    return Optional.of(command.getName().orElseThrow());
  }
}
