package com.sigpwned.discourse.core.format.exception;

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.exception.syntax.UnrecognizedDiscriminatorSyntaxException;
import com.sigpwned.discourse.core.format.help.DefaultHelpFormatter;
import com.sigpwned.discourse.core.format.help.HelpFormatter;
import com.sigpwned.discourse.core.util.Discriminators;
import java.io.PrintStream;

/**
 * An {@link ExceptionFormatter} that handles {@link UnrecognizedDiscriminatorSyntaxException}s when
 * the unrecognized discriminator is exactly {@link Discriminators#HELP}. The formatter works under
 * the interpretation that the user is asking for help, and prints the help message for the command
 * and exits.
 */
public class HelpUnrecognizedDiscriminatorSyntaxExceptionFormatter implements ExceptionFormatter {

  public static final HelpUnrecognizedDiscriminatorSyntaxExceptionFormatter INSTANCE = new HelpUnrecognizedDiscriminatorSyntaxExceptionFormatter();

  @Override
  public boolean handlesException(Throwable e, InvocationContext context) {
    if (e instanceof UnrecognizedDiscriminatorSyntaxException syntax) {
      return syntax.getDiscriminator().equals(Discriminators.HELP);
    }
    return false;
  }

  @Override
  public void formatException(Throwable e, InvocationContext context) {
    UnrecognizedDiscriminatorSyntaxException syntax = (UnrecognizedDiscriminatorSyntaxException) e;

    PrintStream err = context.get(InvocationContext.ERROR_STREAM_KEY).orElse(System.err);
    HelpFormatter formatter = context.get(InvocationContext.HELP_FORMATTER_KEY)
        .orElse(DefaultHelpFormatter.INSTANCE);
    err.print(formatter.formatHelp(syntax.getCommand()));
    err.flush();

    // TODO Should we use a different exit code?
    System.exit(0);
  }
}
