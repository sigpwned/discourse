package com.sigpwned.discourse.core.format.help;

import com.sigpwned.discourse.core.command.planned.PlannedCommand;
import com.sigpwned.discourse.core.format.help.model.CommandSynopsis;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public interface CommandSynopsisComposer {
  public void composeSynopsis(PlannedCommand<?> command, CommandSynopsis synopsis,
      InvocationContext context);
}
