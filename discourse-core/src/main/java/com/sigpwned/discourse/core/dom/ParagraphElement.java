package com.sigpwned.discourse.core.dom;

public final class ParagraphElement extends BlockElement {

  /* default */ ParagraphElement() {
    super(Element.PARAGRAPH);
  }

  @Override
  protected Node cloneNodeShallow() {
    return new ParagraphElement();
  }
}
