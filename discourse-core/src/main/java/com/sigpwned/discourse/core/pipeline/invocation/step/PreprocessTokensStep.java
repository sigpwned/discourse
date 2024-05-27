package com.sigpwned.discourse.core.pipeline.invocation.step;

import java.util.List;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext.Key;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStepBase;
import com.sigpwned.discourse.core.pipeline.invocation.step.preprocess.tokens.TokensPreprocessor;

public class PreprocessTokensStep extends InvocationPipelineStepBase {
  public static final Key<TokensPreprocessor> TOKENS_PREPROCESSOR_KEY =
      Key.of(TokensPreprocessor.class);

  public List<Token> preprocessTokens(List<Token> originalTokens, InvocationContext context) {
    TokensPreprocessor preprocessor = context.get(TOKENS_PREPROCESSOR_KEY).orElseThrow(() -> {
      // TODO better exception
      return new IllegalStateException("No tokens preprocessor");
    });

    List<Token> preprocessedTokens;
    try {
      getListener(context).beforePreprocessTokensStep(originalTokens);
      preprocessedTokens = doPreprocessTokens(preprocessor, originalTokens);
      getListener(context).afterPreprocessTokensStep(originalTokens, preprocessedTokens);
    } catch (Throwable e) {
      getListener(context).catchPreprocessTokensStep(e);
      throw e;
    } finally {
      getListener(context).finallyPreprocessTokensStep();
    }

    return preprocessor.preprocessTokens(preprocessedTokens);
  }

  protected List<Token> doPreprocessTokens(TokensPreprocessor preprocessor,
      List<Token> originalTokens) {
    return preprocessor.preprocessTokens(originalTokens);
  }
}
