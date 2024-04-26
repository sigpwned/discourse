package com.sigpwned.discourse.core.util;

import java.util.Iterator;
import java.util.Optional;

public final class MoreIterables {

  private MoreIterables() {
  }

  public static <T> Optional<T> first(Iterable<T> iterable) {
    Iterator<T> iterator = iterable.iterator();
    if (iterator.hasNext()) {
      return Optional.ofNullable(iterator.next());
    }
    return Optional.empty();
  }
}
