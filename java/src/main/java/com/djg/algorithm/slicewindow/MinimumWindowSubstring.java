package com.djg.algorithm.slicewindow;

import java.util.HashMap;

// 最小覆盖子串
// 输入：s = "ADOBECODEBANC", t = "ABC"
// 输出："BANC"



// https://leetcode-cn.com/problems/minimum-window-substring/submissions/
// https://github.com/jiajunhua/labuladong-fucking-algorithm/blob/master/%E7%AE%97%E6%B3%95%E6%80%9D%E7%BB%B4%E7%B3%BB%E5%88%97/%E6%BB%91%E5%8A%A8%E7%AA%97%E5%8F%A3%E6%8A%80%E5%B7%A7.md


// 1. 设置两个hashmap，need负责维护目标子串中的值及其个数，window负责维护当前窗口中的值及其个数，一旦window中的值都在need中，且个数大于等于need中的个数，则开始收缩窗口
// 2. 收缩窗口时，右移left指针，直到window不再满足need条件
// 3. 重复右移right指针操作


public class MinimumWindowSubstring {
    public String minWindow(String s, String t) {
        HashMap<Character,Integer> need = new HashMap();
        HashMap<Character,Integer> window = new HashMap();
        //need存放的不重复的字符出现的次数
        for(char c:t.toCharArray())
            need.put(c,need.getOrDefault(c,0)+1);

        //left,right 表示滑动窗口的左右指针
        int left = 0 , right = 0;
        //valid表示是否满足了t中的字符，不算重复的
        int valid = 0;
        //记录最小覆盖子串的起始索引及长度
        int start = 0 , len = Integer.MAX_VALUE;
        while(right < s.length()){
            char c = s.charAt(right);
            right++;
            //判断取出的字符是否在需要的Map中
            if(need.containsKey(c)){
                window.put(c,window.getOrDefault(c,0)+1);
                if(window.get(c).equals(need.get(c)))
                    valid++;
            }

            //判断是否需要收缩（即已经找到了合适的覆盖串）
            while(valid == need.size()){
                //更新最小覆盖子串
                if(right - left < len){
                    start = left;
                    len = right - left;
                }

                char c1 = s.charAt(left);
                //左移窗口
                left++;

                //进行窗口内数据的一系列更新
                //如果当前要移动的字符是包含在need中，我们需要进行讨论，如果该字符的次数刚好与我们需要的次数相等，则valid--，并同时更新window中这个值出现的次数
                if(need.containsKey(c1)){
                    if(window.get(c1).equals(need.get(c1)))
                        valid--;
                    window.put(c1,window.getOrDefault(c1,0)-1);
                }
            }
        }
        return len == Integer.MAX_VALUE ? "" : s.substring(start, start + len);
    }
}
