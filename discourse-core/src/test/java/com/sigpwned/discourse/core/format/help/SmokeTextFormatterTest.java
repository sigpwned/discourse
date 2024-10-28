package com.sigpwned.discourse.core.format.help;

import static org.mockito.Mockito.mock;
import org.junit.BeforeClass;
import com.sigpwned.discourse.core.format.help.text.BoldTextFormatterBase;
import com.sigpwned.discourse.core.format.help.text.ItalicTextFormatterBase;
import com.sigpwned.discourse.core.format.help.text.StrikethruTextFormatterBase;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

/**
 * Perform a smoke test on the {@link TextFormatter} classes using a simple implementation built
 * just for this test.
 */
public class SmokeTextFormatterTest extends TextFormatterTestBase {
  @BeforeClass
  public static void setupConsoleTextFormatterTestClass() {
    TextFormatterChain chain = new TextFormatterChain();
    chain.addLast(new BoldTextFormatterBase("<b>", "</b>") {});
    chain.addLast(new ItalicTextFormatterBase("<i>", "</i>") {});
    chain.addLast(new StrikethruTextFormatterBase("<s>", "</s>") {});
    formatter = chain;

    context = mock(InvocationContext.class);

    italicTestExpectedResult = "<i>Hello, World!</i>";
    boldTestExpectedResult = "<b>Hello, World!</b>";
    strikethruTestExpectedResult = "<s>Hello, World!</s>";
  }
}
