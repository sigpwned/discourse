package com.sigpwned.discourse.core.module;

import static java.util.Collections.emptyList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.annotation.DiscourseRequired;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.command.ResolvedCommand;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener;
import com.sigpwned.discourse.core.pipeline.invocation.step.resolve.model.CommandResolution;

public class RequiredParameterModule extends com.sigpwned.discourse.core.Module {
  @Override
  public void registerListeners(Chain<InvocationPipelineListener> chain) {
    chain.addFirst(new InvocationPipelineListener() {
      private Set<String> requiredParameters;

      @Override
      public <T> void afterResolveStep(List<String> args, CommandResolution<? extends T> resolution,
          InvocationContext context) {
        requiredParameters = new HashSet<>();

        ResolvedCommand<? extends T> resolvedCommand = resolution.getCommand();

        for (LeafCommandProperty property : resolvedCommand.getCommand().getProperties()) {
          if (property.getAnnotations().stream().anyMatch(a -> a instanceof DiscourseRequired)) {
            requiredParameters.add(property.getName());
          }
        }
      }

      @Override
      public void afterGroupStep(List<Entry<String, String>> attributedArgs,
          Map<String, List<String>> groupedArgs, InvocationContext context) {
        for (String requiredParameter : requiredParameters) {
          if (groupedArgs.getOrDefault(requiredParameter, emptyList()).isEmpty()) {
            // TODO better exception
            throw new IllegalArgumentException("Missing required parameter: " + requiredParameter);
          }
        }
      }
    });
  }
}
