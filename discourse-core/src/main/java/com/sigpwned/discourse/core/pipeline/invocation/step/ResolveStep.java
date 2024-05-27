package com.sigpwned.discourse.core.pipeline.invocation.step;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStepBase;
import com.sigpwned.discourse.core.pipeline.invocation.step.resolve.CommandResolver;
import com.sigpwned.discourse.core.pipeline.invocation.step.resolve.model.CommandResolution;

public class ResolveStep extends InvocationPipelineStepBase {
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static final InvocationContext.Key<CommandResolver<?>> COMMAND_RESOLVER_KEY =
      (InvocationContext.Key) InvocationContext.Key.of(CommandResolver.class);

  @SuppressWarnings({"unchecked", "rawtypes"})
  public <T> Optional<CommandResolution<? extends T>> resolve(List<String> args,
      InvocationContext context) {
    CommandResolver resolver = context.get(COMMAND_RESOLVER_KEY).orElseThrow(() -> {
      // TODO better exception
      return new IllegalStateException("No command resolver");
    });

    Optional maybeResolvedCommand;

    try {
      getListener(context).beforeResolveStep(args);
      maybeResolvedCommand = doResolve(resolver, args, context);
      getListener(context).afterResolveStep(args, maybeResolvedCommand);
    } catch (Throwable e) {
      getListener(context).catchResolveStep(e);
      throw e;
    } finally {
      getListener(context).finallyResolveStep();
    }

    return maybeResolvedCommand;
  }

  protected <T> Optional<CommandResolution<? extends T>> doResolve(CommandResolver<T> resolver,
      List<String> args, InvocationContext context) {
    return resolver.resolveCommand(args, context);
  }
}
