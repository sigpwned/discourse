package com.sigpwned.discourse.core;

import static java.util.Collections.unmodifiableList;
import java.util.LinkedList;
import java.util.List;
import com.sigpwned.discourse.core.value.sink.ArrayAppendValueSinkFactory;
import com.sigpwned.discourse.core.value.sink.AssignValueSinkFactory;
import com.sigpwned.discourse.core.value.sink.ListAddValueSinkFactory;
import com.sigpwned.discourse.core.value.sink.SetAddValueSinkFactory;
import com.sigpwned.discourse.core.value.sink.SortedSetAddValueSinkFactory;
import com.sigpwned.espresso.BeanProperty;

public class SinkContext {
  private final LinkedList<ValueSinkFactory> sinks;
  private final ValueSinkFactory defaultSink;

  public SinkContext() {
    this(AssignValueSinkFactory.INSTANCE);
  }

  public SinkContext(ValueSinkFactory defaultSink) {
    if (defaultSink == null)
      throw new NullPointerException();
    this.sinks = new LinkedList<>();
    this.defaultSink = defaultSink;

    addLast(SortedSetAddValueSinkFactory.INSTANCE);
    addLast(SetAddValueSinkFactory.INSTANCE);
    addLast(ListAddValueSinkFactory.INSTANCE);
    addLast(ArrayAppendValueSinkFactory.INSTANCE);
  }

  public ValueSink getSink(BeanProperty property) {
    return sinks.stream().filter(d -> d.isSinkable(property)).findFirst().orElse(getDefaultStorer())
        .getSink(property);
  }


  public void addFirst(ValueSinkFactory deserializer) {
    sinks.remove(deserializer);
    sinks.addFirst(deserializer);
  }

  public void addLast(ValueSinkFactory storer) {
    sinks.remove(storer);
    sinks.addLast(storer);
  }

  public List<ValueSinkFactory> getStorers() {
    return unmodifiableList(sinks);
  }

  /**
   * @return the defaultStorer
   */
  private ValueSinkFactory getDefaultStorer() {
    return defaultSink;
  }
}
