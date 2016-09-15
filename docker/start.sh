#!/bin/bash

echo "Welcome to play Slick Liquibase and Docker demo."

if [ -z "$DB_HOST" -a "${DB_HOST+xxx}" = "xxx" ]; then
    echo DB_HOST is set but empty;
    DB_HOST=127.0.0.1
fi

if [ -z "$DB_PORT" -a "${DB_PORT+xxx}" = "xxx" ]; then
    echo DB_PORT is set but empty;
    DB_PORT=3306
fi

if [ -z "$DB_NAME" -a "${DB_NAME+xxx}" = "xxx" ]; then
    echo DB_NAME is set but empty;
    DB_NAME=sampledb
fi
if [ -z "$ONLINE_SCHEMA_CHANGE" -a "${ONLINE_SCHEMA_CHANGE+xxx}" = "xxx" ]; then
    echo ONLINE_SCHEMA_CHANGE is set but empty;
    ONLINE_SCHEMA_CHANGE=false
fi

if [ -z "$DB_USER" -a "${DB_USER+xxx}" = "xxx" ]; then
    echo DB_USER is set but empty;
    DB_USER=root
fi
if [ -z "$DB_PASSWORD" -a "${DB_PASSWORD+xxx}" = "xxx" ]; then
    echo DB_PASSWORD is set but empty;
    DB_PASSWORD=1q2w3e4r5t
fi
APP_HOME="/opt/app"
JDBC_URL="jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?noAccessToProcedureBodies=true&createDatabaseIfNotExist=true&useUnicode=yes&characterEncoding=UTF-8&connectTimeout=30000"
DB_DRIVER=com.mysql.jdbc.Driver


if [ $ONLINE_SCHEMA_CHANGE = "true" ]; then
    echo 'Performing Online Schema Change'
    cd ${APP_HOME}/

    echo 'liquibase is performaning schema update'
    java -jar lib/org.liquibase.liquibase-core-3.1.1.jar \
         --driver=com.mysql.jdbc.Driver \
         --changeLogFile=migration/master.xml \
         --classpath=.:./lib/mysql.mysql-connector-java-5.1.31.jar \
         --url=$JDBC_URL \
         --username=$DB_USER \
         --password=$DB_PASSWORD \
         --logLevel=info \
         update

    cd ${APP_HOME}
fi

rm -f RUNNING_PID

$APP_HOME/bin/play-scala-slick-liquibase-angular-docker \
    -J-XX:+HeapDumpOnOutOfMemoryError \
    -J-Xmx512m \
    -J-Xms256m \
    -Ddb.default.driver=${DB_DRIVER} \
    -Ddb.default.url=$JDBC_URL \
    -Ddb.default.user=${DB_USER} \
    -Ddb.default.password=${DB_PASSWORD} \
    -Ddb.default.dialect=mysql