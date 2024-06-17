package com.sigpwned.discourse.core.format.help;

import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.command.planned.PlannedCommand;
import com.sigpwned.discourse.core.format.help.model.CommandSynopsis;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class CommandSynopsisComposerChain extends Chain<CommandSynopsisComposer>
    implements CommandSynopsisComposer {
  @Override
  public void composeSynopsis(PlannedCommand<?> command, CommandSynopsis synopsis,
      InvocationContext context) {
    for (CommandSynopsisComposer composer : this)
      composer.composeSynopsis(command, synopsis, context);
  }
}
