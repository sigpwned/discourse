package com.sigpwned.discourse.core.command2.impl;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.command2.CommandWalker;
import com.sigpwned.discourse.core.command2.CommandWalkerListener;
import com.sigpwned.discourse.core.util.Discriminators;
import java.lang.reflect.Modifier;
import java.util.Map;

public class DefaultCommandWalker implements CommandWalker {

  private final SubCommandScanner scanner;

  public DefaultCommandWalker(SubCommandScanner scanner) {
    this.scanner = requireNonNull(scanner);
  }

  private SubCommandScanner getScanner() {
    return scanner;
  }

  @Override
  public <T> void walk(Class<T> clazz, CommandWalkerListener<T> listener) {
    listener.beginWalk(clazz);
    subwalk(null, clazz, listener);
    listener.endWalk(clazz);
  }

  protected <T, U extends T> void subwalk(String discriminator, Class<U> clazz,
      CommandWalkerListener<T> listener) {
    Configurable configurable = clazz.getAnnotation(Configurable.class);
    if (configurable == null) {
      throw new IllegalArgumentException("clazz must be a Configurable");
    }

    Map<String, Class<? extends U>> subcommands = getScanner().scanForSubCommands(clazz);

    if (subcommands.isEmpty()) {
      // This is a leaf node. It doesn't have to be concrete, since an abstract class with a factory
      // method is perfectly valid, so we don't check for that here. As a result, this can be either
      // a class or an interface, too, so we don't check for that here either.
    } else {
      // This is a non-leaf node. It must be abstract. It can be a class or an interface.
      if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
        throw new IllegalArgumentException("Non-leaf command " + clazz + " must be abstract");
      }
      for (Map.Entry<String, Class<? extends U>> entry : subcommands.entrySet()) {
        String subdiscriminator = entry.getKey();
        Class<? extends U> subclazz = entry.getValue();
        if (!Discriminators.isValid(subdiscriminator)) {
          throw new IllegalArgumentException("Invalid discriminator: " + subdiscriminator);
        }
        if (!clazz.isAssignableFrom(subclazz)) {
          throw new IllegalArgumentException(
              "Subcommand " + subclazz + " is not a subclass of " + clazz);
        }
      }
    }

    listener.enterClazz(discriminator, clazz, configurable, subcommands);

    listener.visitClazz(discriminator, clazz, configurable, subcommands);

    if (!subcommands.isEmpty()) {
      for (Map.Entry<String, Class<? extends U>> subcommand : subcommands.entrySet()) {
        subwalk(subcommand.getKey(), subcommand.getValue(), listener);
      }
    }

    listener.leaveClazz(discriminator, clazz, configurable, subcommands);
  }
}
