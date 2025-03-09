#!/bin/bash
# 推送jar包到服务器执行 目前推送的gateway

# 获取项目根目录路径
PROJECT_ROOT=$(cd "$(dirname "$0")/.." && pwd)
# 定义服务名称
SERVICE_NAME="search-line-file-service"

# 定义远程服务器信息
REMOTE_IP="192.168.42.106"
REMOTE_USER="root"
REMOTE_PASSWORD="root"

# 定义本地 JAR 文件路径
LOCAL_JAR_PATH="${PROJECT_ROOT}/search-line-service/${SERVICE_NAME}/target/${SERVICE_NAME}-1.0-SNAPSHOT.jar"

# 定义本地 kill_process.sh 脚本路径
LOCAL_KILL_SCRIPT="${PROJECT_ROOT}/shell/kill_process.sh"

# 定义远程 JAR 文件路径
REMOTE_JAR_PATH="/usr/local/jar/${SERVICE_NAME}-1.0-SNAPSHOT.jar"

# 定义远程 kill_process.sh 脚本路径
REMOTE_KILL_SCRIPT="/usr/local/jar/kill_process.sh"

# 检查本地 JAR 文件是否存在
if [ ! -f "$LOCAL_JAR_PATH" ]; then
    echo "错误：本地 JAR 文件不存在，请检查路径：$LOCAL_JAR_PATH"
    exit 1
fi

# 检查本地 kill_process.sh 脚本是否存在
if [ ! -f "$LOCAL_KILL_SCRIPT" ]; then
    echo "错误：本地 kill_process.sh 脚本不存在，请检查路径：$LOCAL_KILL_SCRIPT"
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

# 上传 kill_process.sh 脚本到远程服务器
echo "正在上传 kill_process.sh 脚本到远程服务器..."
/usr/bin/expect <<EOF
set timeout 20
spawn scp $LOCAL_KILL_SCRIPT $REMOTE_USER@$REMOTE_IP:$REMOTE_KILL_SCRIPT
expect {
    "yes/no" { send "yes\r"; exp_continue }
    "password:" { send "$REMOTE_PASSWORD\r" }
}
expect eof
EOF

if [ $? -ne 0 ]; then
    echo "错误：kill_process.sh 脚本上传失败！"
    exit 1
fi
echo "kill_process.sh 脚本上传完成。"

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
# 启动新的JAR文件
send "nohup /usr/local/jdk/jdk-23.0.2/bin/java -jar $REMOTE_JAR_PATH > /dev/null 2>&1 &\r"
expect "root@"
send "exit\r"
expect eof
EOF

if [ $? -ne 0 ]; then
    echo "错误：远程执行命令失败！"
    exit 1
fi
echo "远程服务重启完成。"