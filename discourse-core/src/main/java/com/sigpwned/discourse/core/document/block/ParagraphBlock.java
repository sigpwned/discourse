package com.sigpwned.discourse.core.document.block;

import com.sigpwned.discourse.core.document.Block;
import com.sigpwned.discourse.core.document.Node;

public class ParagraphBlock extends Block {
  private final Node text;

  public ParagraphBlock(Node text) {
    this.text = text;
  }

  public Node getText() {
    return text;
  }
}
