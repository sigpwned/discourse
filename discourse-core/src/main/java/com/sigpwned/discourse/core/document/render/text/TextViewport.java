package com.sigpwned.discourse.core.document.render.text;

import java.util.Objects;
import com.sigpwned.discourse.core.optional.OptionalEnvironmentVariable;

public class TextViewport {
  public static final int DEFAULT_WIDTH = OptionalEnvironmentVariable
      .getenv("DISCOURSE_TEXT_VIEWPORT_DEFAULT_WIDTH").map(Integer::parseInt).orElse(80);

  public static final TextViewport DEFAULT = new TextViewport(DEFAULT_WIDTH);

  private final int width;

  public TextViewport(int width) {
    if (width < 1)
      throw new IllegalArgumentException("width must be positive");
    this.width = width;
  }

  public int getWidth() {
    return width;
  }

  @Override
  public int hashCode() {
    return Objects.hash(width);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TextViewport other = (TextViewport) obj;
    return width == other.width;
  }

  @Override
  public String toString() {
    return "TextViewport [width=" + width + "]";
  }
}
