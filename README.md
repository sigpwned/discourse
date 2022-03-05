# DISCOURSE

Civilized arguments for modern Java.

## Goals

* To provide an easy-to-use library for the most common CLI program configuration idioms

## Non-Goals

* To provide a library that supports all CLI idioms

## Anatomy of a Discourse Command Line

Discourse defines three kinds of command line parameters:

* `FlagParameter` -- A named `boolean`-valued parameter that is assigned `true` if it is present, or `false` otherwise
* `OptionParameter` -- An optional named parameter that can take any value
* `PositionalParameter` -- A parameter at a fixed position on the command line

All named parameters can have up to two forms: a short form, which is a single alphanumeric character; or a long form, which is a string of one or more alphanumeric, dash, or underscore characters. In a command line, a short name is preceded by a single dash (`-`), whereas a long name is preceded by a double dash (`--`).

Option and Positional parameters can be optional or required. Flag parameters are optional, by definition.

### Simple Example

This example shows off the syntax for all parameter types:

    -a -b charlie --delta --echo=1234 --foxtrot 5678 golf hotel
    
It defines the following parameters:

* `-a` -- A flag parameter in short form
* `-b` -- An option parameter in short form, with value `charlie`
* `--delta` -- A flag in long form
* `--echo` -- An option parameter in long form, with value `1234`
* `--foxtrot` -- An option parameter in long form, with value `5678`
* `golf` -- A positional parameter in position 0
* `hotel` -- A positional parameter in position 1

Note that Positional parameters always follow all Flag and Option parameters.

Also note that Option parameters in long form can be given values using a connecting equals sign (`--echo=1234`) or by including the value as the following token (`--foxtrot 5678`).

### Bundled Example

Flag and Option short forms can also be "bundled":

    -abc delta --echo
    
It defines the following parameters:

* `-a` -- A flag parameter in switch form
* `-b` -- A flag parameter in switch form
* `-c` -- An option parameter in switch form, with value `delta`
* `--echo` -- A flag parameter in option form

## Other Supported Configuration Parameters

Discourse also supports two additional parameter types for users who wish to offload all configuration definition and checking to the library:

* `EnvironmentParameter` -- A named environment variable pulled from the environment
* `PropertyParameter` -- A named Java property pulled from Java properties

Environment and Property parameters can both be optional or required.

## Unsupported Command Line Styles

Discourse not support the following CLI idioms:

* Short-form options with a connected value (e.g., `gcc -O2 hello.c`)
* Long-form options with a single dash (e.g., `ant -projecthelp`)
