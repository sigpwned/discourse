package com.sigpwned.discourse.core.format.help;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import java.util.List;
import java.util.Objects;

public class CommandPropertySyntax {
  /**
   * The category to which this syntax belongs. Properties that belong to the same category are
   * grouped together in the help message.
   */
  private final CommandPropertyCategory category;

  /**
   * Determines the order in which properties are displayed within a category.
   */
  private final Comparable<?> key;

  /**
   * The syntaxes for this property.
   */
  private final List<String> syntaxes;

  public CommandPropertySyntax(CommandPropertyCategory category, Comparable<?> key,
      List<String> syntaxes) {
    this.category = requireNonNull(category);
    this.key = requireNonNull(key);
    this.syntaxes = unmodifiableList(syntaxes);
  }

  public CommandPropertyCategory getCategory() {
    return category;
  }

  public Comparable<?> getKey() {
    return key;
  }

  public List<String> getSyntaxes() {
    return syntaxes;
  }

  @Override
  public int hashCode() {
    return Objects.hash(category, key, syntaxes);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CommandPropertySyntax other = (CommandPropertySyntax) obj;
    return Objects.equals(category, other.category) && Objects.equals(key, other.key)
        && Objects.equals(syntaxes, other.syntaxes);
  }
}
