package com.sigpwned.discourse.core.document.render.text.section;

import java.util.Optional;
import com.sigpwned.discourse.core.document.DocumentSection;
import com.sigpwned.discourse.core.document.Node;
import com.sigpwned.discourse.core.document.section.HeaderDocumentSection;

public class TextHeaderDocumentSectionRenderer extends TextDocumentSectionRendererBase {
  @Override
  protected Optional<Node> getText(DocumentSection section) {
    if (!(section instanceof HeaderDocumentSection header))
      return Optional.empty();
    return Optional.of(header.getText());
  }
}
