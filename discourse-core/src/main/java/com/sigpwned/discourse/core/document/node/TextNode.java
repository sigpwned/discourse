package com.sigpwned.discourse.core.document.node;

import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.document.Node;
import com.sigpwned.discourse.core.l11n.UserMessage;

public class TextNode extends Node {
  private final UserMessage text;

  public TextNode(UserMessage text) {
    this.text = requireNonNull(text);
  }

  public UserMessage getText() {
    return text;
  }

  @Override
  protected Node cloneNodeShallow() {
    return new TextNode(getText());
  }
}
