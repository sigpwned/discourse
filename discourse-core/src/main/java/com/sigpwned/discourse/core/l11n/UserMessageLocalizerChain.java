package com.sigpwned.discourse.core.l11n;

import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class UserMessageLocalizerChain extends Chain<UserMessageLocalizer>
    implements UserMessageLocalizer {
  @Override
  public UserMessage localizeUserMessage(UserMessage message, InvocationContext context) {
    for (UserMessageLocalizer localizer : this)
      message = localizer.localizeUserMessage(message, context);
    return message;
  }
}
