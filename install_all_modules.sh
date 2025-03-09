#!/bin/bash

# 设置颜色输出
GREEN="\033[0;32m"
RED="\033[0;31m"
YELLOW="\033[0;33m"
NC="\033[0m" # No Color

echo "${GREEN}开始安装Search-Line项目的所有Maven模块到本地仓库...${NC}"

# 记录开始时间
start_time=$(date +%s)

# 项目根目录
ROOT_DIR="$(pwd)"
echo "${YELLOW}项目根目录: ${ROOT_DIR}${NC}"

# 创建日志目录
LOG_DIR="${ROOT_DIR}/maven_install_logs"
mkdir -p "${LOG_DIR}"

# 安装成功和失败的模块计数
success_count=0
failed_count=0

# 保存失败的模块列表
failed_modules=""

# 首先安装根目录的pom.xml
echo "${YELLOW}安装根目录的pom.xml...${NC}"
if mvn clean install -N -DskipTests > "${LOG_DIR}/root.log" 2>&1; then
    echo "${GREEN}✓ 根目录pom.xml安装成功${NC}"
    ((success_count++))
else
    echo "${RED}✗ 根目录pom.xml安装失败${NC}"
    ((failed_count++))
    failed_modules="${failed_modules}\n- 根目录"
fi

# 查找所有包含pom.xml的目录
echo "${YELLOW}查找所有包含pom.xml的模块...${NC}"
pom_dirs=$(find "${ROOT_DIR}" -name "pom.xml" -not -path "*/target/*" | sort)

# 遍历所有找到的pom.xml文件
for pom_file in $pom_dirs; do
    module_dir=$(dirname "${pom_file}")
    module_name=$(basename "${module_dir}")
    
    # 跳过根目录的pom.xml，因为已经安装过了
    if [ "${module_dir}" = "${ROOT_DIR}" ]; then
        continue
    fi
    
    relative_path=${module_dir#$ROOT_DIR/}
    log_file="${LOG_DIR}/${relative_path//\//_}.log"
    
    echo "${YELLOW}安装模块: ${relative_path}${NC}"
    
    # 切换到模块目录并执行mvn install
    cd "${module_dir}"
    if mvn clean install -DskipTests > "${log_file}" 2>&1; then
        echo "${GREEN}✓ 模块 ${relative_path} 安装成功${NC}"
        ((success_count++))
    else
        echo "${RED}✗ 模块 ${relative_path} 安装失败${NC}"
        ((failed_count++))
        failed_modules="${failed_modules}\n- ${relative_path}"
    fi
    
    # 返回根目录
    cd "${ROOT_DIR}"
done

# 记录结束时间并计算总时间
end_time=$(date +%s)
total_time=$((end_time - start_time))
minutes=$((total_time / 60))
seconds=$((total_time % 60))

# 输出安装结果摘要
echo "\n${GREEN}=== 安装完成 ===${NC}"
echo "${YELLOW}总耗时: ${minutes}分${seconds}秒${NC}"
echo "${GREEN}成功安装: ${success_count} 个模块${NC}"

if [ ${failed_count} -gt 0 ]; then
    echo "${RED}安装失败: ${failed_count} 个模块${NC}"
    echo "${RED}失败的模块:${failed_modules}${NC}"
    echo "${YELLOW}查看日志目录 ${LOG_DIR} 获取详细错误信息${NC}"
    exit 1
else
    echo "${GREEN}所有模块安装成功!${NC}"
    exit 0
fi