package com.sigpwned.discourse.core.format.help;

import java.util.Optional;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.command.planned.PlannedCommand;
import com.sigpwned.discourse.core.format.help.model.synopsis.CommandSynopsisEntry;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class SynopsisEntryFormatterChain extends Chain<SynopsisEntryFormatter>
    implements SynopsisEntryFormatter {

  @Override
  public Optional<String> formatSynopsisEntry(PlannedCommand<?> command, CommandSynopsisEntry entry,
      InvocationContext context) {
    for (SynopsisEntryFormatter formatter : this) {
      Optional<String> result = formatter.formatSynopsisEntry(command, entry, context);
      if (result.isPresent())
        return result;
    }
    return Optional.empty();
  }
}
