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
package com.sigpwned.discourse.core.module.scan.naming;

import java.lang.reflect.Field;
import java.util.Optional;
import com.sigpwned.discourse.core.invocation.phase.scan.NamingScheme;

/**
 * <p>
 * An {@link AccessorNamingScheme} that uses field names to determine the names of attributes.
 * </p>
 *
 * <p>
 * This implementation does not check whether a field is visible or not.
 * </p>
 */
public class FieldNamingScheme implements NamingScheme {

  public static final FieldNamingScheme INSTANCE = new FieldNamingScheme();

  @Override
  public Optional<String> name(Object object) {
    if (!(object instanceof Field field)) {
      return Optional.empty();
    }
    return Optional.of(field.getName());
  }
}
