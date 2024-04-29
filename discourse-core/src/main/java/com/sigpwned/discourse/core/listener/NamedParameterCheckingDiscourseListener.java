package com.sigpwned.discourse.core.listener;

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.CommandWalker;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.coordinate.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.ShortSwitchNameCoordinate;
import com.sigpwned.discourse.core.exception.configuration.DuplicateCoordinateConfigurationException;
import com.sigpwned.discourse.core.model.command.Discriminator;
import com.sigpwned.discourse.core.util.Streams;
import java.util.List;

public class NamedParameterCheckingDiscourseListener implements DiscourseListener {

  public static final NamedParameterCheckingDiscourseListener INSTANCE = new NamedParameterCheckingDiscourseListener();

  @Override
  public <T> void afterScan(Command<T> rootCommand, InvocationContext context) {
    checkNames(rootCommand);
  }

  /**
   * Make sure named parameters are valid. We break this out into a separate method so that it can
   * be overridden by subclasses without worrying about where it is called in the listener
   * lifecycle.
   */
  protected <T> void checkNames(Command<T> command) {
    new CommandWalker().walk(command, new CommandWalker.Visitor<T>() {
      @Override
      public void singleCommand(Discriminator discriminator, SingleCommand<? extends T> command) {
        List<ShortSwitchNameCoordinate> shortNames = command.getParameters().stream()
            .flatMap(p -> p.getCoordinates().stream())
            .mapMulti(Streams.filterAndCast(ShortSwitchNameCoordinate.class)).toList();
        if (Streams.duplicates(shortNames.stream()).findAny().isPresent()) {
          throw new DuplicateCoordinateConfigurationException(
              Streams.duplicates(shortNames.stream()).findFirst().orElseThrow());
        }

        List<LongSwitchNameCoordinate> longNames = command.getParameters().stream()
            .flatMap(p -> p.getCoordinates().stream())
            .mapMulti(Streams.filterAndCast(LongSwitchNameCoordinate.class)).toList();
        if (Streams.duplicates(longNames.stream()).findAny().isPresent()) {
          throw new DuplicateCoordinateConfigurationException(
              Streams.duplicates(longNames.stream()).findFirst().orElseThrow());
        }
      }
    });
  }
}
