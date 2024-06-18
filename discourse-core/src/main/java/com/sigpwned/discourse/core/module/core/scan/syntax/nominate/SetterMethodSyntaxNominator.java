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
package com.sigpwned.discourse.core.module.core.scan.syntax.nominate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxNominator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.CandidateSyntax;
import com.sigpwned.discourse.core.util.Reflection;

/**
 * A {@link SyntaxNominator} that nominates methods with a getter-like signature. This
 * implementation will nominate all non-static methods of the given class and all of its ancestors
 * that have one parameter and a void return type regardless of method name, type, or visibility.
 * 
 * @see Reflection#hasInstanceSetterSignature(Method)
 */
public class SetterMethodSyntaxNominator implements SyntaxNominator {
  public static final SetterMethodSyntaxNominator INSTANCE = new SetterMethodSyntaxNominator();

  @Override
  public List<CandidateSyntax> nominateSyntax(Class<?> clazz, InvocationContext context) {
    List<CandidateSyntax> result = new ArrayList<>();

    for (Class<?> ancestor = clazz; ancestor != null; ancestor = ancestor.getSuperclass()) {
      for (Method method : ancestor.getDeclaredMethods()) {
        if (Reflection.hasInstanceSetterSignature(method)) {
          result.add(new CandidateSyntax(method.getName(), method,
              method.getGenericParameterTypes()[0], List.of(method.getAnnotations())));
        }
      }
    }

    return result;
  }
}
