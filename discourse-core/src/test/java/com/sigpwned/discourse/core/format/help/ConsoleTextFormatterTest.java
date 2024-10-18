package com.sigpwned.discourse.core.format.help;

import static org.mockito.Mockito.mock;
import org.junit.BeforeClass;
import com.sigpwned.discourse.core.format.help.text.console.ConsoleBoldTextFormatter;
import com.sigpwned.discourse.core.format.help.text.console.ConsoleItalicTextFormatter;
import com.sigpwned.discourse.core.format.help.text.console.ConsoleStrikethruTextFormatter;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

/**
 * Test the console text formatters.
 */
public class ConsoleTextFormatterTest extends TextFormatterTestBase {
  @BeforeClass
  public static void setupConsoleTextFormatterTestClass() {
    TextFormatterChain chain = new TextFormatterChain();
    chain.addLast(ConsoleItalicTextFormatter.INSTANCE);
    chain.addLast(ConsoleBoldTextFormatter.INSTANCE);
    chain.addLast(ConsoleStrikethruTextFormatter.INSTANCE);
    formatter = chain;

    context = mock(InvocationContext.class);

    italicTestExpectedResult = "\033[3mHello, World!\033[23m";
    boldTestExpectedResult = "\033[1mHello, World!\033[21m";
    strikethruTestExpectedResult = "\033[9mHello, World!\033[29m";
  }
}
