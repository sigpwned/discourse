# DISCOURSE [![tests](https://github.com/sigpwned/discourse/actions/workflows/tests.yml/badge.svg)](https://github.com/sigpwned/discourse/actions/workflows/tests.yml) [![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=sigpwned_discourse&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=sigpwned_discourse) [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=sigpwned_discourse&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=sigpwned_discourse) [![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=sigpwned_discourse&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=sigpwned_discourse) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.sigpwned/discourse-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.sigpwned/discourse-core)

Civilized command line arguments for modern Java.

## Motivation

The command line is a simple, elegant interface for running even very complex programs. In this
modern era of cloud and containers, the command line is especially important for configuring backend
programs. There are many good libraries for handling command line arguments in Java, but they
generally suffer from an outmoded approach (
e.g., [Apache Commons CLI](https://commons.apache.org/proper/commons-cli/)), too much complexity (
e.g., [picocli](https://picocli.info/)), or make demands on application structure. Discourse is
a [new library](https://xkcd.com/927/) for Java 8 that provides a simple, easy-to-use, modern
approach to handling the most important command line idioms.

## Goals

* To provide an easy-to-use library for the most common CLI program configuration idioms
* To make no demands on the architecture of the host application

## Non-Goals

* To provide a library that supports all CLI idioms

## Quick Start

Add Discourse to your application:

```xml

<dependency>
  <groupId>com.sigpwned</groupId>
  <artifactId>discourse-core</artifactId>
  <version>0.1.0</version>
</dependency>
```

Try out the following class:

```java
import com.sigpwned.discourse.Discourse;
import com.sigpwned.discourse.annotations.Configurable;
import com.sigpwned.discourse.annotations.OptionalParameter;
import com.sigpwned.discourse.annotations.PositionalParameter;

@Configurable(name = "hello", description = "A simple program that greets people")
public class HelloWorld {

  public static void main(String[] args) {
    Discourse.configuration(HelloWorld.class, args).run();
  }

  @OptionalParameter(description = "The greeting to use when addressing people", shortName = "g", longName = "greeting")
  public String greeting = "Hello";

  @PositionalParameter(position = 0, description = "The name of the person to greet", required = true)
  public String name;

  public void run() {
    System.out.println("%s, %s!".formatted(greeting, name));
  }
}
```

And run the following command:

```shell
java -jar hello.jar -g Hi Alice
```

You should see the following output:

```
Hi, Alice!
```

Congratulations! You've just written your first Discourse program!

## Diving Deeper

As you can see, basic Discourse usage is pretty straightforward:

* Create a "command" class and annotate it with `@Configurable`
* Add parameters to the class using `@FlagParameter`, `@OptionParameter`,
  and `@PositionalParameter`. There are many ways to do this, but public non-final fields are the
  easiest.
* Use the discourse framework to parse the command line arguments and build the command object, as
  in `Discourse.configuration(HelloWorld.class, args)` above
* Given the newly-created command object representing the command line arguments, perform your
  application's business logic. You can do this by adding a `run()` method to the command class and
  calling it directly, as above; treating the command object as a parameter to an alternative main
  method; or many, may other ways. It's just a Java object. It's up to you!

Discourse supports a wide variety of Java types out of the box:

* Primitive types and their boxed equivalents
* Enum types
* `String`
* `File`
* `Path`
* `URL`
* `URI`
* `InetAddress`
* `Pattern`
* `Charset`
* `Locale`
* `TimeZone`
* `Currency`
* `UUID`

## Features

Discourse supports a wide variety of command line idioms, including:

* Flag parameters
* Option parameters
* Positional parameters
* Environment variables
* System properties
* Collections
* Custom deserializers
* Custom sinks
* Subcommands
* Help and version tags
* Validation methods
* And more!

## Framework Structure

Discourse uses a 4-step process called the "invocation pipeline" to interpret command line arguments
and build a command object. The pipeline looks like this:

         ┌────────────────────────────────────────────────────────────┐
         │                       Invocation Pipeline                  │
         ├───────────┬─────────────────┬───────────────┬──────────────┤
         │  Scan ────┼───> Resolve ────┼───> Parse ────┼───> Eval     │
         └───────────┴─────────────────┴───────────────┴──────────────┘

The `InvocationPipeline` object is a user-facing object that encapsulates the entire pipeline. For
reference, the `Discourse` util class is simply a wrapper around the invocation pipeline. Pipeline
instances are created using the `InvocationPipelineBuilder` object, which has many built-in hooks
designed to allow the user to extend the framework.

### The Invocation Pipeline

The invocation pipeline steps perform the following functions:

1. `scan` -- Scan the given command class to extract instructions for how to interpret command line
   arguments and how to build the command object.
2. `resolve` -- Determine which specific command among all available commands the user is trying to
   invoke
3. `parse` -- Parse the command line arguments into a map of keys and values
4. `eval` -- Evaluate the rules embedded in the command class to instantiate and initialize the
   command class instance to return to the user



## Structure

Discourse allows users to define `Command` objects. Logically, a `Command` is a description of work
the user wants the application to perform. In the common case, a `Command` is built from an
annotated Java class, where the instance fields model the desired command line arguments, and the
annotations on those fields define the command line syntax. For example:

```java
import com.sigpwned.discourse.Discourse;
import com.sigpwned.discourse.annotations.Configurable;
import com.sigpwned.discourse.annotations.OptionalParameter;
import com.sigpwned.discourse.annotations.PositionalParameter;

@Configurable(name = "hello", description = "A simple program that greets people")
public class HelloWorld {

  public static void main(String[] args) {
    Discourse.configuration(HelloWorld.class, args).run();
  }

  @OptionParameter(description = "The greeting to use when addressing people", shortName = "g", longName = "greeting")
  public String greeting = "Hello";

  @PositionalParameter(position = 0, description = "The name of the person to greet", required = true)
  public String name;

  public void run() {
    System.out.println("%s, %s!".formatted(greeting, name));
  }
}
```

In this example, the `HelloWorld` class is a `Command` object. The `@Configurable` annotation
indicates that the class is a command, and provides metadata about the command, such as its name and
description. The `greeting` and `name` fields model the input to collect from the user.
The `@OptionParameter` and `@PositionalParameter` annotations on those fields define the syntax for
providing the values of those fields (e.g., `-g Hi Alice`).

In this case, the `HelloWorld` class has a `run()` method that implements the business logic of
the command, but that's simply a design choice. The class could just as easily be plain ol' data,
which the application then uses however it sees fit to do its work.

The `Command` object contains application metadata (i.e., name, description, version) and the
properties for the user to configure. Each property has a name, a description, a type, and a set of
coordinates (e.g., a short switch like `-x`, a long switch like `--xray`, a position `0`, etc.) that
define the syntax for the property on the command line.

The `Command` object is created by

* ArgParser + ParametersDefinition -> ParsedArguments (Map<Coordinate, List<String>>)
* ParsedArguments + Deserializer -> Configuration (Map<Coordinate, List<Object>>)
* Configuration + Sink -> Instance (Object)

ArgsParameter = Name + Coordinates
ConfigurationProperty = Name + Type + Coordinates

### Architecture

The fundamental abstraction provided by Discourse is the `ConfigurationProperty` object.
A `ConfigurationProperty` is a description of a single logical command line argument. It has a name,
a type, and a set of coordinates that define the syntax for the argument on the command line. Given
a list of `ConfigurationProperty` objects and a list of command line arguments, Discourse can parse
the arguments into a `Configuration` object, which is simply a collection of values collected from
the command line.

At the core of Discourse is a moderately sophisticated system for creating objects.

    +-------------------------------+
    +        Instance               +
    +-------------------------------+
    +        Command                +
    +-------------------------------+
    +           

### FizzBuzz

Discourse allows users to create "configurable" [JavaBeans](https://en.wikipedia.org/wiki/JavaBeans)
-like classes and parse them from command line arguments. The configurable objects
are [POJOs](https://en.wikipedia.org/wiki/Plain_old_Java_object) with public fields, or private
fields with accessors, that are annotated to indicate their role in a command line. There is nothing
special about these objects except for the annotations.

For example, a simple command line for [FizzBuzz](https://en.wikipedia.org/wiki/Fizz_buzz) might
look like this:

    @Configurable
    public class FizzBuzzConfiguration {
        @PositionalParameter(position=0, required=true)
        public int count;
    }

The program could then parse the command line arguments into this configuration object like this:

    public class FizzBuzz {
        public static void main(String[] args) {
            FizzBuzzConfiguration configuration=Discourse.configuration(FizzBuzzConfiguration.class, args);
            if(count < 1)
                throw new IllegalArgumentException("count must be at least 1);
            for(int i=1;i<=configuration.count;i++) {
                boolean mod3=(i % 3) == 0;
                boolean mod5=(i % 5) == 0;
                if(mod3 && mod5)
                    System.out.println("fizz buzz");
                else if(mod3)
                    System.out.println("fizz");
                else if(mod5)
                    System.out.println("buzz");
                else
                    System.out.println(i);
            }
        }
    }

Notice that Discourse focuses entirely on parsing command line arguments into a configuration
object. It makes no other demands on program structure.

Example command lines:

* `java -jar fizzbuzz.jar 10`

### FizzBuzz with Options

Discourse also allows users include switches (e.g., `-e`, or `--example`) in their configurations.
Here, we let the user choose a different string to use than "fizz" or "buzz" using options:

    @Configurable
    public class FizzBuzzConfiguration {
        @PositionalParameter(position=0, required=true)
        public int count;
        
        @OptionParameter(shortName="f", longName="fizz")
        public String fizz = "fizz";
        
        @OptionParameter(shortName="b", longName="buzz")
        public String buzz = "buzz";
    }
    
    public class FizzBuzz {
        public static void main(String[] args) {
            FizzBuzzConfiguration configuration=Discourse.configuration(FizzBuzzConfiguration.class, args);
            if(count < 1)
                throw new IllegalArgumentException("count must be at least 1);
            for(int i=1;i<=configuration.count;i++) {
                boolean mod3=(i % 3) == 0;
                boolean mod5=(i % 5) == 0;
                if(mod3 && mod5)
                    System.out.println(configuration.fizz+" "+configuration.buzz);
                else if(mod3)
                    System.out.println(configuration.fizz);
                else if(mod5)
                    System.out.println(configuration.buzz);
                else
                    System.out.println(i);
            }
        }
    }

By default, option parameters are not required. Note that we initialize our option parameter
variables to hold the default values of "fizz" and "buzz" so the program continues to behave like
normal if the options are not given.

Example command lines:

* `java -jar fizzbuzz.jar -f foo -b bar 10`
* `java -jar fizzbuzz.jar --fizz foo --buzz bar 10`

### FizzBuzz with Validation Method

Because configuration objects are just POJOs, users can implement a variety of patterns. For
example, users can move validation into the configuration object:

    @Configurable
    public class FizzBuzzConfiguration {
        @PositionalParameter(position=0, required=true)
        public int count;
        
        @OptionParameter(shortName="f", longName="fizz")
        public String fizz = "fizz";
        
        @OptionParameter(shortName="b", longName="buzz")
        public String buzz = "buzz";
    
        public FizzBuzzConfiguration validate() {
            if(count < 1)
                throw new IllegalArgumentException("count must be at least 1");
            return this;
        }
    }
    
    public class FizzBuzz {
        public static void main(String[] args) {
            FizzBuzzConfiguration configuration=Discourse.configuration(FizzBuzzConfiguration.class, args)
                .validate();
            for(int i=1;i<=configuration.count;i++) {
                boolean mod3=(i % 3) == 0;
                boolean mod5=(i % 5) == 0;
                if(mod3 && mod5)
                    System.out.println(configuration.fizz+" "+configuration.buzz);
                else if(mod3)
                    System.out.println(configuration.fizz);
                else if(mod5)
                    System.out.println(configuration.buzz);
                else
                    System.out.println(i);
            }
        }
    }

Example command lines:

* `java -jar fizzbuzz.jar -f foo -b bar 10`
* `java -jar fizzbuzz.jar --fizz foo --buzz bar 10`

### FizzBuzz with Configuration as Application

Users can also implement the program inside the configuration object if they like:

    @Configurable
    public class FizzBuzz {
        @PositionalParameter(position=0, required=true)
        public int count;
    
        @OptionParameter(shortName="f", longName="fizz")
        public String fizz = "fizz";
    
        @OptionParameter(shortName="b", longName="buzz")
        public String buzz = "buzz";
    
        public FizzBuzz validate() {
            if(count < 1)
                throw new IllegalArgumentException("count must be at least 1");
            return this;
        }
    
        public static void main(String[] args) {
            Discourse.configuration(FizzBuzz.class, args)
                .validate()
                .run();
        }
    
        public void run() {
            for(int i=1;i<=count;i++) {
                boolean mod3=(i % 3) == 0;
                boolean mod5=(i % 5) == 0;
                if(mod3 && mod5)
                    System.out.println(fizz+" "+buzz);
                else if(mod3)
                    System.out.println(fizz);
                else if(mod5)
                    System.out.println(buzz);
                else
                    System.out.println(i);
            }
        }
    }

Example command lines:

* `java -jar fizzbuzz.jar -f foo -b bar 10`
* `java -jar fizzbuzz.jar --fizz foo --buzz bar 10`

### FizzBuzz with Help and Version tags

By default, Discourse will print a help message if the program is invoked without any command line
arguments. However, users can easily add "standard" `--help` and `--version` flags simply by
extending `StandardConfigurationBase`. Users can also add metadata to improve the quality of the
help message.

    @Configurable(name="fizzbuzz", description="An implementation of the classic programming interview question")
    public class FizzBuzzConfiguration extends StandardConfigurationBase {
        @PositionalParameter(position=0, description="The number to count to", required=true)
        public int count;
        
        @OptionParameter(shortName="f", longName="fizz", description="The string to print for multiples of 3, fizz by default")
        public String fizz = "fizz";
        
        @OptionParameter(shortName="b", longName="buzz", description="The string to print for multiples of 5, buzz by default")
        public String buzz = "buzz";
    
        public FizzBuzzConfiguration validate() {
            if(count < 1)
                throw new IllegalArgumentException("count must be at least 1");
            return this;
        }
    }
    
    public class FizzBuzz {
        public static void main(String[] args) {
            FizzBuzzConfiguration configuration=Discourse.configuration(FizzBuzzConfiguration.class, args)
                .validate();
            for(int i=1;i<=configuration.count;i++) {
                boolean mod3=(i % 3) == 0;
                boolean mod5=(i % 5) == 0;
                if(mod3 && mod5)
                    System.out.println(fizz+" "+buzz);
                else if(mod3)
                    System.out.println(fizz);
                else if(mod5)
                    System.out.println(buzz);
                else
                    System.out.println(i);
            }
        }
    }

This configuration would produce the following help message:

    Usage: fizzbuzz [ flags ] <count>
    
    An implementation of the classic programming interview question
    
    Flags:
    --help                              Print this help message
    --version                           The current version of this software
    
    Options:
    -b, --buzz <string>                 The string to print for multiples of 5, buzz by default
    -f, --fizz <string>                 The string to print for multiples of 3, fizz by default

Example command lines:

* `java -jar fizzbuzz.jar -f foo -b bar 10`
* `java -jar fizzbuzz.jar -f foo -b bar 10`
* `java -jar fizzbuzz.jar --help`
* `java -jar fizzbuzz.jar --version`

## Anatomy of a Discourse Command Line

Discourse defines three kinds of command line parameters:

* `FlagParameter` -- A switched `boolean`-valued parameter that is assigned `true` if its switch is
  present, or `false` otherwise
* `OptionParameter` -- An switched parameter that can take any type of value
* `PositionalParameter` -- A positional parameter that can take any type of value

Switched parameters are so called because they are given on the command line using a switch, or an
option starting with a dash. Switches can be short form (`-x`) or long form (`--example`).

Positional parameters are so called because they are given at a specific position on the command
line. Positional parameters always follow flag and option parameters. If positional parameters start
with a dash, then the special separator `--` can be used to indicate that all arguments after the
separator should be interpreted as positional parameters instead of switched parameters.

Option and Positional parameters can be optional or required. Flag parameters are optional, by
definition.

### Simple Syntax Example

This example shows off the syntax for all parameter types:

    -a -b charlie --delta --echo=1234 --foxtrot 5678 golf hotel

It defines the following parameters:

* `-a` -- A flag parameter in short form
* `-b` -- An option parameter in short form, with value `charlie`
* `--delta` -- A flag in long form
* `--echo` -- An option parameter in long form, with value `1234`, using connected syntax
* `--foxtrot` -- An option parameter in long form, with value `5678`, using regular syntax
* `golf` -- A positional parameter in position 0
* `hotel` -- A positional parameter in position 1

Note that Positional parameters always follow all Flag and Option parameters.

Also note that Option parameters in long form can be given values using a connecting equals
sign (`--echo=1234`) or by including the value as the following token (`--foxtrot 5678`).

### Bundled Syntax Example

Flag and Option short forms can also be "bundled":

    -abc delta --echo

It defines the following parameters:

* `-a` -- A flag parameter in switch form
* `-b` -- A flag parameter in switch form
* `-c` -- An option parameter in switch form, with value `delta`
* `--echo` -- A flag parameter in option form

## Advanced Usage

### Environment Variables and System Properties

Discourse also supports two additional parameter types for users who wish to offload all
configuration tasks to the library:

* `EnvironmentParameter` -- A
  named [environment variable](https://docs.oracle.com/javase/8/docs/api/java/lang/System.html#getenv-java.lang.String-)
  pulled from the environment
* `PropertyParameter` -- A
  named [system property](https://docs.oracle.com/javase/8/docs/api/java/lang/System.html#getProperty-java.lang.String-)
  pulled from Java system properties

Environment and Property parameters can both be optional or required.

### Collections

Discourse allows users to capture multiple values for some parameter types. In the default
configuration, any option or positional parameter with a type
of `List<T>`, `Set<T>`, `SortedSet<T>`, or `T[]` will automatically capture multiple values of
type `T`. For example, this configuration captures multiple values of type `String` for option `-o`:

    @Configurable
    public String CollectionsExample {
        @OptionParameter(shortName="o")
        public List<String> options;
    }

For positional parameters, only the last position is allowed to be a collection type. Otherwise, the
configuration will generate a `InvalidCollectionParameterPlacementConfigurationException`.

Users can specify their own collection types by registering a new `ValueSinkFactory` in
the `CommandBuilder` instance using a `Module`.

### Custom Types

Discourse allows users to deserialize values of any type from command arguments. The built-in
deserializers support all primitive, boxed, and enum types, as well as Java 8 date
types, `File`, `Path`, and any class with a `fromString` deserialization method.

Users can deserialize custom types by registering a new `ValueDeserializerFactory` in
the `CommandBuilder` instance using a `Module`, or by implementing a `fromString` deserialization
method in the custom type.

### Subcommands

Discourse allows users to structure the CLI interface with subcommands. For example:

    @Configurable(
        subcommands = {
            @Subcommand(discriminator = "foo", configurable = FooMultiExample.class),
            @Subcommand(discriminator = "bar", configurable = BarMultiExample.class)})
    public abstract static class MultiExample {
        @OptionParameter(shortName = "o", longName = "option")
        public String option;
    }
    
    @Configurable(discriminator = "foo")
    public static class FooMultiExample extends MultiExample {
        @OptionParameter(shortName = "a", longName = "alpha")
        public String alpha;
        
        @PositionalParameter(position = 0, required = true)
        public int position0;
    }
    
    @Configurable(discriminator = "bar")
    public static class BarMultiExample extends MultiExample {
        @OptionParameter(shortName = "b", longName = "bravo")
        public String bravo;
    }

This example configuration would accept the following valid commands:

* `foo -o value -a value 10` -- Returns an instance of `FooMultiExample`
* `bar -o value -b value` -- Returns an instance of `BarMultiExample`

Note that the first value on either command line is the "discriminator" value that selects the
appropriate "subcommand" object. All subsequent values are used to populate the selected subcommand
object.

The subcommand types (here, `FooMultiExample` and `BarMultiExample`) must extend the "root"
configuration type (here, `MultiExample`). The parameters of each subcommand object are the union of
all the parameters in the subcommand object and in the root configuration object.

## Unsupported Command Line Styles

Discourse does not support the following CLI idioms:

* Short-form options with a connected value (e.g., `gcc -O2 hello.c`)
* Long-form options with a single dash (e.g., `ant -projecthelp`)
