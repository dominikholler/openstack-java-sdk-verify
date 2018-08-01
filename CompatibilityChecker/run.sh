#!/bin/bash
set -x
mvn clean && mvn compile && mvn dependency:copy-dependencies
echo should fail with OpenStackResponseException: Not Found
./test_on_local.sh

set -e
OPENSTACK_SDK_VERSION=3.2.4-SNAPSHOT
SRC_DIR=~/.m2/repository/com/woorea
for libname in keystone-client openstack-client quantum-model keystone-model quantum-client; do
  rm target/dependency/$libname-3.1.3.jar
  cp $SRC_DIR/$libname/$OPENSTACK_SDK_VERSION/$libname-$OPENSTACK_SDK_VERSION.jar target/dependency
done
rm target/dependency/resteasy-connector-3.1.3.jar
cp $SRC_DIR/resteasy-connector/$OPENSTACK_SDK_VERSION/resteasy-connector-$OPENSTACK_SDK_VERSION.jar target/dependency

./test_on_local.sh
echo CompatibilityChecker succeeded.
