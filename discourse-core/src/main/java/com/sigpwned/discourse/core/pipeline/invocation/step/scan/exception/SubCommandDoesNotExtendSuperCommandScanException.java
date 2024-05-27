package com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.ScanException;

@SuppressWarnings("serial")
public class SubCommandDoesNotExtendSuperCommandScanException extends ScanException {
  /**
   * The supercommand class that the subcommand class does not extend.
   */
  private final Class<?> supercommandClazz;

  /**
   * The subcommand class that does not extend the supercommand class.
   */
  private final Class<?> subcommandClazz;

  /**
   * Constructs a new exception with the given supercommand and subcommand classes.
   * 
   * @param supercommandClazz the supercommand class
   * @param subcommandClazz the subcommand class
   */
  public SubCommandDoesNotExtendSuperCommandScanException(Class<?> supercommandClazz,
      Class<?> subcommandClazz) {
    super(format("Subcommand class %s does not extend the supercommand class %s",
        subcommandClazz.getName(), supercommandClazz.getName()));
    this.supercommandClazz = requireNonNull(supercommandClazz);
    this.subcommandClazz = requireNonNull(subcommandClazz);
  }

  /**
   * The supercommand class that the subcommand class does not extend.
   * 
   * @return the supercommand class
   */
  public Class<?> getSupercommandClazz() {
    return supercommandClazz;
  }

  /**
   * The subcommand class that does not extend the supercommand class.
   * 
   * @return the subcommand class
   */
  public Class<?> getSubcommandClazz() {
    return subcommandClazz;
  }
}
