package com.sigpwned.discourse.core.module;

import static java.util.Collections.emptyList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.command.PlannedCommand;
import com.sigpwned.discourse.core.command.PlannedCommandProperty;
import com.sigpwned.discourse.core.command.ResolvedCommand;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener;

public class DefaultParameterValueModule extends com.sigpwned.discourse.core.Module {
  @Override
  public void registerListeners(Chain<InvocationPipelineListener> chain) {
    chain.addFirst(new InvocationPipelineListener() {
      private Map<String, Object> defaultParameterValues;

      @Override
      public <T> void afterPlanStep(ResolvedCommand<? extends T> resolvedCommand,
          PlannedCommand<? extends T> plannedCommand, InvocationContext context) {
        defaultParameterValues = new HashMap<>();
        for (PlannedCommandProperty property : plannedCommand.getProperties()) {
          if (property.getDefaultValue().isPresent()) {
            defaultParameterValues.put(property.getName(),
                property.getDefaultValue().orElseThrow());
          }
        }
      }

      @Override
      public void afterMapStep(Map<String, List<String>> groupedArgs,
          Map<String, List<Object>> mappedArgs, InvocationContext context) {
        for (Map.Entry<String, Object> defaultParameterValue : defaultParameterValues.entrySet()) {
          if (mappedArgs.getOrDefault(defaultParameterValue.getKey(), emptyList()).isEmpty()) {
            mappedArgs.put(defaultParameterValue.getKey(),
                List.of(defaultParameterValue.getValue()));
          }
        }
      }
    });
  }
}
