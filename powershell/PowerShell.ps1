# 声明变量
New-Variable myval -Value 1 -Force -Option ReadOnly
Write-Output $myval

# 声明常量
New-Variable mycost -Value 2 -Force -Option Constant
Write-Output $mycost

# 数组
$myarr = 1,2,3,4
Write-Output $myarr
$myarr1 = 1..4
Write-Output $myarr1
$myarr2 = @()
Write-Output $myarr2
$myarr3 = ,1
Write-Output $myarr3
$ips = ipconfig.exe
Write-Output $ips[1]
# 数组的判断
Write-Output ($myarr -is [array])
# 数组的追加
$myarr += "元素5"
Write-Output $myarr

# 哈希表
$mymap = @{Name = "ivfzhou"; Age = 18; hobby = "paly game","write code"}
Write-Output $mymap
$mymap.Name = "new name"
Write-Output $mymap.Name
$mymap.Remove("Name")
Write-Output $mymap.Name

# 对象
New-Object obj
Add-Member $obj

# 运算符
# -eq
# -ne
# -gt
# -ge
# -lt
# -le
# -contains
# -notcontains
# !($val)
# -and
# -or 
# -xor
# -not

# 流程语句
if (True) {} else {}
while (True) {}
for ($i=0;i -lt 100; $i++) {}
foreach ($file in Get-ChildItem 'C:\Program Files\go\bin') {
    if ($file.Length -gt 1mb) {
        Write-Output $file.Name
    }
}
Write-Output $mymap | ForEach-Object { $_.Age }

# 函数
function myfn($P1, $P2) {
    <#
    .SYNOPSIS
    简介
    
    .DESCRIPTION
    描述

    .PARAMETER P1
    .PARAMETER P2

    .EXAMPLE
    例子
    #>

    Write-Output $p1 $p2
}
$myfn 1 2

# 异常捕获
try {
    
}
catch {
    <#Do this if a terminating exception happens#>
}
finally {
    <#Do this after the try block regardless of whether an exception occurred or not#>
}
