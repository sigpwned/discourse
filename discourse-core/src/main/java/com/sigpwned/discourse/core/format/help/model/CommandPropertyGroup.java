package com.sigpwned.discourse.core.format.help.model;

import static java.util.Objects.requireNonNull;
import java.util.List;

public class CommandPropertyGroup {
  public static final String OPTIONS_GROUP_ID = "options";

  public static final int OPTIONS_GROUP_PRIORITY = 1000;

  /**
   * Unique identifier for this group. This is used to identify one group among many in the same
   * help document. Individual ID values should be considered well-known.
   */
  private final String id;

  /**
   * The priority of this group. This is used to provide a suggested order of groups in the help
   * document. Groups with lower priority values will be listed before groups with higher priority
   * values.
   */
  private final int priority;

  /**
   * The name of this group. This is used to provide a human-readable name for the group.
   */
  private final List<CommandPropertyDescription> descriptions;

  public CommandPropertyGroup(String id, int priority,
      List<CommandPropertyDescription> descriptions) {
    this.id = requireNonNull(id);
    this.priority = priority;
    this.descriptions = requireNonNull(descriptions);
    if (priority < 0)
      throw new IllegalArgumentException("priority must not be negative");
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @return the priority
   */
  public int getPriority() {
    return priority;
  }

  /**
   * @return the descriptions
   */
  public List<CommandPropertyDescription> getDescriptions() {
    return descriptions;
  }
}
