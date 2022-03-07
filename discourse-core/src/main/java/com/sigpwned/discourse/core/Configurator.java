package com.sigpwned.discourse.core;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.sigpwned.discourse.core.property.FlagConfigurationProperty;
import com.sigpwned.discourse.core.property.OptionConfigurationProperty;
import com.sigpwned.discourse.core.property.PositionalConfigurationProperty;
import com.sigpwned.discourse.core.value.sink.AssignValueSinkFactory;
import com.sigpwned.espresso.BeanInstance;

public class Configurator<T> {
  private final Class<T> rawType;
  private final SerializationContext serializationContext;
  private final SinkContext storageContext;
  private final List<String> args;

  public Configurator(Class<T> rawType) {
    this.rawType = rawType;
    this.serializationContext = new SerializationContext();
    this.storageContext = new SinkContext(AssignValueSinkFactory.INSTANCE);
    this.args = new ArrayList<>();
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

  public Configurator<T> arg(String arg) {
    args.add(arg);
    return this;
  }

  public Configurator<T> args(String... args) {
    args(asList(args));
    return this;
  }

  public Configurator<T> args(List<String> args) {
    this.args.addAll(args);
    return this;
  }

  @SuppressWarnings({"unchecked"})
  public T done() {
    ConfigurationClass configurationClass =
        ConfigurationClass.scan(getStorageContext(), getSerializationContext(), getRawType());

    BeanInstance instance;
    try {
      instance = configurationClass.newInstance();
    }
    catch(InvocationTargetException e) {
      throw new RuntimeException("Failed to create new instance", e);
    }

    Set<String> required = new HashSet<>(
        configurationClass.getProperties().stream().filter(ConfigurationProperty::isRequired)
            .map(ConfigurationProperty::getName).collect(toList()));

    new ArgsParser(configurationClass, new ArgsParser.Handler() {
      @Override
      public void flag(FlagConfigurationProperty property) {
        try {
          property.set(instance.getInstance(), "true");
        }
        catch(InvocationTargetException e) {
          throw new RuntimeException("Failed to set property", e);
        }
        required.remove(property.getName());
      }

      @Override
      public void option(OptionConfigurationProperty property, String text) {
        try {
          property.set(instance.getInstance(), text);
        }
        catch(InvocationTargetException e) {
          throw new RuntimeException("Failed to set property", e);
        }
        required.remove(property.getName());
      }

      @Override
      public void positional(PositionalConfigurationProperty property, String text) {
        try {
          property.set(instance.getInstance(), text);
        }
        catch(InvocationTargetException e) {
          throw new RuntimeException("Failed to set property", e);
        }
        required.remove(property.getName());
      }
    }).parse(getArgs());

    if (!required.isEmpty())
      throw new IllegalArgumentException(
          format("The following required properties were not configured: %s", required));

    return (T) instance.getInstance();
  }

  /**
   * @return the args
   */
  private List<String> getArgs() {
    return args;
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
