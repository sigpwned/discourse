package com.sigpwned.discourse.core.module;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.Module;
import com.sigpwned.discourse.core.command.resolved.ResolvedCommand;
import com.sigpwned.discourse.core.command.tree.LeafCommandProperty;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener;
import com.sigpwned.discourse.core.pipeline.invocation.step.resolve.model.CommandResolution;

public class RequiredParameterModule extends Module {
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
          if (property.isRequired()) {
            requiredParameters.add(property.getName());
          }
        }
      }

      @Override
      public void afterPostprocessPropertiesStep(Map<String, Object> reducedArgs,
          Map<String, Object> postprocessedArgs, InvocationContext context) {
        for (String requiredParameter : requiredParameters) {
          if (postprocessedArgs.get(requiredParameter) == null) {
            // TODO better exception
            throw new IllegalArgumentException("Missing required parameter: " + requiredParameter);
          }
        }
      }
    });
  }
}
