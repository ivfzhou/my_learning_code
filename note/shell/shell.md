# 一、笔记

1. /etc/shells 系统支持的 shell。

2. 脚本文件第一行注释 `#!/bin/bash`，指明脚本的解释器。

3. 标准输入内容：

   ```bash
   <<EOF
   内容...
   EOF
   ```

4. 变量名只能以数字、字母、下划线组合，且不以数字开头。

5. 单引号字符串中不识别变量，不能转义。双引号字符串中可解析变量可转义。

6. 检测系统中是否安装了 bash，并根据检测结果输出 yes 或 no：`command -v bash > /dev/null 2>&1 && echo yes || echo no`。

7. 运行脚本：

   ```shell
   bash -x script.sh # 逐行显示执行过程
   bash -n script.sh # 仅检查语法不执行
   bash -v script.sh # 显示读取的每一行
   ```

8. 算术扩展，可以使用：变量（可省略 `$`，如 `a` 直接代表变量值）、整数常量、算术运算符、比较、逻辑、位运算等：

   ```bash
   a=5
   b=3
   echo $((1 + 1))
   echo $((a + b)) # 8
   echo $((a * b)) # 15
   echo $((a > b ? a : b)) # 5（三元运算）
   
   result=$((x=3, y=5, x+y)))
   echo $result # 8
   ```

9. 命令替换，执行命令，将其标准输出作为字符串插入到当前位置：`var=$(date +%F)`。

10. 大括号扩展：

    ```bash
    # 生成一个从 1 到 8 的整数序列
    echo {1..8}
    # 输出：1 2 3 4 5 6 7 8
    
    # 指定步长
    echo {1..8..2}
    # 生成 1 3 5 7
    
    # 字母序列
    echo {a..z}
    # 生成 a 到 z 的字母
    
    # 逆序
    echo {8..1}
    # 生成 8 7 6 5 4 3 2 1
    
    echo {a,b,c} # 输出：a b c
    echo file{1,2,3}.txt # 输出：file1.txt file2.txt file3.txt
    ```

11. 字面量单词列表：

    ```bash
    echo 1 2 3 4
    
    for i in 1 2 3 4; do
        echo $i
    done
    ```

# 二、Bash

1. 是一个 Shell 解释器，负责读取用户输入或脚本文件，解释命令并执行。

2. 内置命令，不需要启动子进程即可执行命令。外部命令，由 Bash 通过 PATH 环境变量查找并调用独立可执行文件。

   - 内置命令：

   - help：列出所有内置命令及其简要说明。`help cd` 查看内置命令的详细帮助。

   - compgen -b：列出所有内置命令名（不含说明）。

   - type *命令*：查看某个命令是内置命令还是外部命令。

   - cd [*目录*]：切换当前工作目录。无参数时切换到 $HOME。

   - pwd：打印当前工作目录（等价于 `echo $PWD`）。

   - dirs：显示目录栈内容。

   - pushd [*目录*]：将当前目录压入目录栈并切换到指定目录。

   - popd：从目录栈弹出栈顶目录并切换到该目录。

   - export [*变量*=*值*]：将变量设为环境变量，使其可传递给子进程。

   - readonly [*变量*=*值*]：定义只读变量，不可修改或删除。

   - declare [*选项*] *变量*：声明变量并设置属性（如整数、数组、只读等），例如 `declare -i num=10` 声明为整数。

   - typeset：declare 的别名（部分系统）。

   - local *变量*=*值*：在函数内部定义局部变量。

   - unset *变量*：删除变量或函数。

   - set：显示或设置 Shell 变量和选项。

     ```bash
     set -e          # 任何命令失败立即退出
     set -u          # 使用未定义变量时退出
     set -x          # 打印每条执行的命令
     set -o pipefail # 管道中任一命令失败则整体失败
     ```

   - shift [*n*]：将位置参数左移 *n* 位（默认 1）。

   - getopts *optstring* *var* [*args*]：解析命令行选项（用于脚本参数解析）。args 默认为 $@（所有位置参数）。如果遇到非法选项或缺少参数，则设置 var 为 ?（或 :，若处于静默模式）并设置 OPTARG 为错误信息。

     - *optstring* 的语法：

     - 普通字母：表示一个无参数选项，如 "a" 表示接受 -a。

     - 字母后跟冒号 :：表示该选项需要一个参数，如 "b:" 表示 -b value。

     - 开头的冒号 :（可选）：如果 *optstring* 以 : 开头，则 getopts 进入静默错误模式，不自动打印错误信息，由用户自行处理。

     - 变量：

     - OPTARG：保存当前选项的参数值（如果该选项需要参数）。

     - OPTIND：保存下一个要处理的参数在位置参数中的索引。初始值为 1，每次调用 getopts 后更新。

     - 示例 1：

       ```bash
       # ./script.sh -a -b hello
       
       while getopts "ab:" opt; do
           case $opt in
               a)
                   echo "选项 -a 被指定"
                   ;;
               b)
                   echo "选项 -b 被指定，参数为：$OPTARG"
                   ;;
               ?)
                   echo "无效选项或缺少参数"
                   exit 1
                   ;;
           esac
       done
       ```

     - 示例 2：

       ```bash 
       # ./script.sh -a
       
       while getopts ":a:b:" opt; do
           case $opt in
               a)
                   echo "-a 参数：$OPTARG"
                   ;;
               b)
                   echo "-b 参数：$OPTARG"
                   ;;
               :)
                   echo "选项 -$OPTARG 缺少参数"
                   exit 1
                   ;;
               ?)
                   echo "未知选项：-$OPTARG"
                   exit 1
                   ;;
           esac
       done
       ```

     - 示例 3：

       ```bash
       # ./script.sh -v -n Alice file1 file2
       
       while getopts "vn:" opt; do
           case $opt in
               v) verbose=1 ;;
               n) name="$OPTARG" ;;
           esac
       done
       
       shift $((OPTIND - 1))   # 移除已解析的选项，剩余位置参数作为普通参数
       
       echo "verbose = $verbose"
       echo "name = $name"
       echo "剩余参数：$@"
       ```

   - echo [*字符串*]：输出文本到标准输出。支持 -n（不换行）、-e（解释转义字符）。`eval "ls -l"`。

   - printf *格式* [*参数*...]：按格式输出，功能比 echo 更强大，类似 C 语言 printf。

   - read [*变量*]：从标准输入读取一行并赋值给变量。常用 -p（提示）、-a（读入数组）。

   - readarray、mapfile：将文本行读入数组。例如 `readarray lines < file.txt`。

   - eval [*参数*]：将参数作为 Shell 命令重新解析执行。常用于动态构建命令。

   - exec [*命令*]：用指定命令替换当前 Shell 进程（不创建子进程）。若不含命令则用于重定向。

   - source *文件* 或 . *文件*：在当前 Shell 中读取并执行脚本，不启动子进程。

   - command *命令*：执行命令，并绕过函数或别名。常用 `command -v` 检查命令是否存在。`command -v ls` 输出 ls 的路径。

   - builtin *内置命令*：强制执行 Bash 内置命令，即使有同名函数或别名。

   - hash：管理命令哈希表，加速外部命令查找。

   - enable：启用或禁用内置命令。

   - times：显示当前 Shell 及子进程的用户/系统 CPU 时间。

   - jobs	列出当前 Shell 的后台作业。

   - fg [*作业号*]：将后台作业切换到前台运行。

   - bg [*作业号*]：将暂停的后台作业继续在后台运行。

   - kill [*信号*] 作业号/进程号：向作业或进程发送信号。内置 kill 支持作业号（如 %1）。

   - wait [*进程号、作业号*]：等待指定进程或作业结束，并返回其退出状态。

   - disown [*作业号*]：将作业从作业表中移除，使其不受 Shell 退出影响。

   - suspend：挂起当前 Shell（类似 Ctrl+Z）。

   - test *表达式*：条件测试，返回 0（真）或 1（假）。

   - [ *表达式* ]：test 的等价形式，注意方括号两侧必须有空格。

   - [[ *表达式* ]]：Bash 扩展测试，支持正则、模式匹配，更安全灵活。

   - history：显示命令历史列表。常用 -c 清空历史。

   - fc：编辑并重新执行历史命令。

   - alias [*别名*=*命令*]：创建或显示别名。

   - unalias *别名*：删除别名。

   - ulimit：设置或显示资源限制（如文件大小、内存等）。

   - umask：设置或显示文件创建时的默认权限掩码。

   - trap *命令列表* *信号列表*：捕获信号并执行指定命令。`trap 'echo "收到 SIGINT"; exit' INT`。

   - let：执行算术运算（类似 $(( ))）。

   - :：空命令，永远返回 0。常用于占位或无限循环。`: > file.txt` 清空文件。

   - true：返回 0。

   - false：返回 1。

   - logout：退出登录 Shell。

   - caller：返回当前子程序调用的上下文信息。

3. 关键字：

   - if、then、elif、else、fi：条件分支结构。
   - for、while、until、do、done：循环结构。
   - case、esac：多分支选择结构。
   - break：跳出当前循环。
   - continue：跳过本次循环剩余部分，进入下一次循环。
   - return [*n*]：从函数中返回，可带退出状态码。
   - exit [*n*]：退出当前 Shell 或脚本，可带退出状态码。
   - function：定义函数（也可省略）。

4. 变量与环境：

   - 普通 Shell 变量：只在当前 Shell 中有效。
     - 定义变量：*变量名*=*值*。
     - 引用变量：$变量名、${变量名}。
   - 环境变量：可传递给子进程。
   - 常见环境变量：
     - PATH：命令搜索路径。
     - HOME：当前用户主目录。
     - USER：当前用户名。
     - PWD：当前工作目录。
     - OLDPWD：上一个工作目录。
     - PS1：主提示符。
     - PS2：续行提示符。
     - IFS：内部分隔符。
     - RANDOM：随机数。
     - LINENO：当前脚本行号。
     - BASH_VERSION：Bash 版本号。
   - 特殊变量：
     - !!：代表上一条命令。
     - !$：表示上一条命令的最后一个参数。
     - $0：脚本名。
     - $1~$9：位置参数。
     - $#：参数个数。
     - $?：上一条命令的退出状态。
     - $$：当前 Shell 的 PID。
     - $!：最后一个后台进程的 PID。
     - $@：所有位置参数（每个独立）。
     - $*：所有位置参数（作为单个字符串）。

5. 配置文件：

   - /etc/profile：全局登录 Shell 配置。

   - /etc/bash.bashrc：全局交互式 Shell 配置。

   - ~/.bash_profile：用户登录 Shell 配置。

   - ~/.bash_login：用户登录 Shell 配置（备用）。

   - ~/.profile：用户登录 Shell 配置（备用）。

   - ~/.bashrc：用户交互式 Shell 配置。

   - ~/.bash_logout：用户退出登录 Shell 时执行。

   - 配置文件读取顺序：

     - 登录 Shell，例如通过 TTY、SSH 或 `bash --login`：

       1. /etc/profile

       2. ~/.bash_profile、~/.bash_login、~/.profile（三选一）

     - 交互式 Shell，在已登录的图形界面中打开终端模拟器（如 GNOME Terminal、Konsole 等）或执行 bash 命令启动的子 Shell：

       1. /etc/bash.bashrc（如果存在）

       2. ~/.bashrc

     - 非交互式 Shell，当 Bash 用于执行脚本（如 `bash script.sh` 或直接运行 ./script.sh）时：

       1. 仅当设置了 BASH_ENV 时读取该变量指定的文件。

     - 以 sh 调用（POSIX）登录：

       1. ./etc/profile

       2. ~/.profile

     - 以 sh 调用（POSIX）交互式：

       1. 若设置了 ENV，则读取该变量指定的文件。

6. 索引数组与关联数组：

   - 索引数组：

      ```bash
      arr=(apple banana cherry)
      echo ${arr[0]}          # apple
      echo ${#arr[@]}         # 数组长度
      ```

   - 关联数组：

      ```bash
      declare -A person
      person[name]="小明"
      person[age]=20
      echo ${person[name]}
      ```

7. 运算符：

   - 算术运算符：+  -  *  /  %  ** ++ -- ?:。

      ```bash
      a=10
      b=3
      echo $((a + b)) # 13
      echo $((a ** b)) # 1000
      
      let "a = 5 + 3"
      echo $a
      
      b=$(expr 5 + 3)
      echo $b
      ```

   - 字符串比较运算符：

      - =、==：字符串相等。
      - !=：字符串不相等。
      - -z：字符串为空。
      - -n：字符串非空。
      - <：按字典序小于（需转义或在 [[ ]] 中使用）。
      - \>：字典序大于。
      - =~：正则比较，例如 `[[ "abc123" =~ ^[a-z]+[0-9]+$ ]]`。
   - 整数比较运算符：
   
      - -eq：等于。
      - -ne：不等于。
      - -gt：大于。
      - -ge：大于等于。
      - -lt：小于。
      - -le：小于等于。
   
   - 文件测试运算符：
   
      - -e：文件存在。
      - -f：是普通文件。
      - -d：是目录。
      - -r：可读。
      - -w：可写。
      - -x：可执行。
      - -s：文件非空。
      - -L：是符号链接。
   
   - 逻辑运算符：
   
      - &&：逻辑与。
      - ||：逻辑或。
      - !：逻辑非。
   
8. 条件判断与循环：

   ```bash
   if 条件; then
       命令
   elif 条件; then
       命令
   else
       命令
   fi
   
   if test -f /etc/passwd; then
       echo "存在"
   fi
   
   # 等价写法
   if [ -f /etc/passwd ]; then
       echo "存在"
   fi
   
   if [[ $str == *.txt ]]; then
       echo "是 txt 文件"
   fi
   ```

   ```bash
   case 变量 in
       模式1)
           命令 ;;
       模式2)
           命令 ;;
       *)
           默认命令 ;;
   esac
   
   read -p "输入 yes/no: " answer
   case $answer in
       yes|y)
           echo "你选择了是" ;;
       no|n)
           echo "你选择了否" ;;
       *)
           echo "无效输入" ;;
   esac
   ```

   ```bash
   for 变量 in 列表; do
       命令
   done
   
   for i in 1 2 3 4 5; do
       echo $i
   done
   
   # 使用范围
   for i in {1..10}; do
       echo $i
   done
   
   # C 风格
   for ((i=0; i<5; i++)); do
       echo $i
   done
   
   for i in {1..10}; do
       if [ $i -eq 5 ]; then
           break      # 跳出循环
       fi
       if [ $i -eq 3 ]; then
           continue   # 跳过本次循环
       fi
       echo $i
   done
   ```

   ```bash
   while 条件; do
       命令
   done
   
   count=1
   while [ $count -le 5 ]; do
       echo "count = $count"
       ((count++))
   done
   ```

   ```bash
   # 条件为假时执行，直到条件为真时退出
   until 条件; do
       命令
   done
   ```

9. 函数：

   ```bash
   函数名() {
       命令
       return 返回值
   }
   
   # 或
   function 函数名 {
       命令
   }
   
   # 函数内部使用 $1、$2 等获取参数
   greet() {
       local name="$1"
       echo "你好，$name"
   }
   
   # 输出结果供调用者捕获
   add() {
       echo $(( $1 + $2 ))
   }
   result=$(add 3 5)
   echo "3 + 5 = $result"
   ```

10. 重定向：

    ```bash
    command > file       # 标准输出覆盖写入
    command >> file      # 标准输出追加写入
    command 2> file      # 错误输出覆盖写入
    command 2>&1         # 错误输出合并到标准输出
    command < file       # 从文件读取输入
    ```

11. 管道，将一个命令的标准输出直接作为另一个命令的标准输入：

    ```bash
    ls -l | grep ".txt"
    ps aux | awk '{print $2}'
    cat file.txt | sort | uniq
    ```

12. 参数扩展：

    ```bash
    var="Hello World"
    
    echo ${#var}            # 长度：11
    echo ${var:0:5}         # 截取：Hello
    echo ${var/World/Bash}  # 替换：Hello Bash
    echo ${var,,}           # 转小写：hello world
    echo ${var^^}           # 转大写：HELLO WORLD
    
    # 默认值
    echo ${unset_var:-默认值}   # 若变量未设置则使用默认值
    echo ${unset_var:=默认值}   # 若变量未设置则赋值并使用
    
    # 字符串拼接
    var2="abc"$var"123"
    ```

13. 通配符与 Globbing：

    ```bash
    *.txt          # 匹配所有 .txt 文件
    ?              # 匹配单个字符
    [abc]          # 匹配 a、b 或 c
    [0-9]          # 匹配数字
    [!0-9]         # 匹配非数字
    ```


# 三、命令

## 1. 常用命令

- 查看系统信息：lscpu free fdisk top printenv
  - uptime：查看系统启动时间、运行时间。
  - nproc：打印 CPU 核心数量。

- 基础配置：localectl timedatectl date hwclock
- 管理用户：useradd id passwd cracklib-unpacker create-cracklib-dict usermod userdel groupadd groupmod groupdel newgrp
- 管理软件包：vi dnf createrepo
- 管理服务：systemctl ln
- 管理进程：who ps top kill at crontab jobs fg bg atrm atq nice renice kill killall nohub
- 配置网络：nmcli ip ifup ifdown modprobe modinfo ss route nslookup
- LVM：pvcreate pvdisplay pvremove pvchange vgcreate vgdisplay vgchange vgextend vgreduce vgremove lvcreate lvresize lvextend lvreduce lvremove mkfs mount umount blkid resize2fs e2fsck lvchange
- 其他：uptimevmstat sar ps top free lsblk lspci ethtool dmidecode

## 2. more

- more *参数*... *文件*...：适合屏幕查看的文件阅读输出工具。more +num
  - -d 显示帮助而非响铃。
  - -f 计算逻辑行数，而非屏幕行数。
  - -l 屏蔽换页(form feed)后的暂停。
  - -c 不滚动，显示文本并清理行末。
  - -p 不滚动，清除屏幕并显示文本。
  - -s 将多行空行压缩为一行。
  - -u 屏蔽下划线。
  - -<数字> 每屏的行数。
  - +<数字> 从指定行开始显示文件。
  - +/<字符串> 从匹配搜索字符串的位置开始显示文件。
  - -V 打印版本信息。
  - 查看模式：
  - q 或者 Q 退出。
  - n 空格或者 z 翻页。滚动出 n 行。
  - n 回车下一行。滚动出 n 行。
  - h 或者 ? 帮助。
  - ns 跳过下面一行或者 n 行。
  - nd 或者 ctrl+D 滚动十一行或者 n 行，之后默认滚动 n。
  - f 跳过下面一屏或者 n 行。
  - b 或者 ctrl+B 跳过上面一屏或者 n 屏。
  - ' 转到上次搜索开始处。
  - = 打印当前行号。
  - n/PATTERN 搜索正则表达式第 n 次出现处。
  - nn 搜索前一正则表达式第 n 次出现处。
  - !cmd 或者 :!cmd 在子 shell 中执行 \<cmd> 命令。
  - v 启动编辑。
  - ctrl+l 重绘屏幕。
  - n:n 转到后面第 n 个文件。
  - n:p 转到前面第 n 个文件。
  - :f 显示当前文件名和行号。
  - . 重复前一命令。

## 3. less

- less *文件*：类似于 more 命令，但是它允许在文件中和正向操作一样的反向操作，浏览多个文件时，输入:n 切换到上一个文件，输入:p 切换到下一个文件。
  - 空格键 滚动一页。
  - 回车键 滚动一行。
  - \[pagedown] 向下翻动一页。
  - \[pageup] 向上翻动一页。
  - /str 向下搜索字符串的功能。
  - ?str 向上搜索字符串的功能。
  - n 重复前一个搜索（与 / 或 ? 有关）。
  - N 反向重复前一个搜索（与 / 或 ? 有关）。

## 4. vi

- vi *参数*... *文件*：编辑文件。
  - 正常模式：
    - hjkl 左上下右，可以使用 30j 或 30↓ 的组合按键。
    - ctrl+f 前一页。
    - ctrl+b 后一页。
    - ctrl+u 前半页。
    - ctrl+d 后半页。
    - ctrl+n 切换左侧类目树。
    - ctrl+] 函数跳转。
    - ctrl+o 函数跳回。
    - tab 选择 ycm 实时补全选项。
    - ^ 光标移至本行首字符。
    - $ 光标移至本行尾字符。
    - nw 光标移至后 n 个单词首字母。
    - nb 光标移至前 n 个单词首字母。
    - ne 光标移至后 n 个单词尾字母。
    - gg 跳转到首行。
    - G 跳到行位。
    - nG 跳转到第 n 行。
    - nx 删除包含光标处以后 n 个字符。
    - r 修改替换当前光标字符。
    - ndd 剪切 n 行。
    - ndw 剪切包含光标处 n 个单词（含空格）。
    - nde 剪切包含光标处 n 个单词（不含空格）。
    - d$ 剪切从光标字符到行尾。
    - d^ 剪切从光标字符到行首。
    - nJ 合并当前行以下 n 行内容。
    - nyy 复制 n 行。
    - nyw 复制包含光标处 n 个单词（含空格）。
    - nye 复制包含光标处 n 个单词（不含空格）。
    - y$ 复制光标到行尾。
    - y^ 复制光标到行首。
    - p 粘贴到当前文档。
    - u 撤销上次更改。
    - U 取消对当前行进行的所有操作。
    - ctrl+r 撤销上一个动作。
    - . 重复上次动作。
    - i 进入编辑模式，从当前光标处。
    - I 进入编辑模式，从当前行首。
    - a 进入编辑模式，从当前光标后。
    - A 进入编辑模式，从当前行末。
    - o 进入编辑模式，从当前行下插入新行。
    - O 进入编辑模式，从当前行上插入新行。
    - cw 进入编辑模式，删除当前光标到所在单词尾部字符。
    - c$ 进入编辑模式，删除当前光标到行尾的字符。
    - c^ 进入编辑模式，删除当前光标(不包括)之前到行首的字符。
  - 命令模式：
    - :行 跳转到指定行。
    - :set nu 显示行号。
    - :set nonu 取消显示行号。
    - :set nohlsearch 取消搜索高亮。
    - :m,ny m 行到 n 行之间的文本，:100,200y，100 行到 200 的内容。
    - :/word 自上而下查找指定的字符串 word，n 查找下一个（自上而下）,N 反向查找下一个（自下而上）。
    - :?word 自下而上查找指定字符串 word，n 查找下一个（自下而上），N 反向查找下一个（自上而下）。
    - :s/old/new old 被替换成 new。
    - :s/old/new/g 行内全部替换。
    - :#,#s/old/new/g 在行区域内进行替换，#,# 表示行号。
    - :%s/old/new/g 整个文件内的替换，加 % 表示整篇文档。
    - :s/old/new/c 替换确认。
    - esc 退出编辑。
    - :w 文件 保存。
    - :q 退出。
    - :! 命令 临时退出。
    - :q! 强制退出。
    - :qa! 退出，不保存。
    - v 进入字符可视模式。
    - shfit+v 进入行可视模式。
    - ctrl+v 进入块可视模式。

## 5. sed

- sed [*选项*] [*脚本*] [*文件*...] ：对文本查找、替换和删除处理。逐行读取输入，对于每一行，依次执行脚本中所有地址条件满足的命令，然后输出结果。省略文件时，从标准输入读取。如果斜线 `/ `匹配冲突可以换成别的符号作分割符。脚本由地址、命令和参数组成。

  - 地址：

    - *N*：第 *N* 行，例如 `5d` 删除第五行。
    - $：最后一行。
    - /*正则*/：正则匹配的行。
    - *n*,*m*：第 *n* 到第 *m* 行。
    - /*正则1*/,/*正则2*/：扫描到匹配*正则1*的行开始，到匹配*正则2*的行结束。若没到文件末尾，继续这样规则的地址匹配。
    - *N*,$：第 *N* 到最后一行。
    - *地址*!：与*地址*不匹配的行。
    - 省略地址，表示处理所有行。

  - 命令：

    - s/*原内容*/*新内容*/*标志*：替换文本。支持正则表达式。

      - 标志：
      - g：全局替换。
      - p：打印替换成功的行。
      - i：忽略大小写。
      - *数字*：替换第 n 次出现。

    - d：删除当前行，立即开始下一行。

    - p：打印当前行。

    - =：打印当前行号。

    - l：以可见形式打印（显示控制字符、行尾）。

    - a：在匹配行后追加文本。例如 `a\abc`。

    - i：在匹配行前插入文本。例如 `i\abc`。

    - c：用文本替换整行。例如 `c\abc`。

    - r：在匹配行后读取并插入文件内容。例如 `r 文件`。

    - w：将匹配行写入文件（覆盖）。例如 `w 文件`。

    - R：每次读取一行文件内容，逐行追加到输出（GNU sed 扩展）。例如 `R 文件`。

    - W：将当前模式空间写入文件，但不关闭文件（GNU sed 扩展）。例如 `W 文件`。

    - n：输出当前行（除非 `-n`），读取下一行到模式空间。

    - N：追加下一行到模式空间（用换行符连接）。

    - P：打印模式空间的第一部分（到第一个换行符为止）。

    - D：删除模式空间的第一部分，若模式空间还有内容则重新开始脚本。

    - q：退出 sed（可带退出码，如 `q10`）。

    - b：无条件跳转到标签。例如 `b 标签`。

    - t：如果最近一次替换成功，跳转到标签。例如 `t 标签`。

    - :：定义标签。

    - h：将模式空间复制到保持空间。

    - H：将模式空间追加到保持空间。

    - g：将保持空间复制到模式空间。

    - G：将保持空间追加到模式空间。

    - x：交换模式空间和保持空间的内容。

    - {}：命令分组。多个命令需要作用于同一地址。

      ```sed
      地址 {
          命令1
          命令2
          ...
      }
      地址 { 命令1; 命令2; }
      ```

  - 选项：

    - -n：取消默认输出，只输出显式打印的内容。
    - -e *脚本*：添加一个脚本（可多次使用）。
    - -f *脚本文件*：从文件读取脚本。
    - -i[*后缀*]：直接修改文件（可备份为后缀）。
    - -E 或 -r：使用扩展正则表达式（ERE）。
    - -u：无缓冲输出（GNU sed）。

  - 在脚本文件中，`#` 开头的行是注释（GNU sed 支持；POSIX 要求脚本第一行可以是注释）。


## 6. awk

- awk *参数* *文件*
  - -F '分割符或者正则'
  - -f 文件
  - 'print $1 $2' *文件* 打印每行的第一个字段和第二个字段。
  - '/^xxx/print $0' 文件 打印匹配的行内容。
  - FS 和 OFS 字段分割符， OFS 表示输出的字段分割符。
  - RS 记录分割符。
  - NR 和 FNR 行数。
  - NF 字段数量，最后一个可以用 $NF 取出。
  - ARGC ARGV
  - = 变量声明
  - 赋值操作符：++ -- += -= *= /= %= ^=
  - 关系操作符：< > <= >= == != ~ !~ || && !
  - BEGIN{} {} END{}
  - if() else if() else
  - while()
  - do{}while()
  - for(;;)
  - for(v in array)
  - delete array[index]
  - break continue
  - sin() cos() int() rand() srand()
  - gsub(r,s,t) sub(r,s,t) substr(s,p,n) index(s,t) length(s) match(s,r) split(s,a,sep)
  - function name(params){ return result}

## 7. ulimit

- ulimit
  - -a 查看当前用户系统资源使用限制，例如打开文件数。
  - -s 查看栈大小。单位 KB。

## 8. tar

- tar
  - -zxvf xxx.tar.gzip 解压 gzip 文件
  - -Jxvf xxx.tar.xz 解压 xz 文件

## 9. ln

- ln
  - ln -s lib64 /usr/local/lib：创建软连接

## 10. find

- find：搜索文件
  - sudo find / -name xxx -type f：搜索文件。

## 11. uname

- uname
  - uname -s：Linux
  - uname -r：6.1.0-28-amd64

## 12. xprop

- WM_CLASS：点击应用窗口，获取 StartupWMClass 值（输出值的第二个）。

## 13. ss

 - -t：列出 tcp 的端口使用。
 - -u：列出 udp 的端口使用。
 - -p：列出线程信息。
 - -l：仅显示处于 LISTEN（监听）状态的端口。
 - -n：以数字形式显示地址和端口号（不解析主机名/服务名）。

## 14. ps

- -p *pid*：指定线程 ID。
- -o args=：显示线程命令行。
- a：显示与终端关联的所有用户进程。
- x：显示没有控制终端的进程（如守护进程）。
- -e：显示所有进程。
- -C *cmd*：显示命令名完全匹配 *cmd* 的进程。
- -u *user*：显示指定用户（用户名或 UID）的进程。
- -L：显示线程（轻量级进程）信息。
- u：用户导向格式。
