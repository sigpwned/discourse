package com.sigpwned.discourse.core.document.render;

import java.io.IOException;
import java.io.PrintStream;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.document.Block;
import com.sigpwned.discourse.core.document.Document;
import com.sigpwned.discourse.core.document.Node;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class NodeRendererChain extends Chain<NodeRenderer> implements NodeRenderer {
  @Override
  public boolean renderNode(Document document, DocumentSection section, Block block,
      Node node, PrintStream output, InvocationContext context) throws IOException {
    for (NodeRenderer renderer : this)
      if (renderer.renderNode(document, section, null, node, output, context))
        return true;
    return false;
  }
}
