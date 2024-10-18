package com.sigpwned.discourse.core.format.help.model;

import static java.util.Objects.requireNonNull;
import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.command.planned.PlannedCommandProperty;
import com.sigpwned.discourse.core.l11n.UserMessage;

public class CommandPropertyDescription implements Comparable<CommandPropertyDescription> {
  public static CommandPropertyDescription of(PlannedCommandProperty property) {
    return new CommandPropertyDescription(property);
  }

  private final PlannedCommandProperty property;
  private final List<CommandPropertyAssignment> assignments;
  private final List<UserMessage> descriptions;

  public CommandPropertyDescription(PlannedCommandProperty property) {
    this.property = requireNonNull(property);
    this.assignments = new ArrayList<>();
    this.descriptions = new ArrayList<>();
  }

  public PlannedCommandProperty getProperty() {
    return property;
  }

  public List<CommandPropertyAssignment> getAssignments() {
    return assignments;
  }

  public List<UserMessage> getDescriptions() {
    return descriptions;
  }

  @Override
  public int compareTo(CommandPropertyDescription that) {
    // TODO should the default sort use property name? or coordinates?
    return this.getProperty().getName().compareTo(that.getProperty().getName());
  }
}
