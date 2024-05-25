package com.sigpwned.discourse.core.pipeline.invocation.configurable;

import java.util.List;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.pipeline.invocation.command.CommandInvocationPipelineListener;
import com.sigpwned.discourse.core.pipeline.invocation.configurable.model.PreparedClass;
import com.sigpwned.discourse.core.pipeline.invocation.configurable.model.WalkedClass;

public interface ConfigurableInvocationPipelineListener extends CommandInvocationPipelineListener {
  default <T> void beforeScanPhaseWalkStep(Class<T> clazz) {}

  default <T> void afterScanPhaseWalkStep(Class<T> clazz,
      List<WalkedClass<? extends T>> walkedClasses) {}

  default void catchScanPhaseWalkStep(Throwable t) {}

  default void finallyScanPhaseWalkStep() {}

  default <T> void beforeScanPhasePrepareStep(List<WalkedClass<? extends T>> walkedClasses) {}

  default <T> void afterScanPhasePrepareStep(List<WalkedClass<? extends T>> walkedClasses,
      List<PreparedClass<? extends T>> preparedClasses) {}

  default void catchScanPhasePrepareStep(Throwable t) {}

  default void finallyScanPhasePrepareStep() {}

  default <T> void beforeScanPhaseGatherStep(List<PreparedClass<? extends T>> bodiedClasses) {}

  default <T> void afterScanPhaseGatherStep(List<PreparedClass<? extends T>> bodiedClasses,
      RootCommand<T> root) {}

  default void catchScanPhaseGatherStep(Throwable t) {}

  default void finallyScanPhaseGatherStep() {}

}
