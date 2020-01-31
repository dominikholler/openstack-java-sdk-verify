#!/bin/bash

# dnf module enable  pki-deps

BASE="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
OPENSTACK_SDK_DIR=/usr/share/java/openstack-java-sdk

CLASSPATH=$BASE/target/classes
#CLASSPATH=$CLASSPATH:$OPENSTACK_SDK_DIR/resteasy-connector.jar
CLASSPATH=$CLASSPATH:$OPENSTACK_SDK_DIR/*
CLASSPATH=$CLASSPATH:/usr/share/java/jackson-databind.jar
CLASSPATH=$CLASSPATH:/usr/share/java/jackson-core.jar
CLASSPATH=$CLASSPATH:/usr/share/java/jackson-annotations.jar
CLASSPATH=$CLASSPATH:/usr/share/java/jackson-jaxrs-providers/*
CLASSPATH=$CLASSPATH:/usr/share/java/resteasy/*
CLASSPATH=$CLASSPATH:/usr/share/java/jboss-jaxrs-2.0-api.jar
CLASSPATH=$CLASSPATH:/usr/share/java/commons-io.jar
CLASSPATH=$CLASSPATH:/usr/share/java/commons-logging.jar
CLASSPATH=$CLASSPATH:/usr/share/java/httpcomponents/httpclient.jar:/usr/share/java/httpcomponents/httpcore.jar
CLASSPATH=$CLASSPATH:/usr/share/java/jboss-annotations-1.2-api/jboss-annotations-api_1.2_spec.jar
CLASSPATH=$CLASSPATH:/usr/share/java/jboss-logging/jboss-logging.jar

java -classpath $CLASSPATH com.woorea.openstack.examples.network.QuantumListNetworksTimeout
