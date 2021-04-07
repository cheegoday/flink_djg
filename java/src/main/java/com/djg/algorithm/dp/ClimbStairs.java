package com.djg.algorithm.dp;

/**
 * 爬楼梯
 *
 * 思路：
 * 状态定义：dp[i]代表爬i层楼梯所支持的方案
 * 状态转移方程：dp[i] = dp[i-1] + dp[i-2]
 *
 * 使用迭代法，自底向上，进行动态规划
 *
 * https://leetcode-cn.com/problems/climbing-stairs/solution/hua-jie-suan-fa-70-pa-lou-ti-by-guanpengchn/
 *
 */


class ClimbStairs {
    public int climbStairs(int n) {
        int[] dp = new int[n + 1];
        dp[0] = 1;
        dp[1] = 1;
        for(int i = 2; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n];
    }
}