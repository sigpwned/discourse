package com.sigpwned.discourse.core.format.help.parameters;

import static java.util.Collections.unmodifiableList;
import java.util.List;

public class ParameterGroupEntry {
  private final List<String> syntax;
  private final List<String> description;

  public ParameterGroupEntry(List<String> syntax, List<String> description) {
    this.syntax = unmodifiableList(syntax);
    this.description = unmodifiableList(description);
  }

  public List<String> getSyntax() {
    return syntax;
  }

  public List<String> getDescription() {
    return description;
  }
}
