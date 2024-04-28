package com.sigpwned.discourse.validation;

import com.sigpwned.discourse.core.chain.DiscourseListenerChain;

public class ValidationModule extends com.sigpwned.discourse.core.Module {

  @Override
  public void registerDiscourseListeners(DiscourseListenerChain chain) {
    chain.addLast(new ValidatingDiscourseListener());
  }
}
