# Flink 数据湖配置

Flink 1.20.1 同时加载 Iceberg 1.8.1 与 Hudi 1.0.2 的离线 bundle。容器启动时将 `${JAR_ROOT}/flink/` 和 `${JAR_ROOT}/common/` 中的文件复制到 `/opt/flink/lib/`。

- `iceberg-catalog.sql.template`：Iceberg JDBC Catalog 模板，与 Spark、Trino 共用 MySQL Catalog。
- `hive-site.xml`：Hudi Hive Sync 使用的 Hive Metastore 地址。
- `examples/flink/iceberg_min_verify.sql`：读取 Spark 创建的 Iceberg 表。
- `examples/flink/hudi_min_verify.sql`：通过 Hudi Connector 读取同一路径并同步 HMS。

配置文件目前使用 `.env` 的本地开发默认凭据。修改 MinIO 或 MySQL 凭据时，必须同步修改 SQL 模板；Flink SQL 不会自动展开 Compose 环境变量。
