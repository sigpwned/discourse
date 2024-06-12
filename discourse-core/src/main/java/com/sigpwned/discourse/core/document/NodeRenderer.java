package com.sigpwned.discourse.core.document;

import java.io.IOException;
import java.io.PrintStream;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public interface NodeRenderer {
  public boolean renderNode(Doc document, DocumentSection section, Node node, PrintStream output,
      InvocationContext context) throws IOException;
}
