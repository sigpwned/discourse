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

/**
 * Represents a candidate syntax element that might generate new syntax.
 * 
 * @param humanFacingName The human-facing name of the candidate. This is purely for user feedback
 *        and is not used in the actual syntax generation.
 * @param nominated The nominated object that might generate new syntax.
 * @param genericType The generic type of the nominated object.
 * @param annotations The annotations of the nominated object.
 */
public record CandidateSyntax(String humanFacingName, Object nominated, Type genericType,
    List<Annotation> annotations) {

}
