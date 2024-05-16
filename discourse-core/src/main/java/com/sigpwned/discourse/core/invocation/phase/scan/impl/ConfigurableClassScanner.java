package com.sigpwned.discourse.core.invocation.phase.scan.impl;

import static java.util.Objects.requireNonNull;
import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.model.ConfigurableClass;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.RuleDetector;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.RuleNominator;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.model.CandidateRule;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.model.DetectedRule;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.model.NamedRule;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.syntax.CandidateSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.syntax.DetectedSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.syntax.NamedSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.syntax.SyntaxDetector;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.syntax.SyntaxNominator;

public class ConfigurableClassScanner {

  private final NamingScheme namingScheme;
  private final SyntaxNominator syntaxNominator;
  private final SyntaxDetector syntaxDetector;
  private final RuleNominator ruleNominator;
  private final RuleDetector ruleDetector;
  private final ConfigurableClassScannerListener listener;

  public ConfigurableClassScanner(NamingScheme namingScheme, SyntaxNominator syntaxNominator,
      SyntaxDetector syntaxDetector, RuleNominator ruleNominator, RuleDetector ruleDetector,
      ConfigurableClassScannerListener listener) {
    this.namingScheme = requireNonNull(namingScheme);
    this.syntaxNominator = requireNonNull(syntaxNominator);
    this.syntaxDetector = requireNonNull(syntaxDetector);
    this.ruleNominator = requireNonNull(ruleNominator);
    this.ruleDetector = requireNonNull(ruleDetector);
    this.listener = requireNonNull(listener);
  }

  /**
   * There must be a way to instantiate these classes. That does not mean they must not be abstract,
   * or that they must have a public default constructor, or even a public constructor. It just
   * means that there must be a way to instantiate them. A public static factory method, for
   * example, would be sufficient.
   *
   * @param clazz
   * @param <T>
   * @return
   */
  public final <T> ConfigurableClass<T> scan(Class<T> clazz) {
    if (clazz.isPrimitive()) {
      // We disallow primitive functions explicitly
      throw new IllegalArgumentException("Cannot scan primitive class " + clazz);
    }

    Configurable configurable = clazz.getAnnotation(Configurable.class);
    if (configurable == null) {
      // TODO better exception
      throw new IllegalArgumentException("Class " + clazz + " is not annotated with @Configurable");
    }

    List<CandidateSyntax> candidateSyntax = nominateSyntax(clazz);
    List<DetectedSyntax> detectedSyntax = detectSyntax(clazz, candidateSyntax);
    List<NamedSyntax> syntax = nameSyntax(clazz, detectedSyntax);

    List<CandidateRule> candidateRules = nominateRules(clazz, syntax);
    List<DetectedRule> detectedRules = detectRules(clazz, syntax, candidateRules);
    List<NamedRule> rules = nameRules(clazz, syntax, detectedRules);

    // TODO Validate the rules

    return new ConfigurableClass<>(clazz, syntax, rules);
  }

  protected List<CandidateSyntax> nominateSyntax(Class<?> clazz) {
    getListener().beforeNominateSyntax(clazz);

    List<CandidateSyntax> result = getSyntaxNominator().nominateSyntax(clazz);

    getListener().afterNominateSyntax(clazz, result);

    return result;
  }

  private List<DetectedSyntax> detectSyntax(Class<?> clazz, List<CandidateSyntax> candidateSyntax) {
    getListener().beforeDetectSyntax(clazz, candidateSyntax);

    List<DetectedSyntax> result = candidateSyntax.stream()
        .flatMap(csi -> getSyntaxDetector().detectSyntax(clazz, csi).stream()).toList();

    getListener().afterDetectSyntax(clazz, candidateSyntax, result);

    return result;
  }

  private List<NamedSyntax> nameSyntax(Class<?> clazz, List<DetectedSyntax> detectedSyntax) {
    getListener().beforeNameSyntax(clazz, detectedSyntax);

    List<NamedSyntax> result = detectedSyntax.stream().map(ds -> {
      String name = getNamingScheme().name(ds.nominated())
          .orElseThrow(() -> new IllegalStateException("No name for " + ds.nominated()));
      return new NamedSyntax(ds.nominated(), ds.genericType(), ds.annotations(), ds.required(),
          ds.coordinates(), name);
    }).toList();

    getListener().afterNameSyntax(clazz, detectedSyntax, result);

    return result;
  }

  private List<CandidateRule> nominateRules(Class<?> clazz, List<NamedSyntax> namedSyntax) {
    getListener().beforeNominateRules(clazz, namedSyntax);

    List<CandidateRule> result = getRuleNominator().nominateRules(clazz, namedSyntax);

    getListener().afterNominateRules(clazz, namedSyntax, result);

    return result;
  }

  private List<DetectedRule> detectRules(Class<?> clazz, List<NamedSyntax> syntax,
      List<CandidateRule> candidateRules) {
    getListener().beforeDetectRules(clazz, syntax, candidateRules);

    List<DetectedRule> result = candidateRules.stream()
        .flatMap(cri -> getRuleDetector().detectRule(clazz, syntax, cri).stream()).toList();

    getListener().afterDetectRules(clazz, syntax, candidateRules, result);

    return result;
  }

  private List<NamedRule> nameRules(Class<?> clazz, List<NamedSyntax> syntax,
      List<DetectedRule> detectedRules) {
    getListener().beforeNameRules(clazz, syntax, detectedRules);

    List<NamedRule> result = detectedRules.stream().map(dri -> {
      if (!dri.hasConsequent()) {
        return new NamedRule(dri.nominated(), dri.genericType(), dri.annotations(),
            dri.antecedents(), Optional.empty());
      }
      String name = getNamingScheme().name(dri.nominated())
          .orElseThrow(() -> new IllegalStateException("No name for " + dri.nominated()));
      return new NamedRule(dri.nominated(), dri.genericType(), dri.annotations(), dri.antecedents(),
          Optional.of(name));
    }).toList();

    getListener().afterNameRules(clazz, syntax, detectedRules, result);

    return result;
  }

  private NamingScheme getNamingScheme() {
    return namingScheme;
  }

  private SyntaxNominator getSyntaxNominator() {
    return syntaxNominator;
  }

  private SyntaxDetector getSyntaxDetector() {
    return syntaxDetector;
  }

  private RuleNominator getRuleNominator() {
    return ruleNominator;
  }

  private RuleDetector getRuleDetector() {
    return ruleDetector;
  }

  private ConfigurableClassScannerListener getListener() {
    return listener;
  }
}
