package com.sigpwned.discourse.core;

import static java.util.Collections.*;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Implements the chain of responsibility pattern.
 */
public class Chain<T> implements Iterable<T> {

  private final LinkedList<T> elements = new LinkedList<>();

  public void addFirst(T element) {
    elements.addFirst(element);
  }

  public void addLast(T element) {
    elements.addLast(element);
  }

  @Override
  public Iterator<T> iterator() {
    return unmodifiableList(elements).iterator();
  }
}
