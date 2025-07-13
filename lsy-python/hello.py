import sys


import pandas as pd

# 创建一个简单的DataFrame
data = {
    '姓名': ['张三', '李四', '王五'],
    '年龄': [25, 30, 35],
    '城市': ['北京', '上海', '广州']
}
df = pd.DataFrame(data)

# 显示DataFrame
print("\n基本的DataFrame示例:")
print(df)

# 基本统计
print("\n年龄的基本统计信息:")
print(df['年龄'].describe())

# 按城市分组
print("\n按城市分组统计年龄平均值:")
print(df.groupby('城市')['年龄'].mean())

# 添加新列
df['工资'] = [10000, 15000, 20000]
print("\n添加新列后的DataFrame:")
print(df)