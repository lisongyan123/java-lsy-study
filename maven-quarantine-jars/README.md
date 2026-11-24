# maven-quarantine-jars

被私有仓库防火墙隔离（403）的 Maven 依赖备份，全部平铺在本目录，均从
[Maven Central](https://repo1.maven.org/maven2/) 下载并经 SHA1 校验。

点对应 jar → Raw 按钮即可直接下载。

## 文件与坐标

| jar | Maven 坐标 |
|---|---|
| spring-jms-5.3.28.jar | org.springframework:spring-jms:5.3.28 |
| spring-boot-autoconfigure-2.7.13.jar | org.springframework.boot:spring-boot-autoconfigure:2.7.13 |
| tomcat-embed-core-9.0.118.jar | org.apache.tomcat.embed:tomcat-embed-core:9.0.118 |
| plexus-utils-1.1.jar | org.codehaus.plexus:plexus-utils:1.1 |
| plexus-utils-1.4.6.jar | org.codehaus.plexus:plexus-utils:1.4.6 |
| plexus-utils-1.5.12.jar | org.codehaus.plexus:plexus-utils:1.5.12 |
| jackson-databind-2.4.2.jar | com.fasterxml.jackson.core:jackson-databind:2.4.2 |
| jackson-databind-2.8.11.3.jar | com.fasterxml.jackson.core:jackson-databind:2.8.11.3 |
| freemarker-2.3.32.jar | org.freemarker:freemarker:2.3.32 |
| spring-beans-4.3.22.RELEASE.jar | org.springframework:spring-beans:4.3.22.RELEASE |
| dom4j-1.4.jar | dom4j:dom4j:1.4 |

## 装入本地仓库

```powershell
mvn install:install-file -Dfile=<jar文件名> -DgroupId=<groupId> -DartifactId=<artifactId> -Dversion=<version> -Dpackaging=jar
```

对应的 `.pom` 文件按坐标去 Maven Central 同路径取（`https://repo1.maven.org/maven2/<group>/<artifact>/<version>/`）。

> 本仓库 `.gitignore` 含 `*.jar`，往本目录加新 jar 需要 `git add -f`。
