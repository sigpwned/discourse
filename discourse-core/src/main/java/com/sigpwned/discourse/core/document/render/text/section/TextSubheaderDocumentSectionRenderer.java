package com.sigpwned.discourse.core.document.render.text.section;

import java.util.Optional;
import com.sigpwned.discourse.core.document.DocumentSection;
import com.sigpwned.discourse.core.document.Node;
import com.sigpwned.discourse.core.document.section.SubheaderDocumentSection;

public class TextSubheaderDocumentSectionRenderer extends TextDocumentSectionRendererBase {
  @Override
  protected Optional<Node> getText(DocumentSection section) {
    if (!(section instanceof SubheaderDocumentSection subheader))
      return Optional.empty();
    return Optional.of(subheader.getText());
  }
}
