package com.sigpwned.discourse.core.dom;

import static java.util.Objects.requireNonNull;

public abstract class Element extends Node {
  public static final String DOCUMENT = "document";

  public static final String HEADER = "h1";

  public static final String SUBHEADER = "h2";

  public static final String PARAGRAPH = "p";

  public static final String TABLE = "table";

  public static final String TABLEROW = "tr";

  public static final String TABLEDATA = "td";

  public static final String ITALICS = "i";

  public static final String BOLD = "b";

  public static final String UNDERLINE = "u";

  public static final String STRIKETHRU = "s";

  public static final String CODE = "code";

  private final String type;

  /* default */ Element(String type) {
    this.type = requireNonNull(type);
    switch (type) {
      case HEADER:
      case SUBHEADER:
      case PARAGRAPH:
      case TABLE:
        break;
      default:
        throw new IllegalArgumentException("invalid element type");
    }
  }

  public String getType() {
    return type;
  }

  @Override
  public Node cloneNode(boolean deep) {
    Node result = cloneNodeShallow();
    if (deep)
      for (Node child : getChildNodes())
        result.appendChild(child.cloneNode(true));
    return result;
  }

  protected abstract Node cloneNodeShallow();
}
