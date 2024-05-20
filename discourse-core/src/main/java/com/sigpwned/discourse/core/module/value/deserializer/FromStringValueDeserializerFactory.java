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
package com.sigpwned.discourse.core.module.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.util.JodaBeanUtils;

/**
 * <p>
 * A value deserializer factory that uses a static fromString method to deserialize objects. This
 * implementation is capable deserializing instances of any class T with a method that matches the
 * following signature:
 * </p>
 *
 * <pre>
 * public static U fromString(String s);
 * </pre>
 * 
 * <p>
 * ...where U extends T.
 * </p>
 */
public class FromStringValueDeserializerFactory implements ValueDeserializerFactory<Object> {

  public static final FromStringValueDeserializerFactory INSTANCE =
      new FromStringValueDeserializerFactory();

  @Override
  public Optional<ValueDeserializer<? extends Object>> getDeserializer(Type genericType,
      List<Annotation> annotations) {
    // Get the raw class type of the generic type.
    Class<?> classType = JodaBeanUtils.eraseToClass(genericType);
    if (classType == null) {
      // There's nothing wrong with this. It just means that we can't resolve the raw type of this
      // generic type. It may be a type parameter, for example.
      return Optional.empty();
    }

    // Get the fromString method. If the raw type were T, then it should have a signature like:
    //
    // public static U fromString(String s);
    //
    // Where U extends T. If no such method exists, then we can't deserialize this class.
    Method fromString;
    try {
      fromString = classType.getMethod("fromString", String.class);
    } catch (NoSuchMethodException e) {
      // This class has no fromString method. That's fine. It just means that this class is not
      // deserializable by this factory.
      return Optional.empty();
    }
    if (!Modifier.isStatic(fromString.getModifiers())) {
      // The class has a fromString method, but it's not static. That's fine. It just means that
      // this class is not deserializable by this factory.
      return Optional.empty();
    }
    if (!Modifier.isPublic(fromString.getModifiers())) {
      // The class has a fromString method, but it's not public. That's fine. It just means that
      // this class is not deserializable by this factory.
      return Optional.empty();
    }
    if (!classType.isAssignableFrom(fromString.getReturnType())) {
      // The class has a fromString method, but it's return type is not assignable to the class
      // type. That's fine. It just means that this class is not deserializable by this factory.
      return Optional.empty();
    }


    return Optional.of(s -> {
      try {
        return fromString.invoke(null, s);
      } catch (IllegalAccessException e) {
        // We confirmed that this is public. This should never happen.
        throw new AssertionError("illegal access", e);
      } catch (IllegalArgumentException e) {
        // This is fine. Let it through.
        throw e;
      } catch (InvocationTargetException e) {
        // TODO Should we use a better exception here?
        throw new RuntimeException("Failed to deserialize object", e);
      }
    });
  }
}
