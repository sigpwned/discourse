package com.sigpwned.discourse.core.format.help;

import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.command.planned.PlannedCommand;
import com.sigpwned.discourse.core.document.Document;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class CommandHelpDocumentComposerChain extends Chain<CommandHelpDocumentComposer>
    implements CommandHelpDocumentComposer {

  @Override
  public void composeCommandHelpDocument(PlannedCommand<?> command, Document document,
      InvocationContext context) {
    for (CommandHelpDocumentComposer composer : this)
      composer.composeCommandHelpDocument(command, document, context);
  }
}
