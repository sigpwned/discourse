package com.sigpwned.discourse.core.command.builder;

import static java.util.Objects.requireNonNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import com.sigpwned.discourse.core.module.value.deserializer.ValueDeserializer;
import com.sigpwned.discourse.core.module.value.sink.ValueSink;

public class CommandPropertyBuilder {
  private final String name;
  private final Type genericType;
  private final List<Annotation> annotations;
  private ValueDeserializer<?> deserializer;
  private ValueSink sink;

  public CommandPropertyBuilder(String name, Type genericType, List<Annotation> annotations) {
    this.name = requireNonNull(name);
    this.genericType = requireNonNull(genericType);
    this.annotations = requireNonNull(annotations);
  }

  public String name() {
    return name;
  }

  public Type genericType() {
    return genericType;
  }

  public List<Annotation> annotations() {
    return annotations;
  }

  public CommandPropertyBuilder deserializer(ValueDeserializer<?> deserializer) {
    this.deserializer = deserializer;
    return this;
  }

  public ValueDeserializer<?> deserializer() {
    return deserializer;
  }

  public CommandPropertyBuilder sink(ValueSink sink) {
    this.sink = requireNonNull(sink);
    return this;
  }

  public ValueSink sink() {
    return sink;
  }


}
