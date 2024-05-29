package com.sigpwned.discourse.core.pipeline.invocation.step;

import java.util.List;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStepBase;
import com.sigpwned.discourse.core.pipeline.invocation.step.resolve.CommandResolver;
import com.sigpwned.discourse.core.pipeline.invocation.step.resolve.exception.FailedCommandResolutionResolveException;
import com.sigpwned.discourse.core.pipeline.invocation.step.resolve.model.CommandResolution;

public class ResolveStep extends InvocationPipelineStepBase {
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static final InvocationContext.Key<CommandResolver<?>> COMMAND_RESOLVER_KEY =
      (InvocationContext.Key) InvocationContext.Key.of(CommandResolver.class);

  @SuppressWarnings({"unchecked", "rawtypes"})
  public <T> CommandResolution<? extends T> resolve(List<String> args, InvocationContext context) {
    CommandResolver resolver = context.get(COMMAND_RESOLVER_KEY).orElseThrow();

    CommandResolution<? extends T> resolution;
    try {
      getListener(context).beforeResolveStep(args, context);
      resolution = doResolve(resolver, args, context);
      getListener(context).afterResolveStep(args, resolution, context);
    } catch (Throwable e) {
      getListener(context).catchResolveStep(e, context);
      throw e;
    } finally {
      getListener(context).finallyResolveStep(context);
    }

    return resolution;
  }

  protected <T> CommandResolution<? extends T> doResolve(CommandResolver<T> resolver,
      List<String> args, InvocationContext context) {
    return resolver.resolveCommand(args, context).orElseThrow(() -> {
      return new FailedCommandResolutionResolveException();
    });
  }
}
