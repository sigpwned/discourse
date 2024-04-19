package com.sigpwned.discourse.core.value.sink.resolver;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.ValueSink;
import com.sigpwned.discourse.core.ValueSinkFactory;
import com.sigpwned.discourse.core.ValueSinkResolver;
import com.sigpwned.discourse.core.value.sink.AssignValueSinkFactory;
import com.sigpwned.espresso.BeanProperty;
import java.util.LinkedList;

public class DefaultValueSinkResolver implements ValueSinkResolver {

  public static class Builder {

    private final DefaultValueSinkResolver resolver;

    public Builder() {
      resolver = new DefaultValueSinkResolver();
    }

    public Builder(ValueSinkFactory defaultSink) {
      resolver = new DefaultValueSinkResolver(defaultSink);
    }

    public Builder addFirst(ValueSinkFactory storer) {
      resolver.addFirst(storer);
      return this;
    }

    public Builder addLast(ValueSinkFactory storer) {
      resolver.addLast(storer);
      return this;
    }

    public DefaultValueSinkResolver build() {
      return resolver;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  private final LinkedList<ValueSinkFactory> sinks;
  private ValueSinkFactory defaultSink;

  public DefaultValueSinkResolver() {
    this(AssignValueSinkFactory.INSTANCE);
  }

  public DefaultValueSinkResolver(ValueSinkFactory defaultSink) {
    this.sinks = new LinkedList<>();
    this.defaultSink = requireNonNull(defaultSink);
  }

  @Override
  public ValueSink resolveValueSink(BeanProperty property) {
    return sinks.stream().filter(d -> d.isSinkable(property)).findFirst()
        .orElseGet(this::getDefaultSink)
        .getSink(property);
  }

  @Override
  public void addFirst(ValueSinkFactory deserializer) {
    sinks.remove(deserializer);
    sinks.addFirst(deserializer);
  }

  @Override
  public void addLast(ValueSinkFactory storer) {
    sinks.remove(storer);
    sinks.addLast(storer);
  }

  /**
   * @return the default sink
   */
  public ValueSinkFactory getDefaultSink() {
    return defaultSink;
  }

  @Override
  public void setDefaultSink(ValueSinkFactory defaultSink) {
    this.defaultSink = requireNonNull(defaultSink);
  }
}
