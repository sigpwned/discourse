package com.sigpwned.discourse.core.document.section;

import com.sigpwned.discourse.core.document.DocumentSection;
import com.sigpwned.discourse.core.document.Node;

public class ParagraphDocumentSection extends DocumentSection {
  private final Node text;

  public ParagraphDocumentSection(Node text) {
    this.text = text;
  }

  public Node getText() {
    return text;
  }
}
