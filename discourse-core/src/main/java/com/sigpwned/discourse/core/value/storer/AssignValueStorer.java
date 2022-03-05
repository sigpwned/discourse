package com.sigpwned.discourse.core.value.storer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import com.sigpwned.discourse.core.ValueStorer;
import com.sigpwned.espresso.BeanProperty;

public class AssignValueStorer implements ValueStorer {
  public static final AssignValueStorer INSTANCE = new AssignValueStorer();

  @Override
  public boolean isStoreable(BeanProperty property) {
    return true;
  }

  @Override
  public void store(Object instance, BeanProperty property, Object value)
      throws InvocationTargetException {
    property.set(instance, value);
  }

  @Override
  public boolean isCollection() {
    return false;
  }
  
  @Override
  public int hashCode() {
    return 17;
  }
  
  @Override
  public boolean equals(Object other) {
    return getClass().equals(other.getClass());
  }

  @Override
  public String toString() {
    return "AssignValueStorer []";
  }

  @Override
  public Type unpack(BeanProperty property) {
    return property.getGenericType();
  }
}
