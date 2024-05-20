/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
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
package com.sigpwned.discourse.core.module.value.sink;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import com.sigpwned.discourse.core.util.type.SetType;

/**
 * A value sink that stores values by appending them to a list.
 */
public class SetAddValueSinkFactory implements ValueSinkFactory {

  public static final SetAddValueSinkFactory INSTANCE = new SetAddValueSinkFactory();

  @Override
  public Optional<ValueSink> getSink(Type genericType, List<Annotation> annotations) {
    final SetType setType;
    try {
      setType = SetType.parse(genericType);
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
    return Optional.of(new ValueSink() {
      private final Set set = new HashSet<>();

      @Override
      public boolean isCollection() {
        return true;
      }

      @Override
      public Type getGenericType() {
        return setType.getElementType();
      }

      @Override
      public void put(Object value) {
        if (value == null) {
          throw new IllegalArgumentException("Cannot add null to a set");
        }
        set.add(value);
      }

      @Override
      public Optional<Object> get() {
        return Optional.of(set);
      }

      @Override
      public int hashCode() {
        return getGenericType().hashCode();
      }

      @Override
      public boolean equals(Object other) {
        if (other == null) {
          return false;
        }
        if (this == other) {
          return true;
        }
        if (getClass() != other.getClass()) {
          return false;
        }
        ValueSink that = (ValueSink) other;
        return isCollection() == that.isCollection()
            && Objects.equals(getGenericType(), that.getGenericType());
      }
    });
  }
}
