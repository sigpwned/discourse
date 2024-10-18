package com.sigpwned.discourse.core.l11n.message.user;

import static java.util.Objects.requireNonNull;
import java.util.ResourceBundle;
import com.sigpwned.discourse.core.l11n.UserMessage;
import com.sigpwned.discourse.core.l11n.UserMessageLocalizer;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class ResourceBundleUserMessageLocalizer implements UserMessageLocalizer {
  private final ResourceBundle bundle;

  public ResourceBundleUserMessageLocalizer(ResourceBundle bundle) {
    this.bundle = requireNonNull(bundle);
  }

  @Override
  public UserMessage localizeUserMessage(UserMessage message, InvocationContext context) {
    if (!getBundle().containsKey(message.getMessage()))
      return message;

    String localizedMessage = getBundle().getString(message.getMessage());

    return new UserMessage(localizedMessage, message.getArguments());
  }

  /**
   * @return the bundle
   */
  public ResourceBundle getBundle() {
    return bundle;
  }
}
