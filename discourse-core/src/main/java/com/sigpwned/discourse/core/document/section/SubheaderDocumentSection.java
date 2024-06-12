package com.sigpwned.discourse.core.document.section;

import com.sigpwned.discourse.core.document.DocumentSection;
import com.sigpwned.discourse.core.document.Node;

public class SubheaderDocumentSection extends DocumentSection {
  private final Node text;

  public SubheaderDocumentSection(Node text) {
    this.text = text;
  }

  public Node getText() {
    return text;
  }
}
