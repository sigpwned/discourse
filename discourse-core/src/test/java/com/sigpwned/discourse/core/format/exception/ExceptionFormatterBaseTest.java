package com.sigpwned.discourse.core.format.exception;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class ExceptionFormatterBaseTest {
  @Test
  public void givenCustomExceptionAndResourceBundle_whenFormatWithExceptionFormatterBase_thenGetExpectedFormat()
      throws IOException {
    @SuppressWarnings("serial")
    class FooException extends RuntimeException {
      public final int foo;

      public FooException(int foo) {
        super("foo: " + foo);
        this.foo = foo;
      }
    }

    ExceptionFormatterBase<FooException> formatter = new ExceptionFormatterBase<>(
        FooException.class, "com.sigpwned.discourse.core.format.TestMessages") {
      @Override
      protected Object[] getFormatArguments(FooException problem, InvocationContext context) {
        return new Object[] {problem.foo};
      }

      @Override
      protected String getFormatKey() {
        return "ExceptionFormatterBaseTest_foo";
      }
    };

    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    try {
      try (PrintStream out = new PrintStream(buf)) {
        formatter.formatException(out, new FooException(42), null);
      }
    } finally {
      buf.close();
    }

    assertThat(buf.toString(StandardCharsets.UTF_8), is("foo 42 test" + System.lineSeparator()));
  }
}
