package com.sigpwned.discourse.core.pipeline.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.command.PlannedCommand;
import com.sigpwned.discourse.core.command.ResolvedCommand;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.PreparedClass;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.WalkedClass;

public class InvocationPipelineListenerChain extends Chain<InvocationPipelineListener>
    implements InvocationPipelineListener {
  private final InvocationPipelineListener delegate;

  public InvocationPipelineListenerChain() {
    // This is by far the simplest way to implement a chain of listeners for a type with so many
    // methods. It may be slower, but it is much easier to maintain and extend.
    this.delegate = (InvocationPipelineListener) Proxy.newProxyInstance(
        Thread.currentThread().getContextClassLoader(),
        new Class[] {InvocationPipelineListener.class}, new InvocationHandler() {
          @Override
          public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            for (InvocationPipelineListener listener : InvocationPipelineListenerChain.this) {
              try {
                method.invoke(listener, args);
              } catch (InvocationTargetException e) {
                // Unwrap the exception and rethrow it
                throw e.getCause();
              }
            }
            return null;
          }
        });
  }

  /**
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforePipeline(com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void beforePipeline(InvocationContext context) {
    delegate.beforePipeline(context);
  }

  /**
   * @param instance
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterPipeline(java.lang.Object, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void afterPipeline(Object instance, InvocationContext context) {
    delegate.afterPipeline(instance, context);
  }

  /**
   * @param t
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchPipeline(java.lang.Throwable, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void catchPipeline(Throwable t, InvocationContext context) {
    delegate.catchPipeline(t, context);
  }

  /**
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyPipeline(com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void finallyPipeline(InvocationContext context) {
    delegate.finallyPipeline(context);
  }

  /**
   * @param <T>
   * @param clazz
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforeScanStep(java.lang.Class, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public <T> void beforeScanStep(Class<T> clazz, InvocationContext context) {
    delegate.beforeScanStep(clazz, context);
  }

  /**
   * @param <T>
   * @param clazz
   * @param root
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterScanStep(java.lang.Class, com.sigpwned.discourse.core.command.RootCommand, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public <T> void afterScanStep(Class<T> clazz, RootCommand<T> root, InvocationContext context) {
    delegate.afterScanStep(clazz, root, context);
  }

  /**
   * @param t
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchScanStep(java.lang.Throwable, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void catchScanStep(Throwable t, InvocationContext context) {
    delegate.catchScanStep(t, context);
  }

  /**
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyScanStep(com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void finallyScanStep(InvocationContext context) {
    delegate.finallyScanStep(context);
  }

  /**
   * @param <T>
   * @param clazz
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforeScanStepWalk(java.lang.Class, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public <T> void beforeScanStepWalk(Class<T> clazz, InvocationContext context) {
    delegate.beforeScanStepWalk(clazz, context);
  }

  /**
   * @param <T>
   * @param clazz
   * @param walkedClasses
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterScanStepWalk(java.lang.Class, java.util.List, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public <T> void afterScanStepWalk(Class<T> clazz, List<WalkedClass<? extends T>> walkedClasses,
      InvocationContext context) {
    delegate.afterScanStepWalk(clazz, walkedClasses, context);
  }

  /**
   * @param t
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchScanStepWalk(java.lang.Throwable, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void catchScanStepWalk(Throwable t, InvocationContext context) {
    delegate.catchScanStepWalk(t, context);
  }

  /**
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyScanStepWalk(com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void finallyScanStepWalk(InvocationContext context) {
    delegate.finallyScanStepWalk(context);
  }

  /**
   * @param <T>
   * @param walkedClasses
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforeScanStepPrepare(java.util.List, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public <T> void beforeScanStepPrepare(List<WalkedClass<? extends T>> walkedClasses,
      InvocationContext context) {
    delegate.beforeScanStepPrepare(walkedClasses, context);
  }

  /**
   * @param <T>
   * @param walkedClasses
   * @param preparedClasses
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterScanStepPrepare(java.util.List, java.util.List, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public <T> void afterScanStepPrepare(List<WalkedClass<? extends T>> walkedClasses,
      List<PreparedClass<? extends T>> preparedClasses, InvocationContext context) {
    delegate.afterScanStepPrepare(walkedClasses, preparedClasses, context);
  }

  /**
   * @param t
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchScanStepPrepare(java.lang.Throwable, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void catchScanStepPrepare(Throwable t, InvocationContext context) {
    delegate.catchScanStepPrepare(t, context);
  }

  /**
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyScanStepPrepare(com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void finallyScanStepPrepare(InvocationContext context) {
    delegate.finallyScanStepPrepare(context);
  }

  /**
   * @param <T>
   * @param preparedClasses
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforeScanStepTree(java.util.List, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public <T> void beforeScanStepTree(List<PreparedClass<? extends T>> preparedClasses,
      InvocationContext context) {
    delegate.beforeScanStepTree(preparedClasses, context);
  }

  /**
   * @param <T>
   * @param preparedClasses
   * @param root
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterScanStepTree(java.util.List, com.sigpwned.discourse.core.command.RootCommand, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public <T> void afterScanStepTree(List<PreparedClass<? extends T>> preparedClasses,
      RootCommand<T> root, InvocationContext context) {
    delegate.afterScanStepTree(preparedClasses, root, context);
  }

  /**
   * @param t
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchScanStepTree(java.lang.Throwable, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void catchScanStepTree(Throwable t, InvocationContext context) {
    delegate.catchScanStepTree(t, context);
  }

  /**
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyScanStepTree(com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void finallyScanStepTree(InvocationContext context) {
    delegate.finallyScanStepTree(context);
  }

  /**
   * @param args
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforeResolveStep(java.util.List, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void beforeResolveStep(List<String> args, InvocationContext context) {
    delegate.beforeResolveStep(args, context);
  }

  /**
   * @param <T>
   * @param args
   * @param maybeResolvedCommand
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterResolveStep(java.util.List, java.util.Optional, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public <T> void afterResolveStep(List<String> args,
      Optional<ResolvedCommand<? extends T>> maybeResolvedCommand, InvocationContext context) {
    delegate.afterResolveStep(args, maybeResolvedCommand, context);
  }

  /**
   * @param t
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchResolveStep(java.lang.Throwable, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void catchResolveStep(Throwable t, InvocationContext context) {
    delegate.catchResolveStep(t, context);
  }

  /**
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyResolveStep(com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void finallyResolveStep(InvocationContext context) {
    delegate.finallyResolveStep(context);
  }

  /**
   * @param <T>
   * @param resolvedCommand
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforePlanStep(com.sigpwned.discourse.core.command.ResolvedCommand, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public <T> void beforePlanStep(ResolvedCommand<? extends T> resolvedCommand,
      InvocationContext context) {
    delegate.beforePlanStep(resolvedCommand, context);
  }

  /**
   * @param <T>
   * @param resolvedCommand
   * @param plannedCommand
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterPlanStep(com.sigpwned.discourse.core.command.ResolvedCommand, com.sigpwned.discourse.core.command.PlannedCommand, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public <T> void afterPlanStep(ResolvedCommand<? extends T> resolvedCommand,
      PlannedCommand<? extends T> plannedCommand, InvocationContext context) {
    delegate.afterPlanStep(resolvedCommand, plannedCommand, context);
  }

  /**
   * @param t
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchPlanStep(java.lang.Throwable, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void catchPlanStep(Throwable t, InvocationContext context) {
    delegate.catchPlanStep(t, context);
  }

  /**
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyPlanStep(com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void finallyPlanStep(InvocationContext context) {
    delegate.finallyPlanStep(context);
  }

  /**
   * @param originalCoordinates
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforePreprocessCoordinatesStep(java.util.Map, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void beforePreprocessCoordinatesStep(Map<Coordinate, String> originalCoordinates,
      InvocationContext context) {
    delegate.beforePreprocessCoordinatesStep(originalCoordinates, context);
  }

  /**
   * @param originalCoordinates
   * @param preprocessedCoordinates
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterPreprocessCoordinatesStep(java.util.Map, java.util.Map, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void afterPreprocessCoordinatesStep(Map<Coordinate, String> originalCoordinates,
      Map<Coordinate, String> preprocessedCoordinates, InvocationContext context) {
    delegate.afterPreprocessCoordinatesStep(originalCoordinates, preprocessedCoordinates, context);
  }

  /**
   * @param t
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchPreprocessCoordinatesStep(java.lang.Throwable, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void catchPreprocessCoordinatesStep(Throwable t, InvocationContext context) {
    delegate.catchPreprocessCoordinatesStep(t, context);
  }

  /**
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyPreprocessCoordinatesStep(com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void finallyPreprocessCoordinatesStep(InvocationContext context) {
    delegate.finallyPreprocessCoordinatesStep(context);
  }

  /**
   * @param args
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforePreprocessArgsStep(java.util.List, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void beforePreprocessArgsStep(List<String> args, InvocationContext context) {
    delegate.beforePreprocessArgsStep(args, context);
  }

  /**
   * @param args
   * @param preprocessedArgs
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterPreprocessArgsStep(java.util.List, java.util.List, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void afterPreprocessArgsStep(List<String> args, List<String> preprocessedArgs,
      InvocationContext context) {
    delegate.afterPreprocessArgsStep(args, preprocessedArgs, context);
  }

  /**
   * @param t
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchPreprocessArgsStep(java.lang.Throwable, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void catchPreprocessArgsStep(Throwable t, InvocationContext context) {
    delegate.catchPreprocessArgsStep(t, context);
  }

  /**
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyPreprocessArgsStep(com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void finallyPreprocessArgsStep(InvocationContext context) {
    delegate.finallyPreprocessArgsStep(context);
  }

  /**
   * @param args
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforeTokenizeStep(java.util.List, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void beforeTokenizeStep(List<String> args, InvocationContext context) {
    delegate.beforeTokenizeStep(args, context);
  }

  /**
   * @param args
   * @param tokens
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterTokenizeStep(java.util.List, java.util.List, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void afterTokenizeStep(List<String> args, List<Token> tokens, InvocationContext context) {
    delegate.afterTokenizeStep(args, tokens, context);
  }

  /**
   * @param t
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchTokenizeStep(java.lang.Throwable, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void catchTokenizeStep(Throwable t, InvocationContext context) {
    delegate.catchTokenizeStep(t, context);
  }

  /**
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyTokenizeStep(com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void finallyTokenizeStep(InvocationContext context) {
    delegate.finallyTokenizeStep(context);
  }

  /**
   * @param tokens
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforePreprocessTokensStep(java.util.List, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void beforePreprocessTokensStep(List<Token> tokens, InvocationContext context) {
    delegate.beforePreprocessTokensStep(tokens, context);
  }

  /**
   * @param tokens
   * @param preprocessedTokens
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterPreprocessTokensStep(java.util.List, java.util.List, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void afterPreprocessTokensStep(List<Token> tokens, List<Token> preprocessedTokens,
      InvocationContext context) {
    delegate.afterPreprocessTokensStep(tokens, preprocessedTokens, context);
  }

  /**
   * @param t
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchPreprocessTokensStep(java.lang.Throwable, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void catchPreprocessTokensStep(Throwable t, InvocationContext context) {
    delegate.catchPreprocessTokensStep(t, context);
  }

  /**
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyPreprocessTokensStep(com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void finallyPreprocessTokensStep(InvocationContext context) {
    delegate.finallyPreprocessTokensStep(context);
  }

  /**
   * @param tokens
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforeParseStep(java.util.List, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void beforeParseStep(List<Token> tokens, InvocationContext context) {
    delegate.beforeParseStep(tokens, context);
  }

  /**
   * @param tokens
   * @param parsedArgs
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterParseStep(java.util.List, java.util.List, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void afterParseStep(List<Token> tokens, List<Entry<Coordinate, String>> parsedArgs,
      InvocationContext context) {
    delegate.afterParseStep(tokens, parsedArgs, context);
  }

  /**
   * @param t
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchParseStep(java.lang.Throwable, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void catchParseStep(Throwable t, InvocationContext context) {
    delegate.catchParseStep(t, context);
  }

  /**
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyParseStep(com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void finallyParseStep(InvocationContext context) {
    delegate.finallyParseStep(context);
  }

  /**
   * @param parsedArgs
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforeAttributeStep(java.util.List, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void beforeAttributeStep(List<Entry<Coordinate, String>> parsedArgs,
      InvocationContext context) {
    delegate.beforeAttributeStep(parsedArgs, context);
  }

  /**
   * @param parsedArgs
   * @param attributedArgs
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterAttributeStep(java.util.List, java.util.List, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void afterAttributeStep(List<Entry<Coordinate, String>> parsedArgs,
      List<Entry<String, String>> attributedArgs, InvocationContext context) {
    delegate.afterAttributeStep(parsedArgs, attributedArgs, context);
  }

  /**
   * @param t
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchAttributeStep(java.lang.Throwable, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void catchAttributeStep(Throwable t, InvocationContext context) {
    delegate.catchAttributeStep(t, context);
  }

  /**
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyAttributeStep(com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void finallyAttributeStep(InvocationContext context) {
    delegate.finallyAttributeStep(context);
  }

  /**
   * @param attributedArgs
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforeGroupStep(java.util.List, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void beforeGroupStep(List<Entry<String, String>> attributedArgs,
      InvocationContext context) {
    delegate.beforeGroupStep(attributedArgs, context);
  }

  /**
   * @param attributedArgs
   * @param groupedArgs
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterGroupStep(java.util.List, java.util.Map, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void afterGroupStep(List<Entry<String, String>> attributedArgs,
      Map<String, List<String>> groupedArgs, InvocationContext context) {
    delegate.afterGroupStep(attributedArgs, groupedArgs, context);
  }

  /**
   * @param t
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchGroupStep(java.lang.Throwable, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void catchGroupStep(Throwable t, InvocationContext context) {
    delegate.catchGroupStep(t, context);
  }

  /**
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyGroupStep(com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void finallyGroupStep(InvocationContext context) {
    delegate.finallyGroupStep(context);
  }

  /**
   * @param groupedArgs
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforeMapStep(java.util.Map, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void beforeMapStep(Map<String, List<String>> groupedArgs, InvocationContext context) {
    delegate.beforeMapStep(groupedArgs, context);
  }

  /**
   * @param groupedArgs
   * @param mappedArgs
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterMapStep(java.util.Map, java.util.Map, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void afterMapStep(Map<String, List<String>> groupedArgs,
      Map<String, List<Object>> mappedArgs, InvocationContext context) {
    delegate.afterMapStep(groupedArgs, mappedArgs, context);
  }

  /**
   * @param t
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchMapStep(java.lang.Throwable, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void catchMapStep(Throwable t, InvocationContext context) {
    delegate.catchMapStep(t, context);
  }

  /**
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyMapStep(com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void finallyMapStep(InvocationContext context) {
    delegate.finallyMapStep(context);
  }

  /**
   * @param mappedArgs
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforeReduceStep(java.util.Map, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void beforeReduceStep(Map<String, List<Object>> mappedArgs, InvocationContext context) {
    delegate.beforeReduceStep(mappedArgs, context);
  }

  /**
   * @param mappedArgs
   * @param sinkedArgs
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterReduceStep(java.util.Map, java.util.Map, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void afterReduceStep(Map<String, List<Object>> mappedArgs, Map<String, Object> sinkedArgs,
      InvocationContext context) {
    delegate.afterReduceStep(mappedArgs, sinkedArgs, context);
  }

  /**
   * @param t
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchReduceStep(java.lang.Throwable, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void catchReduceStep(Throwable t, InvocationContext context) {
    delegate.catchReduceStep(t, context);
  }

  /**
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyReduceStep(com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void finallyReduceStep(InvocationContext context) {
    delegate.finallyReduceStep(context);
  }

  /**
   * @param sinkedArgs
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforeFinishStep(java.util.Map, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void beforeFinishStep(Map<String, Object> sinkedArgs, InvocationContext context) {
    delegate.beforeFinishStep(sinkedArgs, context);
  }

  /**
   * @param sinkedArgs
   * @param result
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterFinishStep(java.util.Map, java.lang.Object, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void afterFinishStep(Map<String, Object> sinkedArgs, Object result,
      InvocationContext context) {
    delegate.afterFinishStep(sinkedArgs, result, context);
  }

  /**
   * @param t
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchFinishStep(java.lang.Throwable, com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void catchFinishStep(Throwable t, InvocationContext context) {
    delegate.catchFinishStep(t, context);
  }

  /**
   * @param context
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyFinishStep(com.sigpwned.discourse.core.pipeline.invocation.InvocationContext)
   */
  public void finallyFinishStep(InvocationContext context) {
    delegate.finallyFinishStep(context);
  }

}
