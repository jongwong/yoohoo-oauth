#!/bin/bash

# 获取脚本所在的目录，并将其设置为迁移文件夹路径
MIGRATION_FOLDER="$(dirname "$0")"

# 进入迁移目录
cd "$MIGRATION_FOLDER" || exit

# 获取所有的SQL迁移文件并排序
FILES=$(ls V*__*.sql | sort -V)

# 初始化一个计数器
counter=1

# 遍历每个文件并重命名
for file in $FILES; do
  # 提取版本号
  version=$(echo "$file" | sed -E 's/^V([0-9]+)__.*/\1/')

  # 构造新的文件名
  new_name="V${counter}__${file#V${version}__}"

  # 重命名文件
  mv "$file" "$new_name"

  # 打印重命名结果
  echo "Renamed: $file -> $new_name"

  # 递增计数器
  ((counter++))
done

echo "Migration files have been successfully reordered."
