package com.djg.algorithm.dp;

/**
 * 凑零钱
 *
 * 状态定义：所谓状态，即原问题与子问题之间的变化量，这里就是硬币的额度
 * 状态转义方程：
 * dp[n] = 0, n = 0
 * dp[n] = -1, n < 0
 * dp[n] = min(dp[n-coin] + 1 | coin ∈ coins)
 *
 *
 * https://github.com/jiajunhua/labuladong-fucking-algorithm/blob/master/%E5%8A%A8%E6%80%81%E8%A7%84%E5%88%92%E7%B3%BB%E5%88%97/%E5%8A%A8%E6%80%81%E8%A7%84%E5%88%92%E8%AF%A6%E8%A7%A3%E8%BF%9B%E9%98%B6.md
 * https://leetcode-cn.com/problems/coin-change/solution/javabei-wang-lu-dfs-dpyou-hua-quan-bu-zo-4599/
 *
 */
public class CoinChange {
    public int coinChange(int[] coins, int amount) {
        if (amount == 0) return 0;
        // 使用dp table的方式，自底向上进行动态规划
        int[] dp = new int[amount + 1];
        dp[0] = 0;

        // 遍历target数额
        for (int amounttt = 1; amounttt <= amount; amounttt++) {
            // 初始值
            dp[amounttt] = Integer.MAX_VALUE;
            // 遍历coin数额
            for (int coin : coins) {
                int balance = amounttt - coin;
                if (balance >= 0) {
                    dp[amounttt] = Math.min(dp[amounttt], 1 + dp[balance]);
                }
            }
        }
        return dp[amount] == (amount + 1) ? -1 : dp[amount];
    }
}
