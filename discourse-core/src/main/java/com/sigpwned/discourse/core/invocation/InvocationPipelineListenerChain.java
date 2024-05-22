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
package com.sigpwned.discourse.core.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.invocation.model.CommandDereference;
import com.sigpwned.discourse.core.invocation.phase.scan.RulesEngine;
import com.sigpwned.discourse.core.invocation.phase.scan.model.PreparedClass;
import com.sigpwned.discourse.core.invocation.phase.scan.model.WalkedClass;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.NamedRule;

public class InvocationPipelineListenerChain extends Chain<InvocationPipelineListener>
    implements InvocationPipelineListener {

  private class DelegatingInvocationHandler implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      for (InvocationPipelineListener listener : InvocationPipelineListenerChain.this) {
        try {
          method.invoke(listener, args);
        }
        catch (InvocationTargetException e) {
          throw e.getCause();
        }
      }
      return null;
    }
  }

  private final InvocationPipelineListener delegate;

  public InvocationPipelineListenerChain() {
    this.delegate = (InvocationPipelineListener) Proxy.newProxyInstance(
        Thread.currentThread().getContextClassLoader(),
        new Class[] {InvocationPipelineListener.class}, new DelegatingInvocationHandler());
  }

  /**
   * @param parsedArgs
   * @see com.sigpwned.discourse.core.invocation.phase.eval.EvalPhaseListener#beforeEvalPhaseGroupStep(java.util.List)
   */
  public void beforeEvalPhaseGroupStep(List<Entry<String, String>> parsedArgs) {
    delegate.beforeEvalPhaseGroupStep(parsedArgs);
  }

  /**
   * @param coordinates
   * @see com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener#beforeParsePhasePreprocessCoordinatesStep(java.util.Map)
   */
  public void beforeParsePhasePreprocessCoordinatesStep(Map<Coordinate, String> coordinates) {
    delegate.beforeParsePhasePreprocessCoordinatesStep(coordinates);
  }

  /**
   * @param rulesEngine
   * @param initialState
   * @param rules
   * @see com.sigpwned.discourse.core.invocation.phase.factory.FactoryPhaseListener#beforeFactoryPhaseRulesStep(com.sigpwned.discourse.core.invocation.phase.scan.RulesEngine,
   *      java.util.Map, java.util.List)
   */
  public void beforeFactoryPhaseRulesStep(RulesEngine rulesEngine, Map<String, Object> initialState,
      List<NamedRule> rules) {
    delegate.beforeFactoryPhaseRulesStep(rulesEngine, initialState, rules);
  }

  /**
   * @param <T>
   * @param rootCommand
   * @param fullArgs
   * @see com.sigpwned.discourse.core.invocation.phase.resolve.ResolvePhaseListener#beforeResolvePhaseResolveStep(com.sigpwned.discourse.core.command.RootCommand,
   *      java.util.List)
   */
  public <T> void beforeResolvePhaseResolveStep(RootCommand<T> rootCommand, List<String> fullArgs) {
    delegate.beforeResolvePhaseResolveStep(rootCommand, fullArgs);
  }

  /**
   * @param <T>
   * @param rootClazz
   * @see com.sigpwned.discourse.core.invocation.phase.scan.ScanPhaseListener#beforeScanPhaseWalkStep(java.lang.Class)
   */
  public <T> void beforeScanPhaseWalkStep(Class<T> rootClazz) {
    delegate.beforeScanPhaseWalkStep(rootClazz);
  }

  /**
   * @param parsedArgs
   * @param groupedArgs
   * @see com.sigpwned.discourse.core.invocation.phase.eval.EvalPhaseListener#afterEvalPhaseGroupStep(java.util.List,
   *      java.util.Map)
   */
  public void afterEvalPhaseGroupStep(List<Entry<String, String>> parsedArgs,
      Map<String, List<String>> groupedArgs) {
    delegate.afterEvalPhaseGroupStep(parsedArgs, groupedArgs);
  }

  /**
   * @param coordinates
   * @param preprocessedCoordinates
   * @see com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener#afterParsePhasePreprocessCoordinatesStep(java.util.Map,
   *      java.util.Map)
   */
  public void afterParsePhasePreprocessCoordinatesStep(Map<Coordinate, String> coordinates,
      Map<Coordinate, String> preprocessedCoordinates) {
    delegate.afterParsePhasePreprocessCoordinatesStep(coordinates, preprocessedCoordinates);
  }

  /**
   * @param <T>
   * @param rootClazz
   * @param walkedClasses
   * @see com.sigpwned.discourse.core.invocation.phase.scan.ScanPhaseListener#afterScanPhaseWalkStep(java.lang.Class,
   *      java.util.List)
   */
  public <T> void afterScanPhaseWalkStep(Class<T> rootClazz,
      List<WalkedClass<? extends T>> walkedClasses) {
    delegate.afterScanPhaseWalkStep(rootClazz, walkedClasses);
  }

  /**
   * @param problem
   * @see com.sigpwned.discourse.core.invocation.phase.eval.EvalPhaseListener#catchEvalPhaseGroupStep(java.lang.Throwable)
   */
  public void catchEvalPhaseGroupStep(Throwable problem) {
    delegate.catchEvalPhaseGroupStep(problem);
  }

  /**
   * @param <T>
   * @param rootCommand
   * @param fullArgs
   * @param commandDereferences
   * @param resolvedCommand
   * @param remainingArgs
   * @see com.sigpwned.discourse.core.invocation.phase.resolve.ResolvePhaseListener#afterResolvePhaseResolveStep(com.sigpwned.discourse.core.command.RootCommand,
   *      java.util.List, java.util.List, com.sigpwned.discourse.core.command.Command,
   *      java.util.List)
   */
  public <T> void afterResolvePhaseResolveStep(RootCommand<T> rootCommand, List<String> fullArgs,
      List<CommandDereference<? extends T>> commandDereferences,
      Command<? extends T> resolvedCommand, List<String> remainingArgs) {
    delegate.afterResolvePhaseResolveStep(rootCommand, fullArgs, commandDereferences,
        resolvedCommand, remainingArgs);
  }

  /**
   * @param rulesEngine
   * @param initialState
   * @param rules
   * @param newState
   * @see com.sigpwned.discourse.core.invocation.phase.factory.FactoryPhaseListener#afterFactoryPhaseRulesStep(com.sigpwned.discourse.core.invocation.phase.scan.RulesEngine,
   *      java.util.Map, java.util.List, java.util.Map)
   */
  public void afterFactoryPhaseRulesStep(RulesEngine rulesEngine, Map<String, Object> initialState,
      List<NamedRule> rules, Map<String, Object> newState) {
    delegate.afterFactoryPhaseRulesStep(rulesEngine, initialState, rules, newState);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.invocation.phase.eval.EvalPhaseListener#finallyEvalPhaseGroupStep()
   */
  public void finallyEvalPhaseGroupStep() {
    delegate.finallyEvalPhaseGroupStep();
  }

  /**
   * @param problem
   * @see com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener#catchParsePhasePreprocessCoordinatesStep(java.lang.Throwable)
   */
  public void catchParsePhasePreprocessCoordinatesStep(Throwable problem) {
    delegate.catchParsePhasePreprocessCoordinatesStep(problem);
  }

  /**
   * @param mappers
   * @param groupedArgs
   * @see com.sigpwned.discourse.core.invocation.phase.eval.EvalPhaseListener#beforeEvalPhaseEvalStep(java.util.Map,
   *      java.util.Map)
   */
  public void beforeEvalPhaseEvalStep(Map<String, Function<String, Object>> mappers,
      Map<String, List<String>> groupedArgs) {
    delegate.beforeEvalPhaseEvalStep(mappers, groupedArgs);
  }

  /**
   * @param problem
   * @see com.sigpwned.discourse.core.invocation.phase.scan.ScanPhaseListener#catchScanPhaseWalkStep(java.lang.Throwable)
   */
  public void catchScanPhaseWalkStep(Throwable problem) {
    delegate.catchScanPhaseWalkStep(problem);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener#finallyParsePhasePreprocessCoordinatesStep()
   */
  public void finallyParsePhasePreprocessCoordinatesStep() {
    delegate.finallyParsePhasePreprocessCoordinatesStep();
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.invocation.phase.scan.ScanPhaseListener#finallyScanPhaseWalkStep()
   */
  public void finallyScanPhaseWalkStep() {
    delegate.finallyScanPhaseWalkStep();
  }

  /**
   * @param problem
   * @see com.sigpwned.discourse.core.invocation.phase.factory.FactoryPhaseListener#catchFactoryPhaseRulesStep(java.lang.Throwable)
   */
  public void catchFactoryPhaseRulesStep(Throwable problem) {
    delegate.catchFactoryPhaseRulesStep(problem);
  }

  /**
   * @param <T>
   * @param walkedClasses
   * @see com.sigpwned.discourse.core.invocation.phase.scan.ScanPhaseListener#beforeScanPhasePrepareStep(java.util.List)
   */
  public <T> void beforeScanPhasePrepareStep(List<WalkedClass<? extends T>> walkedClasses) {
    delegate.beforeScanPhasePrepareStep(walkedClasses);
  }

  /**
   * @param args
   * @see com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener#beforeParsePhasePreprocessArgsStep(java.util.List)
   */
  public void beforeParsePhasePreprocessArgsStep(List<String> args) {
    delegate.beforeParsePhasePreprocessArgsStep(args);
  }

  /**
   * @param <T>
   * @param problem
   * @see com.sigpwned.discourse.core.invocation.phase.resolve.ResolvePhaseListener#catchResolvePhaseResolveStep(java.lang.Throwable)
   */
  public <T> void catchResolvePhaseResolveStep(Throwable problem) {
    delegate.catchResolvePhaseResolveStep(problem);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.invocation.phase.factory.FactoryPhaseListener#finallyFactoryPhaseRulesStep()
   */
  public void finallyFactoryPhaseRulesStep() {
    delegate.finallyFactoryPhaseRulesStep();
  }

  /**
   * @param newState
   * @see com.sigpwned.discourse.core.invocation.phase.factory.FactoryPhaseListener#beforeFactoryPhaseCreateStep(java.util.Map)
   */
  public void beforeFactoryPhaseCreateStep(Map<String, Object> newState) {
    delegate.beforeFactoryPhaseCreateStep(newState);
  }

  /**
   * @param <T>
   * @param rootClazz
   * @param args
   * @see com.sigpwned.discourse.core.invocation.InvocationPipelineListener#beforeInvocation(java.lang.Class,
   *      java.util.List)
   */
  public <T> void beforeInvocation(Class<T> rootClazz, List<String> args) {
    delegate.beforeInvocation(rootClazz, args);
  }

  /**
   * @param <T>
   * @see com.sigpwned.discourse.core.invocation.phase.resolve.ResolvePhaseListener#finallyResolvePhaseResolveStep()
   */
  public <T> void finallyResolvePhaseResolveStep() {
    delegate.finallyResolvePhaseResolveStep();
  }

  /**
   * @param mappers
   * @param groupedArgs
   * @param mappedArgs
   * @see com.sigpwned.discourse.core.invocation.phase.eval.EvalPhaseListener#afterEvalPhaseEvalStep(java.util.Map,
   *      java.util.Map, java.util.Map)
   */
  public void afterEvalPhaseEvalStep(Map<String, Function<String, Object>> mappers,
      Map<String, List<String>> groupedArgs, Map<String, List<Object>> mappedArgs) {
    delegate.afterEvalPhaseEvalStep(mappers, groupedArgs, mappedArgs);
  }

  /**
   * @param args
   * @param preprocessedArgs
   * @see com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener#afterParsePhasePreprocessArgsStep(java.util.List,
   *      java.util.List)
   */
  public void afterParsePhasePreprocessArgsStep(List<String> args, List<String> preprocessedArgs) {
    delegate.afterParsePhasePreprocessArgsStep(args, preprocessedArgs);
  }

  /**
   * @param <T>
   * @param walkedClasses
   * @param preparedClasses
   * @see com.sigpwned.discourse.core.invocation.phase.scan.ScanPhaseListener#afterScanPhasePrepareStep(java.util.List,
   *      java.util.List)
   */
  public <T> void afterScanPhasePrepareStep(List<WalkedClass<? extends T>> walkedClasses,
      List<PreparedClass<? extends T>> preparedClasses) {
    delegate.afterScanPhasePrepareStep(walkedClasses, preparedClasses);
  }

  /**
   * @param newState
   * @param instance
   * @see com.sigpwned.discourse.core.invocation.phase.factory.FactoryPhaseListener#afterFactoryPhaseCreateStep(java.util.Map,
   *      java.lang.Object)
   */
  public void afterFactoryPhaseCreateStep(Map<String, Object> newState, Object instance) {
    delegate.afterFactoryPhaseCreateStep(newState, instance);
  }

  /**
   * @param <T>
   * @param rootClazz
   * @param args
   * @param invocation
   * @see com.sigpwned.discourse.core.invocation.InvocationPipelineListener#afterInvocation(java.lang.Class,
   *      java.util.List, com.sigpwned.discourse.core.invocation.Invocation)
   */
  public <T> void afterInvocation(Class<T> rootClazz, List<String> args,
      Invocation<? extends T> invocation) {
    delegate.afterInvocation(rootClazz, args, invocation);
  }

  /**
   * @param problem
   * @see com.sigpwned.discourse.core.invocation.phase.eval.EvalPhaseListener#catchEvalPhaseEvalStep(java.lang.Throwable)
   */
  public void catchEvalPhaseEvalStep(Throwable problem) {
    delegate.catchEvalPhaseEvalStep(problem);
  }

  /**
   * @param problem
   * @see com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener#catchParsePhasePreprocessArgsStep(java.lang.Throwable)
   */
  public void catchParsePhasePreprocessArgsStep(Throwable problem) {
    delegate.catchParsePhasePreprocessArgsStep(problem);
  }

  /**
   * @param problem
   * @see com.sigpwned.discourse.core.invocation.phase.scan.ScanPhaseListener#catchScanPhasePrepareStep(java.lang.Throwable)
   */
  public void catchScanPhasePrepareStep(Throwable problem) {
    delegate.catchScanPhasePrepareStep(problem);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.invocation.phase.eval.EvalPhaseListener#finallyEvalPhaseEvalStep()
   */
  public void finallyEvalPhaseEvalStep() {
    delegate.finallyEvalPhaseEvalStep();
  }

  /**
   * @param problem
   * @see com.sigpwned.discourse.core.invocation.phase.factory.FactoryPhaseListener#catchFactoryPhaseCreateStep(java.lang.Throwable)
   */
  public void catchFactoryPhaseCreateStep(Throwable problem) {
    delegate.catchFactoryPhaseCreateStep(problem);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener#finallyParsePhasePreprocessArgsStep()
   */
  public void finallyParsePhasePreprocessArgsStep() {
    delegate.finallyParsePhasePreprocessArgsStep();
  }

  /**
   * @param reducers
   * @param mappedArgs
   * @see com.sigpwned.discourse.core.invocation.phase.eval.EvalPhaseListener#beforeEvalPhaseReduceStep(java.util.Map,
   *      java.util.Map)
   */
  public void beforeEvalPhaseReduceStep(Map<String, Function<List<Object>, Object>> reducers,
      Map<String, List<Object>> mappedArgs) {
    delegate.beforeEvalPhaseReduceStep(reducers, mappedArgs);
  }

  /**
   * @param t
   * @see com.sigpwned.discourse.core.invocation.InvocationPipelineListener#catchInvocation(java.lang.Throwable)
   */
  public void catchInvocation(Throwable t) {
    delegate.catchInvocation(t);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.invocation.phase.scan.ScanPhaseListener#finallyScanPhasePrepareStep()
   */
  public void finallyScanPhasePrepareStep() {
    delegate.finallyScanPhasePrepareStep();
  }

  /**
   * @param preprocessedArgs
   * @see com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener#beforeParsePhaseTokenizeStep(java.util.List)
   */
  public void beforeParsePhaseTokenizeStep(List<String> preprocessedArgs) {
    delegate.beforeParsePhaseTokenizeStep(preprocessedArgs);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.invocation.phase.factory.FactoryPhaseListener#finallyFactoryPhaseCreateStep()
   */
  public void finallyFactoryPhaseCreateStep() {
    delegate.finallyFactoryPhaseCreateStep();
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.invocation.InvocationPipelineListener#finallyInvocation()
   */
  public void finallyInvocation() {
    delegate.finallyInvocation();
  }

  /**
   * @param <T>
   * @param preparedClasses
   * @see com.sigpwned.discourse.core.invocation.phase.scan.ScanPhaseListener#beforeScanPhaseGatherStep(java.util.List)
   */
  public <T> void beforeScanPhaseGatherStep(List<PreparedClass<? extends T>> preparedClasses) {
    delegate.beforeScanPhaseGatherStep(preparedClasses);
  }

  /**
   * @param <T>
   * @param clazz
   * @see com.sigpwned.discourse.core.invocation.InvocationPipelineListener#beforeScanPhase(java.lang.Class)
   */
  public <T> void beforeScanPhase(Class<T> clazz) {
    delegate.beforeScanPhase(clazz);
  }

  /**
   * @param preprocessedArgs
   * @param tokens
   * @see com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener#afterParsePhaseTokenizeStep(java.util.List,
   *      java.util.List)
   */
  public void afterParsePhaseTokenizeStep(List<String> preprocessedArgs, List<Token> tokens) {
    delegate.afterParsePhaseTokenizeStep(preprocessedArgs, tokens);
  }

  /**
   * @param reducers
   * @param mappedArgs
   * @param reducedArgs
   * @see com.sigpwned.discourse.core.invocation.phase.eval.EvalPhaseListener#afterEvalPhaseReduceStep(java.util.Map,
   *      java.util.Map, java.util.Map)
   */
  public void afterEvalPhaseReduceStep(Map<String, Function<List<Object>, Object>> reducers,
      Map<String, List<Object>> mappedArgs, Map<String, Object> reducedArgs) {
    delegate.afterEvalPhaseReduceStep(reducers, mappedArgs, reducedArgs);
  }

  /**
   * @param <T>
   * @param clazz
   * @param command
   * @see com.sigpwned.discourse.core.invocation.InvocationPipelineListener#afterScanPhase(java.lang.Class,
   *      com.sigpwned.discourse.core.command.RootCommand)
   */
  public <T> void afterScanPhase(Class<T> clazz, RootCommand<T> command) {
    delegate.afterScanPhase(clazz, command);
  }

  /**
   * @param <T>
   * @param preparedClasses
   * @param rootCommand
   * @see com.sigpwned.discourse.core.invocation.phase.scan.ScanPhaseListener#afterScanPhaseGatherStep(java.util.List,
   *      com.sigpwned.discourse.core.command.RootCommand)
   */
  public <T> void afterScanPhaseGatherStep(List<PreparedClass<? extends T>> preparedClasses,
      RootCommand<T> rootCommand) {
    delegate.afterScanPhaseGatherStep(preparedClasses, rootCommand);
  }

  /**
   * @param problem
   * @see com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener#catchParsePhaseTokenizeStep(java.lang.Throwable)
   */
  public void catchParsePhaseTokenizeStep(Throwable problem) {
    delegate.catchParsePhaseTokenizeStep(problem);
  }

  /**
   * @param problem
   * @see com.sigpwned.discourse.core.invocation.InvocationPipelineListener#catchScanPhase(java.lang.Throwable)
   */
  public void catchScanPhase(Throwable problem) {
    delegate.catchScanPhase(problem);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener#finallyParsePhaseTokenizeStep()
   */
  public void finallyParsePhaseTokenizeStep() {
    delegate.finallyParsePhaseTokenizeStep();
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.invocation.InvocationPipelineListener#finallyScanPhase()
   */
  public void finallyScanPhase() {
    delegate.finallyScanPhase();
  }

  /**
   * @param problem
   * @see com.sigpwned.discourse.core.invocation.phase.scan.ScanPhaseListener#catchScanPhaseGatherStep(java.lang.Throwable)
   */
  public void catchScanPhaseGatherStep(Throwable problem) {
    delegate.catchScanPhaseGatherStep(problem);
  }

  /**
   * @param <T>
   * @param command
   * @param originalArgs
   * @see com.sigpwned.discourse.core.invocation.InvocationPipelineListener#beforeResolvePhase(com.sigpwned.discourse.core.command.RootCommand,
   *      java.util.List)
   */
  public <T> void beforeResolvePhase(RootCommand<T> command, List<String> originalArgs) {
    delegate.beforeResolvePhase(command, originalArgs);
  }

  /**
   * @param problem
   * @see com.sigpwned.discourse.core.invocation.phase.eval.EvalPhaseListener#catchEvalPhaseReduceStep(java.lang.Throwable)
   */
  public void catchEvalPhaseReduceStep(Throwable problem) {
    delegate.catchEvalPhaseReduceStep(problem);
  }

  /**
   * @param tokens
   * @see com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener#beforeParsePhasePreprocessTokensStep(java.util.List)
   */
  public void beforeParsePhasePreprocessTokensStep(List<Token> tokens) {
    delegate.beforeParsePhasePreprocessTokensStep(tokens);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.invocation.phase.scan.ScanPhaseListener#finallyScanPhaseGatherStep()
   */
  public void finallyScanPhaseGatherStep() {
    delegate.finallyScanPhaseGatherStep();
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.invocation.phase.eval.EvalPhaseListener#finallyEvalPhaseReduceStep()
   */
  public void finallyEvalPhaseReduceStep() {
    delegate.finallyEvalPhaseReduceStep();
  }

  /**
   * @param tokens
   * @param preprocessedTokens
   * @see com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener#afterParsePhasePreprocessTokensStep(java.util.List,
   *      java.util.List)
   */
  public void afterParsePhasePreprocessTokensStep(List<Token> tokens,
      List<Token> preprocessedTokens) {
    delegate.afterParsePhasePreprocessTokensStep(tokens, preprocessedTokens);
  }

  /**
   * @param <T>
   * @param command
   * @param originalArgs
   * @param resolvedCommand
   * @param dereferences
   * @param remainingArgs
   * @see com.sigpwned.discourse.core.invocation.InvocationPipelineListener#afterResolvePhase(com.sigpwned.discourse.core.command.RootCommand,
   *      java.util.List, com.sigpwned.discourse.core.command.Command, java.util.List,
   *      java.util.List)
   */
  public <T> void afterResolvePhase(RootCommand<T> command, List<String> originalArgs,
      Command<? extends T> resolvedCommand, List<CommandDereference<? extends T>> dereferences,
      List<String> remainingArgs) {
    delegate.afterResolvePhase(command, originalArgs, resolvedCommand, dereferences, remainingArgs);
  }

  /**
   * @param problem
   * @see com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener#catchParsePhasePreprocessTokensStep(java.lang.Throwable)
   */
  public void catchParsePhasePreprocessTokensStep(Throwable problem) {
    delegate.catchParsePhasePreprocessTokensStep(problem);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener#finallyParsePhasePreprocessTokensStep()
   */
  public void finallyParsePhasePreprocessTokensStep() {
    delegate.finallyParsePhasePreprocessTokensStep();
  }

  /**
   * @param problem
   * @see com.sigpwned.discourse.core.invocation.InvocationPipelineListener#catchResolvePhase(java.lang.Throwable)
   */
  public void catchResolvePhase(Throwable problem) {
    delegate.catchResolvePhase(problem);
  }

  /**
   * @param preprocessedTokens
   * @see com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener#beforeParsePhaseParseStep(java.util.List)
   */
  public void beforeParsePhaseParseStep(List<Token> preprocessedTokens) {
    delegate.beforeParsePhaseParseStep(preprocessedTokens);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.invocation.InvocationPipelineListener#finallyResolvePhase()
   */
  public void finallyResolvePhase() {
    delegate.finallyResolvePhase();
  }

  /**
   * @param <T>
   * @param resolvedCommand
   * @param remainingArgs
   * @see com.sigpwned.discourse.core.invocation.InvocationPipelineListener#beforeParsePhase(com.sigpwned.discourse.core.command.Command,
   *      java.util.List)
   */
  public <T> void beforeParsePhase(Command<T> resolvedCommand, List<String> remainingArgs) {
    delegate.beforeParsePhase(resolvedCommand, remainingArgs);
  }

  /**
   * @param preprocessedTokens
   * @param parsedArgs
   * @see com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener#afterParsePhaseParseStep(java.util.List,
   *      java.util.List)
   */
  public void afterParsePhaseParseStep(List<Token> preprocessedTokens,
      List<Entry<Coordinate, String>> parsedArgs) {
    delegate.afterParsePhaseParseStep(preprocessedTokens, parsedArgs);
  }

  /**
   * @param <T>
   * @param resolvedCommand
   * @param remainingArgs
   * @param namedArgs
   * @see com.sigpwned.discourse.core.invocation.InvocationPipelineListener#afterParsePhase(com.sigpwned.discourse.core.command.Command,
   *      java.util.List, java.util.List)
   */
  public <T> void afterParsePhase(Command<T> resolvedCommand, List<String> remainingArgs,
      List<Entry<String, String>> namedArgs) {
    delegate.afterParsePhase(resolvedCommand, remainingArgs, namedArgs);
  }

  /**
   * @param problem
   * @see com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener#catchParsePhaseParseStep(java.lang.Throwable)
   */
  public void catchParsePhaseParseStep(Throwable problem) {
    delegate.catchParsePhaseParseStep(problem);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener#finallyParsePhaseParseStep()
   */
  public void finallyParsePhaseParseStep() {
    delegate.finallyParsePhaseParseStep();
  }

  /**
   * @param naming
   * @param parsedArgs
   * @see com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener#beforeParsePhaseAttributeStep(java.util.Map,
   *      java.util.List)
   */
  public void beforeParsePhaseAttributeStep(Map<Coordinate, String> naming,
      List<Entry<Coordinate, String>> parsedArgs) {
    delegate.beforeParsePhaseAttributeStep(naming, parsedArgs);
  }

  /**
   * @param problem
   * @see com.sigpwned.discourse.core.invocation.InvocationPipelineListener#catchParsePhase(java.lang.Throwable)
   */
  public void catchParsePhase(Throwable problem) {
    delegate.catchParsePhase(problem);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.invocation.InvocationPipelineListener#finallyParsePhase()
   */
  public void finallyParsePhase() {
    delegate.finallyParsePhase();
  }

  /**
   * @param <T>
   * @param resolvedCommand
   * @param namedArgs
   * @see com.sigpwned.discourse.core.invocation.InvocationPipelineListener#beforeEvalPhase(com.sigpwned.discourse.core.command.Command,
   *      java.util.List)
   */
  public <T> void beforeEvalPhase(Command<T> resolvedCommand,
      List<Entry<String, String>> namedArgs) {
    delegate.beforeEvalPhase(resolvedCommand, namedArgs);
  }

  /**
   * @param naming
   * @param parsedArgs
   * @param attributedArgs
   * @see com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener#afterParsePhaseAttributeStep(java.util.Map,
   *      java.util.List, java.util.List)
   */
  public void afterParsePhaseAttributeStep(Map<Coordinate, String> naming,
      List<Entry<Coordinate, String>> parsedArgs, List<Entry<String, String>> attributedArgs) {
    delegate.afterParsePhaseAttributeStep(naming, parsedArgs, attributedArgs);
  }

  /**
   * @param <T>
   * @param resolvedCommand
   * @param namedArgs
   * @param state
   * @see com.sigpwned.discourse.core.invocation.InvocationPipelineListener#afterEvalPhase(com.sigpwned.discourse.core.command.Command,
   *      java.util.List, java.util.Map)
   */
  public <T> void afterEvalPhase(Command<T> resolvedCommand, List<Entry<String, String>> namedArgs,
      Map<String, Object> state) {
    delegate.afterEvalPhase(resolvedCommand, namedArgs, state);
  }

  /**
   * @param problem
   * @see com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener#catchParsePhaseAttributeStep(java.lang.Throwable)
   */
  public void catchParsePhaseAttributeStep(Throwable problem) {
    delegate.catchParsePhaseAttributeStep(problem);
  }

  /**
   * @param problem
   * @see com.sigpwned.discourse.core.invocation.InvocationPipelineListener#catchEvalPhase(java.lang.Throwable)
   */
  public void catchEvalPhase(Throwable problem) {
    delegate.catchEvalPhase(problem);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener#finallyParsePhaseAttributeStep()
   */
  public void finallyParsePhaseAttributeStep() {
    delegate.finallyParsePhaseAttributeStep();
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.invocation.InvocationPipelineListener#finallyEvalPhase()
   */
  public void finallyEvalPhase() {
    delegate.finallyEvalPhase();
  }

  /**
   * @param <T>
   * @param resolvedCommand
   * @param state
   * @see com.sigpwned.discourse.core.invocation.InvocationPipelineListener#beforeFactoryPhase(com.sigpwned.discourse.core.command.Command,
   *      java.util.Map)
   */
  public <T> void beforeFactoryPhase(Command<T> resolvedCommand, Map<String, Object> state) {
    delegate.beforeFactoryPhase(resolvedCommand, state);
  }

  /**
   * @param <T>
   * @param resolvedCommand
   * @param state
   * @param instance
   * @see com.sigpwned.discourse.core.invocation.InvocationPipelineListener#afterFactoryPhase(com.sigpwned.discourse.core.command.Command,
   *      java.util.Map, java.lang.Object)
   */
  public <T> void afterFactoryPhase(Command<T> resolvedCommand, Map<String, Object> state,
      T instance) {
    delegate.afterFactoryPhase(resolvedCommand, state, instance);
  }

  /**
   * @param problem
   * @see com.sigpwned.discourse.core.invocation.InvocationPipelineListener#catchFactoryPhase(java.lang.Throwable)
   */
  public void catchFactoryPhase(Throwable problem) {
    delegate.catchFactoryPhase(problem);
  }

  /**
   * 
   * @see com.sigpwned.discourse.core.invocation.InvocationPipelineListener#finallyFactoryPhase()
   */
  public void finallyFactoryPhase() {
    delegate.finallyFactoryPhase();
  }
}
