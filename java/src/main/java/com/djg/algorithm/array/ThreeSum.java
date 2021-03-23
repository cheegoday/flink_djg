package com.djg.algorithm.array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// 使用排序+双指针的方式
// https://leetcode-cn.com/problems/3sum/



public class ThreeSum {
    public List<List<Integer>> ThreeSum(int[] nums) {
        List<List<Integer>> lists = new ArrayList<>();
        // 第一步：排序
        Arrays.sort(nums);
        // 第二步：双指针
        int len = nums.length;
        for(int i = 0;i < len;++i) {
            // 如果当前值比0大，那么往后不存在三数之和为0
            if(nums[i] > 0) return lists;

            if(i > 0 && nums[i] == nums[i-1])
                continue;

            int curr = nums[i];
            int left = i+1, right = len-1;
            while (left < right) {
                int tmp = curr + nums[left] + nums[right];
                if(tmp == 0) {
                    List<Integer> list = new ArrayList<>();
                    list.add(curr);
                    list.add(nums[left]);
                    list.add(nums[right]);
                    lists.add(list);
                    // 过滤重复值
                    while(left < right && nums[left+1] == nums[left]) {
                        ++left;
                    }
                    // 过滤重复值
                    while (left < right && nums[right-1] == nums[right]){
                        --right;
                    }
                    ++left;
                    --right;
                } else if(tmp < 0) {
                    ++left;
                } else {
                    --right;
                }
            }
        }
        return lists;
    }
}
