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
package com.sigpwned.discourse.core.module.core.scan.naming;

import java.lang.reflect.Constructor;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.NamingScheme;
import com.sigpwned.discourse.core.util.Maybe;
import com.sigpwned.discourse.core.util.Reflection;

public class DefaultConstructorNamingScheme implements NamingScheme {
  public static final DefaultConstructorNamingScheme INSTANCE =
      new DefaultConstructorNamingScheme();

  @Override
  public Maybe<String> name(Object object) {
    if (!(object instanceof Constructor constructor))
      return Maybe.maybe();
    if (!Reflection.hasDefaultConstructorSignature(constructor))
      return Maybe.maybe();

    // TODO how do we handle mixins?
    // TODO default constructor label
    return Maybe.yes("");
  }
}
