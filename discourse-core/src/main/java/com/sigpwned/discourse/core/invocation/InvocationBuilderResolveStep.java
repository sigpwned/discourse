package com.sigpwned.discourse.core.invocation;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.model.command.Discriminator;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.model.invocation.MultiCommandDereference;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.exception.syntax.InsufficientDiscriminatorsSyntaxException;
import com.sigpwned.discourse.core.exception.syntax.InvalidDiscriminatorSyntaxException;
import com.sigpwned.discourse.core.exception.syntax.UnrecognizedDiscriminatorSyntaxException;
import java.util.ArrayList;
import java.util.List;

public class InvocationBuilderResolveStep<T> {

  private final Command<T> command;

  public InvocationBuilderResolveStep(Command<T> command) {
    this.command = requireNonNull(command);
  }

  public InvocationBuilderParseStep<T> resolve(List<String> arguments, InvocationContext context) {
    context.get(InvocationContext.DISCOURSE_LISTENER_CHAIN_KEY).ifPresent(listenerChain -> {
      listenerChain.beforeResolve(command, arguments);
    });

    ResolvedCommandAndRemainingArguments<T> resolved = doResolve(arguments, context);

    return new InvocationBuilderParseStep<T>(command, resolved.dereferencedCommands(),
        resolved.resolvedCommand(), resolved.remainingArguments());
  }

  protected static record ResolvedCommandAndRemainingArguments<T>(
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<String> remainingArguments) {

    public ResolvedCommandAndRemainingArguments(
        List<MultiCommandDereference<? extends T>> dereferencedCommands,
        SingleCommand<? extends T> resolvedCommand, List<String> remainingArguments) {
      this.dereferencedCommands = unmodifiableList(dereferencedCommands);
      this.resolvedCommand = requireNonNull(resolvedCommand);
      this.remainingArguments = unmodifiableList(remainingArguments);
    }
  }

  /**
   * extension hook
   */
  protected ResolvedCommandAndRemainingArguments<T> doResolve(List<String> arguments,
      InvocationContext context) {
    Command<? extends T> subcommand = command;
    List<MultiCommandDereference<? extends T>> subcommands = new ArrayList<>();
    while (subcommand instanceof MultiCommand<? extends T> multi) {
      if (arguments.isEmpty()) {
        throw new InsufficientDiscriminatorsSyntaxException(multi);
      }

      String discriminatorString = arguments.remove(0);

      Discriminator discriminator;
      try {
        discriminator = Discriminator.fromString(discriminatorString);
      } catch (IllegalArgumentException e) {
        throw new InvalidDiscriminatorSyntaxException(multi, discriminatorString);
      }

      subcommands.add(new MultiCommandDereference<>(multi, discriminator));

      Command<? extends T> dereferencedSubcommand = multi.getSubcommands().get(discriminator);
      if (dereferencedSubcommand == null) {
        throw new UnrecognizedDiscriminatorSyntaxException(multi, discriminator);
      }

      subcommand = dereferencedSubcommand;
    }

    SingleCommand<? extends T> single = (SingleCommand<? extends T>) subcommand;

    return new ResolvedCommandAndRemainingArguments<>(subcommands, single, arguments);
  }
}
