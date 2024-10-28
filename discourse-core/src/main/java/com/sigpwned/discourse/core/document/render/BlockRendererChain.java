package com.sigpwned.discourse.core.document.render;

import java.io.IOException;
import java.io.PrintStream;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.document.Block;
import com.sigpwned.discourse.core.document.Document;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class BlockRendererChain extends Chain<BlockRenderer> implements BlockRenderer {

  @Override
  public boolean renderBlock(Document document, DocumentSection section, Block block,
      PrintStream out, InvocationContext context) throws IOException {
    for (BlockRenderer renderer : this)
      if (renderer.renderBlock(document, section, block, out, context))
        return true;
    return false;
  }
}
