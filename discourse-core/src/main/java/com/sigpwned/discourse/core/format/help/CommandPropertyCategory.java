package com.sigpwned.discourse.core.format.help;

import static java.util.Objects.requireNonNull;
import java.util.Objects;

public class CommandPropertyCategory implements Comparable<CommandPropertyCategory> {
  public static final int OPTIONS_PRIORITY = 1000;

  public static final int POSITIONAL_ARGUMENTS_PRIORITY = 2000;

  /**
   * The priority of this category. Determines the order in which categories are displayed in help
   * messages. Lower priorities are displayed first. These values are arbitrary and have do not have
   * to be contiguous.
   * 
   * @see #OPTIONS_PRIORITY
   * @see #POSITIONAL_ARGUMENTS_PRIORITY
   */
  private final int priority;

  /**
   * The name of this category. The name has no effect on the order in which categories are
   * displayed in help messages.
   */
  private final HelpMessage name;

  public CommandPropertyCategory(int priority, HelpMessage name) {
    this.priority = priority;
    this.name = requireNonNull(name);
    if (priority < 0)
      throw new IllegalArgumentException("priority must not be negative");
  }

  /**
   * @return the priority
   */
  public int getPriority() {
    return priority;
  }

  /**
   * @return the name
   */
  public HelpMessage getName() {
    return name;
  }

  @Override
  public int compareTo(CommandPropertyCategory that) {
    return Integer.compare(this.getPriority(), that.getPriority());
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, priority);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CommandPropertyCategory other = (CommandPropertyCategory) obj;
    return Objects.equals(name, other.name) && priority == other.priority;
  }

  @Override
  public String toString() {
    return "CommandPropertyCategory [priority=" + priority + ", name=" + name + "]";
  }
}
