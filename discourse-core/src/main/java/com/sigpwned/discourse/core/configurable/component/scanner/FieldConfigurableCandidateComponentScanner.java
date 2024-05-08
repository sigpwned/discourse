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
package com.sigpwned.discourse.core.configurable.component.scanner;

import com.sigpwned.discourse.core.configurable.CandidateConfigurableComponent;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

/**
 * A {@link ConfigurableCandidateComponentScanner} that scans for instance (i.e., non-static)
 * {@link Field fields}. This implementation does not care about the visibility of this field.
 */
public class FieldConfigurableCandidateComponentScanner implements
    ConfigurableCandidateComponentScanner {

  public static final FieldConfigurableCandidateComponentScanner INSTANCE = new FieldConfigurableCandidateComponentScanner();

  @Override
  public List<CandidateConfigurableComponent> scanForCandidateComponents(Class<?> rawType) {
    return Arrays.stream(rawType.getDeclaredFields())
        .filter(field -> !Modifier.isStatic(field.getModifiers()))
        .<CandidateConfigurableComponent>map(field -> new CandidateConfigurableComponent() {
          @Override
          public Object getCodeObject() {
            return field;
          }

          @Override
          public List<Annotation> getAnnotations() {
            return List.of(field.getAnnotations());
          }
        }).toList();
  }
}
