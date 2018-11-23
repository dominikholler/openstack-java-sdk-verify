# openstack-java-sdk-verify

## Run Example Application using updates-testing repo in target fedora
```
docker run -it --rm fedora:29 bash
dnf install -y python3-dnf-plugins-core
dnf install -y java-1.8.0-openjdk-devel maven git
dnf config-manager --set-enabled updates-testing
dnf install -y openstack-java-*-3.2.5
git clone https://github.com/dominikholler/openstack-java-sdk-verify.git
cd openstack-java-sdk-verify/VerifyMtu
vi src/main/java/com/woorea/openstack/examples/network/VerifyMtu.java +20
mvn compile && mvn dependency:copy-dependencies
./test_on_local.sh # verify that test app and the server is OK
./test_on_fedora.sh # verify that the fedora libs are OK
dnf remove -y openstack-java-*-3.2.5
./test_on_local.sh # should still work
./test_on_fedora.sh # should fail with NoClassDefFoundError
```

