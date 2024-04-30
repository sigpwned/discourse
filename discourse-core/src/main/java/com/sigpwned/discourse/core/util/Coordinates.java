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
package com.sigpwned.discourse.core.util;

import com.sigpwned.discourse.core.annotation.EnvironmentParameter;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.annotation.PropertyParameter;
import com.sigpwned.discourse.core.coordinate.Coordinate;
import com.sigpwned.discourse.core.coordinate.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.PositionCoordinate;
import com.sigpwned.discourse.core.coordinate.PropertyNameCoordinate;
import com.sigpwned.discourse.core.coordinate.ShortSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.SwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.VariableNameCoordinate;
import java.lang.annotation.Annotation;
import java.util.List;

// TODO Delete me?
public final class Coordinates {

  private Coordinates() {
  }

  /**
   * <p>
   * Converts the given
   * {@link ParameterAnnotations#isParameterAnnotation(Annotation) parameter annotation} to a list
   * of coordinates. The annotation must be one of the following:
   * </p>
   *
   * <ul>
   *   <li>{@link FlagParameter}</li>
   *   <li>{@link OptionParameter}</li>
   *   <li>{@link PositionalParameter}</li>
   *   <li>{@link EnvironmentParameter}</li>
   *   <li>{@link PropertyParameter}</li>
   * </ul>
   *
   * @param annotation the annotation to convert
   * @return the list of coordinates
   * @throws IllegalArgumentException if the annotation is not one of the expected types
   */
  public static List<? extends Coordinate> fromParameterAnnotation(Annotation annotation) {
    if (annotation instanceof FlagParameter flag) {
      return fromFlagParameterAnnotation(flag);
    }
    if (annotation instanceof OptionParameter option) {
      return fromOptionParameterAnnotation(option);
    }
    if (annotation instanceof PositionalParameter positional) {
      return List.of(fromPositionalParameterAnnotation(positional));
    }
    if (annotation instanceof EnvironmentParameter environment) {
      return List.of(fromEnvironmentParameterAnnotation(environment));
    }
    if (annotation instanceof PropertyParameter property) {
      return List.of(fromPropertyParameterAnnotation(property));
    }
    throw new IllegalArgumentException("unexpected annotation " + annotation);
  }

  public static List<SwitchNameCoordinate> fromFlagParameterAnnotation(FlagParameter flag) {
    if (flag.longName().isEmpty() && flag.shortName().isEmpty()) {
      return List.of();
    }
    if (flag.longName().isEmpty()) {
      return List.of(new ShortSwitchNameCoordinate(flag.shortName()));
    }
    if (flag.shortName().isEmpty()) {
      return List.of(new LongSwitchNameCoordinate(flag.longName()));
    }
    return List.of(new LongSwitchNameCoordinate(flag.longName()),
        new ShortSwitchNameCoordinate(flag.shortName()));
  }

  public static List<SwitchNameCoordinate> fromOptionParameterAnnotation(OptionParameter option) {
    if (option.longName().isEmpty() && option.shortName().isEmpty()) {
      return List.of();
    }
    if (option.longName().isEmpty()) {
      return List.of(new ShortSwitchNameCoordinate(option.shortName()));
    }
    if (option.shortName().isEmpty()) {
      return List.of(new LongSwitchNameCoordinate(option.longName()));
    }
    return List.of(new LongSwitchNameCoordinate(option.longName()),
        new ShortSwitchNameCoordinate(option.shortName()));
  }

  public static PositionCoordinate fromPositionalParameterAnnotation(
      PositionalParameter positional) {
    return new PositionCoordinate(positional.position());
  }

  public static VariableNameCoordinate fromEnvironmentParameterAnnotation(
      EnvironmentParameter environment) {
    return new VariableNameCoordinate(environment.variableName());
  }

  public static PropertyNameCoordinate fromPropertyParameterAnnotation(PropertyParameter property) {
    return new PropertyNameCoordinate(property.propertyName());
  }
}
