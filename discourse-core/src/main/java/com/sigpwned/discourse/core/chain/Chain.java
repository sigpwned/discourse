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
package com.sigpwned.discourse.core.chain;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Predicate;
import java.util.stream.Stream;

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

  public boolean remove(T element) {
    return elements.remove(element);
  }

  public void removeIf(Predicate<T> predicate) {
    elements.removeIf(predicate);
  }

  @Override
  public Iterator<T> iterator() {
    return elements.iterator();
  }

  public Stream<T> stream() {
    return elements.stream();
  }
}
