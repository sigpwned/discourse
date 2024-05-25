package com.sigpwned.discourse.core.pipeline.invocation.args.step;

import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.Syntax;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.pipeline.invocation.args.ArgsInvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.args.ArgsInvocationPipelineListener;
import com.sigpwned.discourse.core.syntax.SyntaxTokenizer;

public class TokenizeStep {
  public List<Token> tokenize(Syntax syntax, List<String> args, ArgsInvocationContext context) {
    ArgsInvocationPipelineListener listener = context.getListener();

    List<Token> result;
    try {
      listener.beforeTokenizeStep(args);
      result = doTokenize(syntax, args);
      listener.afterTokenizeStep(args, result);
    } catch (Throwable problem) {
      listener.catchTokenizeStep(problem);
      throw problem;
    } finally {
      listener.finallyTokenizeStep();
    }

    return unmodifiableList(result);
  }

  protected List<Token> doTokenize(Syntax syntax, List<String> args) {
    List<Token> result = new ArrayList<>();

    SyntaxTokenizer tokenizer = syntax.newTokenizer();
    for (String arg : args) {
      result.addAll(tokenizer.tokenize(arg));
    }

    return unmodifiableList(result);
  }
}
