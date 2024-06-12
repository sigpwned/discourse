package com.sigpwned.discourse.core.dom;

import com.sigpwned.discourse.core.dom.util.Nodes;

public abstract class InlineElement extends Element {

  /* default */ InlineElement(String type) {
    super(type);
  }

  @Override
  public void appendChild(Node child) {
    if (child == null)
      throw new NullPointerException();
    if (!Nodes.isInline(child))
      throw new IllegalArgumentException("invalid child node type");
    super.appendChild(child);
  }
}
