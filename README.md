# openstack-java-sdk-verify

## Run Example Application using updates-testing repo in target fedora
```
docker run -it --rm fedora:28 bash
dnf install -y python3-dnf-plugins-core
dnf install -y java-1.8.0-openjdk-devel maven git
dnf install -y openstack-java-*-3.2.3 # should fail
dnf config-manager --set-enabled updates-testing
dnf install -y openstack-java-*-3.2.3
# install openstack-java-sdk-3.2.3 for maven manually,
# because it is not yet published on maven repo
git clone https://github.com/woorea/openstack-java-sdk.git
cd openstack-java-sdk
git checkout openstack-java-sdk-3.2.3
mvn install -P '!console,!examples,!jersey2,resteasy' -DskipTests -Dskip.sign=true
cd ..
git clone https://github.com/dominikholler/openstack-java-sdk-verify.git
cd openstack-java-sdk-verify/VerifyMtu
vi src/main/java/com/woorea/openstack/examples/network/VerifyMtu.java +19
mvn compile
mvn dependency:copy-dependencies
# check that ouput contains a list of Network, and networks
# attribute mtu, even if the value is null
./test_on_local.sh # verify that test app is OK
./test_on_fedora.sh # verify that the fedora libs are OK
dnf remove -y openstack-java-*-3.2.3
./test_on_local.sh # should still work
./test_on_fedora.sh # should fail
```

