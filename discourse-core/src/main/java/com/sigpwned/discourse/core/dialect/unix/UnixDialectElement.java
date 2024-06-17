package com.sigpwned.discourse.core.dialect.unix;

import com.sigpwned.discourse.core.l11n.UserMessage;
import com.sigpwned.discourse.core.util.Localization;

public interface UnixDialectElement {
  public static final String SHORT_NAME_PREFIX = "-";

  public static final String LONG_NAME_PREFIX = "--";

  public static final String LONG_NAME_VALUE_SEPARATOR = "=";

  public static final String SEPARATOR = "--";

  public static final UserMessage DEFAULT_TYPE_MESSAGE = Localization.DEFAULT_TYPE_MESSAGE;
}
