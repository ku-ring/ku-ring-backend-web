#!/usr/bin/env bash

PROJECT_ROOT="/home/ec2-user/app"
JAR_FILE="$PROJECT_ROOT/spring-webapp.jar"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"
APP_LOG="$PROJECT_ROOT/application.log"

TIME_NOW=$(date +%c)

source ~/.bash_profile

# build 파일 복사
echo "$TIME_NOW > $JAR_FILE 파일 복사" >> $DEPLOY_LOG
cp $PROJECT_ROOT/build/libs/*.jar $JAR_FILE

# 프로젝트 루트로 이동
cd $PROJECT_ROOT

# jar 파일 실행
chmod 755 $JAR_FILE
echo "$TIME_NOW > Java runtime version" >> $DEPLOY_LOG
java -version >> $DEPLOY_LOG 2>&1
echo "$TIME_NOW > JAVA_HOME=$JAVA_HOME" >> $DEPLOY_LOG
echo "$TIME_NOW > PATH=$PATH" >> $DEPLOY_LOG
echo "$TIME_NOW > $JAR_FILE 파일 실행" >> $DEPLOY_LOG
nohup java -jar -Dspring.profiles.active=prod -Xms256m -Xmx256m $JAR_FILE > "$APP_LOG" 2>&1 < /dev/null &

CURRENT_PID=$(pgrep -f $JAR_FILE)
echo "$TIME_NOW > 실행된 프로세스 아이디 $CURRENT_PID 입니다." >> $DEPLOY_LOG
