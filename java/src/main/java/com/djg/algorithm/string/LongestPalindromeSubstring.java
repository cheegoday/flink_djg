package com.djg.algorithm.string;


/**
 *  最大回文子串
 *
 *  思路：中心扩散法
 *  1. 包含两种回文，奇数个：aba，偶数个：abba
 *  2. 从一个字符或者两个字符中间，往两边扩散，找到最大回文
 *
 *  https://github.com/jiajunhua/labuladong-fucking-algorithm/blob/master/%E9%AB%98%E9%A2%91%E9%9D%A2%E8%AF%95%E7%B3%BB%E5%88%97/%E6%9C%80%E9%95%BF%E5%9B%9E%E6%96%87%E5%AD%90%E4%B8%B2.md
 *  https://leetcode-cn.com/problems/longest-palindromic-substring
 */

class LongestPalindromeSubstring {
    public String longestPalindrome(String s) {
        if (s == null || s.length() < 1) return "";
        int start = 0, end = 0;
        for (int i = 0; i < s.length(); i++) {
            // aba
            int len1 = expandAroundCenter(s, i, i);
            // abba
            int len2 = expandAroundCenter(s, i, i + 1);
            int len = Math.max(len1, len2);
            // 从i节点，向两边扩散找到起始点
            if (len > end - start) {
                start = i - (len - 1) / 2;
                end = i + len / 2;
            }
        }
        return s.substring(start, end + 1);
    }


    // 从left和rigth指针向两边扩散，找到最大回文的长度
    private int expandAroundCenter(String s, int left, int right) {
        int L = left, R = right;
        while (L >= 0 && R < s.length() && s.charAt(L) == s.charAt(R)) {
            L--;
            R++;
        }
        return R - L - 1;
    }


    public static void main(String[] args) {
        LongestPalindromeSubstring longestPalindromeSubstring = new LongestPalindromeSubstring();
        System.out.println(longestPalindromeSubstring.longestPalindrome("babad"));
    }
}