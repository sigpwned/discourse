package com.sigpwned.discourse.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import com.sigpwned.espresso.BeanProperty;

public interface ValueStorer {
  /**
   * @param genericType The generic type of the property
   * @param annotations The annotations on the method
   * @return true if this storer can store into the given type, or false otherwise
   */
  public boolean isStoreable(BeanProperty property);
  
  public boolean isCollection();

  public void store(Object instance, BeanProperty property, Object value) throws InvocationTargetException;
  
  public Type unpack(BeanProperty property);
}
