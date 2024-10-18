package com.sigpwned.discourse.core.l11n;

import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public interface UserMessageLocalizer {
  public UserMessage localizeUserMessage(UserMessage message, InvocationContext context);
}
