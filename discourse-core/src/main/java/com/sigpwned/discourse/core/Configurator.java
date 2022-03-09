package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.exception.configuration.NotConfigurableConfigurationException;
import com.sigpwned.discourse.core.value.sink.AssignValueSinkFactory;

public class Configurator<T> {
  private final Class<T> rawType;
  private final SerializationContext serializationContext;
  private final SinkContext storageContext;

  public Configurator(Class<T> rawType) {
    this.rawType = rawType;
    this.serializationContext = new SerializationContext();
    this.storageContext = new SinkContext(AssignValueSinkFactory.INSTANCE);
  }

  public Class<T> getRawType() {
    return rawType;
  }

  public Configurator<T> registerDeserializer(ValueDeserializerFactory<?> deserializer) {
    getSerializationContext().addFirst(deserializer);
    return this;
  }

  public Configurator<T> registerSink(ValueSinkFactory storer) {
    getStorageContext().addFirst(storer);
    return this;
  }

  public Command<T> done() {
    if(getRawType().getAnnotation(Configurable.class) == null)
      throw new NotConfigurableConfigurationException(getRawType());
    return Command.scan(getStorageContext(), getSerializationContext(), getRawType());
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
