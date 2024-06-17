package com.sigpwned.discourse.core.format.help.render.text.synopsis;

import java.util.Optional;
import com.sigpwned.discourse.core.format.help.model.synopsis.OptionsPlaceholderCommandSynopsisEntry;
import com.sigpwned.discourse.core.command.planned.PlannedCommand;
import com.sigpwned.discourse.core.format.help.SynopsisEntryFormatter;
import com.sigpwned.discourse.core.format.help.model.synopsis.CommandSynopsisEntry;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class OptionsPlaceholderSynopsisEntryTextFormatter implements SynopsisEntryFormatter {
  @Override
  public Optional<String> formatSynopsisEntry(PlannedCommand<?> command, CommandSynopsisEntry entry,
      InvocationContext context) {
    if (!(entry instanceof OptionsPlaceholderCommandSynopsisEntry))
      return Optional.empty();
    // TODO localizeOptionsPlaceholderSynopsisEntryFormatter
    return Optional.of("[options]");
  }
}
