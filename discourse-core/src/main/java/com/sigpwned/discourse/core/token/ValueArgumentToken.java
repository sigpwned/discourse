package com.sigpwned.discourse.core.token;

import com.sigpwned.discourse.core.ArgumentToken;

public class ValueArgumentToken extends ArgumentToken {
  public ValueArgumentToken(String text) {
    super(Type.VALUE, text);
  }
  
  public String getValue() {
    return getText();
  }
}
