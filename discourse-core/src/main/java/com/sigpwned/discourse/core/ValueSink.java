package com.sigpwned.discourse.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

public interface ValueSink {
  public boolean isCollection();
  
  public Type getGenericType();

  public void write(Object instance, Object value) throws InvocationTargetException;
}
