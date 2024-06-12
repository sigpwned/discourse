package com.sigpwned.discourse.core.dom;

public abstract class TableRowElement extends Element {

  /* default */ TableRowElement() {
    super(Element.TABLEROW);
  }

  @Override
  public void appendChild(Node child) {
    if (child == null)
      throw new NullPointerException();
    if (!(child instanceof TableDataElement)) {
      throw new IllegalArgumentException("invalid child node type");
    }
    super.appendChild(child);
  }
}
