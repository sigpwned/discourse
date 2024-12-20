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

import static java.util.Collections.unmodifiableList;
import java.util.List;
import com.sigpwned.discourse.core.command.tree.LeafCommandProperty;
import com.sigpwned.discourse.core.util.MoreCollectors;


public class CommandBody {

  private final List<LeafCommandProperty> properties;
  private final List<NamedRule> rules;

  public CommandBody(List<LeafCommandProperty> properties, List<NamedRule> rules) {
    this.properties = unmodifiableList(properties);
    this.rules = unmodifiableList(rules);

    getProperties().stream().flatMap(p -> p.getCoordinates().stream())
        .collect(MoreCollectors.duplicates()).ifPresent(duplicateCoordinates -> {
          throw new IllegalArgumentException("Duplicate coordinates: " + duplicateCoordinates);
        });

    // Do we have any duplicate usage of property names?
    getProperties().stream().map(LeafCommandProperty::getName).collect(MoreCollectors.duplicates())
        .ifPresent(duplicatePropertyNames -> {
          throw new IllegalArgumentException("Duplicate property names: " + duplicatePropertyNames);
        });
  }

  public List<LeafCommandProperty> getProperties() {
    return properties;
  }

  public List<NamedRule> getRules() {
    return rules;
  }
}
