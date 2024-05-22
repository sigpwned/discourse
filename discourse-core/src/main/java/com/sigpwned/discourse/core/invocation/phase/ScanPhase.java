/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 - 2024 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.discourse.core.invocation.phase;

import static java.util.Objects.requireNonNull;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.command.CommandBody;
import com.sigpwned.discourse.core.command.CommandProperty;
import com.sigpwned.discourse.core.command.Discriminator;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.command.SubCommand;
import com.sigpwned.discourse.core.invocation.model.RuleDetection;
import com.sigpwned.discourse.core.invocation.model.SyntaxDetection;
import com.sigpwned.discourse.core.invocation.phase.scan.NamingScheme;
import com.sigpwned.discourse.core.invocation.phase.scan.ScanPhaseListener;
import com.sigpwned.discourse.core.invocation.phase.scan.SubCommandScanner;
import com.sigpwned.discourse.core.invocation.phase.scan.exception.DiscriminatorMismatchConfigurationException;
import com.sigpwned.discourse.core.invocation.phase.scan.exception.DuplicateDiscriminatorScanException;
import com.sigpwned.discourse.core.invocation.phase.scan.exception.DuplicatePropertyNamesScanException;
import com.sigpwned.discourse.core.invocation.phase.scan.exception.InvalidDiscriminatorScanException;
import com.sigpwned.discourse.core.invocation.phase.scan.exception.NoDiscriminatorScanException;
import com.sigpwned.discourse.core.invocation.phase.scan.exception.NotConfigurableScanException;
import com.sigpwned.discourse.core.invocation.phase.scan.exception.SubCommandDoesNotExtendSuperCommandScanException;
import com.sigpwned.discourse.core.invocation.phase.scan.exception.SuperCommandNotAbstractScanException;
import com.sigpwned.discourse.core.invocation.phase.scan.exception.UnexpectedDiscriminatorScanException;
import com.sigpwned.discourse.core.invocation.phase.scan.exception.internal.DuplicateRuleNomineesException;
import com.sigpwned.discourse.core.invocation.phase.scan.exception.internal.DuplicateSyntaxNomineesException;
import com.sigpwned.discourse.core.invocation.phase.scan.model.PreparedClass;
import com.sigpwned.discourse.core.invocation.phase.scan.model.SuperCommand;
import com.sigpwned.discourse.core.invocation.phase.scan.model.WalkedClass;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.CandidateRule;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.DetectedRule;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.NamedRule;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.CandidateSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.DetectedSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.NamedSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleDetector;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleNominator;
import com.sigpwned.discourse.core.invocation.phase.scan.syntax.SyntaxDetector;
import com.sigpwned.discourse.core.invocation.phase.scan.syntax.SyntaxNominator;
import com.sigpwned.discourse.core.module.value.deserializer.ValueDeserializer;
import com.sigpwned.discourse.core.module.value.deserializer.ValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.sink.ValueSink;
import com.sigpwned.discourse.core.module.value.sink.ValueSinkFactory;
import com.sigpwned.discourse.core.util.Graphs;
import com.sigpwned.discourse.core.util.Maybe;
import com.sigpwned.discourse.core.util.Streams;

public class ScanPhase {
  private static final Logger LOGGER = LoggerFactory.getLogger(ScanPhase.class);

  private final ScanPhaseListener listener;

  public ScanPhase(ScanPhaseListener listener) {
    this.listener = requireNonNull(listener);
  }

  public final <T> RootCommand<T> scan(Class<T> clazz, InvocationContext context) {
    List<WalkedClass<? extends T>> walkedClasses = doWalkStep(clazz, context);

    List<PreparedClass<? extends T>> bodiedClasses = doPrepareStep(walkedClasses, context);

    RootCommand<T> gatheredClasses = gatherStep(bodiedClasses, context);

    return gatheredClasses;
  }

  private <T> List<WalkedClass<? extends T>> doWalkStep(Class<T> clazz, InvocationContext context) {
    List<WalkedClass<? extends T>> walkedClasses;
    try {
      getListener().beforeScanPhaseWalkStep(clazz);
      walkedClasses = walkStep(context.getSubCommandScanner(), clazz, context);
      getListener().afterScanPhaseWalkStep(clazz, walkedClasses);
    } catch (Throwable t) {
      getListener().catchScanPhaseWalkStep(t);
      throw t;
    } finally {
      getListener().finallyScanPhaseWalkStep();
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
          throw new DiscriminatorMismatchConfigurationException(currentClazz,
              supercommand.discriminator(), configurableDiscriminator);
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
        throw new DuplicateDiscriminatorScanException(superclazz, duplicateDiscriminators);
      }
    }

    return walkedClasses;
  }

  private <T> List<PreparedClass<? extends T>> doPrepareStep(
      List<WalkedClass<? extends T>> walkedClasses, InvocationContext context) {
    List<PreparedClass<? extends T>> preparedClasses;
    try {
      getListener().beforeScanPhasePrepareStep(walkedClasses);
      preparedClasses = prepareStep(context.getNamingScheme(), context.getSyntaxNominator(),
          context.getSyntaxDetector(), context.getRuleNominator(), context.getRuleDetector(),
          context.getValueSinkFactory(), context.getValueDeserializerFactory(), walkedClasses,
          context);
      getListener().afterScanPhasePrepareStep(walkedClasses, preparedClasses);
    } catch (Throwable t) {
      getListener().catchScanPhasePrepareStep(t);
      throw t;
    } finally {
      getListener().finallyScanPhasePrepareStep();
    }
    return preparedClasses;
  }

  protected <T> List<PreparedClass<? extends T>> prepareStep(NamingScheme naming,
      SyntaxNominator syntaxNominator, SyntaxDetector syntaxDetector, RuleNominator ruleNominator,
      RuleDetector ruleDetector, ValueSinkFactory sinkFactory,
      ValueDeserializerFactory<?> deserializerFactory, List<WalkedClass<? extends T>> walkedClasses,
      InvocationContext context) {
    List<PreparedClass<? extends T>> preparedClasses = new ArrayList<>(walkedClasses.size());

    for (WalkedClass<? extends T> walkedClass : walkedClasses) {
      boolean hasSubcommands = walkedClasses.stream().flatMap(wc -> wc.supercommand().stream())
          .anyMatch(sc -> sc.clazz().equals(walkedClass.clazz()));

      CommandBody<? extends T> body;
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
          throw new DuplicateSyntaxNomineesException(clazz, duplicateSyntaxNominees);
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
          throw new DuplicateRuleNomineesException(clazz, duplicateRuleNominees);
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

        List<CommandProperty> properties = new ArrayList<>();
        for (NamedSyntax namedSyntax : syntax) {
          final ValueSink sink = sinkFactory
              .getSink(namedSyntax.genericType(), namedSyntax.annotations()).orElseThrow(() -> {
                // TODO better exception
                return new IllegalArgumentException("no sink for " + namedSyntax.genericType());
              });
          final ValueDeserializer<?> deserializer = deserializerFactory
              .getDeserializer(sink.getGenericType(), namedSyntax.annotations()).orElseThrow(() -> {
                // TODO better exception
                return new IllegalArgumentException("no deserializer for " + sink.getGenericType());
              });
          // TODO help and version
          properties.add(new CommandProperty(namedSyntax.name(), "", false, false,
              namedSyntax.coordinates(), sink, deserializer));
        }


        body = new CommandBody<>(properties, rules);
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

    // Do any of our classes have multiple help flags?
    for (PreparedClass<? extends T> preparedClass : preparedClasses) {
      if (preparedClass.body().isPresent()) {
        Set<String> helpFlags = preparedClasses.stream().flatMap(pc -> pc.body().stream())
            .flatMap(b -> b.getProperties().stream()).filter(CommandProperty::isHelp)
            .map(CommandProperty::getName).collect(toSet());
        if (helpFlags.size() > 1) {
          // throw new MultipleHelpFlagsScanException(preparedClass.clazz(), helpFlags);
        }
      }
    }

    // Do any of our classes have multiple version flags?
    for (PreparedClass<? extends T> preparedClass : preparedClasses) {
      if (preparedClass.body().isPresent()) {
        Set<String> versionFlags = preparedClasses.stream().flatMap(pc -> pc.body().stream())
            .flatMap(b -> b.getProperties().stream()).filter(CommandProperty::isHelp)
            .map(CommandProperty::getName).collect(toSet());
        if (versionFlags.size() > 1) {
          // throw new MultipleVersionFlagsScanException(preparedClass.clazz(), versionFlags);
        }
      }
    }

    return preparedClasses;
  }

  private <T> RootCommand<T> doGatherStep(List<PreparedClass<? extends T>> bodiedClasses,
      InvocationContext context) {
    RootCommand<T> root;
    try {
      getListener().beforeScanPhaseGatherStep(bodiedClasses);
      root = gatherStep(bodiedClasses, context);
      getListener().afterScanPhaseGatherStep(bodiedClasses, root);
    } catch (Throwable t) {
      getListener().catchScanPhaseGatherStep(t);
      throw t;
    } finally {
      getListener().finallyScanPhaseGatherStep();
    }
    return root;
  }

  protected <T> RootCommand<T> gatherStep(List<PreparedClass<? extends T>> bodiedClasses,
      InvocationContext context) {
    // We need to visit our classes in dependency order. That is, we want to process a node only
    // after all of its dependencies have been processed. (So, leaves first, root last.) We will
    // need a dependency graph to compute that, so let's build it here.
    Map<Class<?>, Set<Class<?>>> shallowDependencies = new HashMap<>();
    for (PreparedClass<? extends T> bodiedClass : bodiedClasses) {
      shallowDependencies.put(bodiedClass.clazz(), new HashSet<>());
    }
    for (PreparedClass<? extends T> bodiedClass : bodiedClasses) {
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
    for (PreparedClass<? extends T> bodiedClass : bodiedClasses) {
      if (bodiedClass.supercommand().isPresent()) {
        discriminators.put(bodiedClass.clazz(), bodiedClass.supercommand().get().discriminator());
      }
    }

    // Now let's create our nodes in the order we just sorted them. This will ensure that we
    // process a node only after all of its dependencies have been processed.
    List<RootCommand<?>> roots = new ArrayList<>();
    Map<Class<?>, SubCommand<?>> subcommands = new HashMap<>();
    for (PreparedClass<? extends T> bodiedClass : bodiedClasses) {
      Class<?> clazz = bodiedClass.clazz();

      Set<Class<?>> deps = shallowDependencies.get(clazz);
      if (deps == null) {
        throw new AssertionError("no dependencies for " + clazz);
      }

      Map<Discriminator, SubCommand<?>> subs = new HashMap<>();
      for (Class<?> dep : deps) {
        SubCommand<?> sub = subcommands.get(dep);
        if (sub == null) {
          throw new AssertionError("no subcommand for " + dep);
        }

        Discriminator discriminator = discriminators.get(dep);
        if (discriminator == null) {
          throw new AssertionError("no discriminator for " + dep);
        }

        subs.put(discriminator, sub);
      }


      if (bodiedClass.supercommand().isPresent()) {
        // This is a subcommand
        Discriminator discriminator = discriminators.get(clazz);
        if (discriminator == null) {
          throw new AssertionError("no discriminator for " + clazz);
        }

        subcommands.put(clazz, new SubCommand(clazz, discriminator,
            bodiedClass.configurable().description(), bodiedClass.body().orElse(null), subs));
      } else {
        // This is a root command
        roots.add(new RootCommand(clazz, bodiedClass.configurable().name(),
            bodiedClass.configurable().version(), bodiedClass.configurable().description(),
            bodiedClass.body().orElse(null), subs));
      }
    }

    if (roots.size() != 1) {
      throw new AssertionError("expected exactly one root command, got " + roots);
    }

    return (RootCommand<T>) roots.get(0);
  }

  /**
   * @return the listener
   */
  private ScanPhaseListener getListener() {
    return listener;
  }
}
