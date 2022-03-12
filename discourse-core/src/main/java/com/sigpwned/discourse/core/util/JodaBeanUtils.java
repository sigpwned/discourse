package com.sigpwned.discourse.core.util;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * From joda-beans.
 * 
 * @see https://github.com/JodaOrg/joda-beans/blob/master/src/main/java/org/joda/beans/JodaBeanUtils.java
 */
public final class JodaBeanUtils {
  private JodaBeanUtils() {}
  
  public static Class<?> eraseToClass(Type type) {
    if (type instanceof Class) {
      return (Class<?>) type;
  } else if (type instanceof ParameterizedType) {
      return eraseToClass(((ParameterizedType) type).getRawType());
  } else if (type instanceof GenericArrayType) {
      Type componentType = ((GenericArrayType) type).getGenericComponentType();
      Class<?> componentClass = eraseToClass(componentType);
      if (componentClass != null) {
          return Array.newInstance(componentClass, 0).getClass();
      }
  } else if (type instanceof TypeVariable) {
      Type[] bounds = ((TypeVariable<?>) type).getBounds();
      if (bounds.length == 0) {
          return Object.class;
      } else {
          return eraseToClass(bounds[0]);
      }
  }
  return null;    
  }
}
