package com.djg.algorithm.list;

/**
 * K 个一组翻转链表
 * 输入：1->2->3->4->5
 * 输出：2->1->4->3->5
 *
 * 思路:
 * 1. 拆解问题，先写一个reverse方法，用来反转a~b节点之间的所有节点
 * 2. 递归地反转连续K个节点，基线条件：不足k个元素，直接返回头结点
 *
 *
 *
 * https://github.com/jiajunhua/labuladong-fucking-algorithm/blob/master/%E9%AB%98%E9%A2%91%E9%9D%A2%E8%AF%95%E7%B3%BB%E5%88%97/k%E4%B8%AA%E4%B8%80%E7%BB%84%E5%8F%8D%E8%BD%AC%E9%93%BE%E8%A1%A8.md
 * https://leetcode-cn.com/problems/reverse-nodes-in-k-group/
 */


public class RotateEveryKList {
    private ListNode reverseKGroup(ListNode head, int k) {
        if (head == null) return null;
        // a~b共计k个节点，通过k来遍历并找到b
        ListNode a, b;
        a = b = head;
        for (int i = 0; i < k; i++) {
            // 基线条件：不足 k 个，不需要反转
            if (b == null)
                return head;
            b = b.next;
        }
        // 反转前 k 个元素
        ListNode newHead = reverse(a, b);
        // 递归反转后续链表并连接起来
        a.next = reverseKGroup(b, k);
        return newHead;
    }


    private ListNode reverse(ListNode a, ListNode b) {
        ListNode pre, cur, nxt;
        pre = null;
        cur = a;
        nxt = a;
        // while 终止的条件改一下就行了
        while (cur != b) {
            nxt = cur.next;
            cur.next = pre;
            pre = cur;
            cur = nxt;
        }
        // 返回反转后的头结点
        return pre;
    }

}
