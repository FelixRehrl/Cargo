#!/bin/bash

# Cleans targets, then compiles and builds JARs
mvn clean verify

# Removes useless JARs built without dependencies
rm examples/Puzzle/target/searchstateexplorer-examples.jar
rm examples/Vacuum/target/searchstateexplorer-examples.jar
rm examples/ProteinFolding/target/searchstateexplorer-examples.jar
