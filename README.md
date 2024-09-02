# SearchStateExplorer

A search framework written in Java. It implements the following algorithms:

- Depth-First Search,
- Breadth-First Search,
- Min-Cost,
- Best First Greedy,
- A*.

## Requirements

### 1. Java >= 8

Tested with openjdk versions 8 and 15.

### 2. Apache Maven
_SearchStateExplorer_ uses _Maven_ as a build automation tool.
This simplifies the compilation phases and the dependencies management.

Maven can be freely downloaded [here](https://maven.apache.org/download.cgi).

_Ubuntu Users_: available on `apt`:
```sudo apt install maven```

## Build
_(These instructions have been tested on Ubuntu 20.10)_

Clone this repository in a local directory using command

`git clone git@bitbucket.org:mclab/searchstateexplorer.git`

Let `{BASEDIR}` the directory where the repository has been cloned (the one that contains the `doc/` directory).

Move inside the directory:

``cd {BASEDIR}``

Run the following command:

``./build.sh``

Alternatively, just run the following (included in `build.sh`):

``mvn package``

The build process generates several JAR files:

* the framework JAR, which is used by all examples and, in general, by any application using the framework: ``{BASEDIR}/searchstateexplorer-framework/target/searchstateexplorer-framework.jar``;
* an _executable_ JAR for each example in `{BASEDIR}/examples/`:
    * _Puzzle_ example: `{BASEDIR}/examples/Puzzle/target/Puzzle.jar`,
    * _Vacuum_ example: `{BASEDIR}/examples/Vacuum/target/Vacuum.jar`,
    * _Protein Folding_ exercise: `{BASEDIR}/examples/ProteinFolding/target/ProteinFolding.jar`.

## Running Examples

Usage is similar for all examples executable JARs.

All commands are run from `{BASEDIR}`.

For each example `{EXAMPLE}`, running 

`java -jar examples/{EXAMPLE}/target/{EXAMPLE}.jar -h`

will display the help for command-line options.

### Puzzle

Run example:

`java -jar examples/Puzzle/target/Puzzle.jar --size 3 --solvableOnly --algorithms="BFG,A*:MANHATTAN"
`

### Vacuum

Run example:

`java -jar examples/Vacuum/target/Vacuum.jar --sizeX 3 --sizeY 3 --algorithms="A*" --maxdepth 30`

### Protein Folding

This is left as an exercise, hence running the JAR will result in `NotImplementedException`s being thrown.


## Maven cheat sheet

To delete all `target` directories, which contain the build outputs, such as `.class` and `.jar` files, run

`mvn clean`.

To build the JARs, run:

`mvn package`

The two previous commands can be merged to save time:

`mvn clean package`

Optionally, you can run the `verify` phase to check validity of the packages.

`mvn verify` or `mvn clean verify`

Note that `verify` also builds the JARs.

For more info, visit the [Maven quick start guide](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html).


## Implementing applications based on SearchStateExplorer

If you want to implement an application based on the SearchStateExplorer framework, you have two options:

1. Create a "classic" application, which needs to have `searchstateexplorer-framework.jar` on its `CLASSPATH`;
2. Create an application based on Maven. The easiest way to do so is to mimic the examples. This boils down to:
    1. setting up a directory structure analogous to the one of, e.g., _Puzzle_ (**This is very important**, since Maven is built on the concept of _convention over configuration_, so it exploits the directory structure in the building process).
    2. Setting `searchstateexplorer-framework` as a _dependency_ in the `pom.xml` file in the root of your application.
    3. Edit the `pom.xml` file to configure your building process.
    
## Who do I talk to?

For support or collaboration, contact:

* Marco Esposito (author) - esposito@di.uniroma1.it