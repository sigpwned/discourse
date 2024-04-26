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
import com.sigpwned.discourse.core.coordinate.Coordinate;
import com.sigpwned.discourse.core.coordinate.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.ShortSwitchNameCoordinate;
import com.sigpwned.discourse.core.util.Generated;
import java.util.Objects;
import java.util.Set;

public final class OptionConfigurationParameter extends ConfigurationParameter {

  private final ShortSwitchNameCoordinate shortName;
  private final LongSwitchNameCoordinate longName;

  public OptionConfigurationParameter(String name, String description, boolean required,
      ValueDeserializer<?> deserializer, ValueSink sink, ShortSwitchNameCoordinate shortName,
      LongSwitchNameCoordinate longName) {
    super(name, description, required, deserializer, sink);
    if (shortName == null && longName == null) {
      throw new IllegalArgumentException("no names");
    }
    this.shortName = shortName;
    this.longName = longName;
  }

  /**
   * @return the shortName
   */
  public ShortSwitchNameCoordinate getShortName() {
    return shortName;
  }

  /**
   * @return the longName
   */
  public LongSwitchNameCoordinate getLongName() {
    return longName;
  }

  @Override
  public boolean isValued() {
    return true;
  }

  @Override
  public Set<Coordinate> getCoordinates() {
    if (getShortName() == null) {
      return Set.of(getLongName());
    } else if (getLongName() == null) {
      return Set.of(getShortName());
    } else {
      return Set.of(getShortName(), getLongName());
    }
  }

  @Override
  @Generated
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(longName, shortName);
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
    OptionConfigurationParameter other = (OptionConfigurationParameter) obj;
    return Objects.equals(longName, other.longName) && Objects.equals(shortName, other.shortName);
  }
}
