package com.sigpwned.discourse.core.invocation.phase.scan.impl;

import com.sigpwned.discourse.core.invocation.model.command.RootCommand;
import com.sigpwned.discourse.core.invocation.phase.ScanPhase;
import com.sigpwned.discourse.core.invocation.phase.scan.CommandScanner;
import com.sigpwned.discourse.core.invocation.phase.scan.CommandWalker;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.RuleDetector;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.RuleNominator;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.RulesEngine;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.syntax.SyntaxDetector;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.syntax.SyntaxNominator;

public class DefaultScanPhase implements ScanPhase {

  private final SubCommandScanner subCommandScanner;
  private final SyntaxNominator syntaxNominator;
  private final SyntaxDetector syntaxDetector;
  private final RuleNominator ruleNominator;
  private final RuleDetector ruleDetector;
  private final NamingScheme namingScheme;
  private final RulesEngine rulesEngine;

  public DefaultScanPhase(SubCommandScanner subCommandScanner, SyntaxNominator syntaxNominator,
      SyntaxDetector syntaxDetector, RuleNominator ruleNominator, RuleDetector ruleDetector,
      NamingScheme namingScheme, RulesEngine rulesEngine) {
    this.subCommandScanner = subCommandScanner;
    this.syntaxNominator = syntaxNominator;
    this.syntaxDetector = syntaxDetector;
    this.ruleNominator = ruleNominator;
    this.ruleDetector = ruleDetector;
    this.namingScheme = namingScheme;
    this.rulesEngine = rulesEngine;
  }

  @Override
  public <T> RootCommand<T> scan(Class<T> clazz) {
    final ConfigurableClassScanner configurableScanner = new ConfigurableClassScanner(
        getNamingScheme(), getSyntaxNominator(), getSyntaxDetector(), getRuleNominator(),
        getRuleDetector(), new ConfigurableClassScannerListener() {
    });
    final CommandWalker commandWalker = new DefaultCommandWalker(getSubCommandScanner());
    final CommandScanner commandScanner = new DefaultCommandScanner(commandWalker,
        configurableScanner,
        getRulesEngine());
    return commandScanner.scanForCommand(clazz);
  }

  protected SubCommandScanner getSubCommandScanner() {
    return subCommandScanner;
  }

  protected SyntaxNominator getSyntaxNominator() {
    return syntaxNominator;
  }

  protected SyntaxDetector getSyntaxDetector() {
    return syntaxDetector;
  }

  protected RuleNominator getRuleNominator() {
    return ruleNominator;
  }

  protected RuleDetector getRuleDetector() {
    return ruleDetector;
  }

  protected NamingScheme getNamingScheme() {
    return namingScheme;
  }

  protected RulesEngine getRulesEngine() {
    return rulesEngine;
  }
}
