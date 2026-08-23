# Iceberg 与 Hudi 架构

```text
Spark 3.5.5 ---------+--> Iceberg JDBC Catalog --> MySQL 8.4
Flink 1.20.1 --------+             |
Trino 476 -----------+             +--> MinIO /warehouse/iceberg

Spark 3.5.5 ---------+--> Hive Metastore 4.0.1 --> MySQL 8.4
Flink 1.20.1 --------+             |
Trino 476 -----------+             +--> MinIO /warehouse/hudi
```

Iceberg 与 Hudi 共享对象存储和计算引擎，但不共享 Catalog 实现：Iceberg 使用 Iceberg JDBC Catalog；Hudi 使用 Hive Metastore。不要把 Hudi 表注册到 Iceberg JDBC Catalog，也不要让两个表格式共用同一个表目录。

Spark 是最小验证流程中的建表和写入端，Flink 验证 Connector 读取能力，Trino 验证跨引擎查询。Hudi 首先采用 Copy On Write，避免最初部署阶段引入 Merge On Read 的实时视图与 compaction 复杂度。
