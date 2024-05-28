package com.sigpwned.discourse.core.util;

import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;

public final class Internationalization {
  private Internationalization() {}

  public static final ResourceBundle MESSAGES =
      ResourceBundle.getBundle("com.sigpwned.discourse.core.Messages");

  public static Optional<String> getMessage(Class<?> clazz) {
    return getMessage(clazz, null);
  }

  public static Optional<String> getMessage(Class<?> clazz, String field) {
    String key = clazz.getSimpleName();
    if (field != null) {
      key = key + "." + field;
    }

    String message;
    try {
      message = MESSAGES.getString(key);
    } catch (MissingResourceException e) {
      // This is fine. We just don't have that value.
      return Optional.empty();
    }

    return Optional.ofNullable(message);
  }
}
