package com.sigpwned.discourse.core.value.sink;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import com.sigpwned.discourse.core.ValueSink;
import com.sigpwned.discourse.core.ValueSinkFactory;
import com.sigpwned.discourse.core.util.Generated;
import com.sigpwned.discourse.core.util.SetType;
import com.sigpwned.espresso.BeanProperty;

public class SetAddValueSinkFactory implements ValueSinkFactory {
  public static final SetAddValueSinkFactory INSTANCE=new SetAddValueSinkFactory();
  
  @Override
  public boolean isSinkable(BeanProperty property) {
    try {
      SetType.parse(property.getGenericType());
    } catch (IllegalArgumentException e) {
      return false;
    }
    return true;
  }

  @Override
  public ValueSink getSink(BeanProperty property) {
    final SetType setType=SetType.parse(property.getGenericType());
    return new ValueSink() {
      @Override
      public boolean isCollection() {
        return true;
      }

      @Override
      public Type getGenericType() {
        return setType.getElementType();
      }

      @Override
      @SuppressWarnings({"unchecked", "rawtypes"})
      public void write(Object instance, Object value) throws InvocationTargetException {
        Set propertyValue = (Set) property.get(instance);
        if (propertyValue == null) {
          propertyValue = new HashSet();
          property.set(instance, propertyValue);
        }
        propertyValue.add(value);
      }

      @Override
      @Generated
      public int hashCode() {
        return 19;
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
