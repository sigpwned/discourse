package com.sigpwned.discourse.core.module;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.Module;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.args.coordinate.OptionCoordinate;
import com.sigpwned.discourse.core.args.token.SwitchNameToken;
import com.sigpwned.discourse.core.args.token.ValueToken;
import com.sigpwned.discourse.core.invocation.model.SyntaxDetection;
import com.sigpwned.discourse.core.invocation.phase.parse.preprocess.CoordinatesPreprocessor;
import com.sigpwned.discourse.core.invocation.phase.parse.preprocess.TokenStreamPreprocessor;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.CandidateSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.syntax.SyntaxDetector;
import com.sigpwned.discourse.core.module.parameter.flag.FlagCoordinate;
import com.sigpwned.discourse.core.module.parameter.flag.FlagParameter;
import com.sigpwned.discourse.core.util.Maybe;
import com.sigpwned.discourse.core.util.Streams;

public class FlagParameterModule extends Module {
  private Set<SwitchName> flags;

  @Override
  public void registerSyntaxDetectors(Chain<SyntaxDetector> chain) {
    chain.addLast(new SyntaxDetector() {
      @Override
      public Maybe<SyntaxDetection> detectSyntax(Class<?> clazz, CandidateSyntax candidate,
          InvocationContext context) {
        FlagParameter flag = candidate.annotations().stream()
            .mapMulti(Streams.filterAndCast(FlagParameter.class)).findFirst().orElse(null);
        if (flag == null)
          return Maybe.maybe();

        Set<Coordinate> coordinates = new HashSet<>(2);
        if (!flag.longName().equals("")) {
          coordinates.add(new FlagCoordinate(SwitchName.fromString(flag.longName())));
        }
        if (!flag.shortName().equals("")) {
          coordinates.add(new FlagCoordinate(SwitchName.fromString(flag.shortName())));
        }
        if (coordinates.isEmpty()) {
          // TODO better exception
          throw new IllegalArgumentException(
              "@FlagParameter must have at least one of longName or shortName");
        }

        return Maybe.yes(new SyntaxDetection(false, flag.help(), flag.version(), coordinates));
      }
    });
  }

  @Override
  public void registerTokenStreamPreprocessors(Chain<TokenStreamPreprocessor> chain) {
    // TODO Where should I get the flags from?
    chain.addLast(new TokenStreamPreprocessor() {
      @Override
      public List<Token> preprocessTokens(List<Token> tokens, InvocationContext context) {
        if (flags == null) {
          // Because of the documented order of operations, this should never happen.
          throw new IllegalStateException("flags not set");
        }
        List<Token> result = new ArrayList<>(tokens.size());
        for (Token token : tokens) {
          result.add(token);
          if (token instanceof SwitchNameToken switchNameToken) {
            SwitchName switchName = switchNameToken.getName();
            if (flags.contains(switchName)) {
              result.add(new ValueToken("true", false));
            }
          }
        }
        return unmodifiableList(result);
      }
    });
  }

  @Override
  public void registerCoordinatesPreprocessors(Chain<CoordinatesPreprocessor> chain) {
    chain.addLast(new CoordinatesPreprocessor() {
      @Override
      public Map<Coordinate, String> preprocessCoordinates(Map<Coordinate, String> coordinates,
          InvocationContext context) {
        if (flags != null) {
          // Because of the documented order of operations, this should never happen.
          throw new IllegalStateException("flags already set");
        }

        Set<SwitchName> flags = new HashSet<>();
        Map<Coordinate, String> preprocessedCoordinates = new HashMap<>();
        for (Map.Entry<Coordinate, String> entry : coordinates.entrySet()) {
          Coordinate coordinate = entry.getKey();
          String propertyName = entry.getValue();
          if (coordinate instanceof FlagCoordinate flagCoordinate) {
            flags.add(flagCoordinate.getName());
            preprocessedCoordinates.put(new OptionCoordinate(flagCoordinate.getName()),
                propertyName);
          } else {
            preprocessedCoordinates.put(coordinate, propertyName);
          }
        }

        FlagParameterModule.this.flags = flags;

        return unmodifiableMap(preprocessedCoordinates);
      }
    });
  }
}
