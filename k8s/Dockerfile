# 基于你原有的 Bitnami ES 镜像
FROM bitnamilegacy/elasticsearch:9.1.2-debian-12-r0

# 下载对应版本的 IK 分词器并安装
# 注意：请确保该版本存在，如果不存在，请将 ES 降级到 8.x 或 7.x 版本
RUN curl -L -o /tmp/elasticsearch-analysis-ik-9.1.2.zip \
    https://release.infinilabs.com/analysis-ik/stable/elasticsearch-analysis-ik-9.1.2.zip && \
    mkdir -p /opt/bitnami/elasticsearch/plugins/analysis-ik && \
    unzip /tmp/elasticsearch-analysis-ik-9.1.2.zip -d /opt/bitnami/elasticsearch/plugins/analysis-ik && \
    rm -f /tmp/elasticsearch-analysis-ik-9.1.2.zip


