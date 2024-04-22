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
package com.sigpwned.discourse.core.value.sink;

import com.sigpwned.discourse.core.ValueSink;
import com.sigpwned.discourse.core.ValueSinkFactory;
import com.sigpwned.discourse.core.util.Generated;
import com.sigpwned.discourse.core.util.type.SortedSetType;
import com.sigpwned.espresso.BeanProperty;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A value sink that stores values by adding them to a sorted set.
 */
public class SortedSetAddValueSinkFactory implements ValueSinkFactory {

  public static final SortedSetAddValueSinkFactory INSTANCE = new SortedSetAddValueSinkFactory();

  @Override
  public boolean isSinkable(BeanProperty property) {
    try {
      SortedSetType.parse(property.getGenericType());
    } catch (IllegalArgumentException e) {
      return false;
    }
    return true;
  }

  @Override
  public ValueSink getSink(BeanProperty property) {
    final SortedSetType sortedSetType = SortedSetType.parse(property.getGenericType());
    return new ValueSink() {
      @Override
      public boolean isCollection() {
        return true;
      }

      @Override
      public Type getGenericType() {
        return sortedSetType.getElementType();
      }

      @SuppressWarnings({"unchecked", "rawtypes"})
      @Override
      public void put(Object instance, Object value) throws InvocationTargetException {
        SortedSet propertyValue = (SortedSet) property.get(instance);
        if (propertyValue == null) {
          propertyValue = new TreeSet();
          property.set(instance, propertyValue);
        }
        propertyValue.add(value);
      }

      @Override
      @Generated
      public int hashCode() {
        return 23;
      }

      @Override
      @Generated
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
        return isCollection() == that.isCollection() && Objects.equals(getGenericType(),
            that.getGenericType());
      }
    };
  }
}
