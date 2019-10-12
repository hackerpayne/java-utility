#!/bin/bash

SERVER="$( cd "$( dirname "$0"  )" && pwd  )"

cd $SERVER

case "$1" in
  start)
#    nohup java -Xmx128m -jar server.jar > $SERVER/server.log 2>&1 &
    nohup java -Xmx512m -jar dubbo-admin-0.1.jar --spring.config.location=application.properties >/dev/null 2>&1 &
    echo $! > $SERVER/run.pid
    ;;

  stop)
    kill `cat $SERVER/run.pid`
    rm -rf $SERVER/run.pid
    ;;

  restart)
    $0 stop
    sleep 1
    $0 start
    ;;

  *)
    echo "Usage: run.sh {start|stop|restart}"
    ;;

esac
exit 0