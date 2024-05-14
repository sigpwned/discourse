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
package com.sigpwned.discourse.core.invocation.phase.parse.parse.model.token;

/**
 * A logical "end of input" token. This token does not appear in the input, but is added to the
 * arguments during parsing to demarcate the end of the input.
 */
public final class EofArgumentToken extends ArgumentToken {

  public static final EofArgumentToken INSTANCE = new EofArgumentToken();

  public static final String TEXT = "$";

  public EofArgumentToken() {
    super(TEXT);
  }
}