package com.sigpwned.discourse.core;

import com.sigpwned.espresso.BeanProperty;

public interface ValueSinkFactory {
  public ValueSink getSink(BeanProperty property);
}
