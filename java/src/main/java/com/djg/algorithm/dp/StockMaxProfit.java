package com.djg.algorithm.dp;

/**
 * 股票的最大利润
 *
 * 状态定义： 设动态规划列表 dp，dp[i] 代表以 prices[i] 为结尾的子数组的最大利润
 * 状态转移方程：dp[i] = max(dp[i−1], prices[i] − min(prices[0:i]))
 *
 *
 * https://leetcode-cn.com/problems/gu-piao-de-zui-da-li-run-lcof/solution/mian-shi-ti-63-gu-piao-de-zui-da-li-run-dong-tai-2/
 *
 */


public class StockMaxProfit {
    public int maxProfit(int[] prices) {
        // 记录最低价格
        int cost = Integer.MAX_VALUE;
        // 即dp[i]
        int profit = 0;

        for (int price : prices) {
            cost = Math.min(cost, price);
            profit = Math.max(profit, price - cost);
        }
        return profit;
    }
}

