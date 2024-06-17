package com.sigpwned.discourse.core.pipeline.invocation;

import java.util.ResourceBundle;
import com.sigpwned.discourse.core.Dialect;
import com.sigpwned.discourse.core.command.resolved.ResolvedCommand;
import com.sigpwned.discourse.core.command.tree.LeafCommand;
import com.sigpwned.discourse.core.command.tree.RootCommand;
import com.sigpwned.discourse.core.error.ExitError;
import com.sigpwned.discourse.core.format.ExceptionFormatter;
import com.sigpwned.discourse.core.format.HelpFormatter;
import com.sigpwned.discourse.core.format.VersionFormatter;
import com.sigpwned.discourse.core.pipeline.invocation.step.ScanStep;

public interface InvocationPipelineStep {
  /**
   * The key for the {@link ResourceBundle application bundle} in the {@link InvocationContext
   * invocation context}. This key is optionally populated by the user before pipeline execution.
   */
  public static final InvocationContext.Key<ResourceBundle> APPLICATION_BUNDLE_KEY =
      InvocationContext.Key.of(ResourceBundle.class);

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
  public static final InvocationContext.Key<ResolvedCommand<?>> RESOLVED_COMMAND_KEY =
      (InvocationContext.Key) InvocationContext.Key.of(ResolvedCommand.class);

  public static final InvocationContext.Key<ExitError.Factory> EXIT_ERROR_FACTORY_KEY =
      InvocationContext.Key.of(ExitError.Factory.class);

  public static final InvocationContext.Key<InvocationPipelineListener> INVOCATION_PIPELINE_LISTENER_KEY =
      InvocationContext.Key.of(InvocationPipelineListener.class);

  public static final InvocationContext.Key<ExceptionFormatter> EXCEPTION_FORMATTER_KEY =
      InvocationContext.Key.of(ExceptionFormatter.class);

  public static final InvocationContext.Key<HelpFormatter> HELP_FORMATTER_KEY =
      InvocationContext.Key.of(HelpFormatter.class);

  public static final InvocationContext.Key<VersionFormatter> VERSION_FORMATTER_KEY =
      InvocationContext.Key.of(VersionFormatter.class);

  public static final InvocationContext.Key<Dialect> DIALECT_KEY =
      InvocationContext.Key.of(Dialect.class);
}
