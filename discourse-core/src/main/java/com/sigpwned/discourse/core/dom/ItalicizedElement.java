package com.sigpwned.discourse.core.dom;

public final class ItalicizedElement extends InlineElement {

  /* default */ ItalicizedElement() {
    super(Element.ITALICS);
  }

  @Override
  protected Node cloneNodeShallow() {
    return new ItalicizedElement();
  }
}
