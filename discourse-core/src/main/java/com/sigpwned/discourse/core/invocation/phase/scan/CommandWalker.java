package com.sigpwned.discourse.core.invocation.phase.scan;

public interface CommandWalker {

  <T> void walk(Class<T> clazz, CommandWalkerListener<T> listener);
}
