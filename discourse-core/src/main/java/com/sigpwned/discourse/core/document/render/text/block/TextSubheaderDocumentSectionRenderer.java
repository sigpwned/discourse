package com.sigpwned.discourse.core.document.render.text.block;

import java.util.Optional;
import com.sigpwned.discourse.core.document.Block;
import com.sigpwned.discourse.core.document.Node;
import com.sigpwned.discourse.core.document.block.SubheaderBlock;

public class TextSubheaderDocumentSectionRenderer extends TextBlockRendererBase {
  @Override
  protected Optional<Node> getText(Block block) {
    if (!(block instanceof SubheaderBlock subheader))
      return Optional.empty();
    return Optional.of(subheader.getText());
  }
}
