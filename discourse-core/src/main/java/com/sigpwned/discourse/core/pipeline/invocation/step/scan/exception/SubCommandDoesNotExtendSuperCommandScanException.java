package com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.ScanException;

/**
 * Used when a subcommand class does not extend the supercommand class.
 */
@SuppressWarnings("serial")
public class SubCommandDoesNotExtendSuperCommandScanException extends ScanException {
  /**
   * The supercommand class that the subcommand class does not extend.
   */
  private final Class<?> supercommandClazz;

  /**
   * Constructs a new exception with the given supercommand and subcommand classes.
   * 
   * @param supercommandClazz the supercommand class
   * @param subcommandClazz the subcommand class
   */
  public SubCommandDoesNotExtendSuperCommandScanException(Class<?> supercommandClazz,
      Class<?> clazz) {
    super(clazz, format("Subcommand class %s does not extend the supercommand class %s",
        clazz.getName(), supercommandClazz.getName()));
    this.supercommandClazz = requireNonNull(supercommandClazz);
  }

  /**
   * The supercommand class that the subcommand class does not extend.
   * 
   * @return the supercommand class
   */
  public Class<?> getSupercommandClazz() {
    return supercommandClazz;
  }

  @Override
  protected Object[] getLocalizedMessageArguments() {
    return new Object[] {getClazz().getName(), getSupercommandClazz().getName()};
  }
}
