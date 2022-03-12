# DISCOURSE

Civilized arguments for modern Java.

## Goals

* To provide an easy-to-use library for the most common CLI program configuration idioms
* To make no demands on the architecture of the host application

## Non-Goals

* To provide a library that supports all CLI idioms

## Getting Started

Using Discourse is easy! Here are some examples to get started.

### FizzBuzz

Discourse allows users to create "configurable" [JavaBeans](https://en.wikipedia.org/wiki/JavaBeans)-like classes and parse them from command line arguments. The configurable objects are [POJOs](https://en.wikipedia.org/wiki/Plain_old_Java_object) with public fields, or private fields with accessors, that are annotated to indicate their role in a command line. There is nothing special about these objects except for the annotations.

For example, a simple command line for [FizzBuzz](https://en.wikipedia.org/wiki/Fizz_buzz) might look like this:

    @Configurable
    public class FizzBuzzConfiguration {
        @PositionalParameter(position=0, required=true)
        public int count;
        
        @OptionParameter(shortName="f", longName="fizz")
        public String fizz = "fizz";
        
        @OptionParameter(shortName="b", longName="buzz")
        public String buzz = "buzz";
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
    
Notice that Discourse focuses entirely on parsing command line arguments into a configuration object. It makes no other demands on program structure.

Example command lines:
* `java -jar fizzbuzz.jar 10`

### FizzBuzz with Options

Discourse also allows users include switches (e.g., `-e`, or `--example`) in their configurations. Here, we let the user choose a different string to use than "fizz" or "buzz" using options:

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
    
By default, option parameters are not required. Note that we initialize our option parameter variables to hold the default values of "fizz" and "buzz" so the program continues to behave like normal if the options are not given.

Example command lines:

* `java -jar fizzbuzz.jar -f foo -b bar 10`
* `java -jar fizzbuzz.jar --fizz foo --buzz bar 10`

### FizzBuzz with Validation Method

Because configuration objects are just POJOs, users can implement a variety of patterns. For example, users can move validation into the configuration object:

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

By default, Discourse will print a help message if the program is invoked without any command line arguments. However, users can easily add "standard" `--help` and `--version` flags simply by extending `StandardConfigurationBase`. Users can also add metadata to improve the quality of the help message.

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

* `FlagParameter` -- A switched `boolean`-valued parameter that is assigned `true` if its switch is present, or `false` otherwise
* `OptionParameter` -- An switched parameter that can take any type of value
* `PositionalParameter` -- A positional parameter that can take any type of value

Switched parameters are so called because they are given on the command line using a switch, or an option starting with a dash. Switches can be short form (`-x`) or long form (`--example`).

Positional parameters are so called because they are given at a specific position on the command line. Positional parameters always follow flag and option parameters. If positional parameters start with a dash, then the special separator `--` can be used to indicate that all arguments after the separator should be interpreted as positional parameters instead of switched parameters.

Option and Positional parameters can be optional or required. Flag parameters are optional, by definition.

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

Also note that Option parameters in long form can be given values using a connecting equals sign (`--echo=1234`) or by including the value as the following token (`--foxtrot 5678`).

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

Discourse also supports two additional parameter types for users who wish to offload all configuration tasks to the library:

* `EnvironmentParameter` -- A named [environment variable](https://docs.oracle.com/javase/8/docs/api/java/lang/System.html#getenv-java.lang.String-) pulled from the environment
* `PropertyParameter` -- A named [system property](https://docs.oracle.com/javase/8/docs/api/java/lang/System.html#getProperty-java.lang.String-) pulled from Java system properties

Environment and Property parameters can both be optional or required.

### Collections

Discourse allows users to capture multiple values for some parameter types. In the default configuration, any option or positional parameter with a type of `List<T>`, `Set<T>`, `SortedSet<T>`, or `T[]` will automatically capture multiple values of type `T`. For example, this configuration captures multiple values of type `String` for option `-o`:

    @Configurable
    public String CollectionsExample {
        @OptionParameter(shortName="o")
        public List<String> options;
    }
    
For positional parameters, only the last position is allowed to be a collection type. Otherwise, the configuration will generate a `InvalidCollectionParameterPlacementConfigurationException`.

Users can specify their own collection types by registering a new `ValueSinkFactory` in the `CommandBuilder` instance using a `Module`.

### Custom Types

Discourse allows users to deserialize values of any type from command arguments. The built-in deserializers support all primitive, boxed, and enum types, as well as Java 8 date types, `File`, `Path`, and any class with a `fromString` deserialization method.

Users can deserialize custom types by registering a new `ValueDeserializerFactory` in the `CommandBuilder` instance using a `Module`, or by implementing a `fromString` deserialization method in the custom type.

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
    
    @Configurable(discriminator = "bravo")
    public static class BarMultiExample extends MultiExample {
        @OptionParameter(shortName = "b", longName = "bravo")
        public String bravo;
    }

This example configuration would accept the following valid commands:

* `foo -o value -a value 10` -- Returns an instance of `FooMultiExample`
* `bar -o value -b value` -- Returns an instance of `BarMultiExample`

Note that the first value on either command line is the "discriminator" value that selects the appropriate "subcommand" object. All subsequent values are used to populate the selected subcommand object.

The subcommand types (here, `FooMultiExample` and `BarMultiExample`) must extend the "root" configuration type (here, `MultiExample`). The parameters of each subcommand object are the union of all the parameters in the subcommand object and in the root configuration object.

## Unsupported Command Line Styles

Discourse does not support the following CLI idioms:

* Short-form options with a connected value (e.g., `gcc -O2 hello.c`)
* Long-form options with a single dash (e.g., `ant -projecthelp`)
