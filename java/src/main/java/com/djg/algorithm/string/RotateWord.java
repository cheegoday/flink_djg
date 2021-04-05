package com.djg.algorithm.string;


/**
 * 翻转单词顺序
 * 输入：dai ji guo
 * 输出：guo ji dai
 *
 * 思路：
 * 1. 使用双指针法，两个指针位于字符串最右边，左指针从右往左遍历字符串，直到遇到空格。将l和r之间的字符串存入数组
 * 2. 将r移动到新字符串的尾部，继续将左移，重复上述步骤
 *
 *
 *
 * https://leetcode-cn.com/problems/fan-zhuan-dan-ci-shun-xu-lcof/solution/mian-shi-ti-58-i-fan-zhuan-dan-ci-shun-xu-shuang-z/
 */


public class RotateWord {
    public String reverseWords(String s) {
        s = s.trim(); // 删除首尾空格
        int j = s.length() - 1, i = j;
        StringBuilder res = new StringBuilder();
        while (i >= 0) {
            while (i >= 0 && s.charAt(i) != ' ')
                i--; // 搜索首个空格
            res.append(s.substring(i + 1, j + 1) + " "); // 添加单词
            while (i >= 0 && s.charAt(i) == ' ')
                i--; // 跳过单词间空格
            j = i; // j 指向下个单词的尾字符
        }
        return res.toString().trim(); // 转化为字符串并返回
    }


    public static void main(String[] args) {
        RotateWord rotateWord = new RotateWord();
        System.out.println(rotateWord.reverseWords("dai ji  guo"));
    }
}
