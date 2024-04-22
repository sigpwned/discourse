package com.sigpwned.discourse.core.util;

import com.sigpwned.discourse.core.Chain;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class Chains {

  private Chains() {
  }

  /**
   * Returns a stream of the elements in the given chain. The elements are provided in order from
   * the first element to the last element.
   *
   * @param chain the chain
   * @param <T>   the type of the elements
   * @return a stream of the elements in the given chain
   */
  public static <T> Stream<T> stream(Chain<T> chain) {
    return StreamSupport.stream(chain.spliterator(), false);
  }
}
