package com.sigpwned.discourse.core.pipeline.invocation.step;

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
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStepBase;
import com.sigpwned.discourse.core.pipeline.invocation.step.parse.exception.MissingOptionValueParseException;

public class ParseStep extends InvocationPipelineStepBase {
  public List<Map.Entry<Coordinate, String>> parse(List<Token> tokens, InvocationContext context) {
    InvocationPipelineListener listener = getListener(context);

    List<Map.Entry<Coordinate, String>> result;
    try {
      listener.beforeParseStep(tokens, context);
      result = doParse(tokens);
      listener.afterParseStep(tokens, result, context);
    } catch (Throwable problem) {
      listener.catchParseStep(problem, context);
      throw problem;
    } finally {
      listener.finallyParseStep(context);
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
          throw new MissingOptionValueParseException(nameToken.getName());

        Token nextToken = tokens.get(index + 1);
        if (!(nextToken instanceof ValueToken valueToken))
          throw new MissingOptionValueParseException(nameToken.getName());

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
