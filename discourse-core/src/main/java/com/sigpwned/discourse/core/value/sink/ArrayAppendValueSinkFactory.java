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
import com.sigpwned.discourse.core.util.type.ArrayType;
import com.sigpwned.discourse.core.util.Generated;
import com.sigpwned.discourse.core.util.Types;
import com.sigpwned.espresso.BeanProperty;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * A value sink that stores values by appending them to an array.
 */
public class ArrayAppendValueSinkFactory implements ValueSinkFactory {

  public static final ArrayAppendValueSinkFactory INSTANCE = new ArrayAppendValueSinkFactory();

  @Override
  public boolean isSinkable(BeanProperty property) {
    try {
      ArrayType.parse(property.getGenericType());
    } catch (IllegalArgumentException e) {
      return false;
    }
    return true;
  }

  @Override
  public ValueSink getSink(BeanProperty property) {
    final ArrayType arrayType = ArrayType.parse(property.getGenericType());
    return new ValueSink() {
      @Override
      public boolean isCollection() {
        return true;
      }

      @Override
      public Type getGenericType() {
        return arrayType.getElementType();
      }

      @Override
      public void put(Object instance, Object value) throws InvocationTargetException {
        Object propertyValue = property.get(instance);

        // Make sure our property value has exactly one empty new value at the top of the array
        if (propertyValue == null) {
          propertyValue = Types.newConcreteArrayInstance(property.getGenericType(), 1);
          property.set(instance, propertyValue);
        } else {
          int length = Array.getLength(propertyValue);
          Object newPropertyValue = Types.newConcreteArrayInstance(property.getGenericType(),
              length + 1);
          System.arraycopy(propertyValue, 0, newPropertyValue, 0, length);
          propertyValue = newPropertyValue;
          property.set(instance, propertyValue);
        }

        Array.set(propertyValue, Array.getLength(propertyValue) - 1, value);
      }

      @Override
      @Generated
      public int hashCode() {
        return 11;
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
