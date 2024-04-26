package com.sigpwned.discourse.core.listener;

import static java.util.stream.Collectors.toSet;

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.format.version.DefaultVersionFormatter;
import com.sigpwned.discourse.core.format.version.VersionFormatter;
import com.sigpwned.discourse.core.model.invocation.MultiCommandDereference;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.util.Streams;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    Set<String> coordinates = resolvedCommand.getParameters().stream()
        .mapMulti(Streams.filterAndCast(FlagConfigurationParameter.class))
        .filter(FlagConfigurationParameter::isVersion).flatMap(flag -> Streams.concat(
            Optional.ofNullable(flag.getShortName()).map(Object::toString).stream(),
            Optional.ofNullable(flag.getLongName()).map(Object::toString).stream()))
        .collect(toSet());
    if (coordinates.stream().anyMatch(remainingArgs::contains)) {
      VersionFormatter formatter = context.get(InvocationContext.VERSION_FORMATTER_KEY)
          .orElse(DefaultVersionFormatter.INSTANCE);
      PrintStream out = context.get(InvocationContext.ERROR_STREAM_KEY).orElse(System.err);
      out.print(formatter.formatVersion(resolvedCommand));
      out.flush();
      // Note we do not exit here
    }
  }
}
