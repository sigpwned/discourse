package com.sigpwned.discourse.core.pipeline.invocation.args.step;

import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.args.coordinate.OptionCoordinate;
import com.sigpwned.discourse.core.args.coordinate.PositionalCoordinate;
import com.sigpwned.discourse.core.args.token.SwitchNameToken;
import com.sigpwned.discourse.core.args.token.ValueToken;
import com.sigpwned.discourse.core.invocation.phase.parse.exception.syntax.OptionValueMissingSyntaxParseException;
import com.sigpwned.discourse.core.pipeline.invocation.args.ArgsInvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.args.ArgsInvocationPipelineListener;

public class ParseStep {
  public List<Map.Entry<Coordinate, String>> parse(List<Token> tokens,
      ArgsInvocationContext context) {
    ArgsInvocationPipelineListener listener = context.getListener();

    List<Map.Entry<Coordinate, String>> result;
    try {
      listener.beforeParseStep(tokens);
      result = doParse(tokens);
      listener.afterParseStep(tokens, result);
    } catch (Throwable problem) {
      listener.catchParseStep(problem);
      throw problem;
    } finally {
      listener.finallyParseStep();
    }

    return unmodifiableList(result);
  }

  protected List<Map.Entry<Coordinate, String>> doParse(List<Token> tokens) {
    List<Map.Entry<Coordinate, String>> result = new ArrayList<>();

    int index = 0;
    PositionalCoordinate position = PositionalCoordinate.ZERO;
    while (index < tokens.size()) {
      Token token = tokens.get(index);
      if (token instanceof SwitchNameToken nameToken) {
        // TODO better exception? EOF Exception?
        if (index + 1 >= tokens.size())
          throw new OptionValueMissingSyntaxParseException(nameToken.getName().toString());

        Token nextToken = tokens.get(index + 1);
        if (!(nextToken instanceof ValueToken valueToken))
          throw new OptionValueMissingSyntaxParseException(nameToken.getName().toString());

        result.add(Map.entry(new OptionCoordinate(nameToken.getName()), valueToken.getValue()));

        index = index + 2;
      } else if (token instanceof ValueToken valueToken) {
        result.add(Map.entry(position, valueToken.getValue()));
        index = index + 1;
      } else {
        // This is an internal error
        // TODO better exception
        throw new IllegalArgumentException("Invalid token type: " + token.getClass().getName());
      }
    }

    return unmodifiableList(result);
  }
}
