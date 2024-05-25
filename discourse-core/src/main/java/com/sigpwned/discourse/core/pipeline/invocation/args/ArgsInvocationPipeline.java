package com.sigpwned.discourse.core.pipeline.invocation.args;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import java.util.List;
import java.util.Map;
import com.sigpwned.discourse.core.Syntax;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.pipeline.invocation.args.step.AttributeStep;
import com.sigpwned.discourse.core.pipeline.invocation.args.step.GroupStep;
import com.sigpwned.discourse.core.pipeline.invocation.args.step.ParseStep;
import com.sigpwned.discourse.core.pipeline.invocation.args.step.TokenizeStep;

public class ArgsInvocationPipeline {
  private final TokenizeStep tokenizeStep;
  private final ParseStep parseStep;
  private final AttributeStep attributeStep;
  private final GroupStep groupStep;

  public ArgsInvocationPipeline() {
    this(new TokenizeStep(), new ParseStep(), new AttributeStep(), new GroupStep());
  }

  /* default */ ArgsInvocationPipeline(TokenizeStep tokenizeStep, ParseStep parseStep,
      AttributeStep attributeStep, GroupStep groupStep) {
    this.tokenizeStep = requireNonNull(tokenizeStep);
    this.parseStep = requireNonNull(parseStep);
    this.attributeStep = requireNonNull(attributeStep);
    this.groupStep = requireNonNull(groupStep);
  }

  // NEEDED:
  // - Set of flags, for rewriting inputs

  // NOT HANDLED YET:
  // - scan
  // - resolve
  // - rewrite flags
  // - rewrite coordinates for attribution

  public Map<String, List<String>> execute(List<String> args, ArgsInvocationContext context) {
    List<Token> tokens = tokenize(args);
    List<Map.Entry<Coordinate, String>> parsedArgs = parse(tokens);
    List<Map.Entry<String, String>> attributedArgs = attribute(parsedArgs);
    Map<String, List<String>> groupedArgs = group(attributedArgs);
    return groupedArgs;
  }

  protected final List<Token> tokenize(List<String> args) {
    Syntax syntax = context.getSyntax();

    List<Token> tokens;
    try {
      getListener().beforeTokenizeStep(args);
      tokens = getTokenizeStep().tokenize(syntax, args, context);
      getListener().afterTokenizeStep(args, tokens);
    } catch (Throwable t) {
      getListener().catchTokenizeStep(t);
      throw t;
    } finally {
      getListener().finallyTokenizeStep();
    }
    return unmodifiableList(tokens);
  }

  protected final List<Map.Entry<Coordinate, String>> parse(List<Token> tokens) {
    List<Map.Entry<Coordinate, String>> parsedArgs;
    try {
      getListener().beforeParseStep(tokens);
      parsedArgs = getParseStep().parse(tokens);
      getListener().afterParseStep(tokens, parsedArgs);
    } catch (Throwable t) {
      getListener().catchParseStep(t);
      throw t;
    } finally {
      getListener().finallyParseStep();
    }
    return unmodifiableList(parsedArgs);
  }

  protected final List<Map.Entry<String, String>> attribute(
      List<Map.Entry<Coordinate, String>> parsedArgs) {
    List<Map.Entry<String, String>> attributedArgs;
    try {
      getListener().beforeAttributeStep(parsedArgs);
      attributedArgs = getAttributeStep().step(parsedArgs);
      getListener().afterAttributeStep(parsedArgs, attributedArgs);
    } catch (Throwable t) {
      getListener().catchAttributeStep(t);
      throw t;
    } finally {
      getListener().finallyAttributeStep();
    }
    return unmodifiableList(attributedArgs);
  }

  protected final Map<String, List<String>> group(List<Map.Entry<String, String>> attributedArgs) {
    Map<String, List<String>> groupedArgs;
    try {
      getListener().beforeGroupStep(attributedArgs);
      groupedArgs = getGroupStep().step(attributedArgs);
      getListener().afterGroupStep(attributedArgs, groupedArgs);
    } catch (Throwable t) {
      getListener().catchGroupStep(t);
      throw t;
    } finally {
      getListener().finallyGroupStep();
    }
    return groupedArgs;
  }

  /**
   * @return the tokenizeStep
   */
  private TokenizeStep getTokenizeStep() {
    return tokenizeStep;
  }

  /**
   * @return the parseStep
   */
  private ParseStep getParseStep() {
    return parseStep;
  }

  /**
   * @return the attributeStep
   */
  private AttributeStep getAttributeStep() {
    return attributeStep;
  }

  /**
   * @return the groupStep
   */
  private GroupStep getGroupStep() {
    return groupStep;
  }

  /**
   * @return the listener
   */
  private ArgsInvocationPipelineListener getListener() {
    return listener;
  }
}
