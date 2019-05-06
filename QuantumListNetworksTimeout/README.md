# QuantumListNetworksTimeout

## Run Example Application using updates-testing repo in target fedora
```
docker run -it --rm fedora:29 bash
dnf install -y python3-dnf-plugins-core \
  java-1.8.0-openjdk-devel maven git \
  wget unzip
dnf install -y openstack-java-*-3.2.5

git clone https://github.com/dominikholler/openstack-java-sdk-verify.git
cd openstack-java-sdk-verify/QuantumListNetworksTimeout
vi src/main/java/com/woorea/openstack/examples/network/QuantumListNetworksTimeout.java +90

mvn clean # only if sequence is repeated
rm -rf target  # only if sequence is repeated

mvn compile -Dopenstack-sdk.version=3.2.5 -Dresteasy.version=2.3.2.Final
mvn dependency:copy-dependencies -Dopenstack-sdk.version=3.2.5 -Dresteasy.version=2.3.2.Final
./test_on_local.sh # should work

# reproduce https://issues.jboss.org/browse/RESTEASY-2231 by local maven libraries
rm -rf target/dependency
mvn dependency:copy-dependencies -Dopenstack-sdk.version=3.2.5 -Dresteasy.version=3.6.3.Final
./test_on_local.sh # should fail

# reproduce https://issues.jboss.org/browse/RESTEASY-2231 by fedora's openstack-java-sdk and downloaded resteasy
wget http://download.jboss.org/resteasy/resteasy-jaxrs-3.6.3.Final-all.zip
unzip resteasy-jaxrs-3.6.3.Final-all.zip
./test_on_fedora_and_zip.sh # should fail

dnf config-manager --set-enabled updates-testing
dnf install -y openstack-java-*-3.2.7
./test_on_fedora_and_zip.sh # should work
```

