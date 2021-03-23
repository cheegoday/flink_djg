package com.djg.algorithm.sort;

import java.util.Arrays;

//Java 代码实现
public class HeapSort{

    public int[] sort(int[] sourceArray) throws Exception {
        // 对arr进行拷贝，不改变参数内容
        int[] arr = Arrays.copyOf(sourceArray, sourceArray.length);
        int len = arr.length;
        heapify(arr, len);
        // 最大值拿出来，放到堆的最后一位，然后对长度为len-1的堆重新siftdown
        for (int i = len - 1; i > 0; i--) {
            // 思考1：此处为什么不需要再走一次heapify？
            swap(arr, 0, i);
            len--;
            siftdown(arr, 0, len);
        }
        return arr;
    }
    // 从最后一个叶子节点的父亲节点开始，往前遍历所有节点，并针对每个节点进行siftdown
    // 思考2：为什么要有这一步？是否可以从i=0进行siftdown操作以取代这一步遍历操作？
    private void heapify(int[] arr, int len) {
        for (int i = (int) Math.floor(len / 2)-1; i >= 0; i--) {
            siftdown(arr, i, len);
        }
    }
    //siftdown：将左右子树中较大者的值与父节点的值对换，递归进行该过程,使得子节点永远小于父节点
    private void siftdown(int[] arr, int i, int len) {
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        int largest = i;

        if (left < len && arr[left] > arr[largest]) {
            largest = left;
        }

        if (right < len && arr[right] > arr[largest]) {
            largest = right;
        }
        // 基线条件：largest == i
        if (largest != i) {
            swap(arr, i, largest);
            //递归条件：因arr[largest]和arr[i]的值进行了对调，无法判断arr[largest]上的当前值是否比子树的值要大，因此要进行递归对比
            siftdown(arr, largest, len);
        }
    }

    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void main(String[] args) {
        HeapSort heapSort = new HeapSort();
        int[] arr = new int[]{3,1,2,9,5};
        heapSort.heapify(arr, 5);
        for (int i=0;i<5;i++){
            System.out.println(arr[i]);
        }
    }

}

