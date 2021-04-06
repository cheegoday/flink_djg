package com.djg.algorithm.datastructure;

import java.util.Stack;

/**
 * 使用栈实现队列
 * 思路：
 * 1. 插入时往stack1插入
 * 2. 取出时，直接从stack2取出，如果stack2为空，则将stack1中的元素全都迁入到stack2
 *
 * https://github.com/jiajunhua/labuladong-fucking-algorithm/blob/master/%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84%E7%B3%BB%E5%88%97/%E9%98%9F%E5%88%97%E5%AE%9E%E7%8E%B0%E6%A0%88%E6%A0%88%E5%AE%9E%E7%8E%B0%E9%98%9F%E5%88%97.md
 *
 *
 */

public class QueueUsingStack {
    private Stack<Integer> s1;
    private Stack<Integer> s2;

    public QueueUsingStack(Stack s1, Stack s2) {
        this.s1 = s1;
        this.s2 = s2;
    }

    /**
     * 添加元素到队尾
     */
    public void push(int x) {
        s1.push(x);
    }

    /**
     * 返回队头元素
     */
    public int peek() {
        if (s2.isEmpty()) {
            // 把 s1 元素压入 s2
            while (!s1.isEmpty())
                s2.push(s1.pop());
        }
        return s2.peek();
    }


    /**
     * 删除队头的元素并返回
     */
    public int pop() {
        // 先调用 peek 保证 s2 非空
        peek();
        return s2.pop();
    }


    /**
     * 判断队列是否为空
     */
    public boolean empty() {
        return s1.isEmpty() && s2.isEmpty();
    }
}