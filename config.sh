#!/bin/bash

# 檢查是否傳入了配置文件參數
if [ -z "$1" ]; then
  echo "Error: 必須提供配置文件路徑作為參數"
  exit 1
fi

CONFIG_FILE=$1

# 檢查是否已設置環境變數
if [ -z "$DB_URL" ] ||[ -z "$SECRET_KEY" ] ||[ -z "$DB_USERNAME" ] || [ -z "$DB_PASSWORD" ]; then
  echo "Error: 必須設置 DB_URL、DB_USERNAME 和 DB_PASSWORD 環境變數"
  exit 1
fi

# 生成指定路徑的 application.conf 並替換相關的參數
cat <<EOF > $CONFIG_FILE
# This is the main configuration file for the application.
# ~~~~~

# Secret key
# ~~~~~
# The secret key is used to secure cryptographics functions.
# If you deploy your application to several instances be sure to use the same key!
#application.secret="%APPLICATION_SECRET%"

# The application languages
# ~~~~~
#application.langs="en"

play.http.parser.maxDiskBuffer = 100MB

parsers.anyContent.maxLength = 100MB

play.http.secret.key="$SECRET_KEY"

play.i18n {
  # The application languages
  langs = [ "zh-TW" ]

  # Whether the language cookie should be secure or not
  #langCookieSecure = true

  # Whether the HTTP only attribute of the cookie should be set to true
  #langCookieHttpOnly = true
}

play.filters.hosts {
  allowed = ["."] 
}

play.http.session.httpOnly=true
play.http.session.secure=false

# Global object class
# ~~~~~
# Define the Global object class for this application.
# Default to Global in the root package.
# application.global=Global

# Router
# ~~~~~
# Define the Router object to use for this application.
# This router will be looked up first when the application is starting up,
# so make sure this is the entry point.
# Furthermore, it's assumed your route file is named properly.
# So for an application router like my.application.Router,
# you may need to define a router file conf/my.application.routes.
# Default to Routes in the root package (and conf/routes)
# application.router=my.application.Routes

# Database configuration
# ~~~~~
# You can declare as many datasources as you want.
# By convention, the default datasource is named default

play.http.parser.maxMemoryBuffer = 500MB

#Ebean
ebean.default = ["models.*"]

db.default.driver=org.postgresql.Driver
db.default.url = "$DB_URL"
db.default.username = "$DB_USERNAME"
db.default.password = "$DB_PASSWORD"
#db.default.maxConnectionsPerPartition=1
#db.default.minConnectionsPerPartition=1

# Evolutions
# ~~~~~
# You can disable evolutions if needed
# evolutionplugin=disabled

play.evolutions.enabled=false
play.evolutions.autoApply=false
play.evolutions.autoApplyDowns=false

# Logger
# ~~~~~
# You can also configure logback (http://logback.qos.ch/),
# by providing an application-logger.xml file in the conf directory.

# Root logger:
logger.root=ERROR

# Logger used by the framework:
logger.play=INFO

# Logger provided to your application:
logger.application=DEBUG

play.filters.enabled=[]
play.filters.enabled += "play.filters.cors.CORSFilter"
play.filters {
  ## CORS filter configuration
  cors.CORSFilter {
    allowedOrigins = ["*"]
    allowedHttpMethods = ["GET", "POST"]
    allowedHttpHeaders = ["Accept"]
  }
  ## Security headers filter configuration
  headers {
    contentSecurityPolicy = "default-src 'self'; script-src 'unsafe-inline'"
  }
}
EOF

# 檢查文件是否成功生成
if [ $? -eq 0 ]; then
  echo "$CONFIG_FILE 已生成並更新成功"
else
  echo "Error: 生成 $CONFIG_FILE 失敗"
  exit 1
fi