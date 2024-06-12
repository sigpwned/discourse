package com.sigpwned.discourse.core.dom.util;

import com.sigpwned.discourse.core.dom.Element;

public final class Elements {
  private Elements() {}

  /**
   * @see <a href=
   *      "https://developer.mozilla.org/en-US/docs/Web/HTML/Content_categories#phrasing_content">Phrasing
   *      Content</a>
   */
  public static boolean isInline(Element element) {
    switch (element.getType()) {
      case Element.BOLD:
      case Element.CODE:
      case Element.ITALICS:
      case Element.STRIKETHRU:
      case Element.UNDERLINE:
        return true;
      default:
        return false;
    }
  }

  public static boolean isBlock(Element element) {
    switch (element.getType()) {
      case Element.HEADER:
      case Element.PARAGRAPH:
      case Element.SUBHEADER:
        return true;
      default:
        return false;
    }
  }

  public static boolean isTable(Element element) {
    switch (element.getType()) {
      case Element.TABLE:
        return true;
      default:
        return false;
    }
  }

  public static boolean isDocument(Element element) {
    switch (element.getType()) {
      case Element.DOCUMENT:
        return true;
      default:
        return false;
    }
  }
}
