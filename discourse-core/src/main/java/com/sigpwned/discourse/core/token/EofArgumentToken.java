package com.sigpwned.discourse.core.token;

import com.sigpwned.discourse.core.ArgumentToken;

public class EofArgumentToken extends ArgumentToken {
  public static final EofArgumentToken INSTANCE = new EofArgumentToken();

  public static final String TEXT = "$";

  public EofArgumentToken() {
    super(Type.EOF, TEXT);
  }
}
