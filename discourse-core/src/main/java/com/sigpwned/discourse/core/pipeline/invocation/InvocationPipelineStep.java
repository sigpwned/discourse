package com.sigpwned.discourse.core.pipeline.invocation;

import com.sigpwned.discourse.core.command.LeafCommand;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.error.ExitError;
import com.sigpwned.discourse.core.format.ExceptionFormatter;
import com.sigpwned.discourse.core.pipeline.invocation.step.ScanStep;

public interface InvocationPipelineStep {
  /**
   * The key for the {@link RootCommand root command} in the {@link InvocationContext invocation
   * context}. This key is populated by the {@link ScanStep scan step}.
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static final InvocationContext.Key<RootCommand<?>> ROOT_COMMAND_KEY =
      (InvocationContext.Key) InvocationContext.Key.of(RootCommand.class);

  /**
   * The key for the {@link LeafCommand leaf command} in the {@link InvocationContext invocation
   * context}. This key is populated by the {@link ScanStep scan step}.
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static final InvocationContext.Key<LeafCommand<?>> LEAF_COMMAND_KEY =
      (InvocationContext.Key) InvocationContext.Key.of(LeafCommand.class);

  public static final InvocationContext.Key<ExitError.Factory> EXIT_ERROR_FACTORY_KEY =
      InvocationContext.Key.of(ExitError.Factory.class);

  public static final InvocationContext.Key<InvocationPipelineListener> INVOCATION_PIPELINE_LISTENER_KEY =
      InvocationContext.Key.of(InvocationPipelineListener.class);

  public static final InvocationContext.Key<ExceptionFormatter> EXCEPTION_FORMATTER_KEY =
      InvocationContext.Key.of(ExceptionFormatter.class);
}
