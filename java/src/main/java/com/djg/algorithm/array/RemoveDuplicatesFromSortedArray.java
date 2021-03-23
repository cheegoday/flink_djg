package com.djg.algorithm.array;

// 使用快慢指针法

// https://github.com/jiajunhua/labuladong-fucking-algorithm/blob/master/%E9%AB%98%E9%A2%91%E9%9D%A2%E8%AF%95%E7%B3%BB%E5%88%97/%E5%A6%82%E4%BD%95%E5%8E%BB%E9%99%A4%E6%9C%89%E5%BA%8F%E6%95%B0%E7%BB%84%E7%9A%84%E9%87%8D%E5%A4%8D%E5%85%83%E7%B4%A0.md
// https://leetcode-cn.com/problems/remove-duplicates-from-sorted-array/

// 输入[0,0,1,1,1,2,2,3,3,4]
// 输出 5

// 1. 移动快指针，当遇到快指针的值 != 满指针的值，右移慢指针，然后将快指针的值赋值给慢指针
// 2. 重复上述逻辑

public class RemoveDuplicatesFromSortedArray {


    private int removeDuplicates(int[] nums) {
        int n = nums.length;
        if (n == 0)
            return 0;
        int slow = 0, fast = 1;
        while (fast < n) {
            if (nums[fast] != nums[slow]) {
                slow++;
                // 维护 nums[0..slow] 无重复
                nums[slow] = nums[fast];
            }
            fast++;
        }
        // 长度为索引 + 1
        return slow + 1;
    }

    public static void main(String[] args) {
        RemoveDuplicatesFromSortedArray obj = new RemoveDuplicatesFromSortedArray();
        int[] arr = {0,0,1,1,1,2,2,3,3,4};
        obj.removeDuplicates(arr);
    }

}
