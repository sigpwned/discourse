package com.sigpwned.discourse.core.format.help;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public abstract class TextFormatterTestBase {
  public static TextFormatter formatter;

  public static InvocationContext context;

  public static String italicTestExpectedResult;

  public static String boldTestExpectedResult;

  public static String strikethruTestExpectedResult;

  @Test
  public void unformattedTest() {
    String originalText = "Hello, World!";

    String formattedText = formatter.formatText(originalText, context);

    assertThat(formattedText, is("Hello, World!"));
  }

  @Test
  public void italicTest() {
    String originalText = "*Hello, World!*";

    String formattedText = formatter.formatText(originalText, context);

    assertThat(formattedText, is(italicTestExpectedResult));
  }

  @Test
  public void boldTest() {
    String originalText = "**Hello, World!**";

    String formattedText = formatter.formatText(originalText, context);

    assertThat(formattedText, is(boldTestExpectedResult));
  }

  @Test
  public void strikethruTest() {
    String originalText = "~~Hello, World!~~";

    String formattedText = formatter.formatText(originalText, context);

    assertThat(formattedText, is(strikethruTestExpectedResult));
  }
}
