package com.sigpwned.discourse.core.value.sink;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Objects;
import com.sigpwned.discourse.core.ValueSink;
import com.sigpwned.discourse.core.ValueSinkFactory;
import com.sigpwned.discourse.core.util.Generated;
import com.sigpwned.espresso.BeanProperty;

public class AssignValueSinkFactory implements ValueSinkFactory {
  public static final AssignValueSinkFactory INSTANCE = new AssignValueSinkFactory();

  @Override
  public boolean isSinkable(BeanProperty property) {
    // We can always just assign a bean property
    return true;
  }

  @Override
  public ValueSink getSink(BeanProperty property) {
    return new ValueSink() {
      @Override
      public boolean isCollection() {
        return false;
      }

      @Override
      public Type getGenericType() {
        return property.getGenericType();
      }

      @Override
      public void write(Object instance, Object value) throws InvocationTargetException {
        property.set(instance, value);
      }

      @Override
      @Generated
      public int hashCode() {
        return 13;
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
