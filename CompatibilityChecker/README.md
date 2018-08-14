# CompatibilityChecker

Checks if an application which is compiled using openstack-java-sdk 3.1.3
 - failing with openstack-java-sdk 3.2.3
 - is working with openstack-java-sdk 3.2.4.

## Run Example Application using updates-testing repo in target fedora
```
docker run -it --rm fedora:28 bash
dnf install -y java-1.8.0-openjdk-devel maven git

git clone https://github.com/dominikholler/openstack-java-sdk-verify.git
cd openstack-java-sdk-verify/CompatibilityChecker
# insert password
vi src/main/java/com/woorea/openstack/examples/network/CompatibilityChecker.java +68
mvn compile && mvn dependency:copy-dependencies

# execute with openstack-java-sdk 3.1.3 as specified in pom.xml
# RawRequestChecker should succeed.
# QuantumChecker fails with OpenStackResponseException: Not Found
# fixed in openstack-java-sdk 3.2.3
# https://github.com/woorea/openstack-java-sdk/commit/72b1c2379c78c753f226d915c0dcb7e339ad3bb0
./test_on_local.sh

dnf install -y openstack-java-*-3.2.3
# execute with openstack-java-sdk 3.2.3 as installed by fedora
# fails with NoSuchMethodError, because of the binary incompability of
# openstack-java-sdk 3.2.3 with the 3.1.3 expected by application
./test_on_fedora.sh

dnf install -y python3-dnf-plugins-core
dnf config-manager --set-enabled updates-testing
dnf update -y openstack-java-*-3.2.4

# execute with openstack-java-sdk 3.2.4 as installed by fedora
# RawRequestChecker and QuantumChecker should succeed
./test_on_fedora.sh 
```

[//]: # ( 
  curl "https://koji.fedoraproject.org/koji/buildinfo?buildID=1136165" | grep noarch.rpm | sed -e 's/.*a href="\([^"]*\).*/\1/'
  )
