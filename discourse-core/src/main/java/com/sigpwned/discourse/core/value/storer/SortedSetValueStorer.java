package com.sigpwned.discourse.core.value.storer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.SortedSet;
import java.util.TreeSet;
import com.sigpwned.discourse.core.ValueStorer;
import com.sigpwned.espresso.BeanProperty;

public class SortedSetValueStorer implements ValueStorer {
  public static final SortedSetValueStorer INSTANCE = new SortedSetValueStorer();

  @Override
  public boolean isStoreable(BeanProperty property) {
    if (property.getGenericType() instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) property.getGenericType();
      return parameterizedType.getRawType().equals(SortedSet.class);
    } else {
      return false;
    }
  }

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void store(Object instance, BeanProperty property, Object value)
      throws InvocationTargetException {
    SortedSet propertyValue = (SortedSet) property.get(instance);
    if (propertyValue == null)
      property.set(instance, propertyValue = new TreeSet());
    propertyValue.add(value);
  }

  @Override
  public boolean isCollection() {
    return true;
  }
  
  @Override
  public int hashCode() {
    return 11;
  }
  
  @Override
  public boolean equals(Object other) {
    return getClass().equals(other.getClass());
  }

  @Override
  public String toString() {
    return "SortedSetValueStorer []";
  }

  @Override
  public Type unpack(BeanProperty property) {
    ParameterizedType parameterizedType = (ParameterizedType) property.getGenericType();
    return parameterizedType.getActualTypeArguments()[0];
  }
}
