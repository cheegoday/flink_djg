package com.djg.algorithm.array;

/**
 * 最长公共前缀
 *
 * 思路：取第一个字符串为初始最长公共前缀，然后依次跟其他字符串作对比，截取两者的公共前缀，更新到最长公共前缀，循环往复。
 *
 * https://leetcode-cn.com/problems/longest-common-prefix/
 */

public class LongestCommonPrefix {
    public String longestCommonPrefix(String[] strs) {
        if(strs.length == 0)
            return "";
        String ans = strs[0];
        for(int i =1;i<strs.length;i++) {
            int j=0;
            for(;j<ans.length() && j < strs[i].length();j++) {
                if(ans.charAt(j) != strs[i].charAt(j))
                    break;
            }
            ans = ans.substring(0, j);
            if(ans.equals(""))
                return ans;
        }
        return ans;
    }
}