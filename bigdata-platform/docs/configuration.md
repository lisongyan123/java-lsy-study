# 数据湖配置规则

## 固定版本

Spark、Flink、Iceberg、Hudi bundle 必须按 `dependency-matrix.md` 成组升级。Hudi 1.0.2 的本项目基线是 Spark 3.5/Scala 2.12 和 Flink 1.20，因此不能把 `.env` 改回 Spark 4 或 Flink 2 后继续复用当前 JAR。

## Catalog

- Iceberg：Spark、Flink、Trino 连接 MySQL 中的 `iceberg_catalog` JDBC Catalog，数据位于 `s3://warehouse/iceberg`。
- Hudi：Spark、Flink、Trino 连接 `thrift://hive-metastore:9083`，数据位于 `s3a://warehouse/hudi`。
- MinIO：S3FileIO 使用 `s3://`；Hudi/Hadoop S3A 使用 `s3a://`。

## 凭据

Trino 配置支持 `${ENV:...}`，会直接读取 Compose 环境变量。Spark properties 和 Flink SQL 模板不会展开 Compose 环境变量；修改 `.env` 中的 MinIO、Iceberg 用户密码后，必须同步更新：

- `config/spark/spark-defaults.conf`
- `config/flink/iceberg-catalog.sql.template`
- `examples/flink/iceberg_min_verify.sql`

生产环境应改用 Secret 注入或启动时渲染配置，不应提交真实凭据。

## 已存在的 MySQL 数据卷

`catalog-db-init` 每次部署都会幂等创建 Iceberg Catalog 与 Hive Metastore 数据库，因此不要求删除现有 MySQL 卷。严禁为应用配置变更在生产环境执行 `docker compose down -v`。
