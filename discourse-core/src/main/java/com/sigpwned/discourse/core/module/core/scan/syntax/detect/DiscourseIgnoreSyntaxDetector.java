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
package com.sigpwned.discourse.core.module.core.scan.syntax.detect;

import com.sigpwned.discourse.core.annotation.DiscourseIgnore;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxDetection;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxDetector;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.CandidateSyntax;
import com.sigpwned.discourse.core.util.Maybe;

/**
 * <p>
 * An {@link SyntaxDetector syntax detector} that uses the {@link DiscourseIgnore} annotation to
 * determine if a {@CandidateRule candidate} should be ignored.
 * </p>
 *
 * <p>
 * When searching {@link CandidateSyntax syntax candidates} for configurable properties, this
 * detector will look for {@code DiscourseIgnore}, and if it is present, then it will return
 * {@link Maybe#no() no}, effectively telling the framework to ignore the candidate. Otherwise, it
 * will return {@link Maybe#maybe() maybe}.
 * </p>
 */
public class DiscourseIgnoreSyntaxDetector implements SyntaxDetector {
  public static final DiscourseIgnoreSyntaxDetector INSTANCE = new DiscourseIgnoreSyntaxDetector();

  @Override
  public Maybe<SyntaxDetection> detectSyntax(Class<?> clazz, CandidateSyntax candidate,
      InvocationContext context) {
    // If we find a @DiscourseIgnore annotation on this candidate, then return a "poison" value.
    // This tells the framework that this particular candidate does not have a rule, and it should
    // stop looking. In other words: Ignore this candidate.
    if (candidate.annotations().stream().anyMatch(a -> a instanceof DiscourseIgnore))
      return Maybe.no();

    // Otherwise, tell it that we didn't find a rule, but it should keep looking.
    return Maybe.maybe();
  }
}
