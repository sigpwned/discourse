package com.sigpwned.discourse.core.document.block;

import com.sigpwned.discourse.core.document.Block;
import com.sigpwned.discourse.core.document.Node;

public class SubheaderBlock extends Block {
  private final Node text;

  public SubheaderBlock(Node text) {
    this.text = text;
  }

  public Node getText() {
    return text;
  }
}
