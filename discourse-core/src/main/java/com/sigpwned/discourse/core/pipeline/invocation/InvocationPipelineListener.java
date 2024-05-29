package com.sigpwned.discourse.core.pipeline.invocation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.command.PlannedCommand;
import com.sigpwned.discourse.core.command.ResolvedCommand;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.PreparedClass;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.WalkedClass;

public interface InvocationPipelineListener {
  // PIPELINE /////////////////////////////////////////////////////////////////////////////////////
  default void beforePipeline(InvocationContext context) {}

  default void afterPipeline(Object instance, InvocationContext context) {}

  default void catchPipeline(Throwable t, InvocationContext context) {}

  default void finallyPipeline(InvocationContext context) {}

  // SCAN STEP ////////////////////////////////////////////////////////////////////////////////////
  default <T> void beforeScanStep(Class<T> clazz, InvocationContext context) {}

  default <T> void afterScanStep(Class<T> clazz, RootCommand<T> root, InvocationContext context) {}

  default void catchScanStep(Throwable t, InvocationContext context) {}

  default void finallyScanStep(InvocationContext context) {}

  ////// WALK /////////////////////////////////////////////////////////////////////////////////////
  default <T> void beforeScanStepWalk(Class<T> clazz, InvocationContext context) {}

  default <T> void afterScanStepWalk(Class<T> clazz, List<WalkedClass<? extends T>> walkedClasses,
      InvocationContext context) {}

  default void catchScanStepWalk(Throwable t, InvocationContext context) {}

  default void finallyScanStepWalk(InvocationContext context) {}

  ////// PREPARE //////////////////////////////////////////////////////////////////////////////////
  default <T> void beforeScanStepPrepare(List<WalkedClass<? extends T>> walkedClasses,
      InvocationContext context) {}

  default <T> void afterScanStepPrepare(List<WalkedClass<? extends T>> walkedClasses,
      List<PreparedClass<? extends T>> preparedClasses, InvocationContext context) {}

  default void catchScanStepPrepare(Throwable t, InvocationContext context) {}

  default void finallyScanStepPrepare(InvocationContext context) {}

  ////// TREE /////////////////////////////////////////////////////////////////////////////////////
  default <T> void beforeScanStepTree(List<PreparedClass<? extends T>> preparedClasses,
      InvocationContext context) {}

  default <T> void afterScanStepTree(List<PreparedClass<? extends T>> preparedClasses,
      RootCommand<T> root, InvocationContext context) {}

  default void catchScanStepTree(Throwable t, InvocationContext context) {}

  default void finallyScanStepTree(InvocationContext context) {}

  // RESOLVE STEP /////////////////////////////////////////////////////////////////////////////////
  default void beforeResolveStep(List<String> args, InvocationContext context) {}

  default <T> void afterResolveStep(List<String> args,
      Optional<ResolvedCommand<? extends T>> maybeResolvedCommand, InvocationContext context) {}

  default void catchResolveStep(Throwable t, InvocationContext context) {}

  default void finallyResolveStep(InvocationContext context) {}

  // PLAN STEP ////////////////////////////////////////////////////////////////////////////////////
  default <T> void beforePlanStep(ResolvedCommand<? extends T> resolvedCommand,
      InvocationContext context) {}

  default <T> void afterPlanStep(ResolvedCommand<? extends T> resolvedCommand,
      PlannedCommand<? extends T> plannedCommand, InvocationContext context) {}

  default void catchPlanStep(Throwable t, InvocationContext context) {}

  default void finallyPlanStep(InvocationContext context) {}

  // PREPROCESS COORDINATES STEP //////////////////////////////////////////////////////////////////
  default void beforePreprocessCoordinatesStep(Map<Coordinate, String> originalCoordinates,
      InvocationContext context) {}

  default void afterPreprocessCoordinatesStep(Map<Coordinate, String> originalCoordinates,
      Map<Coordinate, String> preprocessedCoordinates, InvocationContext context) {}

  default void catchPreprocessCoordinatesStep(Throwable t, InvocationContext context) {}

  default void finallyPreprocessCoordinatesStep(InvocationContext context) {}

  // PREPROCESS ARGS STEP /////////////////////////////////////////////////////////////////////////
  default void beforePreprocessArgsStep(List<String> args, InvocationContext context) {}

  default void afterPreprocessArgsStep(List<String> args, List<String> preprocessedArgs,
      InvocationContext context) {}

  default void catchPreprocessArgsStep(Throwable t, InvocationContext context) {}

  default void finallyPreprocessArgsStep(InvocationContext context) {}

  // TOKENIZE STEP ////////////////////////////////////////////////////////////////////////////////
  default void beforeTokenizeStep(List<String> args, InvocationContext context) {}

  default void afterTokenizeStep(List<String> args, List<Token> tokens,
      InvocationContext context) {}

  default void catchTokenizeStep(Throwable t, InvocationContext context) {}

  default void finallyTokenizeStep(InvocationContext context) {}

  // PREPROCESS TOKENS STEP ///////////////////////////////////////////////////////////////////////
  default void beforePreprocessTokensStep(List<Token> tokens, InvocationContext context) {}

  default void afterPreprocessTokensStep(List<Token> tokens, List<Token> preprocessedTokens,
      InvocationContext context) {}

  default void catchPreprocessTokensStep(Throwable t, InvocationContext context) {}

  default void finallyPreprocessTokensStep(InvocationContext context) {}

  // PARSE STEP ///////////////////////////////////////////////////////////////////////////////////
  default void beforeParseStep(List<Token> tokens, InvocationContext context) {}

  default void afterParseStep(List<Token> tokens, List<Map.Entry<Coordinate, String>> parsedArgs,
      InvocationContext context) {}

  default void catchParseStep(Throwable t, InvocationContext context) {}

  default void finallyParseStep(InvocationContext context) {}

  // ATTRIBUTE STEP ///////////////////////////////////////////////////////////////////////////////
  default void beforeAttributeStep(List<Map.Entry<Coordinate, String>> parsedArgs,
      InvocationContext context) {}

  default void afterAttributeStep(List<Map.Entry<Coordinate, String>> parsedArgs,
      List<Map.Entry<String, String>> attributedArgs, InvocationContext context) {}

  default void catchAttributeStep(Throwable t, InvocationContext context) {}

  default void finallyAttributeStep(InvocationContext context) {}

  // GROUP STEP ///////////////////////////////////////////////////////////////////////////////////
  default void beforeGroupStep(List<Map.Entry<String, String>> attributedArgs,
      InvocationContext context) {}

  default void afterGroupStep(List<Map.Entry<String, String>> attributedArgs,
      Map<String, List<String>> groupedArgs, InvocationContext context) {}

  default void catchGroupStep(Throwable t, InvocationContext context) {}

  default void finallyGroupStep(InvocationContext context) {}

  // MAP STEP /////////////////////////////////////////////////////////////////////////////////////
  default void beforeMapStep(Map<String, List<String>> groupedArgs, InvocationContext context) {}

  default void afterMapStep(Map<String, List<String>> groupedArgs,
      Map<String, List<Object>> mappedArgs, InvocationContext context) {}

  default void catchMapStep(Throwable t, InvocationContext context) {}

  default void finallyMapStep(InvocationContext context) {}

  // REDUCE STEP //////////////////////////////////////////////////////////////////////////////////
  default void beforeReduceStep(Map<String, List<Object>> mappedArgs, InvocationContext context) {}

  default void afterReduceStep(Map<String, List<Object>> mappedArgs, Map<String, Object> sinkedArgs,
      InvocationContext context) {}

  default void catchReduceStep(Throwable t, InvocationContext context) {}

  default void finallyReduceStep(InvocationContext context) {}

  // FINISH STEP //////////////////////////////////////////////////////////////////////////////////
  default void beforeFinishStep(Map<String, Object> sinkedArgs, InvocationContext context) {}

  default void afterFinishStep(Map<String, Object> sinkedArgs, Object result,
      InvocationContext context) {}

  default void catchFinishStep(Throwable t, InvocationContext context) {}

  default void finallyFinishStep(InvocationContext context) {}

}
