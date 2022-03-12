package com.sigpwned.discourse.core.value.sink;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.sigpwned.discourse.core.ValueSink;
import com.sigpwned.discourse.core.ValueSinkFactory;
import com.sigpwned.discourse.core.util.Generated;
import com.sigpwned.discourse.core.util.ListType;
import com.sigpwned.espresso.BeanProperty;

public class ListAddValueSinkFactory implements ValueSinkFactory {
  public static final ListAddValueSinkFactory INSTANCE=new ListAddValueSinkFactory();
  
  @Override
  public boolean isSinkable(BeanProperty property) {
    try {
      ListType.parse(property.getGenericType());
    } catch (IllegalArgumentException e) {
      return false;
    }
    return true;
  }

  @Override
  public ValueSink getSink(BeanProperty property) {
    final ListType listType=ListType.parse(property.getGenericType());
    return new ValueSink() {
      @Override
      public boolean isCollection() {
        return true;
      }

      @Override
      public Type getGenericType() {
        return listType.getElementType();
      }

      @SuppressWarnings({"unchecked", "rawtypes"})
      @Override
      public void write(Object instance, Object value) throws InvocationTargetException {
        List propertyValue = (List) property.get(instance);
        if (propertyValue == null) {
          propertyValue = new ArrayList();          
          property.set(instance, propertyValue);
        }
        propertyValue.add(value);
      }

      @Override
      @Generated
      public int hashCode() {
        return 17;
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
