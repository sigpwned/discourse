package com.sigpwned.discourse.core.dom;

public final class SubheaderElement extends BlockElement {

  /* default */ SubheaderElement() {
    super(Element.SUBHEADER);
  }

  @Override
  protected Node cloneNodeShallow() {
    return new SubheaderElement();
  }
}
