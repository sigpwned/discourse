package com.sigpwned.discourse.core.command;

import static java.util.Collections.*;

import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.model.NamedRule;
import java.util.List;

public class CommandBody<T> {

  private final List<CommandProperty> properties;
  private final List<NamedRule> rules;

  public CommandBody(List<CommandProperty> properties, List<NamedRule> rules) {
    this.properties = unmodifiableList(properties);
    this.rules = unmodifiableList(rules);
  }

  public List<CommandProperty> getProperties() {
    return properties;
  }

  public List<NamedRule> getRules() {
    return rules;
  }
}
