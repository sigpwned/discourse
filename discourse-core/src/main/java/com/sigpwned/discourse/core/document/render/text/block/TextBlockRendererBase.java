package com.sigpwned.discourse.core.document.render.text.block;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.document.Block;
import com.sigpwned.discourse.core.document.Document;
import com.sigpwned.discourse.core.document.Node;
import com.sigpwned.discourse.core.document.render.BlockRenderer;
import com.sigpwned.discourse.core.document.render.DocumentSection;
import com.sigpwned.discourse.core.document.render.NodeRenderer;
import com.sigpwned.discourse.core.document.render.text.TextViewport;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.util.Text;

public abstract class TextBlockRendererBase implements BlockRenderer {
  @Override
  public boolean renderBlock(Document document, DocumentSection section, Block block,
      PrintStream out, InvocationContext context) throws IOException {
    Node text = getText(block).orElse(null);
    if (text == null)
      return false;

    NodeRenderer nodeRenderer = context.get(NodeRenderer.class).orElse(null);
    if (nodeRenderer == null)
      return false;

    TextViewport viewport = context.get(TextViewport.class).orElse(null);
    int width = viewport != null ? viewport.getWidth() : TextViewport.DEFAULT_WIDTH;

    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    try (PrintStream bufout = new PrintStream(buf, false, StandardCharsets.UTF_8)) {
      if (!nodeRenderer.renderNode(document, section, null, text, bufout, context))
        return false;
    }

    String rawText = buf.toString(StandardCharsets.UTF_8);

    // Do whatever transformations we want to the text, e.g., uppercase...
    String transformedText = transform(rawText);

    List<String> wrappedText = Text.wrap(transformedText, width);

    out.println(String.join(System.lineSeparator(), wrappedText));
    out.println();

    return true;
  }

  protected abstract Optional<Node> getText(Block section);

  /**
   * Extension hook. Override this method to transform the text before rendering it, e.g.,
   * converting to uppercase.
   * 
   * @param text
   * @return
   */
  protected String transform(String text) {
    return text;
  }
}
