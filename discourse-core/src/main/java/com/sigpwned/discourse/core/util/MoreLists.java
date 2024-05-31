/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 - 2024 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.discourse.core.util;

import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.thirdparty.com.google.guava.collect.CartesianList;

public final class MoreLists {

  private MoreLists() {}

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
   * @param <E> the element type
   * @return the Cartesian product
   */
  public static <E> List<List<E>> cartesianProduct(List<? extends List<? extends E>> lists) {
    return CartesianList.of(lists);
  }

  public static <E> List<E> concat(List<? extends E> firstList, List<? extends E>... moreLists) {
    List<E> result = new ArrayList<>();
    result.addAll(firstList);
    for (List<? extends E> moreList : moreLists) {
      result.addAll(moreList);
    }
    return unmodifiableList(result);
  }
}
