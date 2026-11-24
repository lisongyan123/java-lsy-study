# maven-quarantine-jars

一些第三方依赖在私有 Maven 仓库被 Sonatype 防火墙组件隔离（HTTP 403 quarantined），此处为从
[Maven Central](https://repo1.maven.org/maven2/) 公网下载并经 SHA1 校验的备份，与官方发布字节一致。

## 用途

开发机拉不到这些构件时，从这里取回并装入本地 Maven 仓库：

```powershell
# 方式一：目录结构与 .m2/repository 一致，直接合并拷入
robocopy .\maven-quarantine-jars "$env:USERPROFILE\.m2\repository" /E

# 方式二：逐个安装
mvn install:install-file -Dfile=spring-jms-5.3.28.jar -DpomFile=spring-jms-5.3.28.pom
```

单文件 raw 下载链接格式：

```
https://github.com/lisongyan123/java-lsy-study/raw/master/maven-quarantine-jars/<group路径>/<artifact>/<version>/<文件名>
```

> 注意：本仓库 `.gitignore` 含 `*.jar`，向本目录添加新 jar 时需要 `git add -f`。

## 构件清单（2026-11-24）

| 构件 | 版本 | 备注 |
|---|---|---|
| org.springframework:spring-jms | 5.3.28 | |
| org.springframework.boot:spring-boot-autoconfigure | 2.7.13 | |
| org.apache.tomcat.embed:tomcat-embed-core | 9.0.118 | |
| org.codehaus.plexus:plexus-utils | 1.1 / 1.4.6 / 1.5.12 | Maven 插件传递依赖 |
| com.fasterxml.jackson.core:jackson-databind | 2.4.2 / 2.8.11.3 | 旧版本含已知 CVE，这也是被隔离的常见原因 |
| org.freemarker:freemarker | 2.3.32 | |
| org.springframework:spring-beans | 4.3.22.RELEASE | |
| dom4j:dom4j | 1.4 | 2003 年老版本；其 pom.sha1 是 ibiblio 时代的带路径格式，并非校验失败 |

## 长期建议

旧版本多因已知漏洞被防火墙拦截，本地安装只是让开发机能完成构建，CI 与他人环境仍会被拦，后续应推动升级到放行版本。
