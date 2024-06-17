package com.sigpwned.discourse.core.document;

import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.document.render.DocumentSection;

public final class Document {
  private final List<DocumentSection> sections;

  public Document() {
    this.sections = new ArrayList<>();
  }

  public List<DocumentSection> getSections() {
    return sections;
  }
}
