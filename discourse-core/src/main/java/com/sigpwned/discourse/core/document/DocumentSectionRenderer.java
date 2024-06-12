package com.sigpwned.discourse.core.document;

import java.io.IOException;
import java.io.PrintStream;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public interface DocumentSectionRenderer {
  public boolean renderDocumentSection(Doc document, DocumentSection section, PrintStream out,
      InvocationContext context) throws IOException;
}
