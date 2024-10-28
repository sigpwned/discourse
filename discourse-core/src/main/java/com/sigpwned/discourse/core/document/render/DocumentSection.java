package com.sigpwned.discourse.core.document.render;

import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.document.Block;

public final class DocumentSection {
  private final List<Block> blocks;

  public DocumentSection(List<Block> blocks) {
    this.blocks = new ArrayList<>(blocks);
  }

  public List<Block> getBlocks() {
    return blocks;
  }
}
