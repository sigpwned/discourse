package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.exception.configuration.NotConfigurableConfigurationException;
import com.sigpwned.discourse.core.module.DefaultModule;
import com.sigpwned.discourse.core.value.sink.AssignValueSinkFactory;

public class CommandBuilder {
  private final SerializationContext serializationContext;
  private final SinkContext sinkContext;

  public CommandBuilder() {
    this.serializationContext = new SerializationContext();
    this.sinkContext = new SinkContext(AssignValueSinkFactory.INSTANCE);
    register(new DefaultModule());
  }
  
  public CommandBuilder register(Module module) {
    module.register(getSerializationContext());
    module.register(getSinkContext());
    return this;
  }
  
  public <T> Command<T> build(Class<T> rawType) {
    if(rawType.getAnnotation(Configurable.class) == null)
      throw new NotConfigurableConfigurationException(rawType);
    return Command.scan(getSinkContext(), getSerializationContext(), rawType);
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
  private SinkContext getSinkContext() {
    return sinkContext;
  }

}
