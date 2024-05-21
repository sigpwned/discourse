package com.sigpwned.discourse.core.invocation.phase.scan;

import java.util.List;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.invocation.phase.scan.model.PreparedClass;
import com.sigpwned.discourse.core.invocation.phase.scan.model.WalkedClass;

public interface ScanPhaseListener {
  // WALK STEP ////////////////////////////////////////////////////////////////////////////////////
  default <T> void beforeScanPhaseWalkStep(Class<T> rootClazz) {}

  default <T> void afterScanPhaseWalkStep(Class<T> rootClazz,
      List<WalkedClass<? extends T>> walkedClasses) {}

  default void catchScanPhaseWalkStep(Throwable problem) {}

  default void finallyScanPhaseWalkStep() {}

  // PREPARE STEP /////////////////////////////////////////////////////////////////////////////////
  default <T> void beforeScanPhasePrepareStep(List<WalkedClass<? extends T>> walkedClasses) {}

  default <T> void afterScanPhasePrepareStep(List<WalkedClass<? extends T>> walkedClasses,
      List<PreparedClass<? extends T>> preparedClasses) {}

  default void catchScanPhasePrepareStep(Throwable problem) {}

  default void finallyScanPhasePrepareStep() {}

  // GATHER STEP //////////////////////////////////////////////////////////////////////////////////
  default <T> void beforeScanPhaseGatherStep(List<PreparedClass<? extends T>> preparedClasses) {}

  default <T> void afterScanPhaseGatherStep(List<PreparedClass<? extends T>> preparedClasses,
      RootCommand<T> rootCommand) {}

  default void catchScanPhaseGatherStep(Throwable problem) {}

  default void finallyScanPhaseGatherStep() {}

}
