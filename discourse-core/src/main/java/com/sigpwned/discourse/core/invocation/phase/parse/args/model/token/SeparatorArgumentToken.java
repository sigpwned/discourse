/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
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
package com.sigpwned.discourse.core.invocation.phase.parse.args.model.token;

/**
 * A token "--" that indicates that no more flags or switches appear in the rest of the
 * resolvedCommand line arguments. It is very useful when (a) generating resolvedCommand lines programmatically, and
 * (b) to disambiguate between flags/switches and positional parameters that happen to start with
 * "-".
 */
public final class SeparatorArgumentToken extends ArgumentToken {

  public static final SeparatorArgumentToken INSTANCE = new SeparatorArgumentToken();

  public static final String TEXT = "--";

  public SeparatorArgumentToken() {
    super(TEXT);
  }
}
