# 一、通过 Docker-Compose 安装运行

docker compose yaml 配置：

```yml
services:
  tusd:
    image: tusproject/tusd:v2.8
    container_name: tusd
    hostname: ivfzhoudockertusd
    privileged: true
    ports:
      - "8080:8080"
    networks:
      network:
        ipv4_address: 172.16.3.141
    extra_hosts:
      - "ivfzhoudebian:172.16.3.1"
    volumes:
      - /home/ivfzhou/volumes/tusd/data:/srv/srv/tusd-data/data:rw
    entrypoint: "tusd -expose-metrics=false -disable-cors -hooks-enabled-events="
```
运行命令：

```shell
mkdir -p ~/volumes/tusd/data
sudo chown 1000:1000 -R ~/volumes/tusd
sudo tee -a /etc/hosts <<EOF
172.16.3.141 ivfzhoudockertusd
EOF
docker-compose -f src/my_learning_code/note/docker/docker-compose.yml up -d tusd
```

