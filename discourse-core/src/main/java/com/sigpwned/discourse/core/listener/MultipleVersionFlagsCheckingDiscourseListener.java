package com.sigpwned.discourse.core.listener;

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.CommandWalker;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.exception.configuration.MultipleVersionFlagsConfigurationException;
import com.sigpwned.discourse.core.model.command.Discriminator;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.util.Streams;

public class MultipleVersionFlagsCheckingDiscourseListener implements DiscourseListener {

  public static final MultipleVersionFlagsCheckingDiscourseListener INSTANCE = new MultipleVersionFlagsCheckingDiscourseListener();

  @Override
  public <T> void afterScan(Command<T> rootCommand, InvocationContext context) {
    checkForMultipleVersionFlags(rootCommand);
  }

  /**
   * Check for multiple version flags in the given command. We break this out into a separate method
   * so that it can be overridden by subclasses without worrying about where it is called in the
   * listener lifecycle.
   */
  protected <T> void checkForMultipleVersionFlags(Command<T> command) {
    new CommandWalker().walk(command, new CommandWalker.Visitor<T>() {
      @Override
      public void singleCommand(Discriminator discriminator, SingleCommand<? extends T> command) {
        if (command.getParameters().stream()
            .mapMulti(Streams.filterAndCast(FlagConfigurationParameter.class))
            .filter(FlagConfigurationParameter::isVersion).count() > 1) {
          throw new MultipleVersionFlagsConfigurationException(command.getRawType());
        }
      }
    });
  }
}
