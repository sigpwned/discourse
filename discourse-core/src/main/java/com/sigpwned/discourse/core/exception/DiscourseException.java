/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 - 2024 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.discourse.core.exception;

import java.text.MessageFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sigpwned.discourse.core.util.Internationalization;

/**
 * The base exception for all exceptions thrown by Discourse.
 */
@SuppressWarnings("serial")
public abstract class DiscourseException extends RuntimeException {
  private static final Logger LOGGER = LoggerFactory.getLogger(DiscourseException.class);

  protected DiscourseException(String message) {
    super(message);
  }

  protected DiscourseException(String message, Throwable cause) {
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
