package com.sigpwned.discourse.core.document;

import java.io.IOException;
import java.io.PrintStream;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public interface DocumentRenderer {
  public void renderDocument(Doc document, PrintStream out, InvocationContext context)
      throws IOException;
}
