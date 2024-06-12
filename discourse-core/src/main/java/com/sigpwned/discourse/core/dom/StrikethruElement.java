package com.sigpwned.discourse.core.dom;

public final class StrikethruElement extends InlineElement {

  /* default */ StrikethruElement() {
    super(Element.STRIKETHRU);
  }
  
  @Override
  protected Node cloneNodeShallow() {
    return new StrikethruElement();
  }
}
