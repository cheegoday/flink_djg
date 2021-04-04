package com.djg.algorithm.slicewindow;

import java.util.HashMap;
import static java.lang.StrictMath.max;
// 最长不含重复字符的子字符串


// 遇到子串问题，首先想到的就是滑动窗口
// 第一步：移动右指针，直到右指针的值出现重复
// 第二步：移动左指针，直到右指针的值不再重复
// 技巧：在窗口中使用map维护字符数

// 输入: "abcabcbb"
// 输出: 3

// https://leetcode-cn.com/problems/zui-chang-bu-han-zhong-fu-zi-fu-de-zi-zi-fu-chuan-lcof/solution/mian-shi-ti-48-zui-chang-bu-han-zhong-fu-zi-fu-d-9/
// https://github.com/jiajunhua/labuladong-fucking-algorithm/blob/master/%E7%AE%97%E6%B3%95%E6%80%9D%E7%BB%B4%E7%B3%BB%E5%88%97/%E6%BB%91%E5%8A%A8%E7%AA%97%E5%8F%A3%E6%8A%80%E5%B7%A7.md
class LongestSubstring {
    public int lengthOfLongestSubstring(String s) {

        int left = 0, right = 0;

        HashMap<Character, Integer> window = new HashMap<>();

        int res = 0; // 记录最长长度

        while (right < s.length()) {
            char c1 = s.charAt(right);
            window.put(c1, window.getOrDefault(c1, 0) + 1);
            right++;
            // 如果 window 中出现重复字符
            // 开始移动 left 缩小窗口
            while (window.get(c1) > 1) {
                char c2 = s.charAt(left);
                window.put(c2, window.get(c2) - 1);
                left++;
            }
            res = max(res, right - left);
        }
        return res;

    }

    public static void main(String[] args) {
        LongestSubstring longestSubstring = new LongestSubstring();
        int result = longestSubstring.lengthOfLongestSubstring("pwwkew");
        System.out.println(result);
    }
}