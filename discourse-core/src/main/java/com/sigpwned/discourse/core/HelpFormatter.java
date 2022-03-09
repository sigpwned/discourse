package com.sigpwned.discourse.core;

@FunctionalInterface
public interface HelpFormatter {
  public String formatHelp(Command<?> command);
}
