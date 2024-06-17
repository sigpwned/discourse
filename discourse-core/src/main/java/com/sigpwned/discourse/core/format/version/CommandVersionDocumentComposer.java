package com.sigpwned.discourse.core.format.version;

import com.sigpwned.discourse.core.command.planned.PlannedCommand;
import com.sigpwned.discourse.core.document.Document;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public interface CommandVersionDocumentComposer {
  public Document composeCommandVersionDocument(PlannedCommand<?> command, Document sections,
      InvocationContext context);
}
