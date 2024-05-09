package com.sigpwned.discourse.core.command2;

public interface CommandWalker {

  <T> void walk(Class<T> clazz, CommandWalkerListener<T> listener);
}
