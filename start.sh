#!/bin/bash

PLAY_DIR=/home/ubuntu/taitra-1.0
CONFIG_FILE=$PLAY_DIR/config/application.conf



if [-n "$SECRET_KEY"] && [ -n "$DB_URL" ] && [ -n "$DB_USERNAME" ] && [ -n "$DB_PASSWORD" ]; then
  echo "所有環境變數已設置，生成配置文件..."
  ./config.sh $CONFIG_FILE
else
  echo "環境變數未完全設置，跳過生成配置文件步驟。"
fi


cd $PLAY_DIR
cat $PLAY_DIR/RUNNING_PID | xargs kill -9
rm $PLAY_DIR/RUNNING_PID
exec $PLAY_DIR/bin/taitra -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Taipei -Dhttp.port=9090 -Dconfig.file=$CONFIG_FILE -Dlogger.file=$PLAY_DIR/conf/logback.xml >> /dev/null &
