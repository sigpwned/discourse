package com.sigpwned.discourse.core.exception;

import java.text.MessageFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sigpwned.discourse.core.util.Internationalization;

/**
 * An exception that indicates a problem with the application using the Discourse library. This is
 * typically used to indicate an error in application code using the library. As such, this
 * exception generally should not be caught by client code, but should be reported to the
 * application developers.
 */
@SuppressWarnings("serial")
public abstract class ApplicationDiscourseException extends DiscourseException {
  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationDiscourseException.class);

  protected ApplicationDiscourseException(String message) {
    super(message);
  }

  protected ApplicationDiscourseException(String message, Throwable cause) {
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
