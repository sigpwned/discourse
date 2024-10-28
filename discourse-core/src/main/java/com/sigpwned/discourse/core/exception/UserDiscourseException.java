package com.sigpwned.discourse.core.exception;

import java.text.MessageFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sigpwned.discourse.core.util.Internationalization;

/**
 * An exception that indicates a problem with the user's interaction with the Discourse library.
 * This is typically used to indicate an error in user input to the application using the Discourse
 * library. As such, this exception generally should be caught by client code and reported to the
 * user so they can correct their input. As a rule, the framework provides reasonable default error
 * messages for user input errors, so most users should not need to handle exceptions of this type
 * directly.
 */
@SuppressWarnings("serial")
public abstract class UserDiscourseException extends DiscourseException {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserDiscourseException.class);

  protected UserDiscourseException(String message) {
    super(message);
  }

  public UserDiscourseException(String message, Throwable cause) {
    super(message, cause);
  }

  @Override
  public String getLocalizedMessage() {
    String template = Internationalization.getMessage(getClass()).orElse(null);
    if (template == null) {
      // Well that's a bummer. Just return the unlocalized message.
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("No localization message for exception {}", getClass().getSimpleName());
      return getMessage();
    }

    MessageFormat format = new MessageFormat(template);
    Object[] arguments = getLocalizedMessageArguments();
    String localizedMessage = format.format(arguments);

    return localizedMessage;
  }

  protected String getLocalizedMessageKey() {
    return getClass().getSimpleName();
  }

  protected abstract Object[] getLocalizedMessageArguments();
}
