package com.sigpwned.discourse.core.dom;

import com.sigpwned.discourse.core.dom.util.Nodes;

public final class Document extends Element {
  public Document() {
    super(Element.DOCUMENT);
  }

  @Override
  public void appendChild(Node child) {
    if (child == null)
      throw new NullPointerException();
    if (!(Nodes.isBlock(child) || Nodes.isTable(child))) {
      throw new IllegalArgumentException("invalid child node type");
    }
    super.appendChild(child);
  }

  @Override
  protected Node cloneNodeShallow() {
    return new Document();
  }
}
