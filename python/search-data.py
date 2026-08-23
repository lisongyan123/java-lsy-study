#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Iceberg 表查看工具
"""

from pyiceberg.catalog.sql import SqlCatalog
from datetime import datetime


def main():
    """主函数"""
    print("=" * 60)
    print("🔍 Iceberg 表查看工具")
    print("=" * 60)

    # ========== 连接 Catalog ==========
    print("\n🔗 连接 SQLite Catalog...")
    catalog = SqlCatalog(
        "default",
        uri="sqlite:///./iceberg_metadata.db",
        warehouse="./data/warehouse",
    )

    # ========== 查看所有表 ==========
    print("\n📋 所有表:")
    for table_name in catalog.list_tables("my_db"):
        print(f"  - {table_name}")

    # ========== 加载表 ==========
    table = catalog.load_table("my_db.students")

    # ========== 查看表信息 ==========
    print("\n📊 表信息:")
    print(f"  表名: {table.name}")
    print(f"  位置: {table.location}")

    # ========== 查看表结构 ==========
    print("\n📋 表结构:")
    for field in table.schema().fields:
        required = "必填" if field.required else "可选"
        print(f"  {field.field_id}: {field.name} ({field.field_type}) - {required}")

    # ========== 查看快照历史 ==========
    print("\n📸 快照历史:")
    for i, snapshot in enumerate(table.snapshots()):
        print(f"\n  快照 {i + 1}:")
        print(f"    ID: {snapshot.snapshot_id}")
        # 时间戳
        if hasattr(snapshot, 'timestamp_ms') and snapshot.timestamp_ms:
            dt = datetime.fromtimestamp(snapshot.timestamp_ms / 1000)
            print(f"    时间: {dt.strftime('%Y-%m-%d %H:%M:%S')}")
        # 操作类型
        try:
            op = snapshot.operation()
            print(f"    操作: {op}")
        except:
            print(f"    操作: 未知")
        # 摘要信息
        summary = snapshot.summary
        print(f"    文件数: {summary.get('total-data-files', 0)}")
        print(f"    记录数: {summary.get('total-records', 0)}")

    # ========== 查看数据文件（通过 scan 获取） ==========
    print("\n📁 数据文件信息:")

    # 使用 table.scan() 的 plan_files() 来获取文件列表
    try:
        # 创建一个 scan 并获取文件列表
        scan = table.scan()
        file_count = 0
        total_size = 0
        total_records = 0

        # 使用 plan_files() 获取文件信息
        for file in scan.plan_files():
            file_count += 1
            total_size += file.file_size_in_bytes
            total_records += file.record_count
            print(f"\n  文件 {file_count}:")
            print(f"    路径: {file.file_path}")
            print(f"    大小: {file.file_size_in_bytes / 1024:.2f} KB")
            print(f"    记录数: {file.record_count}")
            print(f"    格式: {file.format}")

        if file_count == 0:
            print("  (暂无数据文件)")
        else:
            print(f"\n  📊 总计: {file_count} 个文件, {total_size / 1024:.2f} KB, {total_records} 条记录")
    except Exception as e:
        print(f"  ⚠️  获取文件信息失败: {e}")

    # ========== 查询数据 ==========
    print("\n📊 数据预览:")
    try:
        df = table.scan().to_pandas()
        if len(df) > 0:
            # 只显示前10条
            print(df.head(10).to_string(index=False))
            print(f"\n总计: {len(df)} 条记录")
        else:
            print("暂无数据")
    except Exception as e:
        print(f"查询失败: {e}")

    print("\n" + "=" * 60)
    print("✅ 查看完成！")


if __name__ == "__main__":
    main()