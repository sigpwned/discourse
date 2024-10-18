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

import static java.util.Collections.unmodifiableSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import com.sigpwned.discourse.core.ConfigurationParameter;
import com.sigpwned.discourse.core.Coordinate;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueSink;
import com.sigpwned.discourse.core.coordinate.NameCoordinate;
import com.sigpwned.discourse.core.coordinate.name.switches.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.name.switches.ShortSwitchNameCoordinate;
import com.sigpwned.discourse.core.util.Generated;

public class FlagConfigurationParameter extends ConfigurationParameter {
  private final ShortSwitchNameCoordinate shortName;
  private final LongSwitchNameCoordinate longName;
  private final boolean help;
  private final boolean version;

  public FlagConfigurationParameter(String name, String description,
      ValueDeserializer<?> deserializer, ValueSink sink, ShortSwitchNameCoordinate shortName,
      LongSwitchNameCoordinate longName, boolean help, boolean version) {
    super(Type.FLAG, name, description, false, deserializer, sink);
    if (shortName == null && longName == null)
      throw new IllegalArgumentException("no names");
    this.shortName = shortName;
    this.longName = longName;
    this.help = help;
    this.version = version;
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
  
  /**
   * @return the help
   */
  public boolean isHelp() {
    return help;
  }

  /**
   * @return the version
   */
  public boolean isVersion() {
    return version;
  }

  @Override
  public boolean isValued() {
    return false;
  }

  @Override
  public Set<Coordinate> getCoordinates() {
    Set<NameCoordinate> result = new HashSet<>(2);
    if (getShortName() != null)
      result.add(getShortName());
    if (getLongName() != null)
      result.add(getLongName());
    return unmodifiableSet(result);
  }

  @Override
  @Generated
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(help, longName, shortName, version);
    return result;
  }

  @Override
  @Generated
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    FlagConfigurationParameter other = (FlagConfigurationParameter) obj;
    return help == other.help && Objects.equals(longName, other.longName)
        && Objects.equals(shortName, other.shortName) && version == other.version;
  }
}
