package com.sigpwned.discourse.core.util;

import com.sigpwned.discourse.core.l11n.UserMessage;

public final class Localization {
  private Localization() {}

  /**
   * <p>
   * The default type message. For use when presenting type names to the user, but none is
   * available. For example: {@code --foobar <value>}.
   * </p>
   */
  public static final UserMessage DEFAULT_TYPE_MESSAGE = UserMessage.of("value");
}
