package com.sigpwned.discourse.core.format.help;

import com.sigpwned.discourse.core.command.planned.PlannedCommand;
import com.sigpwned.discourse.core.document.Document;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public interface CommandHelpDocumentComposer {
  public void composeCommandHelpDocument(PlannedCommand<?> command, Document document,
      InvocationContext context);
}
