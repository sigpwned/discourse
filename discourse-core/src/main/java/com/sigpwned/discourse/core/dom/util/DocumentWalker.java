package com.sigpwned.discourse.core.dom.util;

import com.sigpwned.discourse.core.dom.Document;
import com.sigpwned.discourse.core.dom.Element;
import com.sigpwned.discourse.core.dom.Node;
import com.sigpwned.discourse.core.dom.TableDataElement;
import com.sigpwned.discourse.core.dom.TableElement;
import com.sigpwned.discourse.core.dom.TableRowElement;
import com.sigpwned.discourse.core.dom.TextNode;

public class DocumentWalker {
  public static interface DocumentVisitor {
    default void beginDocument(Document document) {}

    default void enterElement(Element element) {}

    default void text(TextNode text) {}

    default void leaveElement(Element element) {}

    default void endDocument(Document document) {}
  }

  public void walk(Document document, DocumentVisitor visitor) {
    visitor.beginDocument(document);

    visitor.enterElement(document);

    for (int i = 0; i < document.getNumChildren(); i++) {
      visit(document.getChildNodes().get(i), visitor);
    }

    visitor.leaveElement(document);

    visitor.endDocument(document);
  }

  private void visit(Node node, DocumentVisitor visitor) {
    if (Nodes.isInline(node) || Nodes.isBlock(node)) {
      Element element = (Element) node;

      visitor.enterElement(element);

      for (int i = 0; i < element.getNumChildren(); i++) {
        visit(element.getChildNodes().get(i), visitor);
      }

      visitor.leaveElement(element);
    } else if (Nodes.isTable(node)) {
      TableElement table = (TableElement) node;

      visitor.enterElement(table);

      for (int ri = 0; ri < table.getNumChildren(); ri++) {
        TableRowElement row = (TableRowElement) table.getChildNodes().get(ri);

        visitor.enterElement(row);

        for (int di = 0; di < row.getNumChildren(); di++) {
          TableDataElement data = (TableDataElement) row.getChildNodes().get(di);

          visitor.enterElement(data);

          visit(data, visitor);

          visitor.leaveElement(data);
        }

        visitor.leaveElement(row);
      }

      visitor.leaveElement(table);
    } else {
      throw new AssertionError("unexpected node: " + node);
    }
  }
}
