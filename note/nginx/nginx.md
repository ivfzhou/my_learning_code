# 一、笔记

1. [keepalived.conf](./keepalived.conf)、[nginx_check.sh](./nginx_check.sh)、[nginx_example.conf](./nginx.conf)。

1. 匹配规则：

   ```nginx
   location = /path {
       # 只匹配 /path（完全一致）。
   }
   location ^~ /static/ {
       # 匹配以 /static/ 开头的路径，不检查正则。
   }
   location ~ \.php$ {
       # 正则匹配（区分大小写）。
   }
   location ~* \.jpg$ {
       # 正则匹配（不区分大小写）。
   }
   location /static/ {
       # 普通前缀匹配，最长匹配优先。
   }
   location / {
       # 兜底匹配。
   }
   ```

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
      - /home/ivfzhou/volumes/nginx/ssl:/etc/nginx/ssl:rw
```
运行：

```shell
mkdir -p ~/volumes/nginx/config
mkdir -p ~/volumes/nginx/logs
mkdir -p ~/volumes/nginx/sl
mkdir -p ~/volumes/nginx/config/conf.d
cp ~/src/my_learning_code/note/nginx/default.conf ~/volumes/nginx/config/conf.d/default.conf
cp ~/src/my_learning_code/note/nginx/nginx.conf ~/volumes/nginx/config/nginx.conf
sudo chown -R 101:101 ~/volumes/nginx
```
