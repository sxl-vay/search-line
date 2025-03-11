#!/bin/bash

#source "$(dirname "$0")/kill.properties"
REMOTE_USER="root"
REMOTE_IP="192.168.42.106"
REMOTE_PASSWORD="root"
SERVICE_NAME="line-gateway"
REMOTE_KILL_SCRIPT="/usr/local/jar/kill_process.sh"

# 连接到远程服务器，先执行kill_process.sh，然后启动新的JAR文件
echo "正在连接到远程服务器..."
/usr/bin/expect <<EOF
set timeout 20
spawn ssh $REMOTE_USER@$REMOTE_IP
expect {
    "yes/no" { send "yes\r"; exp_continue }
    "password:" { send "$REMOTE_PASSWORD\r" }
}
expect "root@"
# 执行kill_process.sh脚本，自动终止进程
send "echo y | sh $REMOTE_KILL_SCRIPT $SERVICE_NAME\r"
expect "root@"
send "exit\r"
expect eof
EOF