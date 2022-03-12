package com.sigpwned.discourse.core.util;

import static java.util.Arrays.asList;
import java.util.List;
import com.sigpwned.discourse.core.ArgumentException;
import com.sigpwned.discourse.core.Command;
import com.sigpwned.discourse.core.CommandBuilder;
import com.sigpwned.discourse.core.ConfigurationException;
import com.sigpwned.discourse.core.SyntaxException;
import com.sigpwned.discourse.core.format.help.DefaultHelpFormatter;

public final class Discourse {
  private Discourse() {}

  public static <T> T configuration(Class<T> rawType, String[] args) {
    return configuration(rawType, asList(args));
  }

  public static <T> T configuration(Class<T> rawType, List<String> args) {
    Command<T> command;
    try {
      command = new CommandBuilder().build(rawType);
    } catch (ConfigurationException e) {
      e.printStackTrace(System.err);
      System.exit(1);
      throw new AssertionError("exit");
    }

    T result;
    try {
      result = command.args(args).printHelp().printVersion().configuration();
    } catch (SyntaxException e) {
      System.err.println("ERROR: " + e.getMessage());
      if (args.size() == 0)
        System.err.println(DefaultHelpFormatter.INSTANCE);
      System.exit(2);
      throw new AssertionError("exit");
    } catch (ArgumentException e) {
      System.err.println("ERROR: " + e.getMessage());
      System.exit(3);
      throw new AssertionError("exit");
    } catch (RuntimeException e) {
      System.err.println("ERROR: " + e.getMessage());
      System.exit(4);
      throw new AssertionError("exit");
    }

    return result;
  }
}
