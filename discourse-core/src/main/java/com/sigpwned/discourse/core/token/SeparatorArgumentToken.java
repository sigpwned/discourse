package com.sigpwned.discourse.core.token;

import com.sigpwned.discourse.core.ArgumentToken;

public class SeparatorArgumentToken extends ArgumentToken {
  public static final SeparatorArgumentToken INSTANCE = new SeparatorArgumentToken();

  public static final String TEXT = "--";

  public SeparatorArgumentToken() {
    super(Type.SEPARATOR, TEXT);
  }
}
