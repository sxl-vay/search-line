#!/bin/bash

# 定义远程服务器信息
REMOTE_IP="192.168.40.95"
REMOTE_USER="root"
REMOTE_PASSWORD="root"

# 定义本地 JAR 文件路径
LOCAL_JAR_PATH="/Users/sxl/IdeaProjects/Search-Line/line-gateway/target/line-gateway-1.0-SNAPSHOT.jar"

# 定义远程 JAR 文件路径
REMOTE_JAR_PATH="/usr/local/jar/line-gateway-1.0-SNAPSHOT.jar"

# 检查本地 JAR 文件是否存在
if [ ! -f "$LOCAL_JAR_PATH" ]; then
    echo "错误：本地 JAR 文件不存在，请检查路径：$LOCAL_JAR_PATH"
    exit 1
fi

# 传输 JAR 文件到远程服务器
echo "正在传输 JAR 文件到远程服务器..."
/usr/bin/expect <<EOF
set timeout 20
spawn scp $LOCAL_JAR_PATH $REMOTE_USER@$REMOTE_IP:$REMOTE_JAR_PATH
expect {
    "yes/no" { send "yes\r"; exp_continue }
    "password:" { send "$REMOTE_PASSWORD\r" }
}
expect eof
EOF

if [ $? -ne 0 ]; then
    echo "错误：JAR 文件传输失败！"
    exit 1
fi
echo "JAR 文件传输完成。"

# 连接到远程服务器并执行命令
echo "正在连接到远程服务器并启动 JAR 文件..."
/usr/bin/expect <<EOF
set timeout 20
spawn ssh $REMOTE_USER@$REMOTE_IP
expect {
    "yes/no" { send "yes\r"; exp_continue }
    "password:" { send "$REMOTE_PASSWORD\r" }
}
expect "root@"
send "nohup java -jar $REMOTE_JAR_PATH > /dev/null 2>&1 &\r"
expect "root@"
send "exit\r"
expect eof
EOF

if [ $? -ne 0 ]; then
    echo "错误：远程执行命令失败！"
    exit 1
fi
echo "远程启动 JAR 文件完成。"