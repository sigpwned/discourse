package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueDeserializerFactory;

public class FromStringValueDeserializerFactory implements ValueDeserializerFactory<Object> {
  public static final FromStringValueDeserializerFactory INSTANCE =
      new FromStringValueDeserializerFactory();

  @Override
  public boolean isDeserializable(Type genericType, List<Annotation> annotations) {
    Class<?> classType = getClassType(genericType);
    if (classType == null)
      return false;

    if (!Modifier.isPublic(classType.getModifiers()))
      return false;
    if (Modifier.isAbstract(classType.getModifiers()))
      return false;

    Method fromString = getFromStringMethod(classType);

    if (!Modifier.isPublic(fromString.getModifiers()))
      return false;
    if (!Modifier.isStatic(fromString.getModifiers()))
      return false;
    if (!fromString.getReturnType().equals(classType))
      return false;

    return true;
  }

  @Override
  public ValueDeserializer<Object> getDeserializer(Type genericType, List<Annotation> annotations) {
    Class<?> classType = getClassType(genericType);
    Method fromString = getFromStringMethod(classType);
    return s -> {
      try {
        return fromString.invoke(null, s);
      } catch (IllegalAccessException e) {
        // We confirmed that this is public. It should never happen.
        throw new AssertionError("illegal access", e);
      } catch (IllegalArgumentException e) {
        // This is fine. Let it through.
        throw e;
      } catch (InvocationTargetException e) {
        // TODO Should we use a better exception here?
        throw new RuntimeException("Failed to deserialize object", e);
      }
    };
  }

  private static Class<?> getClassType(Type genericType) {
    if (genericType instanceof Class<?>)
      return (Class<?>) genericType;
    if (genericType instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) genericType;
      return getClassType(parameterizedType.getRawType());
    }
    return null;
  }

  private static Method getFromStringMethod(Class<?> classType) {
    try {
      return classType.getMethod("fromString", String.class);
    } catch (NoSuchMethodException e) {
      return null;
    }
  }
}
