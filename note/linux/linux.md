# 一、笔记

1. [POSIX 定义网址](https://pubs.opengroup.org/onlinepubs/9799919799/)。
1. /ect/default/grub 的参数：GRUB_CMDLINE_LINUX_DEFAULT="quiet splash loglevel=3"。
1. /dev/sd*x* 主设备号（磁盘驱动程序），从设备号（访问地址）。
1. ls -l 输出中的第二字段表示文件夹的子文件个数。
1. umask 值的含义是创建文件（夹）时要去除的权限位。创建文件的默认权限是 0666，创建文件夹的默认权限是 0777。umask 值为 0022 时，默认文件权限为 0644，默认文件夹权限为 0755。
1. cron 格式：分 小时 天 月 星期 命令。
1. LVM 名称：PE LE PV VG LV。
1. Chrome 浏览器下载地址：https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb。
1. UID 类型：1-999 虚拟用户，1000+ 普通用户。
1. 安装拼音输入法：sudo apt install ibus-libpinyin。
1. 制作 U 盘启动盘：sudo dd of=/dev/sda if=path/to/iso status=progress。

# 二、文件类型

ls -l 打印输出中：
- \-：普通文件
- d：目录
- l：链接文件
- p：管理文件
- b：设备文件
- c：设备文件
- s：套接字文件
- f：命令管道

# 三、文件权限

文件的权限含义：r 查看内容，w 修改内容，x 执行。  
文件夹的权限含义：r 查看文件夹下内容；w 删除增加文件夹下内容；x 可以 cd 到该文件夹下，可以看到文件权限信息。

特殊权限：
- SUID 4：用于二进制可执行文件，执行命令时临时获得文件所有者的权限。例如 passwd。权限标志 s。设置方法 chmod 4755 file、chmod u+s。
- SGID 2：用于文件夹，在该目录下创建文件（夹），权限自动更改为该文件夹的属组。用于文件共享。用于文件，执行者临时获得文件所属组的权限。权限标志 s。
- SBIT 1：用于文件夹，在该目录下创建文件（夹）时，仅 root 和自己可以删除。例如 /tmp 文件夹。权限标志 t。

# 四、Debian 软件源配置

配置所在路径：/etc/apt/sources.list。

源配置例子：
```
deb http://ftp.cn.debian.org/debian-security trixie-security main
deb-src http://ftp.cn.debian.org/debian-security trixie-security main

deb http://ftp.cn.debian.org/debian trixie main non-free non-free-firmware contrib
deb-src http://ftp.cn.debian.org/debian trixie main non-free non-free-firmware contrib

deb http://ftp.cn.debian.org/debian/ trixie-updates main non-free non-free-firmware contrib
deb-src http://ftp.cn.debian.org/debian trixie-updates main non-free non-free-firmware contrib


# deb http://ftp.cn.debian.org/debian/ trixie-backports main non-free non-free-firmware contrib
# deb-src http://ftp.cn.debian.org/debian trixie-backports main non-free non-free-firmware contrib
```

# 五、LVM 操作示例

## 5.1 LV 扩容

```shell
sudo lvresize -L +10GiB /dev/vg/var
sudo resize2fs /dev/mapper/vg-var
```

## 5.2 LV 缩容

```shell
sudo lsof | grep '/dev/mapper/vg-var' # 查看文件占用进程。
sudo vim /etc/fstab # 编辑启动分区挂载配置，取消分区自动挂载，然后重启。
sudo umount /dev/mapper/vg-var
sudo e2fsck -f /dev/mapper/vg-var
sudo resize2fs /dev/mapper/vg-var 50G
sudo lvreduce -L 50G /dev/vg/var
mount /dev/mapper/vg-var /var
```

## 5.3 PV 删除

```shell
sudo pvmove /dev/nvme0n1p7 # 将使用的 PE 转移到别的 PV 上去。
sudo pvmove -n /dev/vg01/lv01 /dev/sdb1 /dev/sdc1 # 将 /dev/sdb1 上的所有 PE 移动到 /dev/sdc1 上。只想移动特定的逻辑卷。
sudo vgreduce vg /dev/nvmeon1p7
```

## 5.4 PV 缩容

```shell
sudo fdisk -l
sudo pvdisplay --units b
sudo pvresize --setphysicalvolumesize 40GiB /dev/xxx
sudo pvresize /dev/xxx # 分区缩容。
```

## 5.5 PV 增加

```shell
sudo pvcreate /dev/nvmeon1p7
sudo vgextend vg /dev/nvmeon1p7
```

## 5.6 分区删除

```shell
sudo parted -l
sudo parted /dev/nvme0n1
help
print
rm 7
```

## 5.7 分区缩容

```shell
fdisk /dev/xxx
p
d
n
w
```

# 六、openssl 操作示例

- 生成私钥：
  ```shell
  openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -aes256 -pass pass:123456 -out pkey_rsa_2048_aes256cbc_123456.p8.pem
  openssl genpkey -algorithm EC -pkeyopt ec_paramgen_curve:P-256 -aes256 -pass pass:123456 -out pkey_ecdsa_128_aes256cbc_123456.p8.pem
  openssl genpkey -algorithm ED25519 -aes256 -pass pass:123456 -out pkey_eddsa_25519_aes256cbc_123456.p8.pem
  ```
- RSA 私钥转成 PKCS#1 格式：openssl pkey -traditional -in *pkey_rsa_2048_aes256cbc_123456.p8.pem* -passin *pass:123456* -aes256 -out *pkey_rsa_2048_aes256cbc_123456.p1.pem* -passout pass:123456。
- 查看私钥的对称加密算法：openssl asn1parse -in *pkey_rsa_2048_aes256cbc_123456.p8.pem*。
- 查看私钥位数信息：openssl pkey -in *pkey_rsa_2048_aes256cbc_123456.p8.pem* -passin *pass:123456* -text -noout。
- 获取私钥的公钥：openssl pkey -pubout -in *pkey_rsa_2048_aes256cbc_123456.p8.pem* -passin *pass:123456* -out *pubkey_rsa_2048.spki.pem*。
- 查看公钥算法：openssl asn1parse -in *pubkey_rsa_2048.spki.pem*。
- 生成证书签名请求: 
  ```shell
  # C=Country 国家代码
  # CN=CommonName 通用名称
  # O=Organization 组织
  # OU=OrganizationUnit 组织单位
  # ST=StateOrProvince 州或省
  # L=Locality 城市
  # STREET=StreetAddress 街道
  # EMAIL=EmailAddress 电子邮件
  # DC=DomainComponent 域名组件
  openssl req -new -key pkey_rsa_2048_aes256cbc_123456.p8.pem -passin pass:123456 -subj "/C=CN/ST=Hunan/L=Changsha/O=ivfzhou test/CN=rsa_ca_cert" -out csr_rsa_ca.p10.pem
  ```
- 用私钥自签名证书：openssl x509 -req -in *csr_rsa_ca.p10.pem* -signkey *pkey_rsa_2048_aes256cbc_123456.p8.pem* -passin *pass:123456* -days *3650* -sha256 -extfile *ca_ext.cnf* -out *cert_rsa_ca.x509.pem*。
- 查看证书内容：openssl x509 -in *cert_rsa_ca.x509.pem* -text -noout -fingerprint。
- 从证书中提取公钥：openssl x509 -in *cert_rsa_ca.x509.pem* -pubkey -noout -out *pubkey_rsa_2048.spki.pem*。
- 签发证书：openssl x509 -req -in *csr_rsa_sign.p10.pem* -CAkey *pkey_rsa_2048_aes256cbc_123456.p8.pem* -passin *pass:123456* -CA *cert_rsa_ca.x509.pem* -CAcreateserial -sha256 -days *365* -out *cert_rsa_sign.x509.pem*。
- PEM 与 DER 编码互转：
  ```shell
  openssl pkey -in pkey_rsa_2048_aes256cbc_123456.p8.pem -inform pem -passin pass:123456 -out pkey_rsa_2048.p8.der -outform der
  openssl pkey -pubin -in pubkey_rsa_2048.spki.pem -inform pem -out pubkey_rsa_2048.spki.der -outform der
  openssl x509 -in cert_rsa_ca.x509.pem -inform pem -out cert_rsa_ca.x509.der -outform der
  openssl req -in csr_rsa_ca.p10.pem -inform pem -out csr_rsa_ca.p10.der -outform der
  openssl pkey -in pkey_rsa_2048.p8.der -inform der -out pkey_rsa_2048_aes256cbc_123456.p8.pem -outform pem -aes256 -passout pass:123456
  ```
- 私钥与证书合并成 PKCS#12 格式：openssl pkcs12 -export -inkey *pkey_rsa_2048_aes256cbc_123456.p8.pem* -passin *pass:123456* -in *cert_rsa_ca.x509.pem* -certfile *csr_rsa_ca.p10.pem -out cert_rsa_sign_123456.p12.der* -passout *pass:123456*。
- 查看 PKCS#12 里证书信息：openssl pkcs12 -in *cert_rsa_sign_123456.p12.der* -passin *pass:123456* -nokeys -info。
- PKCS#12 中导出私钥：openssl pkcs12 -in *cert_rsa_sign_123456.p12.der* -passin *pass:123456* -out *pkey_rsa_2048_aes256cbc_123456.p8.pem* -passout *pass:123456* -nocerts。
- PKCS#12 中导出证书：openssl pkcs12 -in *cert_rsa_sign_123456.p12.der* -passin *pass:123456* -out *cert_rsa_sign.x509.pem* -nokeys。
- 验证签名：openssl verify -CAfile *cert_rsa_ca.x509.pem* -untrusted *cert_rsa_middle_ca.x509.pem* *cert_rsa_sign.pem*。
- 对证书请求签名：openssl sha1 -sign *ivfzhou1.pem.key* *ivfzhou1.der.csr* > *sha1.sign*。
- 判断证书和私钥模数：
  ```shell
  openssl rsa -noout -modulus -in file
  openssl x509 -noout -modulus -in file
  ```
- 校验私钥签名：openssl dgst -sha1 -verify *pub* -signature *sign* *origin*。
- 从签名数据中获取散列值：openssl rsautl -verify -pubin -inkey *pub* -in *sign* -out *hash*。
- 例子，[ca_ext.cnf](./ca_ext.cnf)、[server_ext.cnf](./server_ext.cnf)、[code_ext.cnf](./code_ext.cnf)、[client_ext.cnf](./client_ext.cnf)、[email_ext.cnf](./email_ext.cnf)、[time_ext.cnf](./time_ext.cnf)、[ocsp_ext.cnf](./ocsp_ext.cnf)：
  ```shell
  # 生成根 CA。
  openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -aes256 -pass pass:123456 -out pkey_rsa_2048_aes256cbc_123456.p8.pem
  openssl req -new -key pkey_rsa_2048_aes256cbc_123456.p8.pem -passin pass:123456 -subj "/C=CN/ST=Hunan/L=Changsha/O=ivfzhou test/CN=rsa_ca_cert" -out csr_rsa_ca.p10.pem
  openssl x509 -req -in csr_rsa_ca.p10.pem -signkey pkey_rsa_2048_aes256cbc_123456.p8.pem -passin pass:123456 -days 3650 -sha256 -extfile ./ca_ext.cnf -out cert_rsa_ca.x509.pem
  
  # 生成中间 CA。
  openssl genpkey -algorithm EC -pkeyopt ec_paramgen_curve:P-256 -aes256 -pass pass:123456 -out pkey_ecdsa_128_aes256cbc_123456.p8.pem
  openssl req -new -key pkey_ecdsa_128_aes256cbc_123456.p8.pem -passin pass:123456 -subj "/C=CN/ST=Hunan/L=Changsha/O=ivfzhou test/CN=ecdsa_middle_ca_cert" -out csr_ecdsa_middle_ca.p10.pem
  openssl x509 -req -in csr_ecdsa_middle_ca.p10.pem -CAkey pkey_rsa_2048_aes256cbc_123456.p8.pem -passin pass:123456 -CA cert_rsa_ca.x509.pem -CAcreateserial -sha256 -days 1825 -extfile ./ca_ext.cnf -out cert_ecdsa_middle_ca.x509.pem
  
  # 生成服务器证书。
  openssl genpkey -algorithm ED25519 -aes256 -pass pass:123456 -out pkey_server_eddsa_25519_aes256cbc_123456.p8.pem
  openssl req -new -key pkey_server_eddsa_25519_aes256cbc_123456.p8.pem -passin pass:123456 -subj "/C=CN/ST=Hunan/L=Changsha/O=ivfzhou test/CN=eddsa_server_cert" -out csr_server_eddsa.p10.pem
  openssl x509 -req -in csr_server_eddsa.p10.pem -CAkey pkey_ecdsa_128_aes256cbc_123456.p8.pem -passin pass:123456 -CA cert_ecdsa_middle_ca.x509.pem -CAcreateserial -sha256 -days 365 -extfile ./server_ext.cnf -out cert_server_eddsa.x509.pem
  
  # 将服务证书合成 PFX 格式，校验 CA 与证书的关联性。
  openssl pkcs12 -export -inkey pkey_server_eddsa_25519_aes256cbc_123456.p8.pem -passin pass:123456 -in cert_server_eddsa.x509.pem -certfile cert_ecdsa_middle_ca.x509.pem -out cert_server_eddsa_123456.p12.der -passout pass:123456
  openssl verify -CAfile cert_rsa_ca.x509.pem -untrusted cert_ecdsa_middle_ca.x509.pem cert_server_eddsa.x509.pem
  
  # 生成代码签名证书。
  openssl genpkey -algorithm ED25519 -aes256 -pass pass:123456 -out pkey_code_eddsa_25519_aes256cbc_123456.p8.pem
  openssl req -new -key pkey_code_eddsa_25519_aes256cbc_123456.p8.pem -passin pass:123456 -subj "/C=CN/ST=Hunan/L=Changsha/O=ivfzhou test/CN=eddsa_code_cert" -out csr_code_eddsa.p10.pem
  openssl x509 -req -in csr_code_eddsa.p10.pem -CAkey pkey_ecdsa_128_aes256cbc_123456.p8.pem -passin pass:123456 -CA cert_ecdsa_middle_ca.x509.pem -CAcreateserial -sha256 -days 365 -extfile ./code_ext.cnf -out cert_code_eddsa.x509.pem
  
  # 将代码签名证书合成 PFX 格式，校验 CA 与证书的关联性。
  openssl pkcs12 -export -inkey pkey_code_eddsa_25519_aes256cbc_123456.p8.pem -passin pass:123456 -in cert_code_eddsa.x509.pem -certfile cert_ecdsa_middle_ca.x509.pem -out cert_code_eddsa_123456.p12.der -passout pass:123456
  openssl verify -CAfile cert_rsa_ca.x509.pem -untrusted cert_ecdsa_middle_ca.x509.pem cert_code_eddsa.x509.pem
  ```

# 七、配置文件

## 7.1 启动配置

/usr/lib/systemd/system  
/etc/init.d/  
runlevelx.target

## 7.2 sudo 配置

文件所在位置：/etc/sudoers。  
使用 visudo 编辑。  
配置格式：user host=(asuser:asgroup) NOPASSWD: command，使用 ALL 表示所有，%sudo 表示 sudo 用户组成员。

## 7.3 DNS

路径：/etc/resolv.conf。  
内容：nameserver 114.114.114.114。

## 7.4 gnome 桌面图标配置路径

全用户图标位置：/usr/share/applications/  
用户图标位置：$HOME/.local/share/applications  
图标配置例子 vscode.desktop：
```
[Desktop Entry]
Version=1.0
Type=Application
Name=VSCode
Icon=/home/ivfzhou/programs/vscode/resources/app/resources/linux/code.png
Exec=/home/ivfzhou/programs/vscode/code
Comment=VSCode
Categories=Development;IDE;
Terminal=false
StartupNotify=true
StartupWMClass=code
```

## 7.5 启动挂载配置

/etc/fstab  
FileSystem Dir Type Options Dump Pass

## 7.6 定义用户文件夹名

${HOME}/.config/user-dirs.dirs

## 7.7 内核配置文件路径

/boot/configxxx

## 7.8 SELinux 配置文件路径

/etc/selinux/config

## 7.9 用户配置文件

/etc/passwd，七段：名、是否需要密码、uid、gid、注释、家路径、命令解释器。  
/etc/shadow，存贮密码。用户名、密码、最后一修改时间、最小修改时间间隔、密码有效期、密码需要更改前的警告天数、密码过期后的宽限天数、账号失效时间、保留字段。  
/etc/group，用户组名、组密码、gid、组内成员。

## 7.10 网卡配置路径

/etc/sysconfig/network-scripts/
- BOOTPROTO=dhcp 或者 static none，动态或者静态地址 IP。
- IPADDR IP 地址。
- NETMASK 子网掩码。
- DEVICE 网卡名。
- ONBOOT=yes 或者 no 开机启动与否。

## 7.11 系统信息路径

/proc/cpuinfo 显示 cpu 信息。  
/proc/interrupts 显示中断。  
/proc/meminfo 校验内存使用。  
/proc/swaps 显示 swap 使用。  
/proc/version 显示内核版本。  
/proc/net/dev 显示网络适配器及统计。  
/proc/mounts 显示已加载的文件系统。  
/proc/*pid*/fd 程序输入输出。  
/proc/*pid*/cwd 运行目录。

## 7.12 进程日志信息

/var/log/cron 周期性的程序日志。  
/var/log/secure 安全日志。  
/var/log/message 异常信息。  
/var/log/dmesg 内核日志。

# 八、导入证书

```shell
sudo cp <ca.crt> /usr/local/share/ca-certificates/
sudo update-ca-certificates --verbose # 只识别 .crt 结尾的证书。
```

# 九、添加字体

```shell
sudo mkdir /usr/share/fonts/custom
sudo mv fonts/* /usr/share/fonts/custom/
sudo fc-cache -fv
```

# 十、设置交换分区

```shell
sudo swapoff /swapfile
sudo rm /swapfile
sudo dd if=/dev/zero of=/swapfile bs=1G count=8
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
sudo vim /etc/fstab
```
```shell
sudo fdisk /dev/sdx # 新建分区。
sudo mkswap /dev/sdax
sudo swapon /dev/sdax
free -h
echo '/dev/sdax none swap sw 0 0' | sudo tee -a /etc/fstab
```

# 十一、设置时区时间

```shell
# 查看时区。
timedatectl

# 列出可用时区。
timedatectl list-timezones

# 设置系统时区。
sudo timedatectl set-timezone Asia/Shanghai

# 手动设置时间。
sudo timedatectl set-time "YYYY-MM-DD HH:MM:SS"

# 启用 NTP 同步。
sudo timedatectl set-ntp true

# 安装 NTP 服务。
sudo apt install systemd-timesyncd

# 检查 NTP 服务状态。
sudo systemctl status systemd-timesyncd

# 时区的环境变量。
export TZ='Asia/Shanghai'

# 时区设置文件位置。
ls -alhtc /etc/localtime

# 直接设置时区文件。
sudo ln -s /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
```

# 十二、设置 apt 网络代理

1. 配置 apt 网络代理，配置文件路径：/etc/apt/apt.conf.d/proxy.conf。配置内容：
   ```conf
   Acquire {
     HTTP::proxy "http://127.0.0.1:8889";
     HTTPS::proxy "http://127.0.0.1:8889";
   }
   ```

   ```conf
   Acquire {
     HTTP::proxy::download.docker.com "http://127.0.0.1:8889";
     HTTPS::proxy::download.docker.com "http://127.0.0.1:8889;
   }
   ```

# 十三、配置 Debian 网络

1. ip link show：查看网络。
1. ip link set *eth0* up：启用网卡 *eth0*。
1. ip addr add 192.168.137.128/24 dev eth0：配置网卡 ip。
1. ip route add default via 192.168.137.1 dev eth0：配置网卡网关。
1. echo "nameserver 114.114.114.114" > /etc/resolv.conf
1. ping baidu.com
1. sudo systemctl restart networking：重启网络。
1. 编辑 /etc/network/interfaces 设置永久网卡配置。
   ```
   auto eth0
   iface eth0 inet static
       address 192.168.42.128
       netmask 255.255.255.0
       gateway 192.168.42.2
       dns-nameservers 114.114.114.114 8.8.8.8 8.8.4.4
   ```

   ```
   auto eth0
   iface eth0 inet dhcp
   ```

   ```
   auto wlan0
   iface wlan0 inet dhcp
       wpa-ssid "your_SSID"
       wpa-psk "your_password"
   ```

# 十四、设置系统语言

```shell
# 生成语言环境。
sudo vim /etc/locale.gen
sudo locale-gen

# 查看语言。
localectl status

# 设置默认语言。
sudo localectl set-locale LANG=zh_CN.UTF-8 
cat /etc/default/locale

# 设置用户语言。LANG=zh_CN.UTF-8 LC_ALL=zh_CN.UTF-8
vim ~/.profile
vim ~/.bashrc

# 重新登陆会话生效。
sudo reboot
```

# 十五、配置 iptables

[iptables](./iptables.conf) 配置文件路径 /etc/iptables/rules.v4  
raw > mangle > nat > filter，prerouting(r, m ,n) > input(m, f) > forward(m, f) > output(r, m, n, f) > postrouting(m, n)，table > chain > rule

```shell
sudo apt install iptables iptables-persistent
sudo iptables -A INPUT -m state --state RELATED,ESTABLISHED -j ACCEPT
sudo iptables -A INPUT -p icmp -j ACCEPT
sudo iptables -A INPUT -i lo -j ACCEPT
sudo iptables -A INPUT -p tcp -m state --state NEW -m tcp --dport 22 -j ACCEPT
sudo iptables -A INPUT -j REJECT --reject-with icmp-host-prohibited
sudo iptables -A FORWARD -j REJECT --reject-with icmp-host-prohibited
sudo netfilter-persistent save
sudo iptables-restore  /etc/iptables/xxx # 应用配置文件的规则。
sudo iptables -F # 清空规则，机器重启后失效。
```

```shell
# 允许某一个网段的请求。
sudo iptables -A INPUT -s 192.168.3.0/24 -j ACCEPT
```

# 十六、设置 Grub 界面字大小和背景

```shell
sudo grub-mkfont -o /boot/grub/DejaVuSansMono22.ttf -s 22 /usr/share/fonts/truetype/dejavu/DejaVuSansMono.ttf
```

可能需要关闭安全启动。
```shell
vim /etc/default/grub
```

```txt
# 设置分辨率
GRUB_GFXMODE=2880x1800

# 控制从 GRUB 过渡到 Linux 内核时的图形模式保持
GRUB_GFXPAYLOAD_LINUX=keep

# 设置背景图片
GRUB_BACKGROUND=/boot/grub/black2880x1800.png

# 使用图形终端输出
GRUB_TERMINAL_OUTPUT=gfxterm

# 设置字体文件
GRUB_FONT=/boot/grub/DejaVuSansMono22.ttf

# 设置语言
GRUB_LANG=en_US
LANG=en_US
```

```shell
sudo update-grub
```

# 十七、休眠

- 交换内存大于运行内存。
- 关闭 Secure Boot。
- 编辑 grub：
  ```shell
  # 查看交换分区的 UUID。
  sudo blkid
  
  sudo vim /etc/default/grub
  # GRUB_CMDLINE_LINUX_DEFAULT 后面加上 resume=UUID=xxx
  
  sudo update-grub
  ```
- 配置 Initramfs：
  ```shell
  echo "RESUME=UUID=xxx" | sudo tee /etc/initramfs-tools/conf.d/resume
  sudo update-initramfs -u -k all
  ```
- 重启电脑。
- 休眠：`sudo systemctl hibernate`。
- 检查内核配置，确认内核确实编译了休眠支持：`grep -E 'CONFIG_PM_SLEEP|CONFIG_HIBERNATION' /boot/config-$(uname -r)`。
