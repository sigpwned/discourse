package com.sigpwned.discourse.core.document.render.text.block;

import java.util.Optional;
import com.sigpwned.discourse.core.document.Block;
import com.sigpwned.discourse.core.document.Node;
import com.sigpwned.discourse.core.document.block.ParagraphBlock;

public class TextParagraphDocumentSectionRenderer extends TextBlockRendererBase {
  @Override
  protected Optional<Node> getText(Block block) {
    if (!(block instanceof ParagraphBlock paragraph))
      return Optional.empty();
    return Optional.of(paragraph.getText());
  }
}
