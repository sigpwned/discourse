package com.sigpwned.discourse.validation;

import static java.util.stream.Collectors.toMap;
import com.sigpwned.discourse.core.Command;
import com.sigpwned.discourse.core.CommandBuilder;
import com.sigpwned.discourse.core.ValueDeserializerFactory;
import com.sigpwned.discourse.core.ValueSinkFactory;
import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.validation.command.ValidatingMultiCommand;
import com.sigpwned.discourse.validation.command.ValidatingSingleCommand;

public class ValidatingCommandBuilder extends CommandBuilder {
  @Override
  public ValidatingCommandBuilder registerDeserializer(ValueDeserializerFactory<?> deserializer) {
    return (ValidatingCommandBuilder) super.registerDeserializer(deserializer);
  }

  @Override
  public ValidatingCommandBuilder registerSink(ValueSinkFactory storer) {
    return (ValidatingCommandBuilder) super.registerSink(storer);
  }

  @Override
  public <T> Command<T> build(Class<T> rawType) {
    Command<T> result = super.build(rawType);
    switch (result.getType()) {
      case MULTI:
        MultiCommand<T> multi = (MultiCommand<T>) result;
        return new ValidatingMultiCommand<T>(multi.getName(), multi.getDescription(),
            multi.getVersion(), multi.listSubcommands().stream()
                .collect(toMap(d -> d, d -> multi.getSubcommand(d).get())));
      case SINGLE:
        SingleCommand<T> single = (SingleCommand<T>) result;
        return new ValidatingSingleCommand<T>(single.getConfigurationClass());
      default:
        throw new AssertionError("Unrecognized command type " + result.getType());
    }
  }
}
