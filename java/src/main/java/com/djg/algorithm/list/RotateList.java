package com.djg.algorithm.list;

/**
 * 反转链表
 * <p>
 * 思路：
 * 1. 递归基线条件：如果head没有下一个节点，则返回head；
 * <p>
 * <p>
 * https://github.com/jiajunhua/labuladong-fucking-algorithm/blob/master/%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84%E7%B3%BB%E5%88%97/%E9%80%92%E5%BD%92%E5%8F%8D%E8%BD%AC%E9%93%BE%E8%A1%A8%E7%9A%84%E4%B8%80%E9%83%A8%E5%88%86.md
 * https://github.com/jiajunhua/labuladong-fucking-algorithm/blob/master/%E9%AB%98%E9%A2%91%E9%9D%A2%E8%AF%95%E7%B3%BB%E5%88%97/k%E4%B8%AA%E4%B8%80%E7%BB%84%E5%8F%8D%E8%BD%AC%E9%93%BE%E8%A1%A8.md
 * https://leetcode-cn.com/problems/fan-zhuan-lian-biao-lcof/
 */


public class RotateList {
    // 递归反转
    public ListNode reverseList(ListNode head) {
        // 基线条件
        if (head == null || head.next == null) {
            return head;
        }
        // 返回新链表的头结点
        ListNode newHead = reverseList(head.next);
        // head追加到新链表尾节点
        head.next.next = head;
        // 由于head是新链表的尾节点，其后置为null
        head.next = null;
        return newHead;
    }

    // 迭代反转
    public ListNode reverse(ListNode a) {
        ListNode pre, cur, nxt;
        pre = null;
        cur = a;
        nxt = a;
        while (cur != null) {
            nxt = cur.next;
            // 逐个结点反转
            cur.next = pre;
            // 更新指针位置
            pre = cur;
            cur = nxt;
        }
        // 返回反转后的头结点
        return pre;
    }

}
