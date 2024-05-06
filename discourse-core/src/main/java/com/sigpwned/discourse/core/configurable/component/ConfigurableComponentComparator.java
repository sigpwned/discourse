package com.sigpwned.discourse.core.configurable.component;

import java.util.Comparator;

public class ConfigurableComponentComparator implements Comparator<ConfigurableComponent> {

  public static final ConfigurableComponentComparator INSTANCE = new ConfigurableComponentComparator();

  private static final Comparator<ConfigurableComponent> DELEGATE = Comparator.comparing(
          ConfigurableComponent::getDeclaringClass, InheritanceClassComparator.INSTANCE)
      .thenComparingInt(c -> -c.getElements().size()).thenComparing(c -> c.getClass().getName());

  @Override
  public int compare(ConfigurableComponent a, ConfigurableComponent b) {
    return DELEGATE.compare(a, b);
  }
}
