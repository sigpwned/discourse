package com.sigpwned.discourse.core;

@FunctionalInterface
public interface VersionFormatter {
  public String formatVersion(Command<?> command);
}
