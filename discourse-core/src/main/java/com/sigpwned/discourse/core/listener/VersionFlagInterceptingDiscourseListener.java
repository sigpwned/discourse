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
package com.sigpwned.discourse.core.listener;

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.format.version.DefaultVersionFormatter;
import com.sigpwned.discourse.core.format.version.VersionFormatter;
import com.sigpwned.discourse.core.model.invocation.MultiCommandDereference;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.util.Args;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;

/**
 * A {@link DiscourseListener} that looks for the presence of a
 * {@link FlagParameter#version() version flag} in the arguments, and prints the version message if
 * it is found. This interceptor runs during the
 * {@link DiscourseListener#beforeParse(Command, List, SingleCommand, List, InvocationContext)
 * beforeParse} event.
 */
public class VersionFlagInterceptingDiscourseListener implements DiscourseListener {

  public static final VersionFlagInterceptingDiscourseListener INSTANCE = new VersionFlagInterceptingDiscourseListener();

  @Override
  public <T> void beforeParse(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<String> remainingArgs,
      InvocationContext context) {
    // Look for the version flag in the resolved command.
    Optional<FlagConfigurationParameter> maybeVersionFlag = resolvedCommand.findVersionFlag();
    if (maybeVersionFlag.isEmpty()) {
      // There's no help flag, so there's no way to ask for help!
      return;
    }

    // Given the version flag, look for it in the remaining arguments.
    FlagConfigurationParameter versionFlag = maybeVersionFlag.orElseThrow();

    if (Args.containsFlag(remainingArgs, versionFlag.getShortName(), versionFlag.getLongName())) {
      VersionFormatter formatter = context.get(InvocationContext.VERSION_FORMATTER_KEY)
          .orElse(DefaultVersionFormatter.INSTANCE);
      PrintStream out = context.get(InvocationContext.ERROR_STREAM_KEY).orElse(System.err);
      out.print(formatter.formatVersion(resolvedCommand));
      out.flush();
      // Note we do not exit here
    }
  }
}
