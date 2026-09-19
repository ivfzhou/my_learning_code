# -*- coding: utf-8 -*-
"""
fix_mojibake.py — 修复 UTF-8 被误按 GBK 解码（或反向）导致的中文乱码

典型场景：日志/构建工具输出中 "签名" 变成 "绛惧悕"，"参数" 变成 "鍙傛暟"。
成因：原始文本是 UTF-8 字节，却被按 GBK/GB18030 解码显示（再被保存为 UTF-8）。
修复：乱码字符串 .encode('gb18030') -> .decode('utf-8') 还原。

进阶处理：若乱码文本中含 U+FFFD（�，Java/.NET 解码器丢失字节后的替换符），
将每个 U+FFFD 视为“未知的 1 字节”，利用 UTF-8 结构约束枚举候选字节，
并按以下启发式评分动态规划选最优（无法确定的字节以 � 标出）：
  1. 内置汉字频率表（常见字优先）
  2. 文档自身上下文的单字/二元组频率（同一文件用词一致）
  3. 内置日志/技术领域常用词二元组

用法：
    python fix_mojibake.py <输入文件> [-o 输出文件] [--direction utf8-as-gbk|gbk-as-utf8] [--in-place]

默认：-o 省略时输出到 <输入文件>.fixed.<后缀>；只修复判定为乱码的行，其余行原样保留。
退出码：0 全部成功；1 存在无法修复的行。
"""
import argparse
import os
import sys

# 常见 mojibake 标志字符（UTF-8 中文被按 GBK 解码后高频出现的字）
MOJIBAKE_MARKERS = set(
    "锛绛鍑鎴涓鏂姝宸畬灏忓ぇ鏉冮檺鎵嬪姩瀹屾垚寮濮嬭幏鍙栧嚟鎹俊鎭"
    "閫熷簲搴旂姸鎬佽鏇挎崲鍚嶇О璁よ瘉鍚戝姩浠诲姟杩愯寰蒋瀹℃牳"
    "娴峰妯″紡鐐瑰嚮纭畾閿欒涓存椂鍒嗙墖鎵归潰閾惧埡锋眰"
)

# 常用汉字频率表（按使用频率大致降序），用于丢失字节候选打分
COMMON_CHARS = (
    "的一是不了在人有我他这中大来上个国和地说要就去到会也为能于你年得"
    "那后之自家里着成下可天而子过然没所如多对其只以都生发事时心看想作"
    "开从当什前方面经定听公长些还起身行因它分外老十情者无少名头法此之"
    "日二又点动两住已同位间机手正全门题并实重信关意把应明样理战世新文"
    "度物金放太进备件功告务白凭管息数本源路系式目类名品各达内片求运处"
    "查参状结始完获认初径临片批传载审件等准扫签任核执更订单据库图表网"
    "页用户错误失败成功信息数据系统服务任务运行处理路径签名上传下载审核"
    "获取准备等待检查参数结果状态初始化凭据认证批次中文临时凭证时间开始"
    "结束完成文件目录配置版本构建发布测试环境变量输入输出返回创建删除修"
    "改更新查询提交启动停止继续暂停取消确认保存读取写入发送接收请求响应"
    "连接关闭打开解析扫描过滤排序计算比较转换编码解码加密解密压缩解压合"
    "并拆分复制移动重命名权限角色账户登录注册验证授权令牌密钥证书协议地"
    "址端口主机域名缓存队列消息事件日志监控告警统计报表分析展示编辑预览"
    "搜索选择插入替换追加清空重置提交回滚分支标签里程碑项目团队成员工具"
    "设置选项菜单按钮窗口对话框面板布局样式主题语言地区字符段篇章节目录"
    "索引备注说明帮助关于联系方式邮箱电话地址姓名昵称头像简介描述标题内"
    "容摘要关键词标签分类归档评论点赞分享收藏关注粉丝浏览阅读观看播放暂"
    "停快进快退音量静音全屏退出最小化最大化还原拖拽滚动点击双击右键悬停"
    "焦点失焦启用禁用显示隐藏展开折叠锁定解锁刷新加载卸载安装升级降级卸"
    "载备份恢复同步异步阻塞非阻塞并发并行串行分布式集群负载均衡容错降级"
    "限流熔断重试超时幂等事务一致性可用性分区容错性性能优化调优基准压力"
    "容量规划弹性伸缩自动化运维监控告警日志追踪链路调用依赖注入控制反转"
    "面向对象函数式编程响应式声明式命令式同步异步回调 Promise 协程线程进"
    "程锁互斥信号量条件变量读写锁自旋锁死锁活锁饥饿公平非公平可重入不可"
    "重入悲观乐观粗粒度细粒度共享独占排他读写升级降级尝试超时中断取消完"
    "成异常错误堆栈跟踪调试断点单步步入步出跳过继续暂停终止恢复检查监视"
    "表达式变量局部全局静态常量字面值字面量类型推断泛型模板特化实例化构"
    "造析构拷贝移动赋值比较哈希相等不等大于小于大于等于小于等于逻辑与逻"
    "辑或逻辑非按位与按位或按位异或按位取反左移右移无符号右移加法减法乘"
    "法除法取模自增自减赋值复合赋值三元条件空值合并可选链展开运算符剩余"
    "参数解构赋值模块导入导出默认导出具名导出命名空间包库框架平台运行时"
    "虚拟机解释器编译器链接器加载器调试器分析器格式化检查器覆盖率复杂度"
    "重复率技术债务重构整洁代码设计模式原则约定惯例规范标准最佳实践反模"
    "式代码评审走查结对编程测试驱动行为驱动验收标准持续集成持续交付持续"
    "部署蓝绿金丝雀滚动灰度影子流量镜像回放录制模拟桩假对象替身间谍哑"
    "元 fake dummy stub spy mock fixture setup teardown suite case assert expect"
)

# 日志/技术领域常用词（提取二元组用于上下文打分）
COMMON_WORDS = (
    "运行中 审核中 处理中 加载中 等待中 进行中 已完成 已取消 已失败 已成功 "
    "上传到 下载到 保存到 移动到 复制到 发送到 提交到 信息到 路径 文件 成功 "
    "失败 开始 完成 参数 检查 获取 凭据 信息 状态 详情 初始化 处理器 步骤 准备 "
    "扫描 待签名 文档 解析 源路径 目标路径 等待 签名 上传 分片 响应 临时 凭证 "
    "请求 批次 任务 结果 数据 耗时 微软 审核 证书 构建 驱动 每日 海外 证书签名 "
    "电脑管家 加白 替换 方式 模式 定制 摘要 文件签名 时间 错误 警告 重试 超时 "
    "连接 关闭 打开 解析 扫描 过滤 计算 转换 编码 解码 创建 删除 修改 更新 查询 "
    "提交 启动 停止 继续 暂停 取消 确认 保存 读取 写入 发送 接收 返回 输入 输出 "
    "所有 以及 对于 关于 由于 按照 根据 通过 正在 已经 将要 需要 可以 没有 "
    "我们 他们 这个 那个 什么 现在 知道 因为 所以 如果 还是 工作 问题"
)


def _build_bigrams(words: str) -> set:
    bi = set()
    for w in words.split():
        for i in range(len(w) - 1):
            bi.add(w[i:i + 2])
    return bi


WORD_BIGRAMS = _build_bigrams(COMMON_WORDS)
COMMON_INDEX = {c: i for i, c in enumerate(COMMON_CHARS)}
N_COMMON = len(COMMON_CHARS)

# 高频中文标点，按频率降序（丢失字节候选为全角标点时优先，同分取靠前者）
COMMON_PUNCT = "，。、：；？！…—·“”‘’（）《》【】「」"
COMMON_PUNCT_INDEX = {c: i for i, c in enumerate(COMMON_PUNCT)}


def _is_pua(ch: str) -> bool:
    return 0xE000 <= ord(ch) <= 0xF8FF


def _is_cjk(ch: str) -> bool:
    o = ord(ch)
    return 0x4E00 <= o <= 0x9FFF or 0x3400 <= o <= 0x4DBF


def looks_like_mojibake(line: str) -> bool:
    """粗判一行是否包含 UTF-8-as-GBK 乱码迹象。"""
    score = 0
    for ch in line:
        if ch == "�" or _is_pua(ch):
            return True
        if ch in MOJIBAKE_MARKERS:
            score += 1
            if score >= 2:
                return True
    return False


def _char_base_score(ch: str) -> float:
    if _is_cjk(ch):
        return 3.0
    o = ord(ch)
    pidx = COMMON_PUNCT_INDEX.get(ch)
    if pidx is not None:
        return 4.5 + (len(COMMON_PUNCT) - pidx) / len(COMMON_PUNCT)
    if 0x3000 <= o <= 0x303F or 0xFF00 <= o <= 0xFFEF:  # 中文标点/全角
        return 2.5
    if ch.isprintable():
        return 1.0
    return 0.0


class ScoringContext:
    """文档级统计 + 内置词表，用于丢失字节候选打分。"""

    def __init__(self):
        self.doc_uni = {}
        self.doc_bi = set()

    def feed(self, text: str):
        cjk = [c for c in text if _is_cjk(c)]
        for c in cjk:
            self.doc_uni[c] = self.doc_uni.get(c, 0) + 1
        for i in range(len(cjk) - 1):
            self.doc_bi.add(cjk[i] + cjk[i + 1])

    def bigram_hit(self, a: str, b: str) -> bool:
        return bool(a and b) and ((a + b) in self.doc_bi or (a + b) in WORD_BIGRAMS)

    def bonus(self, ch: str, prev: str) -> float:
        b = 0.0
        idx = COMMON_INDEX.get(ch)
        if idx is not None:
            b += (N_COMMON - idx) / N_COMMON * 2.0
        freq = self.doc_uni.get(ch, 0)
        if freq:
            b += min(2.0, 0.5 * freq)
        if self.bigram_hit(prev, ch):
            b += 4.0
        return b


EMPTY_CTX = ScoringContext()


def _decode_next_char(items, j: int):
    """不解码含未知字节的序列；返回位置 j 处的下一个完整字符或 None。"""
    if j >= len(items):
        return None
    b0 = items[j]
    if b0 is None:
        return None
    if b0 < 0x80:
        return chr(b0)
    length = 2 if b0 < 0xE0 else (3 if b0 < 0xF0 else 4)
    if j + length > len(items):
        return None
    seg = items[j:j + length]
    if any(v is None for v in seg):
        return None
    try:
        return bytes(seg).decode("utf-8")
    except UnicodeDecodeError:
        return None


def solve_bytes(items, ctx: ScoringContext = EMPTY_CTX) -> str:
    """
    将字节序列（int 或 None=未知字节）按 UTF-8 解码（正向 DP）。
    未知字节按 UTF-8 结构约束枚举候选，启发式评分取最优。
    """
    n = len(items)
    NEG_INF = float("-inf")
    # dp[i] = (score, text, last_char)
    dp = [(NEG_INF, "", "")] * (n + 1)
    dp[0] = (0.0, "", "")
    for i in range(n):
        sc0, tx0, last0 = dp[i]
        if sc0 == NEG_INF:
            continue
        # 选项 A：放弃该字节
        cand_sc = sc0 - 8.0
        if cand_sc > dp[i + 1][0]:
            dp[i + 1] = (cand_sc, tx0 + "�", "")
        # 选项 B：尝试 1~4 字节 UTF-8 序列
        for L in range(1, 5):
            if i + L > n:
                break
            window = items[i:i + L]
            unk = [k for k in range(L) if window[k] is None]
            if len(unk) > 1:
                break  # 相邻多个未知字节，放弃精确还原
            if not unk:
                candidates = [None]
            else:
                k = unk[0]
                rng = (list(range(0x20, 0x7F)) + list(range(0xC2, 0xF5))) if k == 0 \
                    else range(0x80, 0xC0)
                candidates = list(rng)
            for cand in candidates:
                b = bytearray()
                for k in range(L):
                    v = window[k]
                    b.append(cand if v is None else v)
                try:
                    ch = bytes(b).decode("utf-8")
                except UnicodeDecodeError:
                    continue
                total = sc0
                prev = last0
                for c in ch:
                    total += _char_base_score(c)
                    if _is_cjk(c):
                        total += ctx.bonus(c, prev)
                    prev = c
                # 右侧上下文二元组
                nxt = _decode_next_char(items, i + L)
                if nxt and _is_cjk(ch[-1]) and ctx.bigram_hit(ch[-1], nxt):
                    total += 4.0
                if total > dp[i + L][0]:
                    dp[i + L] = (total, tx0 + ch, ch[-1])
    return dp[n][1]


def recover_text(text: str, src_enc: str, ctx: ScoringContext = EMPTY_CTX) -> str:
    """
    乱码 str -> 按错误解码所用编码编回字节 -> 按 UTF-8 解码。
    U+FFFD 视为未知 1 字节；无法按 src_enc 编码的字符原样保留。
    """
    out, buf = [], []

    def flush():
        if buf:
            out.append(solve_bytes(buf, ctx))
            buf.clear()

    for ch in text:
        if ch == "�":
            buf.append(None)
            continue
        try:
            buf.extend(ch.encode(src_enc))
        except UnicodeEncodeError:
            flush()
            out.append(ch)
    flush()
    return "".join(out)


def fix_line(line: str, src_enc: str, dst_enc: str,
             ctx: ScoringContext = EMPTY_CTX) -> tuple:
    """修复单行。返回 (修复后文本, 状态)：fixed / partial / failed / skipped。"""
    if not looks_like_mojibake(line):
        return line, "skipped"
    try:
        return line.encode(src_enc).decode(dst_enc), "fixed"
    except (UnicodeEncodeError, UnicodeDecodeError):
        pass
    if dst_enc != "utf-8":
        return line, "failed"
    recovered = recover_text(line, src_enc, ctx)
    if recovered != line:
        return recovered, "partial"
    return line, "failed"


def main() -> int:
    ap = argparse.ArgumentParser(description="修复 UTF-8/GBK 双重编码导致的中文乱码")
    ap.add_argument("input", help="输入文件路径")
    ap.add_argument("-o", "--output", help="输出文件路径（默认 <输入>.fixed.<后缀>）")
    ap.add_argument("--direction", choices=["utf8-as-gbk", "gbk-as-utf8"],
                    default="utf8-as-gbk",
                    help="utf8-as-gbk: UTF-8 被按 GBK 解码（“签名”->“绛惧悕”，默认）；"
                         "gbk-as-utf8: GBK 被按 UTF-8 解码（反向场景）")
    ap.add_argument("--in-place", action="store_true", help="直接覆盖原文件")
    args = ap.parse_args()

    if args.direction == "utf8-as-gbk":
        src_enc, dst_enc = "gb18030", "utf-8"
    else:
        src_enc, dst_enc = "utf-8", "gb18030"

    with open(args.input, "rb") as f:
        raw = f.read()
    try:
        text = raw.decode("utf-8")
    except UnicodeDecodeError:
        text = raw.decode("utf-8", errors="replace")

    lines = text.splitlines(keepends=True)

    # 第一遍：不带上下文修复，收集文档级中文统计
    ctx = ScoringContext()
    first_pass = [fix_line(l, src_enc, dst_enc) for l in lines]
    for fixed, status in first_pass:
        if status in ("fixed", "skipped"):
            ctx.feed(fixed)
        elif status == "partial":
            ctx.feed(fixed)

    # 第二遍：用文档统计重试 partial 行，提高丢失字节推断准确率
    stats = {"fixed": 0, "partial": 0, "failed": 0, "skipped": 0}
    fixed_lines = []
    for line, (fixed, status) in zip(lines, first_pass):
        if status == "partial":
            fixed, status = fix_line(line, src_enc, dst_enc, ctx)
        stats[status] += 1
        fixed_lines.append(fixed)
    result = "".join(fixed_lines)

    if args.in_place:
        output = args.input
    elif args.output:
        output = args.output
    else:
        base, ext = os.path.splitext(args.input)
        output = f"{base}.fixed{ext}"

    with open(output, "wb") as f:
        f.write(result.encode("utf-8"))

    print(f"输入: {args.input}")
    print(f"输出: {output}")
    print(f"方向: {args.direction} ({src_enc} -> {dst_enc})")
    print(f"行统计: 完全修复 {stats['fixed']}, 含丢失字节(已推断) {stats['partial']}, "
          f"失败 {stats['failed']}, 无需处理 {stats['skipped']}")
    return 0 if stats["failed"] == 0 else 1


if __name__ == "__main__":
    sys.exit(main())
