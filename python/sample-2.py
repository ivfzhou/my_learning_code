#! /usr/bin/python3
# coding=utf8

# import module, module
# form module import
# fn = module.fn
# from module import * #单一_开头的名字不包含

# 字符串
s = "abc\n"
print(s)
s = r"abc\n"
print(s)
s = u"你好"
print(s)
s = "你_好_吗_"
print(s[0:5:2])
s = "s""s"
print(s)
s = "s" + "s"
print(s)
s = s * 3
print(s)
print(type(s))
print(isinstance(s, str))
print(f'{s}')
print(f'{s + "1"}')
print(f'{s + "1"=}')
del s
print(None)

# 数字
x = 3
y = 2
print(x / y)
print(x // y)

# 列表
list1 = ['a', 'b']
list2 = ['b', 'c']
print(list1 + list2)
print(list1 + ['c'])
list1[0:1] = []
print(list1)
del list1[0]
print(list1)
list1 = ['a', 'b', 'c', 'd']
list1[0:3:2] = ['x', 'y']
print(list1)
print(list1[-1])
list3 = [1] * 5
print(list3)

# 元组
tup = (1, 2, 3, 4)
print(tup)
print(tup[1:3:2])

# 集合
s1 = {'a', 'b', 'c'}
s2 = {'b', "c", "d"}
print('a' in s1)
print('a' not in s1)
print(s1 - s2)
print(s1 | s2)
print(s1 & s2)
print(s1 ^ s2)

# 字典
m = {'key': 'value', 2: 'sd'}
print(m['key'])
print(m[2])
print(m.keys())
del m[2]

# 流程
if True:
    print('ok')
elif True:
    print('no')
else:
    print('no')

for x in [1, 2, 3]:
    print(x, end=" ")  # break continue
else:
    print(x)

while True:
    pass
    break

n = [1]
match n:
    case [1]:
        print(1)
    case 2:
        print(2)
    case _:
        print(3)

# is
a = 1
b = 1
print(a is b)
b = 'a'
print(a is b)


def fn(x, y=9, z='a'):
    print(x, y, z)


fn(1, z='d')


def fn(*args):
    for v in args:
        print(v)


fn(1, 2, 3, 4)

# 推导式
list1 = [1, 2, 3]
print([2 * x for x in list1])
print([2 * x for x in list1 if x != 2])
print([y * x for x in list1 for y in list1])
print({x: y for x in list1 for y in list1}) # 两层循环，外层遍历 x 内层遍历 y

import sys

print(sys.path)

with open('C:\\Users\\ivfzhou\\src\\others\\.gitignore') as f:
    print(f.readline())

print('{0:d}'.format(1))

try:
    print(0 / 0)
except ZeroDivisionError as e:
    print('Oops!')
except:
    raise #外抛异常
else:
    print()
finally:
    print('finally')
