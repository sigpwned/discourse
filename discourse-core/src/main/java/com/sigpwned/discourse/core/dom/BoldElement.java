package com.sigpwned.discourse.core.dom;

public final class BoldElement extends InlineElement {

  /* default */ BoldElement() {
    super(Element.BOLD);
  }

  @Override
  protected Node cloneNodeShallow() {
    return new BoldElement();
  }
}
