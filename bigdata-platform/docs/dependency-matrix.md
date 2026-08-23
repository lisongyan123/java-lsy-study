# 数据湖依赖矩阵

该矩阵是本项目的固定兼容基线，不要只升级其中一个组件。

| 组件 | 固定版本 | Scala/Java | 说明 |
| --- | --- | --- | --- |
| Spark | 3.5.5 | Scala 2.12 / Java 17 | Hudi 1.0.2 不采用原项目的 Spark 4 基线 |
| Flink | 1.20.1 | Scala 2.12 / Java 17 | 使用对应的 Iceberg/Hudi runtime bundle |
| Trino | 476 | Java 22（镜像内置） | Iceberg、Hudi Connector 均由 Trino 镜像内置 |
| Iceberg | 1.8.1 | Spark 3.5 / Flink 1.20 | JDBC Catalog 存储在 MySQL |
| Hudi | 1.0.2 | Spark 3.5 / Flink 1.20 | Hive Metastore 4.0.1 提供跨引擎表发现 |
| Hive Metastore | 4.0.1 | 镜像内置 | MySQL 8.4 保存 HMS 元数据 |
| Hadoop AWS | 3.3.4 | Spark Hadoop 3 基线 | Hudi 访问 MinIO 的 S3A 实现 |

## 必需 JAR

准确文件名、必需性和 Maven Central URL 位于 `config/jars/manifest.tsv`。这些文件放在 `.env` 的 `JAR_ROOT` 外部目录中。Trino 不需要额外下载 Iceberg/Hudi JAR；手动向 Trino插件目录加入不同版本 bundle 会造成类冲突。

Spark 目录：

- `iceberg-spark-runtime-3.5_2.12-1.8.1.jar`
- `iceberg-aws-bundle-1.8.1.jar`
- `hudi-spark3.5-bundle_2.12-1.0.2.jar`
- `hadoop-aws-3.3.4.jar`
- `aws-java-sdk-bundle-1.12.262.jar`

Flink 目录：

- `iceberg-flink-runtime-1.20-1.8.1.jar`
- `iceberg-aws-bundle-1.8.1.jar`
- `hudi-flink1.20-bundle-1.0.2.jar`

`common/mysql-connector-j-9.2.0.jar` 是必需项，供 Hive Metastore 和 Trino Iceberg JDBC Catalog 使用。`common/postgresql-42.7.4.jar` 仅在 Spark 需要访问 PostgreSQL 时使用，基础平台启动不强制要求。

> Maven Central 在线可用性未在当前网络验证。下载电脑上应先确认 URL 和文件大小，再运行校验脚本；不要用 0 字节占位文件代替 JAR。
