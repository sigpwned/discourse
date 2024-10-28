package com.sigpwned.discourse.core.document.render.text.block;

import java.util.Optional;
import com.sigpwned.discourse.core.document.Block;
import com.sigpwned.discourse.core.document.Node;
import com.sigpwned.discourse.core.document.block.HeaderBlock;

public class TextHeaderDocumentSectionRenderer extends TextBlockRendererBase {
  @Override
  protected Optional<Node> getText(Block block) {
    if (!(block instanceof HeaderBlock header))
      return Optional.empty();
    return Optional.of(header.getText());
  }
}
