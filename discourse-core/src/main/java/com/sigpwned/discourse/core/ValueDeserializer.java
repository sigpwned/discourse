package com.sigpwned.discourse.core;

@FunctionalInterface
public interface ValueDeserializer<T> {
  public T deserialize(String value);
}
