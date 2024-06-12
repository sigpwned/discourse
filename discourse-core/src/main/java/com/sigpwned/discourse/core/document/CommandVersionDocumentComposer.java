package com.sigpwned.discourse.core.document;

import java.util.List;
import com.sigpwned.discourse.core.command.PlannedCommand;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public interface CommandVersionDocumentComposer {
  public List<DocumentSection> composeCommandVersionDocument(PlannedCommand<?> command,
      List<DocumentSection> sections, InvocationContext context);
}
