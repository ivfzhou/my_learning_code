# 默认策略
iptables -P INPUT DROP
iptables -P OUTPUT DROP
iptables -P FORWARD DROP

# 回环
iptables -A INPUT -i lo -j ACCEPT
iptables -A OUTPUT -o lo -j ACCEPT

# 已建立和相关连接（使用 conntrack 更佳）
iptables -A INPUT -m conntrack --ctstate ESTABLISHED,RELATED -j ACCEPT
iptables -A OUTPUT -m conntrack --ctstate ESTABLISHED,RELATED -j ACCEPT

# 出站 NEW 连接放行
iptables -A OUTPUT -p udp --dport 53 -m conntrack --ctstate NEW -j ACCEPT
iptables -A OUTPUT -p tcp --dport 53 -m conntrack --ctstate NEW -j ACCEPT
iptables -A OUTPUT -p tcp --dport 80 -m conntrack --ctstate NEW -j ACCEPT
iptables -A OUTPUT -p tcp --dport 443 -m conntrack --ctstate NEW -j ACCEPT
iptables -A OUTPUT -p tcp --dport 22 -m conntrack --ctstate NEW -j ACCEPT
iptables -A OUTPUT -p icmp --icmp-type echo-request -m conntrack --ctstate NEW -j ACCEPT
ip6tables -A OUTPUT -p ipv6-icmp --icmpv6-type echo-request -m conntrack --ctstate NEW -j ACCEPT

# 允许出站邻居请求和路由请求
ip6tables -A OUTPUT -p ipv6-icmp --icmpv6-type router-solicitation -j ACCEPT
ip6tables -A OUTPUT -p ipv6-icmp --icmpv6-type neighbor-solicitation -j ACCEPT

# 允许入站邻居通告和路由通告
ip6tables -A INPUT -p ipv6-icmp --icmpv6-type router-advertisement -j ACCEPT
ip6tables -A INPUT -p ipv6-icmp --icmpv6-type neighbor-advertisement -j ACCEPT

# DHCP（可选）
iptables -A OUTPUT -p udp --dport 67 --sport 68 -m conntrack --ctstate NEW -j ACCEPT
iptables -A INPUT -p udp --sport 67 --dport 68 -m conntrack --ctstate NEW -j ACCEPT

# NTP（可选）
iptables -A OUTPUT -p udp --dport 123 -m conntrack --ctstate NEW -j ACCEPT

# Git 协议（可选）
iptables -A OUTPUT -p tcp --dport 9418 -m conntrack --ctstate NEW -j ACCEPT

# 邮件（可选）
iptables -A OUTPUT -p tcp -m multiport --dports 25,465,587,143,993,110,995 -m conntrack --ctstate NEW -j ACCEPT

# 数据库（可选）
iptables -A OUTPUT -p tcp --dport 3306 -m conntrack --ctstate NEW -j ACCEPT
iptables -A OUTPUT -p tcp --dport 5432 -m conntrack --ctstate NEW -j ACCEPT

# FTP 控制连接（需加载 nf_conntrack_ftp）
modprobe nf_conntrack_ftp
iptables -A OUTPUT -p tcp --dport 21 -m conntrack --ctstate NEW -j ACCEPT
