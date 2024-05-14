package com.sigpwned.discourse.core.util;

import com.sigpwned.discourse.core.thirdparty.com.google.guava.collect.CartesianList;
import java.util.List;

public final class MoreLists {

  private MoreLists() {
  }

  /**
   * <p>
   * Returns the Cartesian product of the input lists.
   * </p>
   *
   * <p>
   * For example, {@code cartesianProduct([[1, 2], [3, 4]])} returns
   * {@code [[1, 3], [1, 4], [2, 3], [2, 4]]}.
   * </p>
   *
   * @param lists the input lists
   * @param <E>   the element type
   * @return the Cartesian product
   */
  public static <E> List<List<E>> cartesianProduct(List<? extends List<? extends E>> lists) {
    return CartesianList.of(lists);
  }
}
