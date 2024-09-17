# 設置 OpenJDK 和 SBT 版本變數
ARG OPENJDK_TAG=11
ARG SBT_VERSION=1.7.2

# 使用指定版本的 OpenJDK 作為基礎映像
FROM openjdk:${OPENJDK_TAG} as BUILDER

# 將專案文件複製到映像中
COPY . /TAITAR

# 設定工作目錄
WORKDIR /TAITAR

# 安裝必要的依賴，包括 curl 以下載 SBT
RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list &&\
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x99E82A75642AC823" | apt-key add - &&\
    apt-get update && \
    apt-get install sbt=1.7.2




# 編譯專案並打包
RUN sbt -Dsbt.rootdir=true compile dist

# 多階段構建：使用更小的映像來運行應用
FROM openjdk:${OPENJDK_TAG}

# 設定工作目錄
WORKDIR /home/ubuntu/

# 從構建階段複製編譯後的文件
COPY --from=BUILDER /TAITAR/target/universal/taitra-1.0.zip /home/ubuntu/
COPY --from=BUILDER /TAITAR/start.sh /home/ubuntu/start.sh
COPY --from=BUILDER /TAITAR/config.sh /home/ubuntu/config.sh

# 開放 API 的端口
EXPOSE 9090

# 接收構建階段參數
ARG SECRET_KEY
ARG DB_URL
ARG DB_USERNAME
ARG DB_PASSWORD

# 將構建階段參數設置為環境變數
ENV SECRET_KEY=${SECRET_KEY}
ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}

# 授予腳本執行權限
RUN chmod +x /home/ubuntu/start.sh /home/ubuntu/config.sh

# 解壓打包的應用
RUN unzip /home/ubuntu/taitra-1.0.zip
RUN /home/ubuntu/config.sh ./taitra-1.0/conf/application.conf
# # 設置容器的啟動指令
ENTRYPOINT ["/home/ubuntu/start.sh"]

