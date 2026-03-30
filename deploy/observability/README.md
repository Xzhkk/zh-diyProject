# Observability Stack

这套目录用于本地启动 `Filebeat -> Kafka -> Logstash -> Elasticsearch -> Kibana` 日志链路。

## 目录说明

- `docker-compose.yml`: 启动整套日志基础设施
- `filebeat/filebeat.yml`: 采集工程里各服务输出的 JSON 日志文件，并写入 Kafka `app-logs` topic
- `logstash/pipeline/logstash.conf`: 消费 Kafka 日志并写入 Elasticsearch

## 启动步骤

1. 在项目根目录启动基础设施

```bash
cd deploy/observability
docker compose up -d
```

2. 启动业务服务

```bash
mvn -pl zh-auth spring-boot:run
mvn -pl zh-gateway spring-boot:run
```

3. 访问接口产生日志

```bash
curl -X POST http://localhost:8082/auth/test -H 'Content-Type: application/json' -d '{}'
```

4. 打开 Kibana

- 地址: `http://localhost:5601`
- 索引模式: `app-logs-*`

## 说明

- 如果你在项目根目录执行 `mvn -pl ... spring-boot:run`，日志会写到项目根目录 `logs/`
- 如果你在模块目录单独启动服务，日志会写到对应模块下的 `logs/`
- `zh-gateway` 会生成或透传 `X-Trace-Id`
- `zh-auth` 会把 `X-Trace-Id` 放入 MDC，并输出请求完成日志
- 如果想看 Logstash 是否正常消费，可以执行:

```bash
docker compose logs -f logstash
```
