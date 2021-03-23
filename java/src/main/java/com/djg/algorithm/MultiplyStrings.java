package com.djg.algorithm;

// 大数乘法

// https://github.com/jiajunhua/labuladong-fucking-algorithm/blob/master/%E7%AE%97%E6%B3%95%E6%80%9D%E7%BB%B4%E7%B3%BB%E5%88%97/%E5%AD%97%E7%AC%A6%E4%B8%B2%E4%B9%98%E6%B3%95.md
// https://leetcode-cn.com/problems/multiply-strings/submissions/

// 输入: num1 = "123", num2 = "456"
// 输出: "56088"


// 1. 两字符串分别在最右边设置指针,双循环遍历
// 2. 结果用数组维护，数组长度为m+n；且有一个规律，两字符串中数值相乘后的两位数乘积，十位落在数组的i+j上，个位落在数组的i+j+1上

public class MultiplyStrings {
    public String multiply(String num1, String num2) {
        if (num1.equals("0") || num2.equals("0")) {
            return "0";
        }
        int m = num1.length();
        int n = num2.length();
        // 结果为m+n位
        int [] res = new int [m+n];
        for(int i=m-1;i>=0;i--){
            for(int j=n-1;j>=0;j--){
                // -0实现字符串转int
                int num = (num1.charAt(i)-'0')*(num2.charAt(j)-'0');
                int p1 = i+j;
                int p2 = i+j+1;
                int sum = num+res[p2];
                // 低位值
                res[p2] = sum%10;
                // 高位值
                //此处的+=是为了处理进位用的，例如19*19，列出竖式看一下就知道了。
                res[p1] += sum/10;
            }
        }
        StringBuilder result = new StringBuilder();



        // 边界
        for(int i=0;i<res.length;i++){
            //这里的i==0是因为只可能出现首位为0的情况，例如一个三位数乘一个两位数不可能出现结果是一个三位数的情况。所以只需要判断首位即可。
            if(res[i]==0&&i==0){
                continue;
            }
            result.append(res[i]);
        }
        return result.toString();
    }
}
