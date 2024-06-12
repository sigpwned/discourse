package com.sigpwned.discourse.core.format.help.synopsis;

import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.document.DocumentSection;
import com.sigpwned.discourse.core.format.help.Synopsis;

public class SynopsisDocumentSection extends DocumentSection {
  private final Synopsis synopsis;

  public SynopsisDocumentSection(Synopsis synopsis) {
    this.synopsis = requireNonNull(synopsis);
  }
  
  public Synopsis getSynopsis() {
    return synopsis;
  }
}
