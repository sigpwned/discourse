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
package com.sigpwned.discourse.core.util.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.SortedSet;
import com.sigpwned.discourse.core.util.Types;

public class SortedSetType {
  public static SortedSetType parse(Type genericType) {
    if (genericType instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) genericType;
      if (!parameterizedType.getRawType().equals(SortedSet.class))
        throw new IllegalArgumentException("Not a SortedSet type");
      Type elementType = parameterizedType.getActualTypeArguments()[0];
      return of(elementType);
    } else {
      throw new IllegalArgumentException("Not a parameterized type");
    }
  }

  public static SortedSetType of(Type elementType) {
    return new SortedSetType(elementType);
  }

  private final Type elementType;

  public SortedSetType(Type elementType) {
    if (!Types.isConcrete(elementType))
      throw new IllegalArgumentException("elementType must be concrete");
    this.elementType = elementType;
  }

  /**
   * @return the elementType
   */
  public Type getElementType() {
    return elementType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(elementType);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SortedSetType other = (SortedSetType) obj;
    return Objects.equals(elementType, other.elementType);
  }

  @Override
  public String toString() {
    return "GenericSortedSetType [elementType=" + elementType + "]";
  }
}
