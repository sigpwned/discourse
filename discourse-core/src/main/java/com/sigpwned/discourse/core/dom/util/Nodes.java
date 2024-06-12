package com.sigpwned.discourse.core.dom.util;

import com.sigpwned.discourse.core.dom.Element;
import com.sigpwned.discourse.core.dom.Node;
import com.sigpwned.discourse.core.dom.TextNode;

public final class Nodes {
  private Nodes() {}

  public static boolean isText(Node node) {
    return node instanceof TextNode;
  }

  public static boolean isInline(Node node) {
    if (node instanceof Element element)
      return Elements.isInline(element);
    if (node instanceof TextNode)
      return true;
    return false;
  }

  public static boolean isBlock(Node node) {
    if (node instanceof Element element)
      return Elements.isBlock(element);
    return false;
  }

  public static boolean isTable(Node node) {
    if (node instanceof Element element)
      return Elements.isTable(element);
    return false;
  }

  public static boolean isDocument(Node node) {
    if (node instanceof Element element)
      return Elements.isDocument(element);
    return false;
  }
}
