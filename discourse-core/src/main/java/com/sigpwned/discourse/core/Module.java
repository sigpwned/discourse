package com.sigpwned.discourse.core;

/**
 * Container for extending Discourse to understand new argument types and containers
 */
public abstract class Module {
  /**
   * Override me with calls to register new functionality
   */
  public void register(SerializationContext context) {
    // b.registerDeserializer(...);
  }

  /**
   * Override me with calls to register new functionality
   */
  public void register(SinkContext context) {
    // b.registerSink(...);
  }
}
