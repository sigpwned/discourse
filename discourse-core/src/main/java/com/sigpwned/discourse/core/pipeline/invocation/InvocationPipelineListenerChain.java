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
   * @param <T>
   * @param clazz
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforeScanStep(java.lang.Class)
   */
  public <T> void beforeScanStep(Class<T> clazz) {
    delegate.beforeScanStep(clazz);
  }

  /**
   * @param <T>
   * @param clazz
   * @param root
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterScanStep(java.lang.Class,
   *      com.sigpwned.discourse.core.command.RootCommand)
   */
  public <T> void afterScanStep(Class<T> clazz, RootCommand<T> root) {
    delegate.afterScanStep(clazz, root);
  }

  /**
   * @param t
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchScanStep(java.lang.Throwable)
   */
  public void catchScanStep(Throwable t) {
    delegate.catchScanStep(t);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyScanStep()
   */
  public void finallyScanStep() {
    delegate.finallyScanStep();
  }

  /**
   * @param <T>
   * @param clazz
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforeScanStepWalk(java.lang.Class)
   */
  public <T> void beforeScanStepWalk(Class<T> clazz) {
    delegate.beforeScanStepWalk(clazz);
  }

  /**
   * @param <T>
   * @param clazz
   * @param walkedClasses
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterScanStepWalk(java.lang.Class,
   *      java.util.List)
   */
  public <T> void afterScanStepWalk(Class<T> clazz, List<WalkedClass<? extends T>> walkedClasses) {
    delegate.afterScanStepWalk(clazz, walkedClasses);
  }

  /**
   * @param t
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchScanStepWalk(java.lang.Throwable)
   */
  public void catchScanStepWalk(Throwable t) {
    delegate.catchScanStepWalk(t);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyScanStepWalk()
   */
  public void finallyScanStepWalk() {
    delegate.finallyScanStepWalk();
  }

  /**
   * @param <T>
   * @param walkedClasses
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforeScanStepPrepare(java.util.List)
   */
  public <T> void beforeScanStepPrepare(List<WalkedClass<? extends T>> walkedClasses) {
    delegate.beforeScanStepPrepare(walkedClasses);
  }

  /**
   * @param <T>
   * @param walkedClasses
   * @param preparedClasses
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterScanStepPrepare(java.util.List,
   *      java.util.List)
   */
  public <T> void afterScanStepPrepare(List<WalkedClass<? extends T>> walkedClasses,
      List<PreparedClass<? extends T>> preparedClasses) {
    delegate.afterScanStepPrepare(walkedClasses, preparedClasses);
  }

  /**
   * @param t
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchScanStepPrepare(java.lang.Throwable)
   */
  public void catchScanStepPrepare(Throwable t) {
    delegate.catchScanStepPrepare(t);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyScanStepPrepare()
   */
  public void finallyScanStepPrepare() {
    delegate.finallyScanStepPrepare();
  }

  /**
   * @param <T>
   * @param preparedClasses
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforeScanStepTree(java.util.List)
   */
  public <T> void beforeScanStepTree(List<PreparedClass<? extends T>> preparedClasses) {
    delegate.beforeScanStepTree(preparedClasses);
  }

  /**
   * @param <T>
   * @param preparedClasses
   * @param root
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterScanStepTree(java.util.List,
   *      com.sigpwned.discourse.core.command.RootCommand)
   */
  public <T> void afterScanStepTree(List<PreparedClass<? extends T>> preparedClasses,
      RootCommand<T> root) {
    delegate.afterScanStepTree(preparedClasses, root);
  }

  /**
   * @param t
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchScanStepTree(java.lang.Throwable)
   */
  public void catchScanStepTree(Throwable t) {
    delegate.catchScanStepTree(t);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyScanStepTree()
   */
  public void finallyScanStepTree() {
    delegate.finallyScanStepTree();
  }

  /**
   * @param args
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforeResolveStep(java.util.List)
   */
  public void beforeResolveStep(List<String> args) {
    delegate.beforeResolveStep(args);
  }

  /**
   * @param args
   * @param maybeResolvedCommand
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterResolveStep(java.util.List,
   *      java.util.Optional)
   */
  public <T> void afterResolveStep(List<String> args,
      Optional<ResolvedCommand<? extends T>> maybeResolvedCommand) {
    delegate.afterResolveStep(args, maybeResolvedCommand);
  }

  /**
   * @param t
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchResolveStep(java.lang.Throwable)
   */
  public void catchResolveStep(Throwable t) {
    delegate.catchResolveStep(t);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyResolveStep()
   */
  public void finallyResolveStep() {
    delegate.finallyResolveStep();
  }

  /**
   * @param originalCoordinates
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforePreprocessCoordinatesStep(java.util.Map)
   */
  public void beforePreprocessCoordinatesStep(Map<Coordinate, String> originalCoordinates) {
    delegate.beforePreprocessCoordinatesStep(originalCoordinates);
  }

  /**
   * @param originalCoordinates
   * @param preprocessedCoordinates
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterPreprocessCoordinatesStep(java.util.Map,
   *      java.util.Map)
   */
  public void afterPreprocessCoordinatesStep(Map<Coordinate, String> originalCoordinates,
      Map<Coordinate, String> preprocessedCoordinates) {
    delegate.afterPreprocessCoordinatesStep(originalCoordinates, preprocessedCoordinates);
  }

  /**
   * @param t
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchPreprocessCoordinatesStep(java.lang.Throwable)
   */
  public void catchPreprocessCoordinatesStep(Throwable t) {
    delegate.catchPreprocessCoordinatesStep(t);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyPreprocessCoordinatesStep()
   */
  public void finallyPreprocessCoordinatesStep() {
    delegate.finallyPreprocessCoordinatesStep();
  }

  /**
   * @param args
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforePreprocessArgsStep(java.util.List)
   */
  public void beforePreprocessArgsStep(List<String> args) {
    delegate.beforePreprocessArgsStep(args);
  }

  /**
   * @param args
   * @param preprocessedArgs
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterPreprocessArgsStep(java.util.List,
   *      java.util.List)
   */
  public void afterPreprocessArgsStep(List<String> args, List<String> preprocessedArgs) {
    delegate.afterPreprocessArgsStep(args, preprocessedArgs);
  }

  /**
   * @param t
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchPreprocessArgsStep(java.lang.Throwable)
   */
  public void catchPreprocessArgsStep(Throwable t) {
    delegate.catchPreprocessArgsStep(t);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyPreprocessArgsStep()
   */
  public void finallyPreprocessArgsStep() {
    delegate.finallyPreprocessArgsStep();
  }

  /**
   * @param args
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforeTokenizeStep(java.util.List)
   */
  public void beforeTokenizeStep(List<String> args) {
    delegate.beforeTokenizeStep(args);
  }

  /**
   * @param args
   * @param tokens
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterTokenizeStep(java.util.List,
   *      java.util.List)
   */
  public void afterTokenizeStep(List<String> args, List<Token> tokens) {
    delegate.afterTokenizeStep(args, tokens);
  }

  /**
   * @param t
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchTokenizeStep(java.lang.Throwable)
   */
  public void catchTokenizeStep(Throwable t) {
    delegate.catchTokenizeStep(t);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyTokenizeStep()
   */
  public void finallyTokenizeStep() {
    delegate.finallyTokenizeStep();
  }

  /**
   * @param tokens
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforePreprocessTokensStep(java.util.List)
   */
  public void beforePreprocessTokensStep(List<Token> tokens) {
    delegate.beforePreprocessTokensStep(tokens);
  }

  /**
   * @param tokens
   * @param preprocessedTokens
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterPreprocessTokensStep(java.util.List,
   *      java.util.List)
   */
  public void afterPreprocessTokensStep(List<Token> tokens, List<Token> preprocessedTokens) {
    delegate.afterPreprocessTokensStep(tokens, preprocessedTokens);
  }

  /**
   * @param t
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchPreprocessTokensStep(java.lang.Throwable)
   */
  public void catchPreprocessTokensStep(Throwable t) {
    delegate.catchPreprocessTokensStep(t);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyPreprocessTokensStep()
   */
  public void finallyPreprocessTokensStep() {
    delegate.finallyPreprocessTokensStep();
  }

  /**
   * @param tokens
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforeParseStep(java.util.List)
   */
  public void beforeParseStep(List<Token> tokens) {
    delegate.beforeParseStep(tokens);
  }

  /**
   * @param tokens
   * @param parsedArgs
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterParseStep(java.util.List,
   *      java.util.List)
   */
  public void afterParseStep(List<Token> tokens, List<Entry<Coordinate, String>> parsedArgs) {
    delegate.afterParseStep(tokens, parsedArgs);
  }

  /**
   * @param t
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchParseStep(java.lang.Throwable)
   */
  public void catchParseStep(Throwable t) {
    delegate.catchParseStep(t);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyParseStep()
   */
  public void finallyParseStep() {
    delegate.finallyParseStep();
  }

  /**
   * @param parsedArgs
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforeAttributeStep(java.util.List)
   */
  public void beforeAttributeStep(List<Entry<Coordinate, String>> parsedArgs) {
    delegate.beforeAttributeStep(parsedArgs);
  }

  /**
   * @param parsedArgs
   * @param attributedArgs
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterAttributeStep(java.util.List,
   *      java.util.List)
   */
  public void afterAttributeStep(List<Entry<Coordinate, String>> parsedArgs,
      List<Entry<String, String>> attributedArgs) {
    delegate.afterAttributeStep(parsedArgs, attributedArgs);
  }

  /**
   * @param t
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchAttributeStep(java.lang.Throwable)
   */
  public void catchAttributeStep(Throwable t) {
    delegate.catchAttributeStep(t);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyAttributeStep()
   */
  public void finallyAttributeStep() {
    delegate.finallyAttributeStep();
  }

  /**
   * @param attributedArgs
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforeGroupStep(java.util.List)
   */
  public void beforeGroupStep(List<Entry<String, String>> attributedArgs) {
    delegate.beforeGroupStep(attributedArgs);
  }

  /**
   * @param attributedArgs
   * @param groupedArgs
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterGroupStep(java.util.List,
   *      java.util.Map)
   */
  public void afterGroupStep(List<Entry<String, String>> attributedArgs,
      Map<String, List<String>> groupedArgs) {
    delegate.afterGroupStep(attributedArgs, groupedArgs);
  }

  /**
   * @param t
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchGroupStep(java.lang.Throwable)
   */
  public void catchGroupStep(Throwable t) {
    delegate.catchGroupStep(t);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyGroupStep()
   */
  public void finallyGroupStep() {
    delegate.finallyGroupStep();
  }

  /**
   * @param groupedArgs
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforeMapStep(java.util.Map)
   */
  public void beforeMapStep(Map<String, List<String>> groupedArgs) {
    delegate.beforeMapStep(groupedArgs);
  }

  /**
   * @param groupedArgs
   * @param mappedArgs
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterMapStep(java.util.Map,
   *      java.util.Map)
   */
  public void afterMapStep(Map<String, List<String>> groupedArgs,
      Map<String, List<Object>> mappedArgs) {
    delegate.afterMapStep(groupedArgs, mappedArgs);
  }

  /**
   * @param t
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchMapStep(java.lang.Throwable)
   */
  public void catchMapStep(Throwable t) {
    delegate.catchMapStep(t);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyMapStep()
   */
  public void finallyMapStep() {
    delegate.finallyMapStep();
  }

  /**
   * @param mappedArgs
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforeReduceStep(java.util.Map)
   */
  public void beforeReduceStep(Map<String, List<Object>> mappedArgs) {
    delegate.beforeReduceStep(mappedArgs);
  }

  /**
   * @param mappedArgs
   * @param sinkedArgs
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterReduceStep(java.util.Map,
   *      java.util.Map)
   */
  public void afterReduceStep(Map<String, List<Object>> mappedArgs,
      Map<String, Object> sinkedArgs) {
    delegate.afterReduceStep(mappedArgs, sinkedArgs);
  }

  /**
   * @param t
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchReduceStep(java.lang.Throwable)
   */
  public void catchReduceStep(Throwable t) {
    delegate.catchReduceStep(t);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyReduceStep()
   */
  public void finallyReduceStep() {
    delegate.finallyReduceStep();
  }

  /**
   * @param sinkedArgs
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#beforeFinishStep(java.util.Map)
   */
  public void beforeFinishStep(Map<String, Object> sinkedArgs) {
    delegate.beforeFinishStep(sinkedArgs);
  }

  /**
   * @param sinkedArgs
   * @param result
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#afterFinishStep(java.util.Map,
   *      java.lang.Object)
   */
  public void afterFinishStep(Map<String, Object> sinkedArgs, Object result) {
    delegate.afterFinishStep(sinkedArgs, result);
  }

  /**
   * @param t
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#catchFinishStep(java.lang.Throwable)
   */
  public void catchFinishStep(Throwable t) {
    delegate.catchFinishStep(t);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener#finallyFinishStep()
   */
  public void finallyFinishStep() {
    delegate.finallyFinishStep();
  }
}
