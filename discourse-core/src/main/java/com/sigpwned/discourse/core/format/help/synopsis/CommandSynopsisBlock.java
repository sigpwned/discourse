package com.sigpwned.discourse.core.format.help.synopsis;

import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.document.Block;
import com.sigpwned.discourse.core.format.help.model.CommandSynopsis;

public class CommandSynopsisBlock extends Block {
  private final CommandSynopsis synopsis;

  public CommandSynopsisBlock(CommandSynopsis synopsis) {
    this.synopsis = requireNonNull(synopsis);
  }

  public CommandSynopsis getSynopsis() {
    return synopsis;
  }
}
