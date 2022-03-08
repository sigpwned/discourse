package com.sigpwned.discourse.core.value.sink;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import com.sigpwned.discourse.core.ValueSink;
import com.sigpwned.discourse.core.ValueSinkFactory;
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

      @SuppressWarnings({"unchecked", "rawtypes"})
      @Override
      public void write(Object instance, Object value) throws InvocationTargetException {
        Set propertyValue = (Set) property.get(instance);
        if (propertyValue == null)
          property.set(instance, propertyValue = new HashSet());
        propertyValue.add(value);
      }
    };
  }
}
