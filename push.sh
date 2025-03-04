#!/bin/bash
# 推送jar包到服务器执行 目前推送的gateway


# 定义远程服务器信息
REMOTE_IP="192.168.42.106"
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

# 删除远程服务器上的旧 JAR 文件
echo "正在删除远程服务器上的旧 JAR 文件..."
/usr/bin/expect <<EOF
set timeout 20
spawn ssh $REMOTE_USER@$REMOTE_IP
expect {
    "yes/no" { send "yes\r"; exp_continue }
    "password:" { send "$REMOTE_PASSWORD\r" }
}
expect "root@"
send "rm -f $REMOTE_JAR_PATH\r"
expect "root@"
send "exit\r"
expect eof
EOF

if [ $? -ne 0 ]; then
    echo "警告：删除旧 JAR 文件失败，可能是文件不存在。"
fi

# 传输新的 JAR 文件到远程服务器
echo "正在传输新的 JAR 文件到远程服务器..."
/usr/bin/expect <<EOF
set timeout 100
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

# 连接到远程服务器，先执行kill.sh，然后启动新的JAR文件
echo "正在连接到远程服务器..."
/usr/bin/expect <<EOF
set timeout 20
spawn ssh $REMOTE_USER@$REMOTE_IP
expect {
    "yes/no" { send "yes\r"; exp_continue }
    "password:" { send "$REMOTE_PASSWORD\r" }
}
expect "root@"
# 执行kill.sh脚本
send "sh /usr/local/jar/kill.sh\r"
expect "root@"
# 启动新的JAR文件
send "nohup /usr/local/jdk/jdk-23.0.2/bin/java -jar $REMOTE_JAR_PATH --server.port=8089 > /dev/null 2>&1 &\r"
expect "root@"
send "exit\r"
expect eof
EOF

if [ $? -ne 0 ]; then
    echo "错误：远程执行命令失败！"
    exit 1
fi
echo "远程服务重启完成。"