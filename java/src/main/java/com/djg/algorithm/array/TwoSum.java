package com.djg.algorithm.array;

import java.util.HashMap;
import java.util.Map;

// https://github.com/jiajunhua/labuladong-fucking-algorithm/blob/master/%E7%AE%97%E6%B3%95%E6%80%9D%E7%BB%B4%E7%B3%BB%E5%88%97/twoSum%E9%97%AE%E9%A2%98%E7%9A%84%E6%A0%B8%E5%BF%83%E6%80%9D%E6%83%B3.md
// https://leetcode-cn.com/problems/two-sum/

// 借助hashmap
// 如[2,7,11,15]，则hashmap为{"2":0,"7":1,"11":2,"15":3}，key存数组中的值，value存数组索引值
class TwoSum {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> hashtable = new HashMap<Integer, Integer>();
        for (int i = 0; i < nums.length; ++i) {
            if (hashtable.containsKey(target - nums[i])) {
                return new int[]{hashtable.get(target - nums[i]), i};
            }
            hashtable.put(nums[i], i);
        }
        return new int[0];
    }
}