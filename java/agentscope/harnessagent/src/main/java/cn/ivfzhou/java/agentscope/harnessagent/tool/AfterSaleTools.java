package cn.ivfzhou.java.agentscope.harnessagent.tool;

import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 售后场景的业务工具集。
 *
 * <p>示例用途：这里全部返回内置的模拟数据，真实接入时把方法体换成 RPC / 数据库查询即可，
 * 工具签名与描述不用改，Agent 侧无感。
 */
public class AfterSaleTools {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final AtomicLong TICKET_SEQ = new AtomicLong(20260000);

    private static final Map<String, String> TICKETS = new ConcurrentHashMap<>();

    @Tool(
            name = "query_order",
            readOnly = true,
            description = "查询订单快照：商品、实付金额、优惠构成、下单/发货/签收时间、订单状态。"
                    + " 需要确认“发生了什么”时先调它。")
    public String queryOrder(
            @ToolParam(name = "order_id", description = "订单号") String orderId) {
        return """
                订单号：%s
                商品：XX 品牌 无线降噪耳机 Pro（黑色） x1
                实付金额：899.00 元（商品 999.00 - 满减 100.00，运费 0.00）
                优惠构成：满减 100.00（平台承担 60.00，商家承担 40.00）
                支付方式：微信支付
                下单时间：2026-09-01 10:12
                发货时间：2026-09-01 19:40
                签收时间：2026-09-03 15:22
                订单状态：已签收（售后期内）
                发票状态：已开票（电子普票）
                """.formatted(orderId);
    }

    @Tool(
            name = "query_logistics",
            readOnly = true,
            description = "查询物流轨迹与异常标记：揽收、在途、派送、签收节点，以及是否停滞、停滞天数。")
    public String queryLogistics(
            @ToolParam(name = "order_id", description = "订单号") String orderId) {
        return """
                运单号：SF%08d
                承运商：顺丰速运
                轨迹：
                - 2026-09-01 19:40 已揽收（深圳宝安集散点）
                - 2026-09-02 02:15 已发出，下一站 武汉中转场
                - 2026-09-02 21:03 到达 武汉中转场
                - 2026-09-03 09:12 已发出，下一站 洪山区网点
                - 2026-09-03 15:22 已签收，签收人：本人
                异常标记：无（无停滞、无破损登记、有签收底单）
                包裹重量：0.62 kg（出库重量 0.60 kg）
                """.formatted(Math.abs(orderId.hashCode()) % 100_000_000L);
    }

    @Tool(
            name = "calc_refund",
            readOnly = true,
            description = "按政策试算退款金额与构成（商品实付、运费、优惠分摊、运费险），不产生任何实际退款。")
    public String calcRefund(
            @ToolParam(name = "order_id", description = "订单号") String orderId,
            @ToolParam(name = "reason", description = "退款原因：quality（质量问题）/ no_reason（无理由）/ logistics（物流异常）")
            String reason,
            @ToolParam(
                    name = "quantity",
                    description = "退款商品数量，默认 1",
                    required = false)
            Integer quantity) {
        int qty = quantity == null ? 1 : quantity;
        boolean sellerPaysFreight = !"no_reason".equalsIgnoreCase(reason);
        double goods = 899.00 * qty;
        double freight = sellerPaysFreight ? 12.00 : 0.00;
        double subsidy = 60.00 * qty;
        double insurance = "quality".equalsIgnoreCase(reason) ? 12.00 : 0.00;
        double total = goods + freight - subsidy + insurance;
        return """
                订单号：%s
                退款类型：%s
                商品实付：%.2f 元（%d 件）
                商家承担运费：%.2f 元
                平台补贴分摊（扣回）：-%.2f 元
                运费险理赔：%.2f 元
                合计退款：%.2f 元
                到账时效：微信支付 1-3 个工作日（预计 %s 前）
                备注：已开票订单需先红冲发票，时效可能顺延 1-3 个工作日
                """.formatted(
                orderId,
                reason,
                goods,
                qty,
                freight,
                subsidy,
                insurance,
                total,
                LocalDateTime.now().plusDays(3).format(FMT));
    }

    @Tool(
            name = "query_user_risk_profile",
            readOnly = true,
            description = "查询用户风险画像：近 90 天退货率与单量、累计赔付金额、账号状态。仅风控审核场景使用。")
    public String queryUserRiskProfile(
            @ToolParam(name = "user_id", description = "用户 ID") String userId) {
        return """
                用户 ID：%s
                近 90 天订单数：14
                近 90 天退货数：3（退货率 21%%）
                近 90 天退款原因分布：质量问题 2 / 无理由 1
                近 30 天累计赔付：0.00 元
                收货地址变更：无
                账号状态：正常（已实名，无设备/地址关联异常）
                """.formatted(userId);
    }

    @Tool(
            name = "create_aftersale_ticket",
            description = "创建售后工单并升级人工复核。单笔赔付超过 500 元、或命中风控规则时必须调用。")
    public String createAftersaleTicket(
            @ToolParam(name = "order_id", description = "订单号") String orderId,
            @ToolParam(name = "type", description = "工单类型：refund / exchange / logistics / risk")
            String type,
            @ToolParam(name = "amount", description = "涉及金额，单位元") double amount,
            @ToolParam(name = "summary", description = "一句话摘要，说明为什么需要人工复核")
            String summary) {
        String ticketId = "AS" + TICKET_SEQ.incrementAndGet();
        TICKETS.put(ticketId, orderId + "|" + type + "|" + amount + "|" + summary);
        return """
                工单号：%s
                关联订单：%s
                类型：%s
                金额：%.2f 元
                摘要：%s
                状态：待人工复核（承诺 24 小时内响应）
                """.formatted(ticketId, orderId, type, amount, summary);
    }

    @Tool(
            name = "draft_reply",
            readOnly = true,
            description = "按标准模板生成发给用户的话术初稿，包含结论、依据、金额、时效、下一步五段。")
    public String draftReply(
            @ToolParam(name = "conclusion", description = "结论：退款/换货/驳回/需人工复核") String conclusion,
            @ToolParam(name = "evidence", description = "依据：政策条款 + 订单事实，用分号分隔")
            String evidence,
            @ToolParam(name = "amount", description = "金额与构成，如：899.00 元（商品 887.00 + 运费 12.00）")
            String amount,
            @ToolParam(name = "deadline", description = "承诺时效，必须是具体日期") String deadline,
            @ToolParam(name = "next_step", description = "下一步：用户与平台分别要做什么")
            String nextStep) {
        return """
                【结论】%s
                【依据】%s
                【金额】%s
                【时效】%s
                【下一步】%s
                """.formatted(conclusion, evidence, amount, deadline, nextStep);
    }
}
