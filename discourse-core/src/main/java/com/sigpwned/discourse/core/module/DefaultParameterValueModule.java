package com.sigpwned.discourse.core.module;

import static java.util.Collections.emptyList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.annotation.DiscourseDefaultValue;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.command.ResolvedCommand;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener;
import com.sigpwned.discourse.core.pipeline.invocation.step.resolve.model.CommandResolution;
import com.sigpwned.discourse.core.util.Streams;

public class DefaultParameterValueModule extends com.sigpwned.discourse.core.Module {
  @Override
  public void registerListeners(Chain<InvocationPipelineListener> chain) {
    chain.addFirst(new InvocationPipelineListener() {
      private Map<String, String> defaultParameterValues;

      @Override
      public <T> void afterResolveStep(List<String> args, CommandResolution<? extends T> resolution,
          InvocationContext context) {
        defaultParameterValues = new HashMap<>();

        ResolvedCommand<? extends T> resolvedCommand = resolution.getCommand();

        for (LeafCommandProperty property : resolvedCommand.getCommand().getProperties()) {
          DiscourseDefaultValue defaultValueAnnotation = property.getAnnotations().stream()
              .mapMulti(Streams.filterAndCast(DiscourseDefaultValue.class)).findFirst()
              .orElse(null);
          if (defaultValueAnnotation != null) {
            defaultParameterValues.put(property.getName(), defaultValueAnnotation.value());
          }
        }
      }

      @Override
      public void afterGroupStep(List<Entry<String, String>> attributedArgs,
          Map<String, List<String>> groupedArgs, InvocationContext context) {
        for (Map.Entry<String, String> defaultParameterValue : defaultParameterValues.entrySet()) {
          if (groupedArgs.getOrDefault(defaultParameterValue.getKey(), emptyList()).isEmpty()) {
            groupedArgs.put(defaultParameterValue.getKey(),
                List.of(defaultParameterValue.getValue()));
          }
        }
      }
    });
  }
}
