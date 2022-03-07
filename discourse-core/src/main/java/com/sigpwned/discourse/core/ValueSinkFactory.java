package com.sigpwned.discourse.core;

import com.sigpwned.espresso.BeanProperty;

public interface ValueSinkFactory {
  public boolean isSinkable(BeanProperty property);
  
  public ValueSink getSink(BeanProperty property);
}
