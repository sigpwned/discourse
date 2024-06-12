package com.sigpwned.discourse.core.dom.util;

import java.util.Stack;
import com.sigpwned.discourse.core.dom.Document;
import com.sigpwned.discourse.core.dom.Element;
import com.sigpwned.discourse.core.dom.Node;
import com.sigpwned.discourse.core.dom.TextNode;

public final class Documents {
  private Documents() {}

  /**
   * Normalize a document by removing empty text nodes and merging adjacent text nodes.
   */
  public static Document normalize(Document doc) {
    final Document result = (Document) doc.cloneNode(false);
    new DocumentWalker().walk(doc, new DocumentWalker.DocumentVisitor() {
      private final Stack<Node> stack = new Stack<>();
      private final StringBuilder buf = new StringBuilder();

      @Override
      public void beginDocument(Document document) {
        stack.push(result);
      }

      @Override
      public void endDocument(Document document) {
        stack.pop();
      }

      @Override
      public void enterElement(Element element) {
        if (buf.length() > 0) {
          top().appendChild(new TextNode(buf.toString()));
          buf.setLength(0);
        }
        stack.push(element.cloneNode(false));
      }

      @Override
      public void text(TextNode text) {
        buf.append(text.getText());
      }

      @Override
      public void leaveElement(Element element) {
        if (buf.length() > 0) {
          top().appendChild(new TextNode(buf.toString()));
          buf.setLength(0);
        }
        Node node = stack.pop();
        top().appendChild(node);
      }

      private Node top() {
        return stack.peek();
      }
    });
    return result;
  }
}
