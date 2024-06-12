package com.sigpwned.discourse.core.document.node;

import com.sigpwned.discourse.core.document.Node;

public class UnderlineNode extends Node {
  @Override
  protected Node cloneNodeShallow() {
    return new UnderlineNode();
  }
}
