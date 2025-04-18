FROM ioyutw/openjdk17
# 创建一个临时挂载点
VOLUME /tmp

# 设置工作目录
WORKDIR /app

# 复制 JAR 文件到容器中
ARG JAR_FILE
COPY ${JAR_FILE} /app/lsy-java-boot.jar

# 触摸 JAR 文件以更新时间戳
RUN touch /app/lsy-java-boot.jar

# 设置容器启动命令
ENTRYPOINT ["java", "-jar", "/app/lsy-java-boot.jar"]

# 暴露端口
EXPOSE 8080

# 添加健康检查
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1










