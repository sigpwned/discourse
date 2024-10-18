package com.sigpwned.discourse.core.document.block;

import com.sigpwned.discourse.core.document.Block;
import com.sigpwned.discourse.core.document.Node;

public class HeaderBlock extends Block {
  private final Node text;

  public HeaderBlock(Node text) {
    this.text = text;
  }

  public Node getText() {
    return text;
  }
}
