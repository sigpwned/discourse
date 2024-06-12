package com.sigpwned.discourse.core.dom;

public final class HeaderElement extends BlockElement {

  /* default */ HeaderElement() {
    super(Element.HEADER);
  }

  @Override
  protected Node cloneNodeShallow() {
    return new HeaderElement();
  }
}
