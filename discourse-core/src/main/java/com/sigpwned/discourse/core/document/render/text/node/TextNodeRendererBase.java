package com.sigpwned.discourse.core.document.render.text.node;

import static java.util.Objects.requireNonNull;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Optional;
import com.sigpwned.discourse.core.document.Doc;
import com.sigpwned.discourse.core.document.DocumentSection;
import com.sigpwned.discourse.core.document.Node;
import com.sigpwned.discourse.core.document.NodeRenderer;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public abstract class TextNodeRendererBase implements NodeRenderer {
  protected static class Bookends {
    public static Bookends of(byte[] before, byte[] after) {
      return new Bookends(before, after);
    }

    private final byte[] before;
    private final byte[] after;

    public Bookends(byte[] before, byte[] after) {
      this.before = requireNonNull(before);
      this.after = requireNonNull(after);
    }

    /**
     * @return the before
     */
    public byte[] getBefore() {
      return before;
    }

    /**
     * @return the after
     */
    public byte[] getAfter() {
      return after;
    }

    @Override
    public int hashCode() {
      return Objects.hash(after, before);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Bookends other = (Bookends) obj;
      return Objects.equals(after, other.after) && Objects.equals(before, other.before);
    }

    @Override
    public String toString() {
      return "Bookends [before=" + before + ", after=" + after + "]";
    }
  }

  @Override
  public boolean renderNode(Doc document, DocumentSection section, Node node, PrintStream output,
      InvocationContext context) throws IOException {
    Bookends bookends = getBookends(node).orElse(null);
    if (bookends == null)
      return false;

    NodeRenderer renderer = context.get(NodeRenderer.class).orElse(null);
    if (renderer == null)
      return false;

    output.write(bookends.getBefore());

    for (Node child : node.getChildNodes())
      renderer.renderNode(document, section, child, output, context);

    output.write(bookends.getAfter());

    return true;
  }

  protected abstract Optional<Bookends> getBookends(Node node);
}
