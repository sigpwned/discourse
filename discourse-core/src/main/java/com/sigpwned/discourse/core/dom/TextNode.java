package com.sigpwned.discourse.core.dom;

import static java.util.Objects.requireNonNull;

public final class TextNode extends Node {
  private final String text;

  public TextNode(String text) {
    this.text = requireNonNull(text);
  }

  public String getText() {
    return text;
  }

  @Override
  public void appendChild(Node child) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void removeChild(Node child) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Node cloneNode(boolean deep) {
    return new TextNode(text);
  }
}
