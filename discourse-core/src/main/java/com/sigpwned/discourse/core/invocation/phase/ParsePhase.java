package com.sigpwned.discourse.core.invocation.phase;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.Syntax;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.args.coordinate.OptionCoordinate;
import com.sigpwned.discourse.core.args.coordinate.PositionalCoordinate;
import com.sigpwned.discourse.core.args.token.SwitchNameToken;
import com.sigpwned.discourse.core.args.token.ValueToken;
import com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener;
import com.sigpwned.discourse.core.invocation.phase.parse.exception.semantic.UnrecognizedParseCoordinateSemanticParseException;
import com.sigpwned.discourse.core.invocation.phase.parse.exception.syntax.OptionValueMissingSyntaxParseException;
import com.sigpwned.discourse.core.invocation.phase.parse.preprocess.ArgsPreprocessor;
import com.sigpwned.discourse.core.invocation.phase.parse.preprocess.CoordinatesPreprocessor;
import com.sigpwned.discourse.core.invocation.phase.parse.preprocess.TokenStreamPreprocessor;
import com.sigpwned.discourse.core.syntax.SyntaxTokenizer;

public class ParsePhase {
  private final ParsePhaseListener listener;

  public ParsePhase(ParsePhaseListener listener) {
    this.listener = requireNonNull(listener);
  }

  public final List<Entry<String, String>> parse(Map<Coordinate, String> names, List<String> args,
      InvocationContext context) {
    Map<Coordinate, String> preprocessedNames = doPreprocessCoordinatesStep(names, context);

    List<String> preprocessedArgs = doPreprocessArgsStep(args, context);

    List<Token> tokens = doTokenizeStep(preprocessedArgs, context);

    List<Token> preprocessedTokens = doPreprocessTokenStreamStep(tokens, context);

    List<Map.Entry<Coordinate, String>> parsedArgs = doParseStep(preprocessedTokens, context);

    List<Map.Entry<String, String>> attributedArgs =
        doAttributeStep(preprocessedNames, parsedArgs, context);

    return unmodifiableList(attributedArgs);
  }

  private Map<Coordinate, String> doPreprocessCoordinatesStep(Map<Coordinate, String> names,
      InvocationContext context) {
    Map<Coordinate, String> preprocessedNames;
    try {
      getListener().beforeParsePhasePreprocessCoordinatesStep(names);
      preprocessedNames =
          preprocessCoordinatesStep(context.getCoordinatesPreprocessor(), names, context);
      getListener().afterParsePhasePreprocessCoordinatesStep(names, preprocessedNames);
    } catch (Throwable problem) {
      getListener().catchParsePhasePreprocessCoordinatesStep(problem);
      throw problem;
    } finally {
      getListener().finallyParsePhasePreprocessCoordinatesStep();
    }
    return unmodifiableMap(preprocessedNames);
  }

  protected Map<Coordinate, String> preprocessCoordinatesStep(CoordinatesPreprocessor preprocessor,
      Map<Coordinate, String> naming, InvocationContext context) {
    return preprocessor.preprocessCoordinates(naming);
  }

  private List<String> doPreprocessArgsStep(List<String> args, InvocationContext context) {
    List<String> preprocessedArgs;
    try {
      getListener().beforeParsePhasePreprocessArgsStep(args);
      preprocessedArgs = preprocessArgsStep(context.getArgsPreprocessor(), args, context);
      getListener().afterParsePhasePreprocessArgsStep(args, preprocessedArgs);
    } catch (Throwable problem) {
      getListener().catchParsePhasePreprocessArgsStep(problem);
      throw problem;
    } finally {
      getListener().finallyParsePhasePreprocessArgsStep();
    }
    return unmodifiableList(preprocessedArgs);
  }

  protected List<String> preprocessArgsStep(ArgsPreprocessor preprocessor, List<String> args,
      InvocationContext context) {
    return preprocessor.preprocessArgs(args);
  }

  private List<Token> doPreprocessTokenStreamStep(List<Token> tokens, InvocationContext context) {
    List<Token> preprocessedTokens;
    try {
      getListener().beforeParsePhasePreprocessTokensStep(tokens);
      preprocessedTokens =
          preprocessTokenStreamStep(context.getTokenStreamPreprocessor(), tokens, context);
      getListener().afterParsePhasePreprocessTokensStep(tokens, preprocessedTokens);
    } catch (Throwable problem) {
      getListener().catchParsePhasePreprocessTokensStep(problem);
      throw problem;
    } finally {
      getListener().finallyParsePhasePreprocessTokensStep();
    }
    return unmodifiableList(preprocessedTokens);
  }

  protected List<Token> preprocessTokenStreamStep(TokenStreamPreprocessor preprocessor,
      List<Token> tokens, InvocationContext context) {
    return preprocessor.preprocessTokens(tokens);
  }

  private List<Token> doTokenizeStep(List<String> args, InvocationContext context) {
    List<Token> tokens;
    try {
      getListener().beforeParsePhaseTokenizeStep(args);
      tokens = tokenizeStep(context.getSyntax(), args, context);
      getListener().afterParsePhaseTokenizeStep(args, tokens);
    } catch (Throwable problem) {
      getListener().catchParsePhaseTokenizeStep(problem);
      throw problem;
    } finally {
      getListener().finallyParsePhaseTokenizeStep();
    }
    return unmodifiableList(tokens);
  }

  protected List<Token> tokenizeStep(Syntax syntax, List<String> args, InvocationContext context) {
    SyntaxTokenizer tokenizer = syntax.newTokenizer();

    List<Token> tokens = new ArrayList<>();
    for (String arg : args) {
      tokens.addAll(tokenizer.tokenize(arg));
    }

    return unmodifiableList(tokens);
  }

  private List<Map.Entry<Coordinate, String>> doParseStep(List<Token> preprocessedTokens,
      InvocationContext context) {
    List<Map.Entry<Coordinate, String>> parsedArgs;
    try {
      getListener().beforeParsePhaseParseStep(preprocessedTokens);
      parsedArgs = parseStep(preprocessedTokens, context);
      getListener().afterParsePhaseParseStep(preprocessedTokens, parsedArgs);
    } catch (Throwable problem) {
      getListener().catchParsePhaseParseStep(problem);
      throw problem;
    } finally {
      getListener().finallyParsePhaseParseStep();
    }
    return unmodifiableList(parsedArgs);
  }

  protected List<Map.Entry<Coordinate, String>> parseStep(List<Token> preprocessedTokens,
      InvocationContext context) {
    List<Map.Entry<Coordinate, String>> parsedArgs = new ArrayList<>(preprocessedTokens.size());

    int index = 0;
    PositionalCoordinate position = PositionalCoordinate.ZERO;
    while (index < preprocessedTokens.size()) {
      Token token = preprocessedTokens.get(index);
      if (token instanceof SwitchNameToken nameToken) {
        // TODO better exception? EOF Exception?
        if (index + 1 >= preprocessedTokens.size())
          throw new OptionValueMissingSyntaxParseException(nameToken.getName().toString());

        Token nextToken = preprocessedTokens.get(index + 1);
        if (!(nextToken instanceof ValueToken valueToken))
          throw new OptionValueMissingSyntaxParseException(nameToken.getName().toString());

        parsedArgs.add(Map.entry(new OptionCoordinate(nameToken.getName()), valueToken.getValue()));

        index = index + 2;
      } else if (token instanceof ValueToken valueToken) {
        parsedArgs.add(Map.entry(position, valueToken.getValue()));
        index = index + 1;
      } else {
        // This is an internal error
        // TODO better exception
        throw new IllegalArgumentException("Invalid token type: " + token.getClass().getName());
      }
    }

    return unmodifiableList(parsedArgs);
  }

  private List<Map.Entry<String, String>> doAttributeStep(Map<Coordinate, String> names,
      List<Map.Entry<Coordinate, String>> parsedArgs, InvocationContext context) {
    List<Map.Entry<String, String>> attributedArgs;
    try {
      getListener().beforeParsePhaseAttributeStep(names, parsedArgs);
      attributedArgs = attributeStep(names, parsedArgs, context);
      getListener().afterParsePhaseAttributeStep(names, parsedArgs, attributedArgs);
    } catch (Throwable problem) {
      getListener().catchParsePhaseAttributeStep(problem);
      throw problem;
    } finally {
      getListener().finallyParsePhaseAttributeStep();
    }
    return unmodifiableList(attributedArgs);
  }

  protected List<Map.Entry<String, String>> attributeStep(Map<Coordinate, String> propertyNames,
      List<Map.Entry<Coordinate, String>> parsedArgs, InvocationContext context) {
    List<Map.Entry<String, String>> attributedArgs = new ArrayList<>(parsedArgs.size());

    for (Map.Entry<Coordinate, String> parsedArg : parsedArgs) {
      Coordinate coordinate = parsedArg.getKey();

      String name = propertyNames.get(coordinate);
      if (name == null)
        throw new UnrecognizedParseCoordinateSemanticParseException(coordinate);

      String value = parsedArg.getValue();

      attributedArgs.add(Map.entry(name, value));
    }

    return unmodifiableList(attributedArgs);
  }

  /**
   * @return the listener
   */
  private ParsePhaseListener getListener() {
    return listener;
  }
}
