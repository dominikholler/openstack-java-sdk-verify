# openstack-java-sdk-verify

## Get a new Fedora VM

```
rootpassword=123456
mirror=https://download.fedoraproject.org/pub
version=25
name=Fedora-Cloud-Base-$version-1.3
osvariant=fedora23
origfile=$name.x86_64.qcow2
image=$name.img

wget -nc $mirror/fedora/linux/releases/$version/CloudImages/x86_64/images/$origfile

truncate -s 40G $image
virt-resize --expand /dev/sda1 $origfile $image
virt-customize -a $image \
    --root-password password:$rootpassword \
    --ssh-inject root \
    --selinux-relabel \
    --uninstall cloud-init \
    --hostname $name \
    --timezone "Europe/Berlin" \
    --update

virt-install --name $name \
    --vcpus 2,maxvcpus=4 \
    --memory 1024,maxmemory=4096 \
    --disk $image \
    --os-type=linux --os-variant=$osvariant \
    --import \
    --connect qemu:///system &
sleep 5
virsh -c qemu:///system setmem  $name 1G
arp | grep `virsh -c qemu:///system domiflist $name | tail -n 2 | sed -E 's/.*([0-9a-fA-F:]{17})/\1/'`

```

## Run Example Application using updates-testing repo in target fedora
```
dnf update -y
dnf install -y java-1.8.0-openjdk-devel maven git
dnf install -y openstack-java-*-3.1.3 # should fail
dnf config-manager --set-enabled updates-testing
dnf install -y openstack-java-*-3.1.3
# install openstack-java-sdk-3.1.3 for maven manually,
# because it is not yet published on maven repo
git clone https://github.com/woorea/openstack-java-sdk.git
cd openstack-java-sdk
git checkout openstack-java-sdk-3.1.3
mvn install -D skip.sign=true
cd ..
git clone https://github.com/dominikholler/openstack-java-sdk-verify.git
cd openstack-java-sdk-verify/QuantumListNetworksTimeout
vi src/main/java/com/woorea/openstack/examples/network/QuantumListNetworksTimeout.java  +90
mvn compile
mvn dependency:copy-dependencies
# check that ouput contains a list of Network, and networks
# attribute mtu, even if the value is null
./test_on_local.sh # verify that test app is OK
./test_on_fedora.sh # verify that the fedora libs are OK
dnf remove -y openstack-java-*-3.1.3
./test_on_local.sh # should still work
./test_on_fedora.sh # should fail
```


