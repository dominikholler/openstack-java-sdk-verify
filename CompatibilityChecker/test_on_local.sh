#!/bin/bash -xe

BASE="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
CLASSPATH=$BASE/target/classes
CLASSPATH=$CLASSPATH:$BASE/target/dependency/*:

java -classpath $CLASSPATH com.woorea.openstack.examples.network.CompatibilityChecker
