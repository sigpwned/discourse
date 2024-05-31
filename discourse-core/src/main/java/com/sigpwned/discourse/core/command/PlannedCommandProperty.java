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

import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import java.util.Optional;
import java.util.Set;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.module.core.plan.value.deserializer.ValueDeserializer;
import com.sigpwned.discourse.core.module.core.plan.value.sink.ValueSink;

public class PlannedCommandProperty {

  /**
   * The name of the property, for example, {@code "help"}.
   */
  private final String name;

  /**
   * The description of the property, for example, {@code "Display this help message"}.
   */
  private final String description;

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

  private final ValueSink sink;

  private final ValueDeserializer<?> deserializer;

  public PlannedCommandProperty(String name, String description, Set<Coordinate> coordinates,
      ValueSink sink, ValueDeserializer<?> deserializer) {
    this.name = requireNonNull(name);
    this.description = description;
    this.coordinates = unmodifiableSet(coordinates);
    this.sink = requireNonNull(sink);
    this.deserializer = requireNonNull(deserializer);
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


  /**
   * @return the coordinates
   */
  public Set<Coordinate> getCoordinates() {
    return coordinates;
  }

  /**
   * @return the sink
   */
  public ValueSink getSink() {
    return sink;
  }

  /**
   * @return the deserializer
   */
  public ValueDeserializer<?> getDeserializer() {
    return deserializer;
  }
}
