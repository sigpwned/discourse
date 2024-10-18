package com.sigpwned.discourse.core.document.node;

import com.sigpwned.discourse.core.document.Node;

public class ItalicsNode extends Node {
  @Override
  protected Node cloneNodeShallow() {
    return new ItalicsNode();
  }
}
