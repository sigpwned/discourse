package com.sigpwned.discourse.core.format.help.synopsis.entry.format;

import java.util.Optional;
import com.sigpwned.discourse.core.command.PlannedCommand;
import com.sigpwned.discourse.core.format.help.synopsis.entry.DiscriminatorSynopsisEntry;
import com.sigpwned.discourse.core.format.help.synopsis.entry.SynopsisEntry;
import com.sigpwned.discourse.core.format.help.synopsis.entry.SynopsisEntryFormatter;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class DiscriminatorSynopsisEntryFormatter implements SynopsisEntryFormatter {
  @Override
  public Optional<String> formatSynopsisEntry(PlannedCommand<?> command, SynopsisEntry entry,
      InvocationContext context) {
    if (!(entry instanceof DiscriminatorSynopsisEntry discriminatorEntry))
      return Optional.empty();
    return Optional.of(command.getParents().get(discriminatorEntry.getIndex()).getDiscriminator());
  }
}
