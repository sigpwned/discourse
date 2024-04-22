package com.sigpwned.discourse.core.invocation;

import com.sigpwned.discourse.core.ConfigurableClassWalker;
import com.sigpwned.discourse.core.ConfigurableInstanceFactory;
import com.sigpwned.discourse.core.ConfigurableInstanceFactoryProviderChain;
import com.sigpwned.discourse.core.ConfigurableParameterScannerChain;
import com.sigpwned.discourse.core.Discriminator;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;

public class InvocationBuilder {

  public <T> InvocationBuilderResolveStep<T> scan(Class<T> rawType, InvocationContext context) {

    context.get(InvocationContext.DISCOURSE_LISTENER_CHAIN_KEY).ifPresent(listener -> {
      listener.beforeScan(rawType);
    });

    Command<T> command = doScan(rawType);

    context.get(InvocationContext.DISCOURSE_LISTENER_CHAIN_KEY).ifPresent(listener -> {
      listener.afterScan(command);
    });

    return new InvocationBuilderResolveStep<>(command);
  }

  /**
   * extension hook
   */
  protected <T> Command<T> doScan(Class<T> rawType) {
    final AtomicReference<Command<? extends T>> result = new AtomicReference<>();

    new ConfigurableClassWalker<T>(rawType).walk(new ConfigurableClassWalker.Visitor<T>() {
      private Stack<Map<Discriminator, Command<? extends T>>> subcommandsStack = new Stack<>();

      @Override
      public <M extends T> void enterMultiCommandClass(Discriminator discriminator, String name,
          String description, Class<M> clazz) {
        subcommandsStack.push(new HashMap<>());
      }

      @Override
      public <M extends T> void leaveMultiCommandClass(Discriminator discriminator, String name,
          String description, Class<M> clazz) {
        Map<Discriminator, Command<? extends M>> subcommands = (Map) subcommandsStack.pop();
        MultiCommand<M> multi = new MultiCommand<>(clazz, name, description, "1.0", subcommands);
        if (discriminator != null) {
          subcommandsStack.peek().put(discriminator, multi);
        } else {
          result.set(multi);
        }
      }

      @Override
      public <M extends T> void visitSingleCommandClass(Discriminator discriminator, String name,
          String description, Class<M> clazz) {
        ConfigurableInstanceFactoryProviderChain factoryProviderChain = null;
        ConfigurableInstanceFactory<M> factory = factoryProviderChain.getConfigurationInstanceFactory(
            clazz).orElseThrow(() -> {
          // TODO better exception
          return new IllegalArgumentException("No factory for " + rawType);
        });

        ConfigurableParameterScannerChain parameterScannerChain = null;
        List<ConfigurationParameter> parameters = parameterScannerChain.scanForParameters(clazz);
        if (parameters.isEmpty()) {
          // TODO Warn
        }

        SingleCommand<M> single = new SingleCommand<>(name, description, "1.0", parameters, null);
        if (discriminator != null) {
          subcommandsStack.peek().put(discriminator, single);
        } else {
          result.set(single);
        }
      }
    });

    return (Command<T>) result.get();
  }
}
