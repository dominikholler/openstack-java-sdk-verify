#!/bin/bash -xe

BASE="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
OPENSTACK_SDK_DIR=/usr/share/java/openstack-java-sdk

CLASSPATH=$BASE/target/classes
CLASSPATH=$CLASSPATH:$OPENSTACK_SDK_DIR/resteasy-connector.jar
CLASSPATH=$CLASSPATH:$OPENSTACK_SDK_DIR/*
CLASSPATH=$CLASSPATH:$BASE/resteasy-jaxrs-3.6.3.Final/lib/*

java -classpath $CLASSPATH com.woorea.openstack.examples.network.QuantumListNetworksTimeout
echo SUCCESS
