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
package com.sigpwned.discourse.core.pipeline.invocation.step.scan.model;

import static com.sigpwned.discourse.core.util.MoreCollectors.duplicates;
import static java.util.Objects.requireNonNull;
import java.util.Optional;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.command.tree.LeafCommandProperty;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.DuplicateCoordinatesScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.DuplicatePropertyNamesScanException;

public record PreparedClass<T>(Optional<SuperCommand<? super T>> supercommand, Class<T> clazz,
    Configurable configurable, Optional<CommandBody> body) {

  public PreparedClass {
    clazz = requireNonNull(clazz);
    configurable = requireNonNull(configurable);
    if (body.isPresent()) {
      final Class<T> theclazz = clazz;
      final CommandBody thebody = body.orElseThrow();

      // Do we have any duplicate usage of coordinates?
      thebody.getProperties().stream().flatMap(p -> p.getCoordinates().stream())
          .collect(duplicates()).ifPresent(duplicateCoordinates -> {
            throw new DuplicateCoordinatesScanException(theclazz, duplicateCoordinates);
          });

      // Do we have any duplicate usage of property names?
      thebody.getProperties().stream().map(LeafCommandProperty::getName).collect(duplicates())
          .ifPresent(duplicateNames -> {
            throw new DuplicatePropertyNamesScanException(theclazz, duplicateNames);
          });
    }
  }
}
