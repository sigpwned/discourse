package com.sigpwned.discourse.core.value.sink;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Objects;
import com.sigpwned.discourse.core.ValueSink;
import com.sigpwned.discourse.core.ValueSinkFactory;
import com.sigpwned.discourse.core.util.ArrayType;
import com.sigpwned.discourse.core.util.Generated;
import com.sigpwned.discourse.core.util.Types;
import com.sigpwned.espresso.BeanProperty;

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
      public void write(Object instance, Object value) throws InvocationTargetException {
        Object propertyValue = property.get(instance);

        // Make sure our property value has exactly one empty new value at the top of the array
        if (propertyValue == null) {
          property.set(instance,
              propertyValue = Types.newConcreteArrayInstance(property.getGenericType(), 1));
        } else {
          int length = Array.getLength(propertyValue);
          Object newPropertyValue =
              Types.newConcreteArrayInstance(property.getGenericType(), length + 1);
          System.arraycopy(propertyValue, 0, newPropertyValue, 0, length);
          property.set(instance, propertyValue = newPropertyValue);
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
        if (other == null)
          return false;
        if (this == other)
          return true;
        if (getClass() != other.getClass())
          return false;
        ValueSink that = (ValueSink) other;
        return isCollection() == that.isCollection()
            && Objects.equals(getGenericType(), that.getGenericType());
      }
    };
  }
}
