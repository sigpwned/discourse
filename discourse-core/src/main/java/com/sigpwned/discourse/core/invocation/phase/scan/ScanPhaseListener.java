package com.sigpwned.discourse.core.invocation.phase.scan;

import java.util.List;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.invocation.phase.scan.model.PreparedClass;
import com.sigpwned.discourse.core.invocation.phase.scan.model.WalkedClass;

public interface ScanPhaseListener {
  public <T> void beforeScanPhaseWalkStep(Class<T> rootClazz);

  public <T> void afterScanPhaseWalkStep(Class<T> rootClazz,
      List<WalkedClass<? extends T>> walkedClasses);

  public <T> void beforeScanPhasePrepareStep(Class<T> rootClazz,
      List<WalkedClass<? extends T>> walkedClasses);

  public <T> void afterScanPhasePrepareStep(Class<T> rootClazz,
      List<WalkedClass<? extends T>> walkedClasses,
      List<PreparedClass<? extends T>> preparedClasses);

  public <T> void beforeScanPhaseGatherStep(Class<T> rootClazz,
      List<PreparedClass<? extends T>> preparedClasses);

  public <T> void afterScanPhaseGatherStep(Class<T> rootClazz,
      List<PreparedClass<? extends T>> preparedClasses, RootCommand<T> rootCommand);

}
