/*
 * Copyright (C) 2012 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * =================================================================================================
 *
 * Modified by Andy Boothe (2024) to remove unnecessary code and annotations for the purposes of
 * embedding in this project.
 */
package com.sigpwned.discourse.core.com.sigpwned.discourse.core.thirdparty.com.google.guava.collect;


import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * Implementation of {@code Lists#cartesianProduct(List)}.
 *
 * @author Louis Wasserman
 */
public final class CartesianList<E> extends AbstractList<List<E>> implements RandomAccess {

  private final transient List<List<E>> axes;
  private final transient int[] axesSizeProduct;

  public static <E> List<List<E>> of(List<? extends List<? extends E>> lists) {
    List<List<E>> axesBuilder = new ArrayList<>(lists.size());
    for (List<? extends E> list : lists) {
      List<E> copy = List.copyOf(list);
      if (copy.isEmpty()) {
        return List.of();
      }
      axesBuilder.add(copy);
    }
    return new CartesianList<>(List.copyOf(axesBuilder));
  }

  CartesianList(List<List<E>> axes) {
    this.axes = axes;
    int[] axesSizeProduct = new int[axes.size() + 1];
    axesSizeProduct[axes.size()] = 1;
    try {
      for (int i = axes.size() - 1; i >= 0; i--) {
        axesSizeProduct[i] = checkedMultiply(axesSizeProduct[i + 1], axes.get(i).size());
      }
    } catch (ArithmeticException e) {
      throw new IllegalArgumentException(
          "Cartesian product too large; must have size at most Integer.MAX_VALUE");
    }
    this.axesSizeProduct = axesSizeProduct;
  }

  private int getAxisIndexForProductIndex(int index, int axis) {
    return (index / axesSizeProduct[axis + 1]) % axes.get(axis).size();
  }

  @Override
  public int indexOf(Object o) {
    if (!(o instanceof List)) {
      return -1;
    }
    List<?> list = (List<?>) o;
    if (list.size() != axes.size()) {
      return -1;
    }
    ListIterator<?> itr = list.listIterator();
    int computedIndex = 0;
    while (itr.hasNext()) {
      int axisIndex = itr.nextIndex();
      int elemIndex = axes.get(axisIndex).indexOf(itr.next());
      if (elemIndex == -1) {
        return -1;
      }
      computedIndex += elemIndex * axesSizeProduct[axisIndex + 1];
    }
    return computedIndex;
  }

  @Override
  public int lastIndexOf(Object o) {
    if (!(o instanceof List)) {
      return -1;
    }
    List<?> list = (List<?>) o;
    if (list.size() != axes.size()) {
      return -1;
    }
    ListIterator<?> itr = list.listIterator();
    int computedIndex = 0;
    while (itr.hasNext()) {
      int axisIndex = itr.nextIndex();
      int elemIndex = axes.get(axisIndex).lastIndexOf(itr.next());
      if (elemIndex == -1) {
        return -1;
      }
      computedIndex += elemIndex * axesSizeProduct[axisIndex + 1];
    }
    return computedIndex;
  }

  @Override
  public List<E> get(int index) {
    checkElementIndex(index, size());
    return new AbstractList<E>() {

      @Override
      public int size() {
        return axes.size();
      }

      @Override
      public E get(int axis) {
        checkElementIndex(axis, size());
        int axisIndex = getAxisIndexForProductIndex(index, axis);
        return axes.get(axis).get(axisIndex);
      }
    };
  }

  @Override
  public int size() {
    return axesSizeProduct[0];
  }

  @Override
  public boolean contains(Object object) {
    if (!(object instanceof List)) {
      return false;
    }
    List<?> list = (List<?>) object;
    if (list.size() != axes.size()) {
      return false;
    }
    int i = 0;
    for (Object o : list) {
      if (!axes.get(i).contains(o)) {
        return false;
      }
      i++;
    }
    return true;
  }

  /**
   * Returns the product of {@code a} and {@code b}, provided it does not overflow. From
   * {@code IntMath}.
   *
   * @throws ArithmeticException if {@code a * b} overflows in signed {@code int} arithmetic
   */
  private static int checkedMultiply(int a, int b) {
    long result = (long) a * b;
    checkNoOverflow(result == (int) result, "checkedMultiply", a, b);
    return (int) result;
  }

  /**
   * From {@code MathPreconditions}.
   */
  private static void checkNoOverflow(boolean condition, String methodName, int a, int b) {
    if (!condition) {
      throw new ArithmeticException("overflow: " + methodName + "(" + a + ", " + b + ")");
    }
  }

  /**
   * Ensures that {@code index} specifies a valid <i>element</i> in an array, list or string of size
   * {@code size}. An element index may range from zero, inclusive, to {@code size}, exclusive. From
   * {@code Preconditions}.
   *
   * @param index a user-supplied index identifying an element of an array, list or string
   * @param size  the size of that array, list or string
   * @return the value of {@code index}
   * @throws IndexOutOfBoundsException if {@code index} is negative or is not less than
   *                                   {@code size}
   * @throws IllegalArgumentException  if {@code size} is negative
   */
  private static int checkElementIndex(int index, int size) {
    return checkElementIndex(index, size, "index");
  }

  /**
   * Ensures that {@code index} specifies a valid <i>element</i> in an array, list or string of size
   * {@code size}. An element index may range from zero, inclusive, to {@code size}, exclusive. From
   * {@code Preconditions}.
   *
   * @param index a user-supplied index identifying an element of an array, list or string
   * @param size  the size of that array, list or string
   * @param desc  the text to use to describe this index in an error message
   * @return the value of {@code index}
   * @throws IndexOutOfBoundsException if {@code index} is negative or is not less than
   *                                   {@code size}
   * @throws IllegalArgumentException  if {@code size} is negative
   */
  private static int checkElementIndex(int index, int size, String desc) {
    // Carefully optimized for execution by hotspot (explanatory comment above)
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException(badElementIndex(index, size, desc));
    }
    return index;
  }

  /**
   * From {@code Preconditions}.
   */
  private static String badElementIndex(int index, int size, String desc) {
    if (index < 0) {
      return String.format("%s (%s) must not be negative", desc, index);
    } else if (size < 0) {
      throw new IllegalArgumentException("negative size: " + size);
    } else { // index >= size
      return String.format("%s (%s) must be less than size (%s)", desc, index, size);
    }
  }
}