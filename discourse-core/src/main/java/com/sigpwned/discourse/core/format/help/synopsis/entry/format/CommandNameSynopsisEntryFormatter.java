package com.sigpwned.discourse.core.format.help.synopsis.entry.format;

import java.util.Optional;
import com.sigpwned.discourse.core.command.PlannedCommand;
import com.sigpwned.discourse.core.format.help.synopsis.entry.CommandNameSynopsisEntry;
import com.sigpwned.discourse.core.format.help.synopsis.entry.SynopsisEntry;
import com.sigpwned.discourse.core.format.help.synopsis.entry.SynopsisEntryFormatter;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class CommandNameSynopsisEntryFormatter implements SynopsisEntryFormatter {
  @Override
  public Optional<String> formatSynopsisEntry(PlannedCommand<?> command, SynopsisEntry entry,
      InvocationContext context) {
    if (!(entry instanceof CommandNameSynopsisEntry))
      return Optional.empty();
    return Optional.of(command.getName().orElseThrow());
  }
}
