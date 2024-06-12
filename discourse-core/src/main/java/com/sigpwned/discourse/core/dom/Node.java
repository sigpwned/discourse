package com.sigpwned.discourse.core.dom;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Node {
  private Node parent;
  private final List<Node> childNodes;

  /* default */ Node() {
    this.childNodes = new ArrayList<>();
  }

  public boolean isConnected() {
    return parent != null;
  }

  public List<Node> getChildNodes() {
    return childNodes;
  }

  public int getNumChildren() {
    return childNodes.size();
  }

  public Optional<Node> getFirstChild() {
    return childNodes.isEmpty() ? Optional.empty() : Optional.of(childNodes.get(0));
  }

  public Optional<Node> getLastChild() {
    return childNodes.isEmpty() ? Optional.empty()
        : Optional.of(childNodes.get(childNodes.size() - 1));
  }

  public Optional<Node> getNextSibling() {
    if (parent == null)
      return Optional.empty();
    List<Node> siblings = parent.getChildNodes();
    int index = siblings.indexOf(this);
    if (index == siblings.size() - 1)
      return Optional.empty();
    return Optional.of(siblings.get(index + 1));
  }

  public Optional<Node> getPreviousSibling() {
    if (parent == null)
      return Optional.empty();
    List<Node> siblings = parent.getChildNodes();
    int index = siblings.indexOf(this);
    if (index == 0)
      return Optional.empty();
    return Optional.of(siblings.get(index - 1));
  }

  public Optional<Node> getParent() {
    if (parent == null)
      return Optional.empty();
    return Optional.of(parent);
  }

  public void appendChild(Node child) {
    if (child == null)
      throw new NullPointerException();

    Optional<Node> maybeOldParent = child.getParent();
    if (maybeOldParent.isPresent()) {
      maybeOldParent.orElseThrow().removeChild(child);
    }

    child.parent = this;

    childNodes.add(child);
  }

  public void removeChild(Node child) {
    if (child == null)
      throw new NullPointerException();

    Optional<Node> maybeParent = child.getParent();
    if (maybeParent.isEmpty())
      return;

    if (maybeParent.orElseThrow() != this)
      throw new IllegalArgumentException("not a child of this node");

    child.parent = null;

    childNodes.remove(child);
  }

  public boolean hasChildNodes() {
    return !childNodes.isEmpty();
  }

  public abstract Node cloneNode(boolean deep);
}
