package com.djg.algorithm.datastructure;

import java.util.Deque;
import java.util.LinkedList;


/**
 * 思路：
 * 1. 使用辅助栈维护最小值，每插入一个新值到栈中，同步插入最小值到辅助栈中
 * 2. 每删除一个栈中的值，同步删除辅助栈中的最小值
 *
 *  https://leetcode-cn.com/problems/min-stack/
 */
public class MinimumStack {
    Deque<Integer> xStack;
    Deque<Integer> minStack;

    public MinimumStack() {
        xStack = new LinkedList<Integer>();
        minStack = new LinkedList<Integer>();
        minStack.push(Integer.MAX_VALUE);
    }

    public void push(int x) {
        xStack.push(x);
        minStack.push(Math.min(minStack.peek(), x));
    }

    public void pop() {
        xStack.pop();
        minStack.pop();
    }

    public int top() {
        return xStack.peek();
    }

    public int getMin() {
        return minStack.peek();
    }


}
