package com.sigpwned.discourse.core.value.storer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;
import com.sigpwned.discourse.core.ValueStorer;
import com.sigpwned.espresso.BeanProperty;

public class SetValueStorer implements ValueStorer {
  public static final SetValueStorer INSTANCE = new SetValueStorer();

  @Override
  public boolean isStoreable(BeanProperty property) {
    if (property.getGenericType() instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) property.getGenericType();
      return parameterizedType.getRawType().equals(Set.class);
    } else {
      return false;
    }
  }

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void store(Object instance, BeanProperty property, Object value)
      throws InvocationTargetException {
    Set propertyValue = (Set) property.get(instance);
    if (propertyValue == null)
      property.set(instance, propertyValue = new LinkedHashSet());
    propertyValue.add(value);
  }

  @Override
  public boolean isCollection() {
    return true;
  }
  
  @Override
  public int hashCode() {
    return 13;
  }
  
  @Override
  public boolean equals(Object other) {
    return getClass().equals(other.getClass());
  }

  @Override
  public String toString() {
    return "SetValueStorer []";
  }

  @Override
  public Type unpack(BeanProperty property) {
    ParameterizedType parameterizedType = (ParameterizedType) property.getGenericType();
    return parameterizedType.getActualTypeArguments()[0];
  }
}
