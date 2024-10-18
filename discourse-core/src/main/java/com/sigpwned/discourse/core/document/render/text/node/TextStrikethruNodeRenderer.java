package com.sigpwned.discourse.core.document.render.text.node;

import java.util.Optional;
import com.sigpwned.discourse.core.document.Node;
import com.sigpwned.discourse.core.document.node.StrikethruNode;

public class TextStrikethruNodeRenderer extends TextNodeRendererBase {
  private static final Bookends BOOKENDS = new Bookends(new byte[] {}, new byte[] {});

  @Override
  protected Optional<Bookends> getBookends(Node node) {
    if (!(node instanceof StrikethruNode))
      return Optional.empty();
    return Optional.of(BOOKENDS);
  }
}
