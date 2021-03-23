package com.djg.algorithm.sort;

import java.util.Arrays;

/*
 * @Author International Dai
 * @Date 22:04 2021-03-23
 * @Description
 *
 * https://leetcode-cn.com/problems/merge-sorted-array/
 *
 *
 * 思路：将一个待排序的数组，拆成左右两个待排序数组；
 * 将左右两个待排序数组再拆分成左右两个待排序数组；
 * 如此递归，递归方法调用栈中，每一层都返回一个merge后的数组（归并排序的前提是假设两个数组都是有序的）
 *
 *
 *
 */

public class MergeSort {


    public int[] sort(int[] sourceArray) throws Exception {
        // 对 arr 进行拷贝，不改变参数内容
        int[] arr = Arrays.copyOf(sourceArray, sourceArray.length);
        // 基线条件，即递归停止条件
        if (arr.length < 2) {
            return arr;
        }
        int middle = (int) Math.floor(arr.length / 2);

        // 将数组均分成左右两个数组，然后进行merge
        int[] left = Arrays.copyOfRange(arr, 0, middle);
        int[] right = Arrays.copyOfRange(arr, middle, arr.length);

        return merge(sort(left), sort(right));
    }




    private int[] merge(int[] left, int[] right) {
        int[] result = new int[left.length + right.length];
        // result数组的索引
        int i = 0;
        while (left.length > 0 && right.length > 0) {
            if (left[0] <= right[0]) {
                result[i++] = left[0];
                // 这里其实就是双指针法，只不过使用了缩小数组的策略替换之

                // left数组长度减一
                left = Arrays.copyOfRange(left, 1, left.length);
            } else {
                result[i++] = right[0];
                // right数组长度减一
                right = Arrays.copyOfRange(right, 1, right.length);
            }
        }

        //执行上面的while循环后，总有一个数组长度会先变成零，长度不为零的数组，全部往目标数组追加
        while (left.length > 0) {
            result[i++] = left[0];
            left = Arrays.copyOfRange(left, 1, left.length);
        }

        while (right.length > 0) {
            result[i++] = right[0];
            right = Arrays.copyOfRange(right, 1, right.length);
        }

        return result;
    }
}
