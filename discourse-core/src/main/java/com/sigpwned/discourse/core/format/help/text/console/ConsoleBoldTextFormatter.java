package com.sigpwned.discourse.core.format.help.text.console;

import com.sigpwned.discourse.core.format.help.text.BoldTextFormatterBase;
import com.sigpwned.discourse.core.util.Consoles;

public class ConsoleBoldTextFormatter extends BoldTextFormatterBase {
  public static final String BEGIN_FORMAT = Consoles.ansiControlSequence(Consoles.BOLD);

  public static final String END_FORMAT = Consoles.ansiControlSequence(Consoles.RESET_BOLD);

  public static final ConsoleBoldTextFormatter INSTANCE = new ConsoleBoldTextFormatter();

  public ConsoleBoldTextFormatter() {
    super(BEGIN_FORMAT, END_FORMAT);
  }
}
