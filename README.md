# Air Cargo Problem 

An implementation of the famous planning problem in Java. This project includes a solution via forward search using a searchstate explorer created by Marco Esposito (author) - esposito@di.uniroma1.it, Providing the following search algorithms: 

- Depth-First Search,
- Breadth-First Search,
- Min-Cost,
- Best First Greedy,
- A*.

Additionally the project includes the implementation of an entire Planning Graph and caculation of the hLevel heursitic. 

The user can choose between four different instances to be solved by either forward search or by creating the relative Planning Graph

`{BASEDIR}/data/first_instance.txt`
`{BASEDIR}/data/second_instance.txt`
`{BASEDIR}/data/third_instance.txt`
`{BASEDIR}/data/fourth_instance.txt`


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
    * _Cargo_ example: `{BASEDIR}/examples/Cargo/target/Cargo.jar`,
    * _CargoPlanningGraph_ example: `{BASEDIR}/examples/CargoGraphPlanner/target/CargoGraphPlanner.jar`,

## Running Examples

Usage is similar for all examples executable JARs.

All commands are run from `{BASEDIR}`.

For each example `{EXAMPLE}`, running 

`java -jar examples/{EXAMPLE}/target/{EXAMPLE}.jar -h`

will display the help for command-line options.

For the file-inputs the basename of the file is used to calculate the path, e.g. ( first_instance.txt ) 

### Cargo

Run example:

`java -jar examples/Cargo/target/Cargo.jar --algorithms="BFG,A*:MANHATTAN  --file={ first_instance.txt, second_instance.txt, third_instance.txt, fourth_instance.txt }"
`

### CargoGraphPlanner

Run example:

`java -jar examples/CargoGraphPlanner/target/CargoGraphPlanner.jar --file={ first_instance.txt, second_instance.txt, third_instance.txt, fourth_instance.txt }"
`

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


    

