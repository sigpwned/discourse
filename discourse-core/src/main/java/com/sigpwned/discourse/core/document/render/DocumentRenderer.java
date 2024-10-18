package com.sigpwned.discourse.core.document.render;

import java.io.IOException;
import java.io.PrintStream;
import com.sigpwned.discourse.core.document.Document;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public interface DocumentRenderer {
  public void renderDocument(Document document, PrintStream out, InvocationContext context)
      throws IOException;
}
