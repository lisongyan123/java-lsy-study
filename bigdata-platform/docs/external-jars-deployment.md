# 外部 JAR 与服务器部署清单

本方案允许迁移 `bigdata-platform` 时完全排除 `artifacts/`。Compose 通过 `.env` 的 `JAR_ROOT` 挂载目标服务器上单独准备的 JAR。

## 1. 外部目录结构

Linux 示例：`JAR_ROOT=/opt/bigdata-jars`；Windows 示例：`JAR_ROOT=D:/bigdata-jars`。

```text
JAR_ROOT/
├─ common/
│  ├─ mysql-connector-j-9.2.0.jar
│  └─ postgresql-42.7.4.jar                 # 可选
├─ spark/
│  ├─ iceberg-spark-runtime-3.5_2.12-1.8.1.jar
│  ├─ iceberg-aws-bundle-1.8.1.jar
│  ├─ hudi-spark3.5-bundle_2.12-1.0.2.jar
│  ├─ hadoop-aws-3.3.4.jar
│  └─ aws-java-sdk-bundle-1.12.262.jar
└─ flink/
   ├─ iceberg-flink-runtime-1.20-1.8.1.jar
   ├─ iceberg-aws-bundle-1.8.1.jar
   └─ hudi-flink1.20-bundle-1.0.2.jar
```

不要放入旧的 0 字节 `mysql-connector-j-8.4.0.jar`，也不要把 Spark/Flink bundle 放入 Trino 插件目录。

## 2. JAR 清单

| 必需 | 相对路径 | 版本 | 用途 |
| --- | --- | --- | --- |
| 是 | `common/mysql-connector-j-9.2.0.jar` | 9.2.0 | Hive Metastore、Trino Iceberg JDBC Catalog 访问 MySQL |
| 否 | `common/postgresql-42.7.4.jar` | 42.7.4 | Spark 访问 PostgreSQL；基础平台不依赖 |
| 是 | `spark/iceberg-spark-runtime-3.5_2.12-1.8.1.jar` | 1.8.1 | Spark Iceberg Runtime |
| 是 | `spark/iceberg-aws-bundle-1.8.1.jar` | 1.8.1 | Spark Iceberg S3 FileIO |
| 是 | `spark/hudi-spark3.5-bundle_2.12-1.0.2.jar` | 1.0.2 | Spark Hudi Runtime |
| 是 | `spark/hadoop-aws-3.3.4.jar` | 3.3.4 | Spark S3A 文件系统 |
| 是 | `spark/aws-java-sdk-bundle-1.12.262.jar` | 1.12.262 | Spark S3A AWS SDK |
| 是 | `flink/iceberg-flink-runtime-1.20-1.8.1.jar` | 1.8.1 | Flink Iceberg Runtime |
| 是 | `flink/iceberg-aws-bundle-1.8.1.jar` | 1.8.1 | Flink Iceberg S3 FileIO |
| 是 | `flink/hudi-flink1.20-bundle-1.0.2.jar` | 1.0.2 | Flink Hudi Runtime |

机器可联网时，可以用 `scripts/download-offline-jars.ps1 -JarRoot <目录>` 或 `scripts/download-offline-jars.sh <目录>` 下载。精确 URL 和机器可读清单位于 `config/jars/manifest.tsv`。

目前 `config/jars/checksums.sha256` 只记录两个已验证 JDBC 驱动的 SHA-256。其他 JAR 从可信源准备后，应在交付服务器前计算 SHA-256 并追加到该文件；校验脚本会自动校验其中已有的条目。

## 3. 项目迁移

- [ ] 复制项目时排除整个 `artifacts/`。
- [ ] 保留隐藏文件 `.env.example`；不要把开发机 `.env` 中的默认密码直接用于生产。
- [ ] 在目标服务器复制 `.env.example` 为 `.env`。
- [ ] 将 `JAR_ROOT` 设置为外部 JAR 绝对路径。
- [ ] 修改全部 `change-me` 密码、`KAFKA_ADVERTISED_HOST`、端口和镜像地址。
- [ ] 确保 Docker 守护进程用户可读取 `JAR_ROOT` 下全部文件和目录。
- [ ] 确保 Docker Engine 与 Docker Compose v2 已安装，磁盘和内存满足所有服务需求。

## 4. 镜像清单

联网服务器由启动脚本拉取；离线服务器需要提前 `docker save`/`docker load` 以下固定基线：

- MinIO Server 与 MinIO Client
- MySQL 8.4
- PostgreSQL 16 Alpine
- Kafka 4.1.0
- Spark 3.5.5
- Trino 476
- Flink 1.20.1 Scala 2.12 Java 17
- Hive Metastore 4.0.1
- Elasticsearch 8.13.4
- OpenMetadata 1.5.9

实际镜像名以目标 `.env` 为准。离线环境启动时不要使用拉取参数。

## 5. 启动

### Linux

```bash
chmod +x scripts/*.sh
./scripts/verify-offline-jars.sh
./scripts/start-platform.sh --pull-images
```

若镜像已离线导入，执行 `./scripts/start-platform.sh`。只部署基础数据平台、不部署 Elasticsearch/OpenMetadata 时加 `--skip-metadata`。

### Windows

```powershell
.\scripts\verify-offline-jars.ps1
.\scripts\start-platform.ps1 -PullImages
```

若镜像已离线导入，省略 `-PullImages`。也可临时用 `-JarRoot D:\bigdata-jars` 覆盖 `.env`。

启动脚本按顺序执行：检查 Docker、校验 9 个必需 JAR、校验已有 SHA-256、检查 Compose 配置、可选拉取镜像、启动并等待健康检查、输出全部容器状态。任一步失败都会停止。

## 6. 部署后验收

- [ ] `docker compose ps -a` 中长期服务为 `healthy`/`running`，初始化和迁移服务为成功退出。
- [ ] MinIO Console、Spark Master/Worker UI、Flink UI、Trino 可访问。
- [ ] 启用 metadata Profile 时，OpenMetadata 和 Elasticsearch 健康。
- [ ] 执行 Spark Iceberg 建表写入：`docker compose exec spark-sql /opt/spark/bin/spark-sql --properties-file /workspace/config/spark/iceberg.conf -f /workspace/sql/bootstrap.sql`。
- [ ] 执行 Spark Hudi 示例：`docker compose exec spark-sql /opt/spark/bin/spark-sql -f /workspace/sql/hudi_min_verify.sql`。
- [ ] 在 Flink SQL Client 中执行 Iceberg、Hudi 最小读取示例。
- [ ] 执行 Trino 跨引擎验证：`docker compose exec trino trino -f /workspace/sql/iceberg_cross_engine.sql` 和 `docker compose exec trino trino -f /workspace/sql/hudi_min_verify.sql`。
- [ ] 查看异常日志：`docker compose logs --tail 200 <服务名>`。

## 7. 范围说明

默认 Compose 中的 `paimon` 是 memory catalog。真实 Paimon 需要另外准备兼容 Trino 476 Paimon 插件的自定义镜像，并设置 `TRINO_PAIMON_IMAGE` 后叠加 `compose.paimon-real.yml`；它不属于上述通用 JAR 目录。

`k8s/` 当前部署 Spark Operator、History Server 和平台服务发现示例，不等同于 Compose 全平台。Kubernetes Spark 示例还要求提前构建并推送包含运行依赖的 Spark Lakehouse 镜像。
