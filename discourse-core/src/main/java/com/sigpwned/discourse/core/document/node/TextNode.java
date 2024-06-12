package com.sigpwned.discourse.core.document.node;

import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.document.Node;

public class TextNode extends Node {
  private final String text;

  public TextNode(String text) {
    this.text = requireNonNull(text);
  }

  public String getText() {
    return text;
  }

  @Override
  protected Node cloneNodeShallow() {
    return new TextNode(getText());
  }
}
