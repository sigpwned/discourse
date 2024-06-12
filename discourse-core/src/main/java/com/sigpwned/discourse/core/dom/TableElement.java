package com.sigpwned.discourse.core.dom;

public abstract class TableElement extends Element {

  /* default */ TableElement() {
    super(Element.TABLE);
  }

  @Override
  public void appendChild(Node child) {
    if (child == null)
      throw new NullPointerException();
    if (!(child instanceof TableRowElement)) {
      throw new IllegalArgumentException("invalid child node type");
    }
    super.appendChild(child);
  }
}
