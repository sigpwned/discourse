package com.sigpwned.discourse.core.format.help.group.property;

import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.document.Block;
import com.sigpwned.discourse.core.format.help.model.CommandPropertyDescription;

public class CommandPropertyGroupBlock extends Block {
  /**
   * The name of this group. This is used to provide a human-readable name for the group.
   */
  private final List<CommandPropertyDescription> descriptions;

  public CommandPropertyGroupBlock() {
    this.descriptions = new ArrayList<>();
  }

  /**
   * @return the descriptions
   */
  public List<CommandPropertyDescription> getDescriptions() {
    return descriptions;
  }
}
