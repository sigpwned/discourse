/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
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
    if (fromString == null)
      return false;

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
    if (classType == null)
      throw new IllegalArgumentException("Not a valid concrete class: " + genericType);
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
