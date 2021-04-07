package com.djg.algorithm.dp;

/**
 * 连续子数组最大和
 *
 * 输入: nums = [-2,1,-3,4,-1,2,1,-5,4]
 * 输出: 6
 * 解释: 连续子数组 [4,-1,2,1] 的和最大，为 6
 *
 * 思路：
 * 状态定义：dp[i]代表以元素nums[i]为结尾的连续子数组的最大和
 *
 * 状态转移方程：
 * 当 dp[i - 1] > 0 时：执行 dp[i] = dp[i-1] + nums[i] ；
 * 当 dp[i − 1] ≤ 0 时：执行 dp[i] = nums[i] ；
 *
 * 使用迭代法，自底向上，进行动态规划
 *
 *
 * https://leetcode-cn.com/problems/lian-xu-zi-shu-zu-de-zui-da-he-lcof/
 *
 */



public class ContinuousSubarrayMaxSum {
    public int maxSubArray(int[] nums) {
        // 初始状态： dp[0] = nums[0]，即以 nums[0] 结尾的连续子数组最大和为 nums[0]
        int res = nums[0];
        for(int i = 1; i < nums.length; i++) {
            nums[i] += Math.max(nums[i - 1], 0);
            res = Math.max(res, nums[i]);
        }
        return res;
    }
}