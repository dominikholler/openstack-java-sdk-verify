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

## Run Example Application using updates-testing repo
```
ssh root@92.168.122.204
dnf config-manager --set-enabled updates-testing
dnf install openstack-java-*-3.1.2 java-1.8.0-openjdk-devel maven git
git clone git@github.com:dominikholler/openstack-java-sdk-verify.git
cd openstack-java-sdk-verify/QuantumListNetworksTimeout
# configure user credentials
vi src/main/java/com/woorea/openstack/examples/network/QuantumListNetworksTimeout.java  +90
mvn compile # compile using libs from maven
# rm -rf ~/.m2/repository/ # remove libs from manve
./test_on_fedora.sh # run app with the libs from fedora packages

```


