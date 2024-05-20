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
package com.sigpwned.discourse.core;

import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.invocation.phase.parse.preprocess.ArgsPreprocessor;
import com.sigpwned.discourse.core.invocation.phase.parse.preprocess.CoordinatesPreprocessor;
import com.sigpwned.discourse.core.invocation.phase.parse.preprocess.TokenStreamPreprocessor;
import com.sigpwned.discourse.core.invocation.phase.resolve.CommandResolver;
import com.sigpwned.discourse.core.invocation.phase.scan.NamingScheme;
import com.sigpwned.discourse.core.invocation.phase.scan.RulesEngine;
import com.sigpwned.discourse.core.invocation.phase.scan.SubCommandScanner;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleDetector;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleNominator;
import com.sigpwned.discourse.core.invocation.phase.scan.syntax.SyntaxDetector;
import com.sigpwned.discourse.core.invocation.phase.scan.syntax.SyntaxNominator;
import com.sigpwned.discourse.core.module.value.deserializer.ValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.sink.ValueSinkFactory;

/**
 * An invocation context that provides information about the current invocation environment. This is
 * a collection of key-value pairs, where the keys are strings and the values are arbitrary objects.
 * This is used to pass information between different parts of the system, such as the help
 * formatter, the version formatter, and the error stream.
 */
public interface InvocationContext {

  public static record Key<T>(String name, Class<T> type) {

    public static <T> Key<T> of(String name, Class<T> type) {
      return new Key<>(name, type);
    }

    public Key {
      name = requireNonNull(name);
      type = requireNonNull(type);
    }
  }

  public Syntax getSyntax();

  public SubCommandScanner getSubCommandScanner();

  public SyntaxNominator getSyntaxNominator();

  public SyntaxDetector getSyntaxDetector();

  public RuleNominator getRuleNominator();

  public RuleDetector getRuleDetector();

  public NamingScheme getNamingScheme();

  public ValueSinkFactory getValueSinkFactory();

  public ValueDeserializerFactory<?> getValueDeserializerFactory();

  public CommandResolver getCommandResolver();

  public CoordinatesPreprocessor getCoordinatesPreprocessor();

  public ArgsPreprocessor getArgsPreprocessor();

  public TokenStreamPreprocessor getTokenStreamPreprocessor();

  public RulesEngine getRulesEngine();
}
