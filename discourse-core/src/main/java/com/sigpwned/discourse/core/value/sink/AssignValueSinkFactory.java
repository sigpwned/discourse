package com.sigpwned.discourse.core.value.sink;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import com.sigpwned.discourse.core.ValueSink;
import com.sigpwned.discourse.core.ValueSinkFactory;
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
    };
  }

  @Override
  public int hashCode() {
    return 17;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other)
      return true;
    if (other == null)
      return false;
    if (getClass() != other.getClass())
      return false;
    return true;
  }
}
