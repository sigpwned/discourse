package com.sigpwned.discourse.core;

import com.sigpwned.espresso.BeanProperty;

public interface ValueSinkResolver {

  ValueSink resolveValueSink(BeanProperty property);

  void addFirst(ValueSinkFactory sink);

  void addLast(ValueSinkFactory sink);

  void setDefaultSink(ValueSinkFactory sink);
}
