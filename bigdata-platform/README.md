# Big Data Platform

可整体复制、声明式部署的本地大数据实验平台。基础环境由 Docker Compose 管理，Kubernetes 基础资源由 Kustomize 管理，不依赖 PowerShell 或其他部署脚本。

## 包含组件

| 组件 | 默认启用 | 用途 |
| --- | --- | --- |
| MinIO | 是 | Warehouse 与 Checkpoint 对象存储 |
| MySQL | 是 | CDC 数据源、Iceberg JDBC Catalog、OpenMetadata 数据库 |
| Hive Metastore | 是 | Hudi 在 Spark、Flink、Trino 之间共享表元数据 |
| PostgreSQL | 是 | JDBC Sink 示例 |
| Kafka | 是 | 流式消息 |
| Spark Master/Worker/SQL | 是 | 批处理与 SQL |
| Flink JobManager/TaskManager/SQL | 是 | 流处理与 CDC |
| Trino | 是 | 统一查询入口 |
| Elasticsearch/OpenMetadata | 否 | `metadata` Profile 下的元数据治理 |
| Spark Operator/History Server | K8S 可选 | Kubernetes 上的 Spark 基础设施 |

## 目录结构

```text
bigdata-platform/
├─ config/jars/          # 外部 JAR 文件清单与已知校验值（不存放 JAR）
├─ config/               # Spark、Flink 与 Trino 运行配置
├─ docs/                 # 架构、配置、运维和迁移说明
├─ examples/             # Spark、Flink、Trino SQL 示例
├─ init/mysql/           # MySQL 首次初始化 SQL
├─ k8s/                  # Kustomize 一键部署入口
├─ .env                  # 当前环境参数
├─ .env.example          # 可复制的配置模板
├─ compose.paimon-real.yml
└─ docker-compose.yml    # Compose 唯一主入口
```

项目配置挂载使用相对路径，JAR 挂载使用 `.env` 中的 `JAR_ROOT`。因此迁移时不需要复制 `artifacts/`，只需在目标服务器准备外部 JAR 目录。

## 一键部署

前置条件：Docker Desktop 或 Docker Engine 已启动，并支持 Docker Compose v2。先按 `config/jars/manifest.tsv` 在目标服务器准备外部 JAR；本项目不会在启动时联网下载 JAR。

```powershell
Copy-Item .env.example .env
# 将 JAR_ROOT 改为目标服务器上的绝对路径，再修改密码、镜像和端口。
.\scripts\start-platform.ps1 -PullImages
```

Linux 使用 `chmod +x scripts/*.sh` 后执行 `./scripts/start-platform.sh --pull-images`。不要绕过启动脚本直接启动；脚本会先检查全部必需 JAR、哈希和 Compose 配置。

查看状态：

```powershell
docker compose ps
docker compose logs --tail 100
```

启用 OpenMetadata：

```powershell
docker compose --profile metadata up -d --wait
```

复制到其他电脑后，推荐在项目根目录执行 `scripts/start-platform.ps1 -PullImages`；镜像已离线导入时省略 `-PullImages`。详细步骤见 `docs/migration.md`。

首次启用时，`openmetadata-migrate` 会先幂等创建或升级 MySQL 表结构，成功退出后才启动 OpenMetadata Server。

停止平台：

```powershell
docker compose down
```

停止并清除所有 Compose 数据卷：

```powershell
docker compose down -v
```

> `down -v` 会永久删除 MinIO、MySQL、PostgreSQL、Kafka 和 Elasticsearch 数据。

## 服务入口

端口均可在 `.env` 中修改。

| 服务 | 默认地址 |
| --- | --- |
| MinIO API | http://localhost:9000 |
| MinIO Console | http://localhost:9001 |
| Spark Master UI | http://localhost:8080 |
| Spark Worker UI | http://localhost:8081 |
| Flink UI | http://localhost:8082 |
| Trino | http://localhost:8088 |
| Hive Metastore | thrift://localhost:9083 |
| OpenMetadata | http://localhost:8585 |
| Elasticsearch | http://localhost:9200 |
| MySQL | localhost:3306 |
| PostgreSQL | localhost:5433 |
| Kafka | localhost:29092 |

## JAR 管理

JAR 不再要求存放或迁移到项目的 `artifacts/`。Compose 从 `.env` 的 `JAR_ROOT` 只读挂载，目录结构固定如下：

```text
JAR_ROOT/
├─ common/
├─ spark/
└─ flink/
```

精确文件名、用途、必需性和下载 URL 位于 `config/jars/manifest.tsv`，已知哈希位于 `config/jars/checksums.sha256`。完整清单和服务器部署步骤见 `docs/external-jars-deployment.md`。准备后执行：

```powershell
.\scripts\verify-offline-jars.ps1 -JarRoot D:\bigdata-jars
```

校验失败时不要启动 Spark/Flink 数据湖任务，也不要使用 0 B 占位 JAR。

## 示例 SQL

Spark Iceberg：

```powershell
docker compose exec spark-sql /opt/spark/bin/spark-sql --properties-file /workspace/config/spark/iceberg.conf -f /workspace/sql/bootstrap.sql
```

Spark Hudi（建表、写入并同步 Hive Metastore）：

```powershell
docker compose exec spark-sql /opt/spark/bin/spark-sql -f /workspace/sql/hudi_min_verify.sql
```

Spark Delta：

```powershell
docker compose exec spark-sql /opt/spark/bin/spark-sql -f /workspace/sql/delta_min_verify.sql
```

Flink SQL Client：

```powershell
docker compose exec flink-sql-client /opt/flink/bin/sql-client.sh
```

进入客户端后执行：

```sql
SOURCE '/workspace/sql/iceberg_min_verify.sql';
SOURCE '/workspace/sql/hudi_min_verify.sql';
```

Trino 示例：

```powershell
docker compose exec trino trino -f /workspace/sql/iceberg_ops.sql
```

跨引擎验证：

```powershell
docker compose exec trino trino -f /workspace/sql/iceberg_cross_engine.sql
docker compose exec trino trino -f /workspace/sql/hudi_min_verify.sql
```

推荐顺序是 Spark 写 Iceberg、Spark 写 Hudi、Flink 读取、Trino 读取。Iceberg 使用 JDBC Catalog，Hudi 使用 Hive Metastore，两者不能互换。

Trino 固定为 476，并配置 2 GB JVM 堆、查询内存上限、较长启动健康检查和优雅停止。不要切换回 `latest`；Catalog 中的未知属性或空 Kafka 表描述会导致 Trino 在启动阶段整体退出。

## Paimon 配置

默认 `paimon` catalog 使用 memory connector，确保没有 Paimon 插件时 Trino 仍可启动。

启用真实 Paimon 配置时，需要先使用包含 Paimon Connector 的 Trino 镜像，然后执行：

```powershell
docker compose -f docker-compose.yml -f compose.paimon-real.yml up -d --force-recreate trino
```

## Kubernetes 一键部署

当前 K8S 上下文必须已经指向目标集群：

```powershell
kubectl config current-context
kubectl apply -k k8s
```

该入口部署：

- 平台示例 Namespace、ConfigMap、Secret 占位配置
- Spark Driver RBAC
- Spark History Server 与 event log PVC
- Spark Operator CRD、RBAC 和 Controller

检查状态：

```powershell
kubectl get deployment,pods -n spark-operator
kubectl get deployment,service,pvc,pods -n bigdata-platform
kubectl get crd sparkapplications.sparkoperator.k8s.io scheduledsparkapplications.sparkoperator.k8s.io
```

Ingress 示例默认不部署，因为它依赖目标集群的 IngressClass 和真实业务 Service。

验证原生配置、服务发现与网关链路会部署一个最小的 `orders` 示例服务：

```powershell
kubectl apply -f k8s/platform-services/examples/verification-demo.yaml
kubectl rollout status deployment/orders-demo -n platform-services
kubectl get service,endpointslice -n platform-services
```

Ingress 需要按目标集群修改并单独应用 `k8s/platform-services/examples/nginx-ingress.yaml`。

## 修改配置

- 镜像、端口、资源和本地凭据：`.env`
- Spark：`config/spark/spark-defaults.conf`
- Trino：`config/trino/`
- MySQL 首次初始化：`init/mysql/`
- Compose 服务：`docker-compose.yml`
- K8S：`k8s/`

MySQL 初始化 SQL 只会在 `mysql-data` 卷首次创建时执行。修改初始化 SQL 后，如需在测试环境重建，必须执行 `docker compose down -v` 后重新部署；生产数据禁止这样处理。

## 进一步阅读

- `docs/architecture.md`：组件拓扑和依赖关系
- `docs/configuration.md`：配置修改规则
- `docs/operations.md`：部署、检查、故障排查和迁移步骤
- `docs/dependency-matrix.md`：Iceberg/Hudi 固定版本和必需 JAR
- `docs/migration.md`：Hive/HMS 迁移状态
- `docs/external-jars-deployment.md`：外部 JAR 完整清单和部署检查表
