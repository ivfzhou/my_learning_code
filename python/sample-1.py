#! /path/to/python
# coding=gb18030
# _*_ coding: gb18030 _*_
# 表示该脚本应由哪个解释器执行。类比bash。
# 指定源代码的字符编码，脚本非utf8编码时须要指定。

import keyword

print("标识符第一个字符不能是数字，允许非ASCII编码字符。")
变量_1a = "1a你"
print(变量_1a, "\n")

print("关键字")
print(keyword.kwlist, "\n")

print("判断变量类型")
print(type(1))  # int
print(type(""))  # str
print(type(''))  # str
print(type(()))  # tuple
print(type({}))  # dict
print(type([]), "\n")  # list

print("字符串切割，[起始:结束:步长]。从右往左从-1开始。")
v = "123你好abc"
print("输出第四个字符：", v[3])
print("输出第一个到倒数第二个字符串：", v[0:-1])
print("输出第一个到倒数第二个每隔两字符：", v[0:-1:2])
print("重复字符串：", v * 2)
print("字符串拼接：", v + v)
print(r"raw string 不转义\n", "\n")

# print("输入")
# v0 = input("请输入")
# print(v0, "\n")

print("判断变量类型，isinstance会认为子类是父类类型，而type不会。")
print(isinstance(1., int), "\n")

print("同行多个变量赋值")
v1 = "s1"
v1, v2 = "ss1", v1
print("v1: ", v1, ", v2: ", v2, "\n")

print("续行符")
v3 = "line1" \
     "line2"
print(v3, "\n")

print("列表同字符串一样切割，列表也可以拼接操作，列表可以切割出来修改")
v4 = [1, 2, 3]
v5 = [3, 4, 5]
print("v4 + v 5=", v4 + v5)
v4[0:1] = [0, 0]
print(v4)
v4[0:4:2] = [1, 3]
print(v4, "\n")

print("元组中元素不能修改，元组也能被切割，元组也能拼接。")
v6 = ()
print(v6)
v7 = (1,)
print("一个元素的元素须要加个逗号", v7, "\n")

print("集合元素不重复，可以进行差交并运算。")
v8 = set()
print("空集合", v8)
v9 = {1, 2, 3}
v10 = {3, 4, 5}
print("差集", v9 - v10)
print("并集", v9 | v10)
print("交集", v9 & v10)
print("非交集", v9 ^ v10)
print("in关键字判断", 2 in v9)
v11 = set("abcdefg")
print("字符集", v11, "\n")

print("字典，键须要是不可变类型。")
v12 = {}
print("空字典", v12)
v12["key"] = 1  # 赋值
v12["key0"] = 2
v12["key1"] = 3
print("in关键字判断", "key" in v12)
del v12["key"]  # 删除键
print("获取所有键", list(v12.keys()))
print("排序", sorted(v12.keys()))
print("not关键字", "key" not in v12)
print("其他构建字典方式", dict([("key", 1), ("key0", 2)]))
print("其他构建字典方式", dict(key=1, key0=2))
print("推导式构建字典方式", {x: x + 1 for x in [1, 2, 3]}, "\n")

print("幂运算符")
print(2 ** 3, "\n")

print("身份函数")
print(id(1) == id(1), "\n")
