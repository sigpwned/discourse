/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 - 2024 Andy Boothe
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
package com.sigpwned.discourse.core.command;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import com.sigpwned.discourse.core.args.Coordinate;

public class LeafCommandProperty {

  /**
   * The name of the property, for example, {@code "help"}.
   */
  private final String name;

  /**
   * The description of the property, for example, {@code "Display this help message"}.
   */
  private final String description;

  private final boolean required;

  private final String defaultValue;

  /**
   * <p>
   * The syntax for specifying the value of this property. For example:
   * </p>
   *
   * <pre>
   * Map.of("-h", "flag", "--help", "flag")
   * </pre>
   */
  private final Set<Coordinate> coordinates;

  private final Type genericType;

  private final List<Annotation> annotations;

  public LeafCommandProperty(String name, String description, boolean required, String defaultValue,
      Set<Coordinate> coordinates, Type genericType, List<Annotation> annotations) {
    this.name = requireNonNull(name);
    this.description = description;
    this.required = required;
    this.defaultValue = defaultValue;
    this.coordinates = unmodifiableSet(coordinates);
    this.genericType = requireNonNull(genericType);
    this.annotations = unmodifiableList(annotations);
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the description
   */
  public Optional<String> getDescription() {
    return Optional.ofNullable(description);
  }

  public boolean isRequired() {
    return required;
  }

  public Optional<String> getDefaultValue() {
    return Optional.ofNullable(defaultValue);
  }

  public boolean isGuaranted() {
    return isRequired() || getDefaultValue().isPresent();
  }

  /**
   * @return the coordinates
   */
  public Set<Coordinate> getCoordinates() {
    return coordinates;
  }

  /**
   * @return the genericType
   */
  public Type getGenericType() {
    return genericType;
  }

  /**
   * @return the annotations
   */
  public List<Annotation> getAnnotations() {
    return annotations;
  }
}
