package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.exception.configuration.NotConfigurableConfigurationException;
import com.sigpwned.discourse.core.value.sink.AssignValueSinkFactory;

public class CommandBuilder {
  private final SerializationContext serializationContext;
  private final SinkContext storageContext;

  public CommandBuilder() {
    this.serializationContext = new SerializationContext();
    this.storageContext = new SinkContext(AssignValueSinkFactory.INSTANCE);
  }

  public CommandBuilder registerDeserializer(ValueDeserializerFactory<?> deserializer) {
    getSerializationContext().addFirst(deserializer);
    return this;
  }

  public CommandBuilder registerSink(ValueSinkFactory storer) {
    getStorageContext().addFirst(storer);
    return this;
  }

  public <T> Command<T> build(Class<T> rawType) {
    if(rawType.getAnnotation(Configurable.class) == null)
      throw new NotConfigurableConfigurationException(rawType);
    return Command.scan(getStorageContext(), getSerializationContext(), rawType);
  }

  /**
   * @return the serializationContext
   */
  private SerializationContext getSerializationContext() {
    return serializationContext;
  }

  /**
   * @return the storageContext
   */
  private SinkContext getStorageContext() {
    return storageContext;
  }

}
