# 一、配置

1.  `/` 通配所有。
1. `~` 正则匹配。
1. `^~` 开头正则匹配。
1. `~*` 后缀正则匹配。
1. [keepalived.conf](./keepalived.conf)、[nginx_check.sh](./nginx_check.sh)、[nginx_example.conf](./nginx.conf)。

# 二、编译安装

```shell
./configure --prefix=~/nginx --with-http_stub_status_module --with-http_realip_module --with-http_ssl_module
make install
```

# 三、Docker-Compose 配置

```yaml
version: "3.9"
services:
  nginx:
    image: nginx:1.25.2
    container_name: nginx
    hostname: ivfzhoudcokernginx
    networks:
      network:
        ipv4_address: 172.16.3.139
    ports:
      - "80:80"
    volumes:
      - /home/ivfzhou/volumes/nginx/config/nginx.conf:/etc/nginx/nginx.conf:rw
      - /home/ivfzhou/volumes/nginx/config/conf.d:/etc/nginx/conf.d:rw
      - /home/ivfzhou/volumes/nginx/logs:/var/log/nginx:rw
```
运行：

```shell
mkdir -p ~/volumes/nginx/config
mkdir -p ~/volumes/nginx/logs
mkdir -p ~/volumes/nginx/config/conf.d
cp ~/src/my_learning_code/note/nginx/default.conf ~/volumes/nginx/config/conf.d/default.conf
cp ~/src/my_learning_code/note/nginx/nginx.conf ~/volumes/nginx/config/nginx.conf
sudo chown -R 101:101 ~/volumes/nginx
```
