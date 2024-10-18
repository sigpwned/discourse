package com.sigpwned.discourse.core.l11n.util;

import java.text.MessageFormat;
import com.sigpwned.discourse.core.l11n.UserMessage;

public final class UserMessages {
  private UserMessages() {}

  
  public static String render(UserMessage message) {
    return MessageFormat.format(message.getMessage(),
        message.getArguments().toArray(Object[]::new));
  }
}
