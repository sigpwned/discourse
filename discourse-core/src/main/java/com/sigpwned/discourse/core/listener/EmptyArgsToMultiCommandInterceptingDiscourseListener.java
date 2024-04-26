package com.sigpwned.discourse.core.listener;

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.core.format.help.DefaultHelpFormatter;
import com.sigpwned.discourse.core.format.help.HelpFormatter;
import java.io.PrintStream;
import java.util.List;

/**
 * A {@link DiscourseListener} that looks for the presence of an empty argument list when a
 * {@link MultiCommand} is the root command, and prints the help message and exits if it is found. A
 * {@link MultiCommand} always requires at least one argument (the discriminator) to resolve to a
 * subcommand that can be executed. If no arguments are provided, the user probably just wants help.
 * So oblige by printing the help message and exiting.
 */
public class EmptyArgsToMultiCommandInterceptingDiscourseListener implements DiscourseListener {

  public static final EmptyArgsToMultiCommandInterceptingDiscourseListener INSTANCE = new EmptyArgsToMultiCommandInterceptingDiscourseListener();

  @Override
  public <T> void beforeResolve(Command<T> rootCommand, List<String> args,
      InvocationContext context) {
    if (rootCommand instanceof MultiCommand<T> multiCommand && args.isEmpty()) {
      HelpFormatter formatter = context.get(InvocationContext.HELP_FORMATTER_KEY)
          .orElse(DefaultHelpFormatter.INSTANCE);
      PrintStream err = context.get(InvocationContext.ERROR_STREAM_KEY).orElse(System.err);
      err.print(formatter.formatHelp(rootCommand));
      err.flush();
      System.exit(0);
    }
  }
}
