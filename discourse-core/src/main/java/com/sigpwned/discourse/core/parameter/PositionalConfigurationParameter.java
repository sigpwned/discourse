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
package com.sigpwned.discourse.core.parameter;

import com.sigpwned.discourse.core.value.deserializer.ValueDeserializer;
import com.sigpwned.discourse.core.value.sink.ValueSink;
import com.sigpwned.discourse.core.model.coordinate.Coordinate;
import com.sigpwned.discourse.core.model.coordinate.PositionCoordinate;
import com.sigpwned.discourse.core.util.Generated;
import java.util.Objects;
import java.util.Set;

/**
 * A {@link ConfigurationParameter} that is configured by a positional argument.
 */
public final class PositionalConfigurationParameter extends ConfigurationParameter {

  private final PositionCoordinate position;

  public PositionalConfigurationParameter(String name, String description, boolean required,
      ValueDeserializer<?> deserializer, ValueSink sink, PositionCoordinate position) {
    super(name, description, required, deserializer, sink);
    this.position = position;
  }

  /**
   * @return the position
   */
  public PositionCoordinate getPosition() {
    return position;
  }

  @Override
  public boolean isValued() {
    return false;
  }

  @Override
  public Set<Coordinate> getCoordinates() {
    return Set.of(getPosition());
  }

  @Override
  @Generated
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(position);
    return result;
  }

  @Override
  @Generated
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    PositionalConfigurationParameter other = (PositionalConfigurationParameter) obj;
    return Objects.equals(position, other.position);
  }
}
