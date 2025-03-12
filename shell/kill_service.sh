#!/bin/bash

#source "$(dirname "$0")/kill.properties"
REMOTE_USER="root"
REMOTE_PASSWORD="root"
REMOTE_KILL_SCRIPT="/usr/local/jar/kill_process.sh"

# 定义服务器IP数组
REMOTE_IPS=("192.168.42.106" "192.168.42.234")
# 定义服务名数组  "line-gateway"  "search-line-es-core-service"
SERVICE_NAMES=("search-line-file-service")

# 遍历服务器IP数组
for REMOTE_IP in "${REMOTE_IPS[@]}"; do
    # 遍历服务名数组
    for SERVICE_NAME in "${SERVICE_NAMES[@]}"; do
        echo "正在连接到远程服务器 $REMOTE_IP 杀死服务 $SERVICE_NAME..."
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
        echo "服务 $SERVICE_NAME 在服务器 $REMOTE_IP 上已停止"
    done
done