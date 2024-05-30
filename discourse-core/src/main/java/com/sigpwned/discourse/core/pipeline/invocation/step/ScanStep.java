package com.sigpwned.discourse.core.pipeline.invocation.step;

import static java.util.stream.Collectors.toSet;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.Discriminator;
import com.sigpwned.discourse.core.command.LeafCommand;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStepBase;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.CommandBody;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.NamingScheme;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleDetection;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleDetector;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleEvaluator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleNominator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RulesEngine;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SubCommandScanner;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxDetection;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxDetector;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxNominator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.DiscriminatorMismatchScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.DuplicateDiscriminatorsScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.DuplicatePropertyNamesScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.DuplicateRuleNomineesScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.DuplicateSyntaxNomineesScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.InvalidDiscriminatorScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.NoDiscriminatorScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.NotConfigurableScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.SubCommandDoesNotExtendSuperCommandScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.SuperCommandNotAbstractScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.UnexpectedDiscriminatorScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.CandidateRule;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.CandidateSyntax;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.DetectedRule;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.DetectedSyntax;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedRule;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedSyntax;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.PreparedClass;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.SuperCommand;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.WalkedClass;
import com.sigpwned.discourse.core.util.Graphs;
import com.sigpwned.discourse.core.util.Maybe;
import com.sigpwned.discourse.core.util.Streams;

public class ScanStep extends InvocationPipelineStepBase {
  private static final Logger LOGGER = LoggerFactory.getLogger(ScanStep.class);

  public static final InvocationContext.Key<SubCommandScanner> SUB_COMMAND_SCANNER_KEY =
      InvocationContext.Key.of(SubCommandScanner.class);

  public static final InvocationContext.Key<NamingScheme> NAMING_SCHEME_KEY =
      InvocationContext.Key.of(NamingScheme.class);

  public static final InvocationContext.Key<SyntaxNominator> SYNTAX_NOMINATOR_KEY =
      InvocationContext.Key.of(SyntaxNominator.class);

  public static final InvocationContext.Key<SyntaxDetector> SYNTAX_DETECTOR_KEY =
      InvocationContext.Key.of(SyntaxDetector.class);

  public static final InvocationContext.Key<RuleNominator> RULE_NOMINATOR_KEY =
      InvocationContext.Key.of(RuleNominator.class);

  public static final InvocationContext.Key<RuleDetector> RULE_DETECTOR_KEY =
      InvocationContext.Key.of(RuleDetector.class);

  public static final InvocationContext.Key<RuleEvaluator> RULE_EVALUATOR_KEY =
      InvocationContext.Key.of(RuleEvaluator.class);

  public final <T> RootCommand<T> scan(Class<T> clazz, InvocationContext context) {
    RootCommand<T> tree;

    try {
      getListener(context).beforeScanStep(clazz, context);
      tree = doScan(clazz, context);
      getListener(context).afterScanStep(clazz, tree, context);
    } catch (Throwable t) {
      getListener(context).catchScanStep(t, context);
      throw t;
    } finally {
      getListener(context).finallyScanStep(context);
    }

    return tree;
  }

  private <T> RootCommand<T> doScan(Class<T> clazz, InvocationContext context) {
    List<WalkedClass<? extends T>> walkedClasses = doWalkStep(clazz, context);

    List<PreparedClass<? extends T>> bodiedClasses = doPrepareStep(walkedClasses, context);

    RootCommand<T> tree = doTreeStep(bodiedClasses, context);

    return tree;
  }

  private <T> List<WalkedClass<? extends T>> doWalkStep(Class<T> clazz, InvocationContext context) {
    SubCommandScanner scanner = context.get(SUB_COMMAND_SCANNER_KEY).orElseThrow();

    List<WalkedClass<? extends T>> walkedClasses;
    try {
      getListener(context).beforeScanStepWalk(clazz, context);
      walkedClasses = walkStep(scanner, clazz, context);
      getListener(context).afterScanStepWalk(clazz, walkedClasses, context);
    } catch (Throwable t) {
      getListener(context).catchScanStepWalk(t, context);
      throw t;
    } finally {
      getListener(context).finallyScanStepWalk(context);
    }
    return walkedClasses;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  protected <T> List<WalkedClass<? extends T>> walkStep(SubCommandScanner scanner, Class<T> clazz,
      InvocationContext context) {
    record WalkingClass<T>(Optional<SuperCommand<? super T>> supercommand, Class<T> clazz) {
    }

    List<WalkedClass<? extends T>> walkedClasses = new ArrayList<>();

    Queue<WalkingClass<? extends T>> queue = new LinkedList<>();
    queue.add(new WalkingClass<>(Optional.empty(), clazz));
    do {
      WalkingClass<? extends T> current = queue.poll();
      SuperCommand<? super T> supercommand = (SuperCommand) current.supercommand().orElse(null);
      Class<? extends T> currentClazz = (Class) current.clazz();

      // It's a hard requirement that all command classes have a @Configurable annotation. This is
      // how we know that a class is a command class, and it's how we know what the discriminator
      // is, plus various other metadata.
      Configurable configurable = currentClazz.getAnnotation(Configurable.class);
      if (configurable == null) {
        throw new NotConfigurableScanException(currentClazz);
      }

      // We need to validate some things about our command structure, depending on whether or not
      // we have a supercommand.
      if (supercommand != null) {
        // This is an important test. We want to ensure that the superclazz is actually a superclass
        // of the clazz to enforce that the result of a subcommand is assignable to the type of the
        // supercommand. This is a useful invariant, but it also guarantees that there are no cycles
        // in the inheritance graph.
        if (!supercommand.clazz().isAssignableFrom(currentClazz)) {
          throw new SubCommandDoesNotExtendSuperCommandScanException(supercommand.clazz(),
              currentClazz);
        }

        // If we have a supercommand, then we need to have a discriminator.
        if (configurable.discriminator().isEmpty()) {
          throw new NoDiscriminatorScanException(currentClazz);
        }

        // If we have a supercommand, then we need to ensure that the discriminator matches the
        // discriminator of the supercommand.
        Discriminator configurableDiscriminator;
        try {
          configurableDiscriminator = Discriminator.of(configurable.discriminator());
        } catch (IllegalArgumentException e) {
          throw new InvalidDiscriminatorScanException(currentClazz, configurable.discriminator());
        }
        if (!configurableDiscriminator.equals(supercommand.discriminator())) {
          throw new DiscriminatorMismatchScanException(currentClazz, supercommand.discriminator(),
              configurableDiscriminator);
        }
      } else {
        // If we don't have a supercommand, then we should not have a discriminator.
        if (!configurable.discriminator().isEmpty()) {
          throw new UnexpectedDiscriminatorScanException(currentClazz);
        }
      }

      List<Map.Entry<String, Class<? extends T>>> subcommands =
          (List) scanner.scanForSubCommands(currentClazz).orElseGet(Collections::emptyList);

      // A Command with SubCommands (i.e., a SuperCommand) has some special requirements. A Command
      // with no SubCommands has some special requirements, too. Let's enforce them both here.
      if (subcommands.isEmpty()) {
        // This is a leaf command.

        // Leaf commands must not be abstract.
        if (Modifier.isAbstract(currentClazz.getModifiers())) {
          // TODO better exception
          throw new IllegalArgumentException("leaf node must not be abstract");
        }
      } else {
        // This is a super command.

        // Super commands must be abstract.
        if (!Modifier.isAbstract(currentClazz.getModifiers())) {
          throw new SuperCommandNotAbstractScanException(currentClazz);
        }
      }

      walkedClasses
          .add(new WalkedClass(Optional.ofNullable(supercommand), currentClazz, configurable));

      for (Map.Entry<String, Class<? extends T>> e : subcommands) {
        // This is a little ticklish. Depending on the subcommand scanner(s) in use, the
        // discriminator might come from the class itself, or it might come from the subcommand. We
        // do our best providing a pointer to the offending discriminator.
        Discriminator expectedDiscriminator;
        try {
          expectedDiscriminator = Discriminator.of(e.getKey());
        } catch (IllegalArgumentException x) {
          throw new InvalidDiscriminatorScanException(currentClazz, e.getKey());
        }

        Class<? extends T> subcommand = e.getValue();

        queue.add(new WalkingClass(
            Optional.of(new SuperCommand<>(currentClazz, expectedDiscriminator)), subcommand));
      }
    } while (!queue.isEmpty());

    // For each class, we need to ensure that there are no duplicate discriminators. This is a
    // requirement for the command hierarchy to be well-defined. It's fine if two different
    // supercommands have subcommands with the same distriminator. It's not fine if one supercommand
    // has two subcommands with the same discriminator.
    for (WalkedClass<? extends T> walkedClass : walkedClasses) {
      // Let's look for all subcommands of this class.
      final Class<?> superclazz = walkedClass.clazz();

      // Here are the subcommands of this class.
      List<WalkedClass<?>> subcommands =
          (List) walkedClasses.stream().filter(wc -> wc.supercommand().isPresent())
              .filter(wc -> wc.supercommand().orElseThrow().clazz() == superclazz).toList();

      // Are there any duplicate discriminators?
      Set<Discriminator> duplicateDiscriminators = Streams
          .duplicates(
              subcommands.stream().map(sc -> sc.supercommand().orElseThrow().discriminator()))
          .collect(toSet());
      if (!duplicateDiscriminators.isEmpty()) {
        throw new DuplicateDiscriminatorsScanException(superclazz, duplicateDiscriminators);
      }
    }

    return walkedClasses;
  }

  private <T> List<PreparedClass<? extends T>> doPrepareStep(
      List<WalkedClass<? extends T>> walkedClasses, InvocationContext context) {
    NamingScheme naming = context.get(NAMING_SCHEME_KEY).orElseThrow();

    SyntaxNominator syntaxNominator = context.get(SYNTAX_NOMINATOR_KEY).orElseThrow();

    SyntaxDetector syntaxDetector = context.get(SYNTAX_DETECTOR_KEY).orElseThrow();

    RuleNominator ruleNominator = context.get(RULE_NOMINATOR_KEY).orElseThrow();

    RuleDetector ruleDetector = context.get(RULE_DETECTOR_KEY).orElseThrow();

    List<PreparedClass<? extends T>> preparedClasses;
    try {
      getListener(context).beforeScanStepPrepare(walkedClasses, context);
      preparedClasses = prepareStep(naming, syntaxNominator, syntaxDetector, ruleNominator,
          ruleDetector, walkedClasses, context);
      getListener(context).afterScanStepPrepare(walkedClasses, preparedClasses, context);
    } catch (Throwable t) {
      getListener(context).catchScanStepPrepare(t, context);
      throw t;
    } finally {
      getListener(context).finallyScanStepPrepare(context);
    }
    return preparedClasses;
  }

  protected <T> List<PreparedClass<? extends T>> prepareStep(NamingScheme naming,
      SyntaxNominator syntaxNominator, SyntaxDetector syntaxDetector, RuleNominator ruleNominator,
      RuleDetector ruleDetector, List<WalkedClass<? extends T>> walkedClasses,
      InvocationContext context) {
    List<PreparedClass<? extends T>> preparedClasses = new ArrayList<>(walkedClasses.size());

    for (WalkedClass<? extends T> walkedClass : walkedClasses) {
      boolean hasSubcommands = walkedClasses.stream().flatMap(wc -> wc.supercommand().stream())
          .anyMatch(sc -> sc.clazz().equals(walkedClass.clazz()));

      CommandBody body;
      if (hasSubcommands) {
        // If a class has subcommands, we don't want to create a body for it. We'll create a body
        // for its subcommands instead.
        body = null;
      } else {
        // If a class doesn't have subcommands, we'll create a body for it.
        Class<?> clazz = walkedClass.clazz();

        ///////////////////////////////////////////////////////////////////////////////////////////
        // SYNTAX /////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////

        // Nominate all our candidate syntax. That is, we want to identify all the various and
        // sundry class members that might be syntax-bearing.
        List<CandidateSyntax> candidateSyntax = syntaxNominator.nominateSyntax(clazz, context);

        // Let's deduplicate our candidates at the record level. We don't want to process
        // duplicates. In theory, it's an error if our syntax nominators nominate the same thing
        // twice, but if we can just remove duplicates and carry on with our lives, then we should.
        if (new HashSet<>(candidateSyntax).size() != candidateSyntax.size()) {
          // Welp, we have duplicates. Remove them, preserving order, and log an error.
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Ignoring exact duplicates for syntax nominees {}",
                Streams.duplicates(candidateSyntax.stream()).map(CandidateSyntax::nominated)
                    .collect(toSet()));
          candidateSyntax = candidateSyntax.stream().distinct().toList();
        }

        // If we still have duplicates at the nominee level after removing duplicates at the record
        // level, then we have a problem.
        Set<Object> duplicateSyntaxNominees = Streams
            .duplicates(candidateSyntax.stream().map(CandidateSyntax::nominated)).collect(toSet());
        if (!duplicateSyntaxNominees.isEmpty()) {
          throw new DuplicateSyntaxNomineesScanException(clazz, duplicateSyntaxNominees);
        }

        // Detect all the syntax. That is, we want to identify all the syntax-bearing class members
        // from among the candidates that are actually syntax. The detector must preserve the
        // nominee from the candidate exactly. If the detector nominates something different, then
        // an exception is thrown.
        List<DetectedSyntax> detectedSyntax = new ArrayList<>();
        for (CandidateSyntax csi : candidateSyntax) {
          Maybe<SyntaxDetection> maybeSyntaxDetection =
              syntaxDetector.detectSyntax(clazz, csi, context);
          if (maybeSyntaxDetection.isYes()) {
            detectedSyntax.add(
                DetectedSyntax.fromCandidateAndDetection(csi, maybeSyntaxDetection.orElseThrow()));
          } else {
            // This is fine. Not every candidate is actually syntax.
          }
        }

        // Now that we have all our syntax, let's name it.
        List<NamedSyntax> syntax = new ArrayList<>();
        for (DetectedSyntax dsi : detectedSyntax) {
          Maybe<String> maybeName = naming.name(dsi.nominated());
          if (maybeName.isYes()) {
            syntax.add(NamedSyntax.fromDetectedSyntax(dsi, maybeName.orElseThrow()));
          } else {
            // This is not OK. Everything has to be named.
            // TODO better exception
            throw new IllegalStateException("No name for " + dsi.nominated());
          }
        }

        // Did we end up with any duplicate names?
        Set<String> duplicateSyntaxNames =
            Streams.duplicates(syntax.stream().map(NamedSyntax::name)).collect(toSet());
        if (!duplicateSyntaxNames.isEmpty()) {
          throw new IllegalArgumentException("Duplicate syntax names: " + duplicateSyntaxNames);
          // TODO better exception
          // throw new DuplicateSyntaxNamesScanException(clazz, duplicateSyntaxNames);
        }

        ///////////////////////////////////////////////////////////////////////////////////////////
        // RULES //////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////

        // Nominate all our candidate rules. That is, we want to identify all the various and
        // sundry class members that might be rules.
        List<CandidateRule> candidateRules = ruleNominator.nominateRules(clazz, syntax, context);

        // Let's deduplicate our candidates at the record level. We don't want to process
        // duplicates. In theory, it's an error if our syntax nominators nominate the same thing
        // twice, but if we can just remove duplicates and carry on with our lives, then we should.
        if (new HashSet<>(candidateRules).size() != candidateRules.size()) {
          // Welp, we have duplicates. Remove them, preserving order, and log an error.
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Ignoring exact duplicates for rule nominees {}",
                Streams.duplicates(candidateRules.stream()).map(CandidateRule::nominated)
                    .collect(toSet()));
          candidateRules = candidateRules.stream().distinct().toList();
        }

        // If we still have duplicates at the nominee level after removing duplicates at the record
        // level, then we have a problem.
        Set<Object> duplicateRuleNominees = Streams
            .duplicates(candidateRules.stream().map(CandidateRule::nominated)).collect(toSet());
        if (!duplicateRuleNominees.isEmpty()) {
          throw new DuplicateRuleNomineesScanException(clazz, duplicateRuleNominees);
        }

        // Detect all the rules. That is, we want to identify all the rule-bearing class members
        // from among the candidates that are actually rules.
        List<DetectedRule> detectedRules = new ArrayList<>();
        for (CandidateRule cri : candidateRules) {
          Maybe<RuleDetection> maybeRuleDetection =
              ruleDetector.detectRule(clazz, syntax, cri, context);
          if (maybeRuleDetection.isYes()) {
            detectedRules
                .add(DetectedRule.fromCandidateAndDetection(cri, maybeRuleDetection.orElseThrow()));
          } else {
            // This is fine. Not every candidate is actually a rule.
          }
        }

        // Now that we have all our rules, let's name them.
        List<NamedRule> rules = new ArrayList<>();
        for (DetectedRule dri : detectedRules) {
          if (dri.hasConsequent()) {
            // We only name consequents. Antecedents come named. So we only need to name the
            // rule if it has a consequent.
            Maybe<String> maybeName = naming.name(dri.nominated());
            if (maybeName.isYes()) {
              rules.add(new NamedRule(dri.nominated(), dri.genericType(), dri.annotations(),
                  dri.antecedents(), Optional.of(maybeName.orElseThrow())));
            } else {
              // This is not OK. Everything has to be named.
              // TODO better exception
              throw new IllegalStateException("No name for " + dri.nominated());
            }
          } else {
            // No consequent, no name.
            rules.add(new NamedRule(dri.nominated(), dri.genericType(), dri.annotations(),
                dri.antecedents(), Optional.empty()));
          }
        }

        // Duplicate rules are fine. We let the reactor sort it out.

        List<LeafCommandProperty> properties = new ArrayList<>();
        for (NamedSyntax namedSyntax : syntax) {
          properties.add(new LeafCommandProperty(namedSyntax.name(), "", namedSyntax.coordinates(),
              namedSyntax.genericType(), namedSyntax.annotations()));
        }


        body = new CommandBody(properties, rules);
      }

      preparedClasses.add(new PreparedClass(walkedClass.supercommand(), walkedClass.clazz(),
          walkedClass.configurable(), Optional.ofNullable(body)));
    }

    // Do we have any duplicate usage of coordinates?
    for (PreparedClass<? extends T> preparedClass : preparedClasses) {
      Set<Coordinate> duplicateCoordinates =
          Streams.duplicates(preparedClass.body().stream().flatMap(b -> b.getProperties().stream())
              .flatMap(p -> p.getCoordinates().stream())).collect(toSet());
      if (!duplicateCoordinates.isEmpty()) {
        throw new IllegalArgumentException("Duplicate coordinates: " + duplicateCoordinates);
        // TODO better exception
        // throw new DuplicateCoordinatesScanException(preparedClass.clazz(), duplicateCoordinates);
      }
    }

    // Do we have any duplicate usage of property names?
    for (PreparedClass<? extends T> preparedClass : preparedClasses) {
      Set<String> duplicatePropertyNames = Streams.duplicates(preparedClass.body().stream()
          .flatMap(b -> b.getProperties().stream()).map(p -> p.getName())).collect(toSet());
      if (!duplicatePropertyNames.isEmpty()) {
        throw new DuplicatePropertyNamesScanException(preparedClass.clazz(),
            duplicatePropertyNames);
      }
    }

    return preparedClasses;
  }

  private <T> RootCommand<T> doTreeStep(List<PreparedClass<? extends T>> preparedClasses,
      InvocationContext context) {
    RuleEvaluator evaluator = context.get(RULE_EVALUATOR_KEY).orElseThrow();

    RulesEngine reactor = new RulesEngine(evaluator);

    RootCommand<T> root;
    try {
      getListener(context).beforeScanStepTree(preparedClasses, context);
      root = treeStep(reactor, preparedClasses, context);
      getListener(context).afterScanStepTree(preparedClasses, root, context);
    } catch (Throwable t) {
      getListener(context).catchScanStepTree(t, context);
      throw t;
    } finally {
      getListener(context).finallyScanStepTree(context);
    }

    return root;
  }

  protected <T> RootCommand<T> treeStep(RulesEngine reactor,
      List<PreparedClass<? extends T>> preparedClasses, InvocationContext context) {
    // We need to visit our classes in dependency order. That is, we want to process a node only
    // after all of its dependencies have been processed. (So, leaves first, root last.) We will
    // need a dependency graph to compute that, so let's build it here.
    Map<Class<?>, Set<Class<?>>> shallowDependencies = new HashMap<>();
    for (PreparedClass<? extends T> bodiedClass : preparedClasses) {
      shallowDependencies.put(bodiedClass.clazz(), new HashSet<>());
    }
    for (PreparedClass<? extends T> bodiedClass : preparedClasses) {
      if (bodiedClass.supercommand().isPresent()) {
        SuperCommand<?> supercommand = bodiedClass.supercommand().get();
        shallowDependencies.get(supercommand.clazz()).add(bodiedClass.clazz());
      }
    }

    // Now compute the topological ordering of our graph. In other words, we want to sort our
    // classes in dependency order. We want to process a node only after all of its dependencies
    // have been processed. So leaves first, root last.
    List<Class<?>> sortedClasses = new ArrayList<>(shallowDependencies.keySet());
    sortedClasses.sort(Graphs.topologicalOrdering(shallowDependencies));


    // Let's gather our discriminators
    Map<Class<?>, Discriminator> discriminators = new HashMap<>();
    for (PreparedClass<? extends T> bodiedClass : preparedClasses) {
      if (bodiedClass.supercommand().isPresent()) {
        discriminators.put(bodiedClass.clazz(), bodiedClass.supercommand().get().discriminator());
      }
    }

    // Now let's create our nodes in the order we just sorted them. This will ensure that we
    // process a node only after all of its dependencies have been processed.
    List<RootCommand<?>> roots = new ArrayList<>();
    Map<Class<?>, Command<?>> subcommands = new HashMap<>();
    for (PreparedClass<? extends T> preparedClass : preparedClasses) {
      Class<?> clazz = preparedClass.clazz();

      Set<Class<?>> deps = shallowDependencies.get(clazz);
      if (deps == null) {
        throw new AssertionError("no dependencies for " + clazz);
      }

      Map<Discriminator, Command<?>> subs = new HashMap<>();
      for (Class<?> dep : deps) {
        Command<?> sub = subcommands.get(dep);
        if (sub == null) {
          throw new AssertionError("no subcommand for " + dep);
        }

        Discriminator discriminator = discriminators.get(dep);
        if (discriminator == null) {
          throw new AssertionError("no discriminator for " + dep);
        }

        subs.put(discriminator, sub);
      }

      // TODO should command be generic?
      Command<?> command;
      if (subs.isEmpty()) {
        CommandBody body = preparedClass.body().orElseThrow(() -> {
          // TODO better exception
          throw new IllegalArgumentException("leaf command has no body");
        });
        // We want the leaf command to be immutable by default. If anyone wants to make it mutable
        // down the line, they can always make a copy.
        List<LeafCommandProperty> immutablePropertiesCopy = List.copyOf(body.getProperties());
        command =
            new LeafCommand<>(preparedClass.configurable().description(), immutablePropertiesCopy,
                toReactor(reactor, body.getRules()), toConstructor(preparedClass.clazz()));
      } else {
        command = new com.sigpwned.discourse.core.command.SuperCommand(
            preparedClass.configurable().description(), subs);
      }

      if (preparedClass.supercommand().isPresent()) {
        // This is a subcommand
        Discriminator discriminator = discriminators.get(clazz);
        if (discriminator == null) {
          throw new AssertionError("no discriminator for " + clazz);
        }

        subcommands.put(clazz, command);
      } else {
        // This is a root command
        roots.add(new RootCommand(preparedClass.configurable().name(),
            preparedClass.configurable().version(), command));
      }
    }

    if (roots.size() != 1) {
      throw new AssertionError("expected exactly one root command, got " + roots);
    }

    return (RootCommand<T>) roots.get(0);
  }

  private Consumer<Map<String, Object>> toReactor(RulesEngine reactor, List<NamedRule> rules) {
    return arguments -> {
      // TODO instance constant
      Map<String, Object> reacted = reactor.run(arguments, rules);
      arguments.clear();
      arguments.putAll(reacted);
    };
  }

  private <T> Function<Map<String, Object>, T> toConstructor(Class<T> clazz) {
    return arguments -> {
      // TODO instance constant
      Object instance = arguments.get("");
      return clazz.cast(instance);
    };
  }
}
