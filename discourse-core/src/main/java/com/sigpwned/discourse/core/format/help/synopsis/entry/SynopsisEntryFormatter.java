package com.sigpwned.discourse.core.format.help.synopsis.entry;

import java.util.Optional;
import com.sigpwned.discourse.core.command.PlannedCommand;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public interface SynopsisEntryFormatter {
  public Optional<String> formatSynopsisEntry(PlannedCommand<?> command, SynopsisEntry entry,
      InvocationContext context);
}
