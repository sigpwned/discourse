package com.sigpwned.discourse.core.document.render.text.section;

import java.util.Optional;
import com.sigpwned.discourse.core.document.DocumentSection;
import com.sigpwned.discourse.core.document.Node;
import com.sigpwned.discourse.core.document.section.ParagraphDocumentSection;

public class TextParagraphDocumentSectionRenderer extends TextDocumentSectionRendererBase {
  @Override
  protected Optional<Node> getText(DocumentSection section) {
    if (!(section instanceof ParagraphDocumentSection paragraph))
      return Optional.empty();
    return Optional.of(paragraph.getText());
  }
}
