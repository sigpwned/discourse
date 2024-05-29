package com.sigpwned.discourse.core.pipeline.invocation.step;

import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.Dialect;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.dialect.DialectTokenizer;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStepBase;

public class TokenizeStep extends InvocationPipelineStepBase {
  public static final InvocationContext.Key<Dialect> SYNTAX_KEY =
      InvocationContext.Key.of(Dialect.class);

  public List<Token> tokenize(List<String> args, InvocationContext context) {
    Dialect syntax = context.get(SYNTAX_KEY).orElseThrow();

    InvocationPipelineListener listener = getListener(context);

    List<Token> result;
    try {
      listener.beforeTokenizeStep(args, context);
      result = doTokenize(syntax, args);
      listener.afterTokenizeStep(args, result, context);
    } catch (Throwable problem) {
      listener.catchTokenizeStep(problem, context);
      throw problem;
    } finally {
      listener.finallyTokenizeStep(context);
    }

    return unmodifiableList(result);
  }

  protected List<Token> doTokenize(Dialect dialect, List<String> args) {
    List<Token> result = new ArrayList<>();

    DialectTokenizer tokenizer = dialect.newTokenizer();
    for (String arg : args) {
      result.addAll(tokenizer.tokenize(arg));
    }

    return unmodifiableList(result);
  }
}
