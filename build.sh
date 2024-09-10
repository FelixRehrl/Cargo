#!/bin/bash

# Cleans targets, then compiles and builds JARs
mvn clean verify

# Removes useless JARs built without dependencies
rm examples/Cargo/target/searchstateexplorer-examples.jar
rm examples/CargoGraphPlanner/target/searchstateexplorer-examples.jar
