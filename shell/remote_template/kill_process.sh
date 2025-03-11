#!/bin/bash
# 推送到服务器上的杀死进程的脚本
# 设置颜色输出
GREEN="\033[0;32m"
RED="\033[0;31m"
YELLOW="\033[0;33m"
NC="\033[0m" # No Color

# 检查是否提供了进程名称
if [ $# -eq 0 ]; then
    echo -e "${RED}错误: 请提供进程名称作为参数${NC}"
    echo -e "用法: $0 <进程名称>"
    exit 1
fi

# 获取进程名称
PROCESS_NAME=$1

# 查找进程ID，排除grep本身和脚本本身
echo -e "${YELLOW}正在查找进程: ${PROCESS_NAME}...${NC}"
PIDS=$(ps aux | grep "${PROCESS_NAME}" | grep -v "grep" | grep -v "$0" | awk '{print $2}')

# 检查是否找到进程
if [ -z "$PIDS" ]; then
    echo -e "${RED}未找到匹配的进程: ${PROCESS_NAME}${NC}"
    exit 1
fi

# 显示找到的进程
echo -e "${GREEN}找到以下进程:${NC}"
for PID in $PIDS; do
    PROCESS_INFO=$(ps -p $PID -o pid,ppid,user,%cpu,%mem,command | grep -v PID)
    echo -e "${YELLOW}$PROCESS_INFO${NC}"
done

# 询问用户是否确认终止进程
echo -e "${RED}警告: 即将终止以上进程${NC}"
read -p "是否继续? (y/n): " CONFIRM

if [ "$CONFIRM" != "y" ] && [ "$CONFIRM" != "Y" ]; then
    echo -e "${YELLOW}操作已取消${NC}"
    exit 0
fi

# 终止进程
for PID in $PIDS; do
    echo -e "${YELLOW}正在终止进程 PID: $PID...${NC}"
    if kill -9 $PID 2>/dev/null; then
        echo -e "${GREEN}成功终止进程 PID: $PID${NC}"
    else
        echo -e "${RED}无法终止进程 PID: $PID${NC}"
    fi
done

# 再次检查进程是否已终止
SLEEP_TIME=1
echo -e "${YELLOW}等待 ${SLEEP_TIME} 秒确认进程已终止...${NC}"
sleep $SLEEP_TIME

REMAINING_PIDS=$(ps aux | grep "${PROCESS_NAME}" | grep -v "grep" | grep -v "$0" | awk '{print $2}')

if [ -z "$REMAINING_PIDS" ]; then
    echo -e "${GREEN}所有匹配的进程已成功终止${NC}"
else
    echo -e "${RED}警告: 以下进程仍在运行:${NC}"
    for PID in $REMAINING_PIDS; do
        PROCESS_INFO=$(ps -p $PID -o pid,ppid,user,%cpu,%mem,command | grep -v PID)
        echo -e "${YELLOW}$PROCESS_INFO${NC}"
    done
fi

exit 0