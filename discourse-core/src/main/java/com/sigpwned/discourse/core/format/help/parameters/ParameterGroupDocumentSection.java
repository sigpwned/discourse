package com.sigpwned.discourse.core.format.help.parameters;

import static java.util.Collections.unmodifiableList;
import java.util.List;
import com.sigpwned.discourse.core.document.DocumentSection;

public class ParameterGroupDocumentSection extends DocumentSection {
  private final List<ParameterGroupEntry> entries;

  public ParameterGroupDocumentSection(List<ParameterGroupEntry> entries) {
    this.entries = unmodifiableList(entries);
  }

  public List<ParameterGroupEntry> getEntries() {
    return entries;
  }
}
