package com.sigpwned.discourse.core.format.help;

import java.util.Optional;
import com.sigpwned.discourse.core.command.planned.PlannedCommand;
import com.sigpwned.discourse.core.format.help.model.synopsis.CommandSynopsisEntry;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public interface SynopsisEntryFormatter {
  public Optional<String> formatSynopsisEntry(PlannedCommand<?> command, CommandSynopsisEntry entry,
      InvocationContext context);
}
