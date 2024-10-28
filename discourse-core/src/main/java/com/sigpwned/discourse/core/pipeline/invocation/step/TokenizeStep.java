package com.sigpwned.discourse.core.pipeline.invocation.step;

import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.Dialect;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.dialect.ArgTokenizer;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipeline;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStep;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStepBase;
import com.sigpwned.discourse.core.pipeline.invocation.step.parse.exception.InvalidSyntaxParseException;

/**
 * A {@link InvocationPipelineStep invocation pipeline step} that converts the application arguments
 * into a series of {@link Token tokens} using the configured {@link Dialect syntax} that can be
 * processed by the rest of the pipeline.
 * 
 * @see InvocationPipeline
 */
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

    ArgTokenizer tokenizer = dialect.newTokenizer();
    for (String arg : args) {
      result.addAll(tokenizer.tokenize(arg).orElseThrow(() -> {
        // This is ticklish. This could be considered invalid user input, which would obviously be a
        // user error, or it could be considered missing required syntax support, which would be
        // an application error. We'll treat it as a user error for now, since this is one of the
        // few parts of the application that actually deals with user input directly, but we might
        // need to revisit this later.
        return new InvalidSyntaxParseException(arg);
      }));
    }

    return unmodifiableList(result);
  }
}
