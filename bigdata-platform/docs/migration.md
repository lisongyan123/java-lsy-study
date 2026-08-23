# 整体迁移与启动

## 必须复制的内容

复制整个 `bigdata-platform` 目录，不能只复制 Compose 文件。必须保留：

- `.env` 和 `.env.example`
- `config/`、`init/`、`examples/`、`scripts/`
- `docker-compose.yml`

`artifacts/` 不需要复制。JAR 在目标服务器单独放置，并通过 `.env` 的 `JAR_ROOT` 指向；目录中必须包含 `common/`、`spark/`、`flink/` 三个子目录。

## 新电脑首次启动

1. 安装并启动 Docker Desktop 或 Docker Engine，确认支持 Docker Compose v2。
2. 复制 `.env.example` 为 `.env`，把 `JAR_ROOT` 改为目标服务器上的外部绝对路径并更换密码。
3. 按 `config/jars/manifest.tsv` 准备 9 个必需 JAR；PostgreSQL JDBC 驱动为可选项。
4. Windows 运行 `scripts/verify-offline-jars.ps1`；Linux 运行 `scripts/verify-offline-jars.sh`。
5. 联网 Windows 运行 `scripts/start-platform.ps1 -PullImages`；联网 Linux 运行 `scripts/start-platform.sh --pull-images`。
6. 镜像已经离线导入时省略拉取参数。

脚本默认同时部署 OpenMetadata 和 Elasticsearch。只启动基础数据平台可增加 `-SkipMetadata`。

启动过程会自动完成 MinIO Bucket、Iceberg JDBC Catalog 表、Hive Metastore Schema 和 OpenMetadata 数据库迁移。

## 离线镜像

目标电脑不能访问镜像仓库时，应在有镜像的电脑上使用 `docker save` 导出 `.env` 中声明的所有固定版本镜像，将镜像包复制到目标电脑后使用 `docker load` 导入。导入完成后不要使用 `-PullImages`。

## 数据迁移边界

复制项目目录只迁移配置、SQL 和 JAR，不会迁移 Docker Named Volume 中的业务数据。当前 MinIO、MySQL、PostgreSQL、Kafka、Elasticsearch 数据分别位于 Compose Named Volume 中。

- 只需要全新环境：直接启动，初始化服务会创建空数据库和 Bucket。
- 需要保留现有 Iceberg/Hudi/OpenMetadata 数据：必须同时备份并恢复 MinIO 与 MySQL；只迁移其中一个会导致对象文件和 Catalog 元数据不一致。
- 不要直接复制正在运行的 Docker Volume 文件。应先停止写入并使用数据库备份、MinIO 镜像同步或经过验证的 Volume 备份流程。

## 验证

启动后检查所有服务状态，并验证：

- Trino `SHOW CATALOGS`
- Trino `SHOW SCHEMAS FROM iceberg`
- Trino `SHOW SCHEMAS FROM hudi`
- OpenMetadata `http://localhost:8585/api/v1/system/version`
- Spark、Flink Web UI

完整迁移检查清单和 JAR 文件名见 `docs/external-jars-deployment.md`。
