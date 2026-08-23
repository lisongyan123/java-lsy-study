# Platform Services 示例

本目录不包含在默认 `kubectl apply -k k8s` 部署中。

- `verification-demo.yaml`：部署 `orders` 示例 Deployment 与 Service，用于验证 ConfigMap、Secret、Service 和 EndpointSlice。
- `nginx-ingress.yaml`：Ingress 示例；使用前必须确认集群已有 Ingress Controller，并修改 IngressClass 与后端 Service。

部署验证服务：

```powershell
kubectl apply -f k8s/platform-services/examples/verification-demo.yaml
kubectl rollout status deployment/orders-demo -n platform-services
kubectl get service,endpointslice -n platform-services
```

如需 Ingress，再单独应用并按目标环境修改：

```powershell
kubectl apply -f k8s/platform-services/examples/nginx-ingress.yaml
```

## 通过 Kubernetes API 修改并验证配置

在一个 PowerShell 窗口运行 `kubectl proxy --port=8001`，然后在另一个窗口执行以下命令，将 `FEATURE_X_ENABLED` 改为 `true`：

也可以直接执行一键脚本；它会修改 Kubernetes API 中的 ConfigMap、输出修改后的值、重启示例服务，并等待网关 API 返回相同的新值：

```powershell
.\k8s\platform-services\examples\set-demo-config.ps1 -Key FEATURE_X_ENABLED -Value true
```

脚本接受任意环境变量风格的键名，例如 `ORDER_TIMEOUT_SECONDS`。新增字段会写入 ConfigMap，并在重启后自动出现在 `/orders/config` 响应中，无需修改 Nginx 模板。手动调用 API 的等价写法如下：

```powershell
$apiBase = 'http://127.0.0.1:8001'
$patch = @{ data = @{ FEATURE_X_ENABLED = 'true' } } | ConvertTo-Json -Compress

Invoke-RestMethod `
	-Uri "$apiBase/api/v1/namespaces/platform-services/configmaps/platform-config" `
	-Method Patch `
	-ContentType 'application/merge-patch+json' `
	-Body $patch
```

验证 API 中的 ConfigMap 值：

```powershell
(Invoke-RestMethod "$apiBase/api/v1/namespaces/platform-services/configmaps/platform-config").data
```

示例服务通过环境变量读取配置，需要重启后才会生效：

```powershell
kubectl rollout restart deployment/orders-demo -n platform-services
kubectl rollout status deployment/orders-demo -n platform-services
Invoke-RestMethod http://localhost:30080/orders/config
```

最终响应应包含刚修改的同名字段，例如 `ORDER_TIMEOUT_SECONDS=30`。该响应同时验证了 ConfigMap → Pod → Service/EndpointSlice → Ingress 的完整链路。

## 批量新增字段

`config-batch.example.json` 提供了 100 个字段的 JSON 模板。复制后修改键和值，再用同一个脚本批量提交：

```powershell
Copy-Item k8s/platform-services/examples/config-batch.example.json .\my-config.json
# 编辑 .\my-config.json 后执行：
.\k8s\platform-services\examples\set-demo-config.ps1 -ConfigFile .\my-config.json
```

脚本会使用一次 Kubernetes API `PATCH` 提交所有字段，并等待网关 API 同时返回每个字段的新值。
