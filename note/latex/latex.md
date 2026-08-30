# 一、笔记

1. 空格会被忽略，使用 \,、\quad、\qqaud。
1. 使用字符 `# % & _ { }` 需要用反斜杠转义。

# 二、语法

## 2.1 命令

以反斜杠开头，后跟命令名：`\command`。  
命令的必填参数放在命令后，以花括号包围：`\command{必填参数}`。  
命令的可选参数也放命令后，以方括号包围：`\command[可选参数]{必选参数}`。

## 2.2 环境

以 \begin{环境名} 开始，以 \end{环境名} 结束：
```latex
\begin{环境名}
    内容
\end{环境名}
```

## 2.3 上标

脱字符 ^ 后跟要上标的字符，多个字符用花括号包围：`x^{12}`。  
例子：  
`x^{12}`：  
$x^{10}$

## 2.4 下标

下划线 _ 后跟要下标的字符，多个字符用花括号包围：`_{12}`。  
例子：  
`log_{2}10`：  
$log_{2}10$

# 三、命令

## 3.1 分数

`\frac{分子}{分母}`：  
$\frac{a+b}{c+d}$

## 3.2 根号

`\sqrt[n]{x}`：  
$\sqrt[3]{9}$

## 3.3 希腊字母

```latex
\alpha \beta \gamma \delta
\pi \theta \lambda \mu
\Sigma \Omega \Delta
```

$$
\alpha \beta \gamma \delta
\pi \theta \lambda \mu
\Sigma \Omega \Delta
$$

## 3.4 求和

`\sum_{i=1}^{n} i`：  
$\sum_{i=1}^{n} i$。

## 3.5 积分

`\int_{0}^{1} x^2 \, dx`：  
$\int_{0}^{1} x^2 \, dx$

## 3.6 极限

`\lim_{x \to \infty} f(x)`：  
$\lim_{x \to \infty} f(x)$

## 3.7 矩阵

```latex
\begin{pmatrix}
    a & b \\
    c & d
\end{pmatrix}
```

$$
\begin{pmatrix}
    a & b \\
    c & d
\end{pmatrix}
$$

```latext
\begin{bmatrix}
    1 & 0 \\
    0 & 1
\end{bmatrix}
```

$$
\begin{bmatrix}
    1 & 0 \\
    0 & 1
\end{bmatrix}
$$

# 3.8 符号

```latex
\leq  \geq  \neq  \approx  \times  \div  \pm
\infty  \in  \subset  \cup  \cap  \forall  \exists
```

$$
\leq  \geq  \neq  \approx  \times  \div  \pm
\infty  \in  \subset  \cup  \cap  \forall  \exists
$$

# 3.9 加粗

`\textbf{加粗}`： 
$\textbf{加粗}$

## 3.10 文本

`\text{abc}`：  
$\text{abc}$

# 四、环境

## 4.1 矩阵

列用 & 分隔，行用 \\\ 分隔。
- matrix 无括号
- pmatrix 圆括号 ( )
- bmatrix 方括号 [ ]
- Bmatrix 花括号 { }
- vmatrix 单竖线 | |
- Vmatrix 双竖线 || ||
```latex
\begin{pmatrix}
  1 & 2 \\
  3 & 4
\end{pmatrix}
```

$$
\begin{pmatrix}
  1 & 2 \\
  3 & 4
\end{pmatrix}
$$

## 4.2 对齐与排版

用 & 指定对齐位置，\\\ 换行。
- aligned 在公式内部实现多行对齐：
  ```latex
  $$
  \begin{aligned}
    f(x) &= (x+1)^2 \\
         &= x^2 + 2x + 1
  \end{aligned}
  $$
  ```

  $$
  \begin{aligned}
    f(x) &= (x+1)^2 \\
         &= x^2 + 2x + 1
  \end{aligned}
  $$
- gathered 将多行公式居中排列，不进行对齐：
  ```latex
  \begin{gathered}
    a = b + c \\
    d = e + f
  \end{gathered}
  ```

  $$
  \begin{gathered}
    a = b + c \\
    d = e + f
  \end{gathered}
  $$
- cases 用于分段函数或分类表达式：
  ```latex
  f(x) = \begin{cases}
    x^2, & x \geq 0 \\
    -x, & x < 0
  \end{cases}
  ```

  $$
  f(x) = \begin{cases}
    x^2, & x \geq 0 \\
    -x, & x < 0
  \end{cases}
  $$
- array 自定义表格或矩阵，可指定列对齐方式和列分隔线。常需要配合 \left( 和 \right) 等手动加括号。{cc|c} 表示两列居中、一条竖线、第三列居中：
  ```latex
  \left(
  \begin{array}{cc|c}
    1 & 2 & 3 \\
    4 & 5 & 6
  \end{array}
  \right)
  ```

  $$
  \left(
  \begin{array}{cc|c}
    1 & 2 & 3 \\
    4 & 5 & 6
  \end{array}
  \right)
  $$
- split 用于单个公式的多行拆分：
  ```latex
  \begin{split}
    a &= b + c \\
      &= d + e
  \end{split}
  ```

  $$
  \begin{split}
    a &= b + c \\
      &= d + e
  \end{split}
  $$
- equation 带编号的单行公式。
- equation* 不带编号的单行公式。
- align 多行对齐公式，每行可编号。
- align* 多行对齐公式，不编号。
- gather 多行居中公式，每行可编号。
- gather* 多行居中公式，不编号。
- multline 多行公式，首行左对齐，末行右对齐。
- multline* 不编号的 multline。
