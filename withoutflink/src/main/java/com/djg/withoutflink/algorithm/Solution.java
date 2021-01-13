package com.djg.withoutflink.algorithm;

public class Solution {
    public int xxayChangeNGF(int[] xx) {

        int sum = 0;

        for (int i=0; i<xx.length - 1; i++){
            int tmp = xx[i+1];
            if (xx[i]+1 > xx[i+1]){
                xx[i+1] = xx[i] + 1;
                sum = sum + xx[i] + 1 - tmp;
            }
        }

        return sum;
    }

    public static void main(String[] args) {
        Solution solution = new Solution();
        int result = solution.xxayChangeNGF(new int[]{1, 1, 1});
        System.out.println(result);
    }
}