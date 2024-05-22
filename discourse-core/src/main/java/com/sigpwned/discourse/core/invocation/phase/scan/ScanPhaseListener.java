/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 - 2024 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
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
