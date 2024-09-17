#!/bin/bash

PLAY_DIR=taitra-1.0


if [ -n "$SECRET_KEY" ] && [ -n "$DB_URL" ] && [ -n "$DB_USERNAME" ] && [ -n "$DB_PASSWORD" ]; then
  echo "所有環境變數已設置，生成配置文件..."
  ./config.sh /home/ubuntu/taitra-1.0/conf/application.conf
else
  echo "環境變數未完全設置，跳過生成配置文件步驟。"
fi



# Safely kill the process if RUNNING_PID exists and contains a valid PID
if [ -f "$PLAY_DIR/RUNNING_PID" ]; then
  cat $PLAY_DIR/RUNNING_PID | xargs kill -9
  rm $PLAY_DIR/RUNNING_PID
else
  echo "RUNNING_PID 文件不存在，無需刪除。"
fi

# Start the Play application
exec $PLAY_DIR/bin/taitra \
  -Dfile.encoding=UTF-8 \
  -Duser.timezone=Asia/Taipei \
  -Dhttp.port=9090 \
  -Dconfig.file=/home/ubuntu/taitra-1.0/conf/application.conf \
  -Dlogger.file=/home/ubuntu/taitra-1.0/conf/logback.xml
  >> /dev/null 2>&1 &