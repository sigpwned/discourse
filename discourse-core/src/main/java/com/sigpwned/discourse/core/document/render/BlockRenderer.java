package com.sigpwned.discourse.core.document.render;

import java.io.IOException;
import java.io.PrintStream;
import com.sigpwned.discourse.core.document.Block;
import com.sigpwned.discourse.core.document.Document;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public interface BlockRenderer {
  public boolean renderBlock(Document document, DocumentSection section, Block block,
      PrintStream out, InvocationContext context) throws IOException;
}
