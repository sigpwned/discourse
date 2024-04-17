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
package com.sigpwned.discourse.core.util;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * From joda-beans.
 *
 * @see <a
 * href="https://github.com/JodaOrg/joda-beans/blob/master/src/main/java/org/joda/beans/JodaBeanUtils.java">https://github.com/JodaOrg/joda-beans/blob/master/src/main/java/org/joda/beans/JodaBeanUtils.java</a>
 */
public final class JodaBeanUtils {

  private JodaBeanUtils() {
  }

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
