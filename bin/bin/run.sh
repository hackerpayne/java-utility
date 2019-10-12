#!/usr/bin/env bash

# jar名称
JAR_NAME='wechat-1.0.jar'

# 生成路径的变量
#cd `dirname $0`
#BIN_DIR=`pwd`
#cd ..
#DEPLOY_DIR=`pwd`

# deploy dir
DEPLOY_DIR="$( cd "$( dirname "$0"  )" && pwd  )"
DEPLOY_DIR="$(dirname "$DEPLOY_DIR")"
# bin dir
BIN_DIR=$DEPLOY_DIR/bin
# lib dir
LIB_DIR=$DEPLOY_DIR/lib
# conf dir
CONF_DIR=$DEPLOY_DIR/config
# log dir
LOGS_DIR=$DEPLOY_DIR/logs

# Enable Java Environment
source /etc/profile

export CLASS_PATH=$CLASS_PATH:$BIN_DIR/
echo "Current Path is: $DEPLOY_DIR"

cd $DEPLOY_DIR

PROG_FILE=$LIB_DIR/$JAR_NAME
PID_FILE=$BIN_DIR/run.pid

start() {

	echo "Starting Java"
	# sleep 5
	mkdir -p $LOGS_DIR

	# 必须指定MainClass
	nohup java -jar $PROG_FILE > $LOGS_DIR/result.log 2>&1 &

	# 不用指定mainclass的情况
	#nohup java -cp "wechat-1.0.jar:lib/*.jar" > logs/result.log 2>&1 &

	echo $! > $PID_FILE

	sleep 1

	tail -F $LOGS_DIR/result.log
}

stop() {
	echo -e "Stopping the $PROG_FILE ..."

	PIDS=`cat $PID_FILE`
    kill -9 $PIDS > /dev/null 2>&1 &

    COUNT=0
	while [ $COUNT -lt 1 ]; do
	    echo -e ".\c"
	    sleep 1
	    COUNT=1
	    for PID in $PIDS ; do
	        PID_EXIST=`ps -f -p $PID | grep java`
	        if [ -n "$PID_EXIST" ]; then
	            COUNT=0
	            break
	        fi
	    done
	done

	echo 'Stopped'
}

restart() {
    stop
    start
}

case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    status)
		if ps -p $(cat $PID_FILE) > /dev/null
		then
		   echo "$PROG_FILE is running"
		else
		   	echo "$PROG_FILE is Stopped"
		fi
        ;;
    restart)
        restart
        ;;
    *)
        echo $"Usage: $prog {start|stop|restart|status}"
        RETVAL=2
esac

exit $RETVAL
