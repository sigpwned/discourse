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
package com.sigpwned.discourse.core.pipeline.invocation.configurable.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.pipeline.invocation.configurable.step.scan.SyntaxDetection;

public record DetectedSyntax(Object nominated, Type genericType, List<Annotation> annotations,
    boolean required, boolean help, boolean version, Set<Coordinate> coordinates) {
  public static DetectedSyntax fromCandidateAndDetection(CandidateSyntax candidate,
      SyntaxDetection detection) {
    return new DetectedSyntax(candidate.nominated(), candidate.genericType(),
        candidate.annotations(), detection.required(), detection.help(), detection.version(),
        detection.coordinates());
  }
}
