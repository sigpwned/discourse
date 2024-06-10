package com.sigpwned.discourse.core.format.help.synopsis.entry;

import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.format.help.synopsis.entry.id.SynopsisEntryId;

/**
 * Represents a user-given value in a synopsis. It can be an option, positional argument, or
 * anything else given by the user.
 */
public class UserSynopsisEntry extends SynopsisEntry {
  private final boolean required;

  private final boolean variadic;

  private final String value;

  public UserSynopsisEntry(SynopsisEntryId id, boolean required, boolean variadic,
      String value) {
    super(id);
    this.required = required;
    this.variadic = variadic;
    this.value = requireNonNull(value);
  }

  public boolean isRequired() {
    return this.required;
  }

  public boolean isVariadic() {
    return this.variadic;
  }

  public String getValue() {
    return this.value;
  }

  @Override
  public String getText() {
    final String open, close;
    if (isRequired()) {
      open = "<";
      close = ">";
    } else {
      open = "[";
      close = "]";
    }

    String result = open + this.value + close;
    if (isVariadic())
      result = open + result + " ..." + close;

    return result;
  }
}
