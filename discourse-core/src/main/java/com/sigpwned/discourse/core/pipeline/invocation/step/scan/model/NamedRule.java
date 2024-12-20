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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Definition of a configurable rule.
 *
 * @param humanReadableName A human-readable name for the rule. Used only for user feedback.
 * @param nominated
 * @param genericType
 * @param annotations
 * @param antecedents
 * @param consequent
 */
public record NamedRule(String humanReadableName, Object nominated, Type genericType,
    List<Annotation> annotations, Set<String> antecedents, Set<Set<String>> conditions,
    Optional<String> consequent) {

  public static NamedRule fromDetectedRule(DetectedRule rule, String name) {
    return new NamedRule(rule.humanReadableName(), rule.nominated(), rule.genericType(),
        rule.annotations(), rule.antecedents(), rule.conditions(), Optional.ofNullable(name));
  }
}
