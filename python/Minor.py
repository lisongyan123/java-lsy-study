#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
一键 Iceberg 演示脚本
使用本地文件系统 + SQLite Catalog（不需要 MinIO）
自动完成：连接 → 建表 → 插数据 → 查询 → Minor Compaction
"""

import time
import sys
import os
from datetime import datetime
import pyarrow as pa


def main():
    """主函数"""
    print("=" * 60)
    print("🚀 Iceberg 一键演示 (本地文件系统 + SQLite)")
    print("=" * 60)

    # ========== 1. 导入依赖 ==========
    print("\n📦 加载依赖...")
    try:
        from pyiceberg.catalog.sql import SqlCatalog
        from pyiceberg.schema import Schema
        from pyiceberg.types import (
            IntegerType, StringType, FloatType,
            BooleanType, TimestampType, NestedField
        )
        from pyiceberg.expressions import GreaterThan
        print("✅ 依赖加载成功")
    except ImportError as e:
        print(f"❌ 缺少依赖: {e}")
        print("请先运行: pip install pyiceberg[sql] pandas pyarrow sqlalchemy")
        sys.exit(1)

    # ========== 2. 创建本地数据目录 ==========
    os.makedirs("./data/warehouse", exist_ok=True)
    print(f"📁 本地数据目录: ./data/warehouse")

    # ========== 3. 连接 Catalog ==========
    print("\n🔗 连接 SQLite Catalog...")
    try:
        catalog = SqlCatalog(
            "default",
            uri="sqlite:///./iceberg_metadata.db",
            warehouse="./data/warehouse",
        )

        namespaces = catalog.list_namespaces()
        print(f"✅ 连接成功！当前命名空间: {namespaces}")
    except Exception as e:
        print(f"❌ 连接失败: {e}")
        sys.exit(1)

    # ========== 4. 准备环境 ==========
    NAMESPACE = "my_db"
    TABLE_NAME = "students"
    FULL_TABLE = f"{NAMESPACE}.{TABLE_NAME}"

    # 删除已存在的表（如果有）
    try:
        catalog.drop_table(FULL_TABLE)
        print(f"🗑️  已删除旧表: {FULL_TABLE}")
    except:
        pass

    # 创建命名空间
    try:
        catalog.create_namespace(NAMESPACE)
        print(f"📁 创建命名空间: {NAMESPACE}")
    except:
        print(f"📁 命名空间已存在: {NAMESPACE}")

    # ========== 5. 创建表 ==========
    print(f"\n📋 创建表: {FULL_TABLE}")
    schema = Schema(
        NestedField(1, "id", IntegerType(), required=True),
        NestedField(2, "name", StringType(), required=False),
        NestedField(3, "age", IntegerType(), required=False),
        NestedField(4, "score", FloatType(), required=False),
        NestedField(5, "is_active", BooleanType(), required=False),
        NestedField(6, "created_at", TimestampType(), required=False),
    )

    table = catalog.create_table(
        FULL_TABLE,
        schema=schema,
        location=f"./data/warehouse/{NAMESPACE}/{TABLE_NAME}"
    )
    print(f"✅ 表创建成功！")

    # ========== 6. 插入数据 ==========
    print("\n📝 插入数据...")

    now = datetime.now()

    # PyArrow Schema 需要标记 nullable
    arrow_schema = pa.schema([
        pa.field("id", pa.int32(), nullable=False),   # required = False
        pa.field("name", pa.string(), nullable=True),
        pa.field("age", pa.int32(), nullable=True),
        pa.field("score", pa.float32(), nullable=True),
        pa.field("is_active", pa.bool_(), nullable=True),
        pa.field("created_at", pa.timestamp('us'), nullable=True),
    ])

    # 准备数据
    data = [
        {"id": 1, "name": "张三", "age": 20, "score": 95.5, "is_active": True, "created_at": now},
        {"id": 2, "name": "李四", "age": 22, "score": 87.0, "is_active": True, "created_at": now},
        {"id": 3, "name": "王五", "age": 21, "score": 76.5, "is_active": False, "created_at": now},
        {"id": 4, "name": "赵六", "age": 23, "score": 92.0, "is_active": True, "created_at": now},
        {"id": 5, "name": "孙七", "age": 20, "score": 88.5, "is_active": True, "created_at": now},
    ]

    # 创建 PyArrow Table
    arrow_table = pa.Table.from_pylist(data, schema=arrow_schema)

    table.append(arrow_table)
    print(f"✅ 插入了 {len(data)} 条数据")

    # ========== 7. 查询数据 ==========
    print("\n🔍 查询全部数据:")
    df = table.scan().to_pandas()
    print(df.to_string(index=False))

    print("\n🔍 查询 score > 80 的数据:")
    filtered = table.scan(row_filter=GreaterThan("score", 80)).to_pandas()
    print(filtered.to_string(index=False))

    # ========== 8. 查看快照历史 ==========
    print("\n📸 快照历史:")
    for snapshot in table.snapshots():
        summary = snapshot.summary
        print(f"  ID: {snapshot.snapshot_id}, 文件数: {summary.get('total-data-files', 0)}, 记录数: {summary.get('total-records', 0)}")

    # ========== 9. 制造小文件 ==========
    print("\n🔄 模拟多次写入产生小文件...")
    for i in range(10):
        batch_data = [{"id": 100 + i, "name": f"用户_{i}", "age": 18 + i, "score": 60.0 + i,
                       "is_active": True, "created_at": datetime.now()}]
        batch_table = pa.Table.from_pylist(batch_data, schema=arrow_schema)
        table.append(batch_table)
        time.sleep(0.1)

    print("\n📁 写入后文件统计:")
    for snapshot in table.snapshots():
        summary = snapshot.summary
        print(f"  Snapshot: {snapshot.snapshot_id}, 文件数: {summary.get('total-data-files', 0)}")

    # ========== 10. 执行 Minor Compaction ==========
    print("\n🔄 执行 Minor Compaction（文件合并）...")
    try:
        table.rewrite_data_files(
            strategy="replace",
            options={
                "min-input-files": 2,
                "target-file-size-bytes": 52428800
            }
        )
        print("✅ Minor Compaction 执行完成！")
    except Exception as e:
        print(f"⚠️  Minor Compaction 失败: {e}")
        print("（继续演示，不影响其他功能）")

    # ========== 11. 查看合并后结果 ==========
    print("\n📁 合并后文件统计:")
    for snapshot in table.snapshots():
        summary = snapshot.summary
        print(f"  Snapshot: {snapshot.snapshot_id}, 文件数: {summary.get('total-data-files', 0)}")

    # ========== 12. 最终数据 ==========
    print("\n📊 最终全部数据:")
    df_final = table.scan().to_pandas()
    print(df_final.to_string(index=False))
    print(f"\n📊 总计: {len(df_final)} 条记录")

    # ========== 13. 元数据信息 ==========
    print("\n📁 数据文件位置:")
    print(f"  数据目录: ./data/warehouse/{NAMESPACE}/{TABLE_NAME}/")
    print(f"  元数据文件: ./data/warehouse/{NAMESPACE}/{TABLE_NAME}/metadata/")
    print(f"  SQLite 元数据: ./iceberg_metadata.db")

    print("\n" + "=" * 60)
    print("✅ 演示完成！")
    print("=" * 60)
    print("\n💡 接下来可以:")
    print("  1. 查看数据目录: ./data/warehouse/my_db/students/")
    print("  2. 查看元数据 JSON 文件: metadata/*.metadata.json")
    print("  3. 查看本地 SQLite: iceberg_metadata.db")


if __name__ == "__main__":
    main()