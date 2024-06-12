package com.sigpwned.discourse.core.dom.render;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sigpwned.discourse.core.dom.Document;
import com.sigpwned.discourse.core.dom.Element;
import com.sigpwned.discourse.core.dom.TextNode;
import com.sigpwned.discourse.core.dom.util.DocumentWalker;

public class PlainTextDocumentRenderer {
  private static final Logger LOGGER = LoggerFactory.getLogger(PlainTextDocumentRenderer.class);

  public String render(Document doc) {
    final StringBuilder result = new StringBuilder();
    new DocumentWalker().walk(doc, new DocumentWalker.DocumentVisitor() {
      @Override
      public void enterElement(Element element) {
        switch (element.getType()) {
          case Element.BOLD:
          case Element.ITALICS:
          case Element.UNDERLINE:
          case Element.STRIKETHRU:
          case Element.CODE:
            // We don't do anything for these elements...
            break;
          case Element.PARAGRAPH:
          case Element.HEADER:
          case Element.SUBHEADER:
          case Element.TABLE:
          case Element.TABLEROW:
          case Element.TABLEDATA:
            // We don't do anything BEFORE these elements...
            break;
          default:
            if (LOGGER.isDebugEnabled())
              LOGGER.debug("unknown element type {}", element.getType());
            break;
        }
      }

      @Override
      public void text(TextNode text) {
        result.append(text.getText());
      }

      @Override
      public void leaveElement(Element element) {
        switch (element.getType()) {
          case Element.BOLD:
          case Element.ITALICS:
          case Element.UNDERLINE:
          case Element.STRIKETHRU:
          case Element.CODE:
            // We don't do anything for these elements...
            break;
          case Element.PARAGRAPH:
            // We do two line separators AFTER these elements...
            result.append(System.lineSeparator());
            result.append(System.lineSeparator());
            break;
          case Element.HEADER:
          case Element.SUBHEADER:
            // We do one line separator AFTER these elements...
            result.append(System.lineSeparator());
            break;
          case Element.TABLE:
          case Element.TABLEROW:
          case Element.TABLEDATA:
            // We don't do anything BEFORE these elements...
            break;
          default:
            if (LOGGER.isDebugEnabled())
              LOGGER.debug("unknown element type {}", element.getType());
            break;
        }
      }
    });
    return result.toString();
  }
}
