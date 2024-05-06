package com.sigpwned.discourse.core.configuration;

public interface InstanceBuilder {
  // TODO should this include a method to list dependencies, maybe with types?

  public static interface Factory {

    public InstanceBuilder newInstanceBuilder();
  }

  public void add(String name, Object value);

  public Object build();
}
