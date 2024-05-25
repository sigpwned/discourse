package com.sigpwned.discourse.core.util;

import java.util.List;
import java.util.function.Function;
import com.sigpwned.discourse.core.module.value.sink.ValueSink;

public final class ValueSinks {
  private ValueSinks() {}

  public static Function<List<Object>, Object> toReducer(ValueSink sink) {
    return (values) -> {
      for (Object value : values) {
        sink.put(sink);
      }
      return sink.get();
    };
  }
}
