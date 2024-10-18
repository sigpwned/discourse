package com.sigpwned.discourse.core.document.node;

import com.sigpwned.discourse.core.document.Node;

public class BoldNode extends Node {
  @Override
  protected Node cloneNodeShallow() {
    return new BoldNode();
  }
}
