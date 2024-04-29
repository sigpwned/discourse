package com.sigpwned.discourse.core.listener;

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.CommandWalker;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.exception.configuration.MultipleHelpFlagsConfigurationException;
import com.sigpwned.discourse.core.model.command.Discriminator;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.util.Streams;

public class MultipleHelpFlagsCheckingDiscourseListener implements DiscourseListener {

  public static final MultipleHelpFlagsCheckingDiscourseListener INSTANCE = new MultipleHelpFlagsCheckingDiscourseListener();

  @Override
  public <T> void afterScan(Command<T> rootCommand, InvocationContext context) {
    checkForMultipleHelpFlags(rootCommand);
  }

  /**
   * Check for multiple help flags in the given command. We break this out into a separate method so
   * that it can be overridden by subclasses without worrying about where it is called in the
   * listener lifecycle.
   */
  protected <T> void checkForMultipleHelpFlags(Command<T> command) {
    new CommandWalker().walk(command, new CommandWalker.Visitor<>() {
      @Override
      public void singleCommand(Discriminator discriminator, SingleCommand<? extends T> command) {
        if (command.getParameters().stream()
            .mapMulti(Streams.filterAndCast(FlagConfigurationParameter.class))
            .filter(FlagConfigurationParameter::isHelp).count() > 1) {
          throw new MultipleHelpFlagsConfigurationException(command.getRawType());
        }
      }
    });
  }
}
