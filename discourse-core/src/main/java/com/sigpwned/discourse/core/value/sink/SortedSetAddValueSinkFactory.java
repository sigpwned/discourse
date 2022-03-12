package com.sigpwned.discourse.core.value.sink;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import com.sigpwned.discourse.core.ValueSink;
import com.sigpwned.discourse.core.ValueSinkFactory;
import com.sigpwned.discourse.core.util.Generated;
import com.sigpwned.discourse.core.util.SortedSetType;
import com.sigpwned.espresso.BeanProperty;

public class SortedSetAddValueSinkFactory implements ValueSinkFactory {
  public static final SortedSetAddValueSinkFactory INSTANCE=new SortedSetAddValueSinkFactory();
  
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
    final SortedSetType sortedSetType=SortedSetType.parse(property.getGenericType());
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
      public void write(Object instance, Object value) throws InvocationTargetException {
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
